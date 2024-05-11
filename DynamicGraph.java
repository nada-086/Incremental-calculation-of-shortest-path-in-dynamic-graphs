package org.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DynamicGraph {
    private static final Logger logger = LogManager.getLogger(DynamicGraph.class);
    private HashMap<Integer, HashSet<Integer>> graph;
    private HashMap<Integer, HashSet<Integer>> reversedGraph;
    private int graphInitialSize = 0;

    public DynamicGraph(String filePath){
        createGraph(filePath);
    }

    public static void generateGraph(String filePath, int graphSize, double denistyRatio){
        int edgesNumber = (int)(graphSize * (graphSize - 1) * denistyRatio);
        try {
            FileWriter writer = new FileWriter(filePath);
            Random rand = new Random(graphSize);
            for(int i = 0; i < edgesNumber; i++){
                writer.write(rand.nextInt(graphSize) + " " + rand.nextInt(graphSize) + "\n");
            }
            writer.write("S");
            writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void createGraph(String filePath) {
        graph = new HashMap<>();
        reversedGraph = new HashMap<>();
        try{
            FileInputStream fileInputStream = new FileInputStream(filePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.charAt(0) == 'S') break;
                String[] edge = line.split(" ");
                int u = Integer.parseInt(edge[0]), v = Integer.parseInt(edge[1]);
                if (!graph.containsKey(u)){
                    graph.put(u, new HashSet<Integer>());
                }
                graph.get(u).add(v);

                if (!reversedGraph.containsKey(v)){
                    reversedGraph.put(v, new HashSet<Integer>());
                }
                reversedGraph.get(v).add(u);

                graphInitialSize = Math.max(graphInitialSize, Math.max(u, v));
            }
            bufferedReader.close();
            logger.info("Graph created with initial size: " + graphInitialSize);
        } catch (Exception e) {
            logger.error("Error reading file: " + e.getMessage());
        }
    }

    public void add(int u, int v){
        if (!graph.containsKey(u)){
            graph.put(u, new HashSet<Integer>());
        }
        graph.get(u).add(v);

        if (!reversedGraph.containsKey(v)){
            reversedGraph.put(v, new HashSet<Integer>());
        }
        reversedGraph.get(v).add(u);
    }

    public void delete(int u, int v) {
        if (graph.containsKey(u)){
            graph.get(u).remove(v);
        }

        if (reversedGraph.containsKey(v)){
            reversedGraph.get(v).remove(u);
        }
    }

    public int shortestPath(int u, int v , String algorithm) {
        if(algorithm == "BFS")
            return BFS(u,v);
        else
            return bidirectionalBFS(u, v);
    }

    int BFS(int u, int v){
        if (u == v) return 0;

        HashMap<Integer, Integer> visited= new HashMap<>();

        Queue<Integer> queue = new LinkedList<>();

        visited.put(u, 0);
        queue.add(u);

        while (!queue.isEmpty()) {
            int current= queue.remove();
            if (graph.containsKey(current)) {
                for (int neighbor : graph.get(current)) {
                    if (!visited.containsKey(neighbor)) {
                        if(neighbor == v)
                            return visited.get(current) + 1;

                        visited.put(neighbor, visited.get(current) + 1);
                        queue.add(neighbor);
                    }
                }
            }
        }

        return -1;
    }

    int bidirectionalBFS(int u, int v){
        if (u == v) return 0;

        HashMap<Integer, Integer> visitedForward = new HashMap<>();
        HashMap<Integer, Integer> visitedBackward = new HashMap<>();

        Queue<Integer> queueForward = new LinkedList<>();
        Queue<Integer> queueBackward = new LinkedList<>();

        visitedForward.put(u, 0);
        visitedBackward.put(v, 0);
        queueForward.add(u);
        queueBackward.add(v);

        while (!queueForward.isEmpty() && !queueBackward.isEmpty()) {
            // Forward BFS
            int currentForward = queueForward.remove();
            if (graph.containsKey(currentForward)) {
                for (int neighbor : graph.get(currentForward)) {
                    if (!visitedForward.containsKey(neighbor)) {
                        visitedForward.put(neighbor, visitedForward.get(currentForward) + 1);
                        queueForward.add(neighbor);
                    }
                    if (visitedBackward.containsKey(neighbor)) {
                        return visitedForward.get(neighbor) + visitedBackward.get(neighbor);
                    }
                }
            }

            // Backward BFS
            int currentBackward = queueBackward.remove();
            if (reversedGraph.containsKey(currentBackward)) {
                for (int neighbor : reversedGraph.get(currentBackward)) {
                    if (!visitedBackward.containsKey(neighbor)) {
                        visitedBackward.put(neighbor, visitedBackward.get(currentBackward) + 1);
                        queueBackward.add(neighbor);
                    }
                    if (visitedForward.containsKey(neighbor)) {
                        return visitedForward.get(neighbor) + visitedBackward.get(neighbor);
                    }
                }
            }
        }

        return -1;
    }

    int getGraphInitialSize(){
        return graphInitialSize;
    }
}