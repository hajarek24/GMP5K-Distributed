package benchmark;  
  
import java.rmi.*;  
import java.rmi.server.*;  
import java.rmi.registry.*;  
import java.io.*;  
  
interface PingPongIORemote extends Remote {  
    void receiveFile(byte[] data, String filename) throws RemoteException;  
}  
  
class PingPongIOServer extends UnicastRemoteObject implements PingPongIORemote {  
    public PingPongIOServer() throws RemoteException {  
        super();  
    }  
      
    @Override  
    public void receiveFile(byte[] data, String filename) throws RemoteException {  
        try {  
            FileOutputStream fos = new FileOutputStream(filename);  
            fos.write(data);  
            fos.close();  
            System.out.println("[PingPongIOServer] Fichier reçu: " + filename + " (" + data.length + " bytes)");  
        } catch(IOException e) {  
            throw new RemoteException("Erreur I/O", e);  
        }  
    }  
      
    // AJOUTER CETTE MÉTHODE  
    public static void main(String[] args) throws Exception {  
        if(args.length != 1) {  
            System.out.println("Usage: java benchmark.PingPongIOServer <port>");  
            return;  
        }  
          
        int port = Integer.parseInt(args[0]);  
        LocateRegistry.createRegistry(port);  
        PingPongIOServer server = new PingPongIOServer();  
        Naming.rebind("//localhost:" + port + "/PingPongIO", server);  
        System.out.println("PingPongIO server démarré sur port " + port);  
    }  
}
class PingPongIOClient {  
    public static void main(String[] args) throws Exception {  
        if(args.length != 2) {  
            System.out.println("Usage: java benchmark.PingPongIOClient <host> <port>");  
            return;  
        }  
          
        String host = args[0];  
        int port = Integer.parseInt(args[1]);  
        int[] sizes = {1024, 10240, 102400, 1024000}; // 1KB, 10KB, 100KB, 1MB  
          
        String url = "rmi://" + host + ":" + port + "/PingPongIO";  
        PingPongIORemote remote = (PingPongIORemote) Naming.lookup(url);  
          
        for(int size : sizes) {  
            // Créer un fichier temporaire de taille N  
            byte[] data = new byte[size];  
            String filename = "test_" + size + ".dat";  
              
            // Mesure avec I/O : lecture + envoi + réception + écriture  
            long t1 = System.nanoTime();  
              
            // Lecture du fichier (simulée ici avec un tableau)  
            FileInputStream fis = new FileInputStream(filename);  
            fis.read(data);  
            fis.close();  
              
            // Envoi via RMI  
            remote.receiveFile(data, "received_" + filename);  
              
            long t2 = System.nanoTime();  
              
            long duration = t2 - t1;  
            double throughput = (size * 1000000000.0) / duration;  
              
            System.out.printf("Taille: %d bytes, Débit avec I/O: %.2f MB/s\n",   
                size, throughput / (1024 * 1024));  
        }  
    }  
}