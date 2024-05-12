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
    private GraphUtils graphUtils;
    private DynamicGraph graph;
    private final Map<Long, ArrayList<Long>> performance;

    private final Map<Long, ArrayList<Long>> performance2;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    protected Server() throws RemoteException {
        super();
        // Add the absolut path for the graph file
        graphUtils = new GraphUtils("graph.txt");

        graph = new DynamicGraph("graph.txt");

        performance = new HashMap<>();

        performance2 = new HashMap<>();

    }

    @Override
    public ArrayList<String> executeBatch(ArrayList<String> batch) throws RemoteException {
        logger.info("Server started a batch_1");
        ArrayList<String> outputs = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        addToPerformance(Thread.currentThread().threadId(), true, startTime,performance);

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
                    case "F" -> {}
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
        addToPerformance(Thread.currentThread().threadId(), false, stopTime,performance);
        logger.info("Server finnished a batch_1");
        return outputs;
    }

    @Override
    public synchronized ArrayList<String> processBatch(String batch, String algoritm) throws RemoteException {
        logger.info("Server started a batch_2");
        //StringBuilder result = new StringBuilder();
        ArrayList<String> result = new ArrayList<>();
        String[] batchLines=batch.split("\n");
        long startTime = System.currentTimeMillis();
        addToPerformance(Thread.currentThread().threadId(), true, startTime,performance2);

        for(String line : batchLines){

            String[] operation =line.split(" ");
            if (operation.length != 3) {
                logger.log(Level.WARNING, "Invalid input: " + line);
                continue;
            }
            char queryType =operation[0].charAt(0);
            if (queryType == 'F') break;
            int u = Integer.parseInt(operation[1]);
            int v = Integer.parseInt(operation[2]);

            synchronized (this) {
                switch (queryType) {
                    case 'Q' -> {
                        int out = graph.shortestPath(u, v , algoritm);
                        logger.info("Shortest path between " + u + " and " + v + " usign " + algoritm + " is: " + out);
                        result.add(Integer.toString(out));
                    }
                    case 'A' -> {
                        graph.add(u, v);
                        logger.info("Edge added from " + u + " to " + v);
                    }
                    case 'D' -> {
                        graph.delete(u, v);
                        logger.info("Edge removed from " + u + " to " + v);
                    }
                    case 'F' -> {}
                    default -> logger.log(Level.WARNING, "Invalid operation: " + queryType);
                }
            }
//            if(queryType == 'A'){
//                graph.add(u, v);
//                logger.info("Edge added from " + u + " to " + v);
//            }
//            else if(queryType == 'D'){
//                graph.delete(u, v);
//                logger.info("Edge removed from " + u + " to " + v);
//            }
//            else if (queryType == 'Q'){
//                int out = graph.shortestPath(u, v , algoritm);
//                logger.info("Shortest path between " + u + " and " + v + " usign " + algoritm + " is: " + out);
//                result.add(Integer.toString(out));
//            }
//            else {
//                logger.log(Level.WARNING, "Invalid operation: " + queryType);
//            }
        }
        long stopTime = System.currentTimeMillis();
        addToPerformance(Thread.currentThread().threadId(), false, stopTime,performance2);
        logger.info("Server finnished a batch_2");
        return result;
    }


    private void addToPerformance(long id, boolean start, long time,Map<Long, ArrayList<Long>> performance) {
        if (start) {
            performance.put(id, new ArrayList<>());
            performance.get(id).add(time);
        } else {
            if (performance.containsKey(id)) {
                performance.get(id).add(time);
                logger.info("Thread ID: " + id);
                logger.info("Execution Time: " + (time - performance.get(id).get(0)) + " ms");
            }
        }
    }

    private void deleteEdge(int src, int destination) {
        try {
            graphUtils.deleteEdge(src, destination);
            logger.info("Edge removed from " + src + " to " + destination);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting edge", e);
        }
    }

    private void addEdge(int src, int destination) {
        try {
            graphUtils.addEdge(src, destination);
            logger.info("Edge added from " + src + " to " + destination);
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
