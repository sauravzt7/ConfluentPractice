package tailCommand;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TailCommand {

    /**
     * Approach 1: Sliding Window using ArrayDeque. Streaming Ring Buffer
     * Best for InputStreams or small/medium files.
     */
    public static List<String> tailFromStream(String path, int n) throws IOException {
        if (n <= 0) return new ArrayList<>();

        // ArrayDeque is faster than LinkedList for sliding windows
        Deque<String> q = new ArrayDeque<>(n);
        String line;

        try(BufferedReader r = new BufferedReader(new FileReader(path))){
            while((line = r.readLine()) != null){
                q.addLast(line);
                if(q.size() > n) q.pollFirst();
            }
        }

        return new ArrayList<>(q);
    }
}