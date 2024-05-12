package clientRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import static clientRMI.BatchRequestGenerator.getBatch;

public class Client extends Thread implements Runnable {
    private String logFileName;

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public static void startClientProcess() throws RemoteException, NotBoundException {
        int graphSize = 5;
        int writePercentage =  50;
        int batchSize = 4;
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IRemoteMethod reg = (IRemoteMethod) registry.lookup("RemoteMethod");

        ArrayList<String> batch = getBatch(writePercentage, batchSize, graphSize);
        System.out.println("batch generated is:  \n" + batch);

        long startTime = System.currentTimeMillis();
        ArrayList<String> response = reg.executeBatch(batch);
        long endTime = System.currentTimeMillis();

        long responseTime = endTime - startTime;
        System.out.println("Response from server is :  \n" + response
                + "\n Response Time is: "+responseTime );

        logToFile(logFileName, batch, response, responseTime);

    }
    
private static void logToFile(String fileName, ArrayList<String> batch, ArrayList<String> responses, long responseTime) {
    try {
        FileWriter fileWriter = new FileWriter(fileName, true);
        
        // Log batch generated
        fileWriter.write("Batch generated:\n");
        for (String operation : batch) {
            fileWriter.write(operation + "\n");
        }
        
        // Log each response from the server
        fileWriter.write("Response from server:\n");
        for (String response : responses) {
            fileWriter.write(response + "\n");
        }
        
        // Log response time
        fileWriter.write("Response Time: " + responseTime + " ms\n");
        
        // Add a separator between entries for better readability
        fileWriter.write("------------------------\n");
        
        fileWriter.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @Override
    public void run() {
        try {
            startClientProcess();
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

//        public static void main(String[] args) throws NotBoundException, RemoteException {
//             startClientProcess();
//        }

}
