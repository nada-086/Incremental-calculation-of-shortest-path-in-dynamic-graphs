package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteMethod extends Remote {
    ArrayList<String> processBatch(ArrayList<String> batchLines, String algoritm) throws RemoteException;
    boolean serverReady() throws RemoteException;
}
