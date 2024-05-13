package Client;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Threads {
    public static int noOfClients = 2;

    public static void startClient() throws InterruptedException {
        ArrayList<Client> threads = new ArrayList<>();
        for (int i = 0; i<noOfClients; ++i) {
            Client client = new Client();
            
            // Create log file for each client
            String logFileName = "log" + i + ".txt";
            client.setLogFileName(logFileName); // Set log file name for the client
            createLogFile(logFileName);
                        
            threads.add(client);
            threads.get(i).start();
            Thread.sleep(1000);
        }

        for (int j = 0; j<noOfClients; ++j){
            threads.get(j).join();
        }

    }
    private static void createLogFile(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
