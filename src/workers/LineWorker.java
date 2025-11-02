package workers;  
  
import java.io.*;  
  
/**  
 * Worker qui traite une seule ligne de texte  
 */  
public class LineWorker {  
    private int workerId;  
      
    public LineWorker(int id) {  
        this.workerId = id;  
    }  
      
    /**  
     * Compte les mots dans une ligne  
     */  
    public int countWordsInLine(String line) {  
        if(line == null || line.trim().isEmpty()) return 0;  
          
        int words = 0;  
        boolean inWord = false;  
          
        for(char c : line.toCharArray()) {  
            if(Character.isWhitespace(c)) {  
                if(inWord) words++;  
                inWord = false;  
            } else {  
                inWord = true;  
            }  
        }  
        if(inWord) words++;  
          
        System.out.println("[Worker " + workerId + "] Ligne traitée: \"" +   
                          line.substring(0, Math.min(30, line.length())) +   
                          "...\" -> " + words + " mots");  
        return words;  
    }  
      
    public int getId() {  
        return workerId;  
    }  
}