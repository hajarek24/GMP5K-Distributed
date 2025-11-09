package benchmark;  
  
import java.rmi.*;  
import java.rmi.server.*;  
import java.rmi.registry.*;  
  
interface PingPongRemote extends Remote {  
    byte[] ping(byte[] data) throws RemoteException;  
}  
  
class PingPongServer extends UnicastRemoteObject implements PingPongRemote {  
    public PingPongServer() throws RemoteException {  
        super();  
    }  
      
    @Override  
    public byte[] ping(byte[] data) throws RemoteException {  
        return new byte[1]; // Pong de taille 1  
    }  
      
    public static void main(String[] args) throws Exception {  
        int port = Integer.parseInt(args[0]);  
        LocateRegistry.createRegistry(port);  
        PingPongServer server = new PingPongServer();  
        Naming.rebind("//localhost:" + port + "/PingPong", server);  
        System.out.println("PingPong server démarré sur port " + port);  
    }  
}  
  
class PingPongClient {  
    public static void main(String[] args) throws Exception {  
        String host = args[0];  
        int port = Integer.parseInt(args[1]);  
        int[] sizes = {1, 1024, 10240, 102400, 1024000};  
          
        String url = "rmi://" + host + ":" + port + "/PingPong";  
        PingPongRemote remote = (PingPongRemote) Naming.lookup(url);  
          
        // Mesure RTT (N=1)  
        long t1 = System.nanoTime();  
        remote.ping(new byte[1]);  
        long t2 = System.nanoTime();  
        long rtt = t2 - t1;  
          
        System.out.println("RTT: " + (rtt / 1000000.0) + " ms");  
        System.out.println("Latence: " + (rtt / 2000000.0) + " ms");  
          
        // Mesures pour différentes tailles  
        for(int size : sizes) {  
            byte[] data = new byte[size];  
            t1 = System.nanoTime();  
            remote.ping(data);  
            t2 = System.nanoTime();  
              
            long duration = t2 - t1;  // ← CHANGEMENT ICI  
            double throughput = (size * 1000000000.0) / duration;  
              
            System.out.printf("Taille: %d bytes, Débit: %.2f MB/s\n",   
                size, throughput / (1024 * 1024));  
        }  
    }  
}