package clientRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteMethod extends Remote {
    ArrayList<String> executeBatch(ArrayList<String> batch) throws RemoteException;
}
