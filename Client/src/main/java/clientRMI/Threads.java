package clientRMI;

import java.util.ArrayList;

public class Threads {
    public static int noOfClients = 2;

    public static void main(String[] args) throws InterruptedException {
        ArrayList<Client> threads = new ArrayList<>();
        for (int i = 0; i<noOfClients; ++i) {
            threads.add(new Client());
            
            // Create log file for each client
            String logFileName = "log" + i + ".txt";
            createLogFile(logFileName);
            
            threads.get(i).start();
            Thread.sleep(1000);
        }

        for (int j = 0; j<noOfClients; ++j){
            threads.get(j).join();
        }

    }
    private static void createLogFile(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            // Write header or any initial content if needed
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
