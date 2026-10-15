package tailCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TailCommand {

    /**
     * Approach 1: Sliding Window using ArrayDeque.
     * Best for InputStreams or small/medium files.
     */
    public static List<String> tailFromStream(BufferedReader reader, int n) throws IOException {
        if (n <= 0) return new ArrayList<>();

        // ArrayDeque is faster than LinkedList for sliding windows
        Queue<String> window = new ArrayDeque<>(n);
        String line;

        while ((line = reader.readLine()) != null) {
            if (window.size() == n) {
                window.poll(); // Remove the oldest line
            }
            window.offer(line); // Add the new line
        }

        return new ArrayList<>(window);
    }
}