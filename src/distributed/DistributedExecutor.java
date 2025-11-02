package distributed;  
  
import parser.MakefileParser;  
import java.io.*;  
import java.util.*;  
  
/**  
 * Point d'entrée pour le make distribué  
 */  
public class DistributedExecutor {  
    public static void main(String[] args) throws Exception {  
        if(args.length < 2) {  
            System.out.println("Usage: java distributed.DistributedExecutor <Makefile> <workers.conf>");  
            return;  
        }  
          
        String makefilePath = args[0];  
        String workersConf = args[1];  
          
        // Parser le Makefile  
        MakefileParser parser = new MakefileParser();  
        parser.parse(makefilePath);  
          
        // Lire la configuration des workers  
        List<String> workers = readWorkersConfig(workersConf);  
        System.out.println("[DistributedExecutor] " + workers.size() + " workers configurés");  
          
        // Créer le graphe distribué  
        DistributedTaskGraph graph = new DistributedTaskGraph(  
            parser.getDeps(),  
            parser.getCommands(),  
            workers  
        );  
          
        // Exécuter  
        graph.executeDistributed("all");  
        graph.shutdown();  
          
        System.out.println("[DistributedExecutor] Exécution terminée");  
    }  
      
    private static List<String> readWorkersConfig(String path) throws IOException {  
        List<String> workers = new ArrayList<>();  
        BufferedReader reader = new BufferedReader(new FileReader(path));  
        String line;  
          
        while((line = reader.readLine()) != null) {  
            line = line.trim();  
            if(!line.isEmpty() && !line.startsWith("#")) {  
                workers.add(line);  
            }  
        }  
          
        reader.close();  
        return workers;  
    }  
}