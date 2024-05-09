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

    public ArrayList<ArrayList<Integer>> parseGraph(String fileName) throws IOException {
        ArrayList<Point> nodes = new ArrayList<>();
        ArrayList<String> data = readFromFile(fileName);
        int max = -1;

        for (String line : data) {
            String[] s = line.split(" ");
            int index1 = Integer.parseInt(s[0]);
            int index2 = Integer.parseInt(s[1]);
            nodes.add(new Point(index1, index2));
            if (index1 > max) max = index1;
            if (index2 > max) max = index2;
        }

        for (int j = 0; j < max; j++) {
            graph.add(new ArrayList<>());
        }

        for (Point node : nodes) {
            graph.get(node.x - 1).add(node.y - 1);
        }
        return graph;
    }

    private ArrayList<String> readFromFile(String fileName) throws IOException {
        ArrayList<String> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading from file: " + fileName, e);
            throw e;
        }
        return records;
    }

    public void deleteEdge(int src, int dest) {
        try {
            if (src < graph.size()) {
                for (int i = 0; i < graph.get(src).size(); i++) {
                    if (graph.get(src).get(i) == dest) {
                        graph.get(src).remove(i);
                        return;
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            logger.log(Level.WARNING, "Index out of bounds in deleteEdge", e);
        }
    }

    public void addEdge(int src, int dest) {
        try {
            if (src < graph.size()) {
                if (!graph.get(src).contains(dest)) {
                    graph.get(src).add(dest);
                }
            } else {
                graph.add(new ArrayList<>());
                graph.get(src).add(dest);
            }
            if (dest == graph.size()) {
                graph.add(new ArrayList<>());
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
                if (node.nodeNum() >= graph.size()) {
                    continue;
                }
                ArrayList<Integer> adjacent = graph.get(node.nodeNum());
                for (int adjNode : adjacent) {
                    if (adjNode == dest) {
                        numberOfNodes = node.cost() + 1;
                        return numberOfNodes;
                    }
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