package distributed;  
  
import java.io.*;  
import java.util.*;  
import java.util.concurrent.*;  
  
/**  
 * Graphe de tâches avec exécution parallèle distribuée  
 */  
public class DistributedTaskGraph {  
    private Map<String, List<String>> deps;  
    private Map<String, List<String>> commands; 
    private List<String> workers;              
    private ExecutorService executor;
    private Set<String> executed = Collections.synchronizedSet(new HashSet<>());  
    //private ExecutorService executor;  
    private List<String> workerNodes;  
      
    public DistributedTaskGraph(Map<String, List<String>> deps,   
                            Map<String, List<String>> commands,  
                            List<String> workers) {  
        this.deps = deps;  
        this.commands = commands;  
        this.workers = workers;  
        this.executor = Executors.newFixedThreadPool(workers.size());  
} 
      
    /**  
     * Exécute une cible de manière distribuée  
     */  
    public void executeDistributed(String target) throws Exception {  
        System.out.println("[DEBUG] Executing target: " + target);  
        
        if(executed.contains(target)) {  
            System.out.println("[DEBUG] Target already executed: " + target);  
            return;  
        }  
  
        // Exécuter les dépendances d'abord  
        List<String> depList = deps.getOrDefault(target, new ArrayList<>());  
        System.out.println("[DEBUG] Dependencies for " + target + ": " + depList);  
        
        for(String dep : depList) {  
            executeDistributed(dep);  // Appel récursif  
        }  
    
        // Exécuter les commandes de cette cible  
        List<String> cmdList = commands.get(target);  
        System.out.println("[DEBUG] Commands for " + target + ": " + cmdList);  
        
        if(cmdList != null) {  
            for(String cmd : cmdList) {  
                System.out.println("[DistributedTaskGraph] Executing: " + cmd);  
                executeCommand(cmd);  
            }  
        }  
    
        executed.add(target);  
    }  
      
    /**  
     * Exécute une commande (localement pour l'instant)  
     */  
    private void executeCommand(String cmd) throws Exception {  
        String[] cmdArray = cmd.split(" ");  
        ProcessBuilder pb = new ProcessBuilder(cmdArray);  
        pb.directory(new File("."));  
        pb.inheritIO();  
        Process p = pb.start();  
        p.waitFor();  
    }  
      
    public void shutdown() {  
        executor.shutdown();  
    }  
}