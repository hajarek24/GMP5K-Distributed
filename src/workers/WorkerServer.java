package workers;  
  
import java.rmi.*;  
import java.rmi.server.*;  
import java.rmi.registry.*;  
  
public class WorkerServer extends UnicastRemoteObject implements WorkerRemote {  
    private int workerId;  
    private LineWorker worker;  
      
    public WorkerServer(int workerId) throws RemoteException {  
        super();  
        this.workerId = workerId;  
        this.worker = new LineWorker(workerId);  
    }  
      
    @Override  
    public int countWordsInLine(String line) throws RemoteException {  
        return worker.countWordsInLine(line);  
    }  
      
    public static void main(String[] args) {  
        if(args.length != 2) {  
            System.out.println("Usage: java workers.WorkerServer <workerId> <port>");  
            return;  
        }  
          
        try {  
            int workerId = Integer.parseInt(args[0]);  
            int port = Integer.parseInt(args[1]);  
              
            // Créer le registre RMI sur ce port  
            LocateRegistry.createRegistry(port);  
              
            // Créer et enregistrer le worker  
            WorkerServer server = new WorkerServer(workerId);  
            String name = "Worker" + workerId;  
            Naming.rebind("//localhost:" + port + "/" + name, server);  
              
            System.out.println("[WorkerServer " + workerId + "] Démarré sur port " + port);  
        } catch(Exception e) {  
            System.err.println("[WorkerServer] Erreur: " + e.getMessage());  
            e.printStackTrace();  
        }  
    }  
}