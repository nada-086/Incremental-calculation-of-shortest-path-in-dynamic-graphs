package Server;

import java.io.Serial;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class Server extends UnicastRemoteObject implements IRemoteMethod {
    @Serial
    private static final long serialVersionUID = 1L;
    private final DynamicGraph graph;
    private final Map<Long, ArrayList<Long>> performance2;
    private static final Logger logger = Logger.getLogger(Server.class.getName());


    protected Server() throws RemoteException, IOException {
        super();
        graph = new DynamicGraph("graph.txt");
        performance2 = new HashMap<>();
        setupLogger();
    }

    private void setupLogger() throws IOException {
        logger.setUseParentHandlers(false);
        FileHandler fileHandler = new FileHandler("server_log.log");
        fileHandler.setFormatter(new CustomFormatter());
        logger.addHandler(fileHandler);
    }

    static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getMessage() + System.lineSeparator();
        }
    }

    @Override
    public synchronized ArrayList<String> processBatch(ArrayList<String> batchLines, String algorithm) throws IOException {
        logger.info("Server started a batch_2");

        ArrayList<String> result = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        addToPerformance(Thread.currentThread().threadId(), true, startTime,performance2);

        for (String line : batchLines) {
            String[] operation = line.split(" ");
            if (operation.length != 3) {
                logger.log(Level.WARNING,"");
                continue;
            }
            char queryType = operation[0].charAt(0);
            int u = Integer.parseInt(operation[1]);
            int v = Integer.parseInt(operation[2]);

            synchronized (this) {
                switch (queryType) {
                    case 'Q' -> {
                        int out = graph.shortestPath(u, v, algorithm);
                        logger.info("Shortest path between " + u + " and " + v + " using " + algorithm + " is: " + out);
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
                    case 'F' -> {
                              // logger.info("---------------------------------");
                    }
                    default ->  logger.log(Level.WARNING, "Invalid operation: " + queryType);
                }
            }
        }
        long stopTime = System.currentTimeMillis();
        addToPerformance(Thread.currentThread().threadId(), false, stopTime,performance2);
        logger.info("Server finished a batch_2");
        logger.info("------------------------------------------");
        return result;
    }

    private synchronized void addToPerformance(long id, boolean start, long time,Map<Long, ArrayList<Long>> performance) {
        if (start) {
            performance.put(id, new ArrayList<>());
            performance.get(id).add(time);
        } else {
            if (performance.containsKey(id)) {
                performance.get(id).add(time);
                //logger.info("Thread ID: " + id);
                logger.info("Execution Time: " + (time - performance.get(id).getFirst()) + " ms");
            }
        }
    }

    @Override
    public boolean serverReady() throws RemoteException {
        return Objects.equals(graph.getReady(), "R");
    }

    public static void startServer() {
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
