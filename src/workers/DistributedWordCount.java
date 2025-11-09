package workers;  
  
import java.io.*;  
import java.rmi.*;  
import java.util.*;  
import java.util.concurrent.*;  
  
public class DistributedWordCount {  
    private List<WorkerNode> workers;  
    private ExecutorService executor;  
      
    public DistributedWordCount(List<WorkerNode> workers) {  
        this.workers = workers;  
        this.executor = Executors.newFixedThreadPool(workers.size());  
    }  
      
    public int processFile(String inputFile) throws Exception {  
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));  
        List<String> lines = new ArrayList<>();  
        String line;  
          
        while((line = reader.readLine()) != null) {  
            if(!line.trim().isEmpty()) {  
                lines.add(line);  
            }  
        }  
        reader.close();  
          
        System.out.println("[DistributedWordCount] " + lines.size() + " lignes à traiter");  
          
        List<Future<Integer>> futures = new ArrayList<>();  
        for(int i = 0; i < lines.size(); i++) {  
            final String currentLine = lines.get(i);  
            final WorkerNode worker = workers.get(i % workers.size());  
              
            Future<Integer> future = executor.submit(() -> {  
                return sendToWorker(worker, currentLine);  
            });  
            futures.add(future);  
        }  
          
        int totalWords = 0;  
        for(Future<Integer> future : futures) {  
            totalWords += future.get();  
        }  
          
        executor.shutdown();  
        return totalWords;  
    }  
      
    private int sendToWorker(WorkerNode worker, String line) throws Exception {  
        String url = "rmi://" + worker.getHost() + ":" + worker.getPort() + "/Worker" + worker.getId();  
        WorkerRemote remote = (WorkerRemote) Naming.lookup(url);  
        return remote.countWordsInLine(line);  
    }  
      
    public static void main(String[] args) throws Exception {  
        if(args.length < 3) {  
            System.out.println("Usage: java workers.DistributedWordCount <input> <output> <worker1:port> [worker2:port] ...");  
            return;  
        }  
          
        String inputFile = args[0];  
        String outputFile = args[1];  
          
        List<WorkerNode> workers = new ArrayList<>();  
        for(int i = 2; i < args.length; i++) {  
            String[] parts = args[i].split(":");  
            workers.add(new WorkerNode(i-2, parts[0], Integer.parseInt(parts[1])));  
        }  
          
        System.out.println("[DistributedWordCount] " + workers.size() + " workers configurés");  
          
        DistributedWordCount dwc = new DistributedWordCount(workers);  
        int totalWords = dwc.processFile(inputFile);  
          
        PrintWriter writer = new PrintWriter(new FileWriter(outputFile));  
        writer.println("Word count: " + totalWords);  
        writer.close();  
          
        System.out.println("[DistributedWordCount] Traitement terminé: " + totalWords + " mots");  
    }  
}  
  
// AJOUTEZ CETTE CLASSE ICI  
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