
import Server.Server;
import Client.Threads;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.io.File;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        deleteLogFiles();
        System.out.println("starting server firstly");
        Server.startServer();
        System.out.println("Then Starting client");
        while(true){
            Threads.startClient();
            Thread.sleep(10000);
        }


    }
    private static void deleteLogFiles() {
    // Delete log files
    for (int i = 0; i < Threads.noOfClients; i++) {
        String logFileName = "log" + i + ".txt";
        File logFile = new File(logFileName);
        if (logFile.exists()) {
            logFile.delete();
            System.out.println("Deleted log file: " + logFileName);
        } else {
            System.out.println("Log file does not exist: " + logFileName);
        }
    }
    }
}
