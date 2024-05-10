package clientRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static clientRMI.BatchRequestGenerator.getBatch;

public class Client extends Thread implements Runnable {

    public static void startClientProcess() throws RemoteException, NotBoundException {
        int graphSize = 5;
        int writePercentage =  50;
        int batchSize = 4;
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IRemoteMethod reg = (IRemoteMethod) registry.lookup("RemoteMethod");

        String batch = getBatch(writePercentage, batchSize, graphSize);
        System.out.println("batch generated is:  \n" + batch);

        long startTime = System.currentTimeMillis();
        String response = reg.executeBatch(batch);
        long endTime = System.currentTimeMillis();

        long responseTime = endTime - startTime;
        System.out.println("Response from server is :  \n" + response
                + "\n Response Time is: "+responseTime );

    }

    @Override
    public void run() {
        try {
            startClientProcess();
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }

    }

    //    public static void main(String[] args) throws NotBoundException, RemoteException {
//        startClientProcess();
//    }

}
