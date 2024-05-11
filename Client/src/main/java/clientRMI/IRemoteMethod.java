package clientRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteMethod extends Remote {
    String executeBatch(String batch) throws RemoteException;
}
