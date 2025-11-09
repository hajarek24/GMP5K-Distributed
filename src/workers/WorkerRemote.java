package workers;  
  
import java.rmi.Remote;  
import java.rmi.RemoteException;  
  
public interface WorkerRemote extends Remote {  
    int countWordsInLine(String line) throws RemoteException;  
}