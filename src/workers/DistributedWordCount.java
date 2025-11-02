package workers;  
  
import java.io.*;  
import java.net.*;  
import java.util.*;  
import java.util.concurrent.*;  
  
/**  
 * Application distribuée qui compte les mots  
 * en distribuant les lignes aux workers  
 */  
public class DistributedWordCount {  
    private List<WorkerNode> workers;  
    private ExecutorService executor;  
      
    public DistributedWordCount(List<WorkerNode> workers) {  
        this.workers = workers;  
        this.executor = Executors.newFixedThreadPool(workers.size());  
    }  
      
    /**  
     * Traite un fichier en distribuant les lignes  
     */  
    public int processFile(String inputFile) throws Exception {  
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));  
        List<String> lines = new ArrayList<>();  
        String line;  
          
        // Lire toutes les lignes  
        while((line = reader.readLine()) != null) {  
            if(!line.trim().isEmpty()) {  
                lines.add(line);  
            }  
        }  
        reader.close();  
          
        System.out.println("[DistributedWordCount] " + lines.size() + " lignes à traiter");  
          
        // Distribuer les lignes aux workers  
        List<Future<Integer>> futures = new ArrayList<>();  
        for(int i = 0; i < lines.size(); i++) {  
            final String currentLine = lines.get(i);  
            final WorkerNode worker = workers.get(i % workers.size());  
              
            Future<Integer> future = executor.submit(() -> {  
                return sendToWorker(worker, currentLine);  
            });  
            futures.add(future);  
        }  
          
        // Collecter les résultats  
        int totalWords = 0;  
        for(Future<Integer> future : futures) {  
            totalWords += future.get();  
        }  
          
        executor.shutdown();  
        return totalWords;  
    }  
      
    /**  
     * Envoie une ligne à un worker et récupère le résultat  
     */  
    private int sendToWorker(WorkerNode worker, String line) throws IOException {  
        Socket socket = new Socket(worker.getHost(), worker.getPort());  
          
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);  
        BufferedReader in = new BufferedReader(  
            new InputStreamReader(socket.getInputStream())  
        );  
          
        out.println(line);  
        String response = in.readLine();  
          
        socket.close();  
          
        return Integer.parseInt(response);  
    }  
      
    public static void main(String[] args) throws Exception {  
        if(args.length < 3) {  
            System.out.println("Usage: java workers.DistributedWordCount <input> <output> <worker1:port> [worker2:port] ...");  
            return;  
        }  
          
        String inputFile = args[0];  
        String outputFile = args[1];  
          
        // Parser la liste des workers  
        List<WorkerNode> workers = new ArrayList<>();  
        for(int i = 2; i < args.length; i++) {  
            String[] parts = args[i].split(":");  
            workers.add(new WorkerNode(i-2, parts[0], Integer.parseInt(parts[1])));  
        }  
          
        System.out.println("[DistributedWordCount] " + workers.size() + " workers configurés");  
          
        DistributedWordCount dwc = new DistributedWordCount(workers);  
        int totalWords = dwc.processFile(inputFile);  
          
        // Écrire le résultat  
        PrintWriter writer = new PrintWriter(new FileWriter(outputFile));  
        writer.println("Word count: " + totalWords);  
        writer.close();  
          
        System.out.println("[DistributedWordCount] Traitement terminé: " + totalWords + " mots");  
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