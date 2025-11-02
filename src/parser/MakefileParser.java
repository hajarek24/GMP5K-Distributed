package parser;  
  
import java.io.*;  
import java.util.*;  
  
public class MakefileParser {  
    private Map<String, List<String>> deps = new HashMap<>();  
    private Map<String, List<String>> commands = new HashMap<>();  
  
    public void parse(String filename) throws IOException {  
        BufferedReader br = new BufferedReader(new FileReader(filename));  
        String line;  
        String currentTarget = null;  
  
        while((line = br.readLine()) != null) {  
            // Vérifier si la ligne commence par une tabulation AVANT trim()  
            if(line.startsWith("\t") && currentTarget != null) {  
                if(!commands.containsKey(currentTarget)) {  
                    commands.put(currentTarget, new ArrayList<>());  
                }  
                commands.get(currentTarget).add(line.trim());  
                continue;  
            }  
              
            // Vérifier si vide APRÈS avoir vérifié les commandes  
            if(line.trim().isEmpty()) continue;  
              
            if(line.contains(":")) {  
                String[] parts = line.split(":");  
                currentTarget = parts[0].trim();  
                deps.put(currentTarget, new ArrayList<>());  
                if(parts.length > 1) {  
                    String[] d = parts[1].trim().split("\\s+");  
                    for(String dep : d) {  
                        if(!dep.isEmpty()) {  
                            deps.get(currentTarget).add(dep);  
                        }  
                    }  
                }  
            }  
        }  
        br.close();  
    }  
  
    public Map<String, List<String>> getDeps() {  
        return deps;  
    }  
  
    public Map<String, List<String>> getCommands() {  
        return commands;  
    }  
}