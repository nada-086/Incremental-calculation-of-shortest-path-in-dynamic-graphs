package Client;

import java.util.ArrayList;
import java.util.Random;

public class BatchRequestGenerator {

    private static final Random rand = new Random();

    public static ArrayList<String> getBatch(int writePercentage, int batchSize, int graphSize) {
        ArrayList<String> batchesList = new ArrayList<>();

        for (int i = 0; i < batchSize; ++i) {
            int operation = rand.nextInt(100);

            if (operation < writePercentage) {
                generateUpdateOperation(graphSize, batchesList);
            } else {
                generateQueryOperation(graphSize, batchesList);
            }
        }
        batchesList.add("F");
        return batchesList;
    }

    private static void generateUpdateOperation(int graphSize, ArrayList<String> batchesList) {
        int v1 = rand.nextInt(1, graphSize);
        int v2 = rand.nextInt(1, graphSize);
        StringBuilder batch = new StringBuilder();

        char op = (rand.nextInt(2) == 0) ? 'A' : 'D'; // Randomly choose between 'A' (addition) or 'D' (deletion)

        batch.append(op).append(' ').append(v1).append(' ').append(v2);
        batchesList.add(batch.toString());
    }

    private static void generateQueryOperation( int graphSize, ArrayList<String> batchesList) {
        int v1 = rand.nextInt(1, graphSize);
        int v2 = rand.nextInt(1, graphSize);

        batchesList.add("Q" + ' ' + v1 + ' ' + v2);
    }
}
