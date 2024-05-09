package org.example;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends UnicastRemoteObject implements IRemoteMethod {
    @Serial
    private static final long serialVersionUID = 1L;
    private final GraphUtils graphUtils;
    private final Map<Long, ArrayList<Long>> performance;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    protected Server() throws RemoteException {
        super();
        graphUtils = new GraphUtils();
        performance = new HashMap<>();
    }

    @Override
    public ArrayList<String> executeBatch(ArrayList<String> batch) throws RemoteException {
        ArrayList<String> outputs = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        addToPerformance(Thread.currentThread().threadId(), true, startTime);

        for (String s : batch) {
            String[] str = s.split(" ");
            if (str.length != 3) {
                logger.log(Level.WARNING, "Invalid input: " + s);
                continue;
            }
            int src, destination;
            try {
                src = Integer.parseInt(str[1]) - 1;
                destination = Integer.parseInt(str[2]) - 1;
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid input: " + s, e);
                continue;
            }

            synchronized (this) {
                switch (str[0]) {
                    case "Q" -> outputs.add(query(src, destination));
                    case "A" -> addEdge(src, destination);
                    case "D" -> deleteEdge(src, destination);
                    default -> logger.log(Level.WARNING, "Invalid operation: " + str[0]);
                }
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Thread interrupted", e);
                Thread.currentThread().interrupt();
            }
        }

        long stopTime = System.currentTimeMillis();
        addToPerformance(Thread.currentThread().threadId(), false, stopTime);
        return outputs;
    }

    private void addToPerformance(long id, boolean start, long time) {
        if (start) {
            performance.put(id, new ArrayList<>());
            performance.get(id).add(time);
        } else {
            if (performance.containsKey(id)) {
                performance.get(id).add(time);
                logger.info("Thread ID: " + id);
                logger.info("Execution Time: " + (time - performance.get(id).getFirst()) + " ms");
            }
        }
    }

    private void deleteEdge(int src, int destination) {
        try {
            graphUtils.deleteEdge(src, destination);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting edge", e);
        }
    }

    private void addEdge(int src, int destination) {
        try {
            graphUtils.addEdge(src, destination);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding edge", e);
        }
    }

    private String query(int src, int destination) {
        try {
            return Integer.toString(graphUtils.findShortestPath(src, destination));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error executing query", e);
            return "-1";
        }
    }

    @Override
    public String serverReady() throws RemoteException {
        return "R";
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            Server server = new Server();
            registry.rebind("RemoteMethod", server);
            System.out.println("Server is running...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }
}