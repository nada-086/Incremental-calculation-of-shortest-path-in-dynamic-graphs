package Client;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;

import static Client.BatchRequestGenerator.getBatch;
import Server.IRemoteMethod;
public class Client extends Thread implements Runnable {
    private static String logFileName;

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public static void startClientProcess() throws IOException, NotBoundException {
        int graphSize = 6;
        int writePercentage =  50;
        int batchSize = 4;
        Registry registry = LocateRegistry.getRegistry("127.0.1.1",1099);
        IRemoteMethod reg = (IRemoteMethod) registry.lookup("RemoteMethod");

        ArrayList<String> batch = getBatch(writePercentage, batchSize, graphSize);
        System.out.println("batch generated is:  \n" + batch);
        if(reg.serverReady()){
            long startTime = System.currentTimeMillis();
            ArrayList<String> response = reg.processBatch(batch, "notBFS");
            long endTime = System.currentTimeMillis();

            long responseTime = endTime - startTime;
            System.out.println("Response from server is :  \n" + response
                    + "\n Response Time is: "+responseTime );

            logToFile(logFileName, batch, response, responseTime);
        }
    }
     public static void performanceWritePercentage() throws IOException, NotBoundException {
        int graphSize = 6;
        int[] writePercentage =  {30, 50, 70, 90};
        int batchSize = 4;
        Registry registry = LocateRegistry.getRegistry("127.0.1.1",1099);
        IRemoteMethod reg = (IRemoteMethod) registry.lookup("RemoteMethod");
        long[] median = new long[5];
        int noOfReadings = 5;
        for (int k : writePercentage) {
           // long sumTime = 0;
            for (int j = 0; j < noOfReadings; j++) {
                ArrayList<String> batch = getBatch(k, batchSize, graphSize);
                System.out.println("batch generated is:  \n" + batch);
                if (reg.serverReady()) {
                    long startTime = System.currentTimeMillis();
                    ArrayList<String> response = reg.processBatch(batch, "BFS");
                    long endTime = System.currentTimeMillis();

                    long responseTime = endTime - startTime;
                    System.out.println("response time : "+ responseTime);
                   // sumTime += responseTime;
                    median[j] = responseTime;
                }

            }
            Arrays.sort(median);
            System.out.println("average response time for write percentage :" + k
                    + " and 10 readings equals: " + (median[noOfReadings/2]) + "ms");

        }
    }

    public static void ResponseTimeNodes() throws IOException, NotBoundException {
        int graphSize = 10;
        int batchSize = 10;
        Registry registry = LocateRegistry.getRegistry("127.0.1.1",1099);
        IRemoteMethod reg = (IRemoteMethod) registry.lookup("RemoteMethod");
        int noOfReadings = 5;
        int writePercentage = 50;
        long sum = 0;
        for (int j = 0; j < noOfReadings; j++) {
            ArrayList<String> batch = getBatch(writePercentage, batchSize, graphSize);
            System.out.println("batch generated is:  \n" + batch);
            System.out.println(reg.serverReady());
            if (reg.serverReady()) {
                System.out.println("Entered");
                long startTime = System.nanoTime();
                ArrayList<String> response = reg.processBatch(batch, "BFS");
                System.out.println("Size of Response: " + response.size());
                long endTime = System.nanoTime();
                long responseTime = endTime - startTime;
                System.out.println("response time : "+ responseTime);
                sum += responseTime;
            }
        }
        System.out.println("Average Response = " + (sum / 5) + " ns");
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
           //            startClientProcess();
            ResponseTimeNodes();
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//        public static void main(String[] args) throws NotBoundException, RemoteException {
//             startClientProcess();
//        }
}
