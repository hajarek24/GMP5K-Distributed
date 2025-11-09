package workers;  
  
import java.io.*;  
import java.net.*;  
import java.util.*;  
import java.util.concurrent.*; 
import java.rmi.*;
import java.rmi.registry.*; 
  
public class DynamicDistributedWordCount {  
    private List<WorkerNode> workers;  
    private BlockingQueue<String> taskQueue;  
    private ExecutorService executor;  
      
    public DynamicDistributedWordCount(List<WorkerNode> workers) {  
        this.workers = workers;  
        this.taskQueue = new LinkedBlockingQueue<>();  
        this.executor = Executors.newFixedThreadPool(workers.size());  
    }  
      
    public int processFile(String inputFile) throws Exception {  
        // Lire toutes les lignes et les mettre dans la queue  
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));  
        String line;  
        int lineCount = 0;  
          
        while((line = reader.readLine()) != null) {  
            if(!line.trim().isEmpty()) {  
                taskQueue.offer(line);  
                lineCount++;  
            }  
        }  
        reader.close();  
          
        System.out.println("[DynamicDistributedWordCount] " + lineCount + " lignes dans la queue");  
        System.out.println("[DynamicDistributedWordCount] " + workers.size() + " workers actifs");  
          
        // Lancer les workers qui vont consommer la queue  
        List<Future<Integer>> futures = new ArrayList<>();  
        for(WorkerNode worker : workers) {  
            Future<Integer> future = executor.submit(() -> {  
                return processTasksFromQueue(worker);  
            });  
            futures.add(future);  
        }  
          
        // Attendre que tous les workers terminent  
        int totalWords = 0;  
        for(Future<Integer> future : futures) {  
            totalWords += future.get();  
        }  
          
        executor.shutdown();  
        return totalWords;  
    }  
      
    private int processTasksFromQueue(WorkerNode worker) {  
        int workerTotal = 0;  
        int tasksProcessed = 0;  
          
        while(true) {  
            String line = taskQueue.poll();  
            if(line == null) {  
                break; // Plus de tâches  
            }  
              
            try {  
                int count = sendToWorker(worker, line);  
                workerTotal += count;  
                tasksProcessed++;  
            } catch(IOException e) {  
                System.err.println("[Worker " + worker.getId() + "] Erreur: " + e.getMessage());  
            }  
        }  
          
        System.out.println("[Worker " + worker.getId() + "] Traité " + tasksProcessed + " lignes, " + workerTotal + " mots");  
        return workerTotal;  
    }  
      
    private int sendToWorker(WorkerNode worker, String line) throws Exception {  
    String url = "rmi://" + worker.getHost() + ":" + worker.getPort() + "/Worker" + worker.getId();  
    WorkerRemote remote = (WorkerRemote) Naming.lookup(url);  
    return remote.countWordsInLine(line);  
    } 
      
    public static void main(String[] args) throws Exception {  
        if(args.length < 3) {  
            System.out.println("Usage: java workers.DynamicDistributedWordCount <input> <output> <worker1:port> [worker2:port] ...");  
            return;  
        }  
          
        String inputFile = args[0];  
        String outputFile = args[1];  
          
        List<WorkerNode> workers = new ArrayList<>();  
        for(int i = 2; i < args.length; i++) {  
            String[] parts = args[i].split(":");  
            workers.add(new WorkerNode(i-2, parts[0], Integer.parseInt(parts[1])));  
        }  
          
        DynamicDistributedWordCount dwc = new DynamicDistributedWordCount(workers);  
        int totalWords = dwc.processFile(inputFile);  
          
        PrintWriter writer = new PrintWriter(new FileWriter(outputFile));  
        writer.println("Word count: " + totalWords);  
        writer.close();  
          
        System.out.println("[DynamicDistributedWordCount] Traitement terminé: " + totalWords + " mots");  
    }  
}
/**  
 * Représente un nœud worker  
 */  
class WorkerNode {  
    private int id;  
    private String host;  
    private int port;  
      
    public WorkerNode(int id, String host, int port) {  
        this.id = id;  
        this.host = host;  
        this.port = port;  
    }  
      
    public int getId() { return id; }  
    public String getHost() { return host; }  
    public int getPort() { return port; }  
      
    @Override  
    public String toString() {  
        return "Worker" + id + "@" + host + ":" + port;  
    }  
}

interface WorkerRemote extends java.rmi.Remote {  
    int countWordsInLine(String line) throws java.rmi.RemoteException;  
}