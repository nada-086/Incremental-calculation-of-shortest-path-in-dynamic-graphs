package org.example;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphUtils {

    private static final Logger logger = Logger.getLogger(GraphUtils.class.getName());

    private ArrayList<ArrayList<Integer>> graph;

    public GraphUtils() {
        this.graph = new ArrayList<>();
    }

    // Constructor to initialize the graph from a file
    public GraphUtils(String fileName) {
        this.graph = new ArrayList<>();
        try {
            initializeFromFile(fileName);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error initializing graph from file: " + fileName, e);
        }
    }

    private void initializeFromFile(String fileName) throws IOException {
        ArrayList<String> data = readFromFile(fileName);
        ArrayList<Point> nodes = new ArrayList<>();
        int max = -1;

        for (String line : data) {
            if (line.equals("S")) {
                break; // Stop reading when 'S' is encountered
            }

            String[] parts = line.split(" ");
            if (parts.length != 2) {
                logger.log(Level.WARNING, "Invalid input format: " + line);
                continue;
            }

            try {
                int nodeIndex1 = Integer.parseInt(parts[0]);
                int nodeIndex2 = Integer.parseInt(parts[1]);
                nodes.add(new Point(nodeIndex1, nodeIndex2));
                max = Math.max(max, Math.max(nodeIndex1, nodeIndex2));
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid number format: " + line, e);
            }
        }

        for (int i = 0; i <= max; i++) {
            graph.add(new ArrayList<>());
        }

        for (Point node : nodes) {
            graph.get(node.x).add(node.y);
        }
    }

    private ArrayList<String> readFromFile(String fileName) throws IOException {
        ArrayList<String> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
        }
        return records;
    }

    public void deleteEdge(int src, int dest) {
        try {
            if (src < graph.size()) {
                graph.get(src).remove((Integer) dest);
            }
        } catch (IndexOutOfBoundsException e) {
            logger.log(Level.WARNING, "Index out of bounds in deleteEdge", e);
        }
    }

    public void addEdge(int src, int dest) {
        try {
            if (src < graph.size() && !graph.get(src).contains(dest)) {
                graph.get(src).add(dest);
            }
            if (dest < graph.size() && !graph.get(dest).contains(src)) {
                graph.get(dest).add(src); // Assuming the graph is undirected
            }
        } catch (IndexOutOfBoundsException e) {
            logger.log(Level.WARNING, "Index out of bounds in addEdge", e);
        }
    }

    public int findShortestPath(int src, int dest) {
        int numberOfNodes = -1;
        try {
            boolean[] visited = new boolean[graph.size()];
            ArrayList<Node> queue = new ArrayList<>();
            visited[src] = true;
            queue.add(new Node(src, 0));
            while (!queue.isEmpty()) {
                Node node = queue.remove(0);
                if (node.nodeNum() == dest) {
                    numberOfNodes = node.cost();
                    return numberOfNodes;
                }
                for (int adjNode : graph.get(node.nodeNum())) {
                    if (!visited[adjNode]) {
                        visited[adjNode] = true;
                        queue.add(new Node(adjNode, node.cost() + 1));
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            logger.log(Level.WARNING, "Index out of bounds in findShortestPath", e);
        }
        return numberOfNodes;
    }
}
