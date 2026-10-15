package tailCommand;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class TailCommandOptimized {

    /**
     * Approach 2: Reading backwards using RandomAccessFile.
     * Best for massive files.
     */
    public static List<String> tailFromFile(File file, int n) throws IOException {
        List<String> result = new ArrayList<>();
        if (n <= 0 || file.length() == 0) return result;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long pointer = file.length() - 1;
            int newlines = 0;

            // Step 1: Read backwards to find the starting point
            while (pointer >= 0) {
                raf.seek(pointer);
                char c = (char) raf.readByte();

                if (c == '\n') {
                    newlines++;
                    // If we found N newlines, the pointer is exactly at the newline 
                    // character before our target lines. 
                    if (newlines == n + 1) {
                        break;
                    }
                }
                pointer--;
            }

            // Step 2: Set the pointer to the start of our target content
            if (pointer >= 0) {
                // Move one byte forward so we don't read the preceding newline itself
                raf.seek(pointer + 1);
            } else {
                // We reached the beginning of the file (file has fewer than N lines)
                raf.seek(0);
            }

            // Step 3: Read forward normally from the calculated pointer
            String line;
            while ((line = raf.readLine()) != null) {
                // Note: readLine() parses ISO-8859-1. For UTF-8, you would read 
                // raw bytes into a buffer and construct new String(bytes, StandardCharsets.UTF_8).
                result.add(line);
            }
        }

        return result;
    }
}