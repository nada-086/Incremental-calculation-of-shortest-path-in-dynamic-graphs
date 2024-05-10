package clientRMI;

import java.util.Random;

public class BatchRequestGenerator {

    private static final Random rand = new Random();

    public static String getBatch(int writePercentage, int batchSize, int graphSize) {
        StringBuilder batch = new StringBuilder();

        for (int i = 0; i < batchSize; ++i) {
            int operation = rand.nextInt(100);

            if (operation < writePercentage) {
                generateUpdateOperation(batch, graphSize);
            } else {
                generateQueryOperation(batch, graphSize);
            }
        }
        batch.append('F');
        return batch.toString();
    }

    private static void generateUpdateOperation(StringBuilder batch, int graphSize) {
        int v1 = rand.nextInt(graphSize);
        int v2 = rand.nextInt(graphSize);

        char op = (rand.nextInt(2) == 0) ? 'A' : 'D'; // Randomly choose between 'A' (addition) or 'D' (deletion)
        batch.append(op).append(' ').append(v1).append(' ').append(v2).append('\n');
    }

    private static void generateQueryOperation(StringBuilder batch, int graphSize) {
        int v1 = rand.nextInt(graphSize);
        int v2 = rand.nextInt(graphSize);

        batch.append('Q').append(' ').append(v1).append(' ').append(v2).append('\n');
    }
}
