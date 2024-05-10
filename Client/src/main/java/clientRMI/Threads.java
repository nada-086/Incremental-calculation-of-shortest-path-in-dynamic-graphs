package clientRMI;

import java.util.ArrayList;

public class Threads {
    public static int noOfClients = 2;

    public static void main(String[] args) throws InterruptedException {
        ArrayList<Client> threads = new ArrayList<>();
        for (int i = 0; i<noOfClients; ++i) {
            threads.add(new Client());
            threads.get(i).start();
            Thread.sleep(1000);
        }

        for (int j = 0; j<noOfClients; ++j){
            threads.get(j).join();

    }






    }
}
