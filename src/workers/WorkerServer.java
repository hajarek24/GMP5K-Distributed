package workers;  
  
import java.io.*;  
import java.net.*;  
  
/**  
 * Serveur qui tourne sur chaque nœud worker  
 * Écoute les requêtes et traite les lignes  
 */  
public class WorkerServer {  
    private int port;  
    private int workerId;  
    private LineWorker worker;  
      
    public WorkerServer(int workerId, int port) {  
        this.workerId = workerId;  
        this.port = port;  
        this.worker = new LineWorker(workerId);  
    }  
      
    public void start() throws IOException {  
        ServerSocket serverSocket = new ServerSocket(port);  
        System.out.println("[WorkerServer " + workerId + "] Démarré sur port " + port);  
          
        while(true) {  
            try {  
                Socket clientSocket = serverSocket.accept();  
                handleClient(clientSocket);  
            } catch(IOException e) {  
                System.err.println("[WorkerServer " + workerId + "] Erreur: " + e.getMessage());  
            }  
        }  
    }  
      
    private void handleClient(Socket socket) throws IOException {  
        BufferedReader in = new BufferedReader(  
            new InputStreamReader(socket.getInputStream())  
        );  
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);  
          
        String line = in.readLine();  
        if(line != null) {  
            int wordCount = worker.countWordsInLine(line);  
            out.println(wordCount);  
        }  
          
        socket.close();  
    }  
      
    public static void main(String[] args) throws IOException {  
        if(args.length != 2) {  
            System.out.println("Usage: java workers.WorkerServer <workerId> <port>");  
            return;  
        }  
          
        int workerId = Integer.parseInt(args[0]);  
        int port = Integer.parseInt(args[1]);  
          
        WorkerServer server = new WorkerServer(workerId, port);  
        server.start();  
    }  
}