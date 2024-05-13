
import Server.Server;
import Client.Threads;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("starting server firstly");
        Server.startServer();
        System.out.println("Then Starting client");
        while(true){
            Threads.startClient();
            Thread.sleep(10000);
        }


    }
}