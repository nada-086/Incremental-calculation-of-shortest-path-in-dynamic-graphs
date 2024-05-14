package Server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteMethod extends Remote {
    ArrayList<String> processBatch(ArrayList<String> batchLines, String algoritm) throws RemoteException, IOException;
    boolean serverReady() throws RemoteException;
}
