package org.example;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteMethod {
    ArrayList<String> executeBatch(ArrayList<String> batch) throws RemoteException;

    ArrayList<String> processBatch(String batch, String algoritm) throws RemoteException;
    String serverReady() throws RemoteException;

}
