package tailCommand;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TailCommandOptimized {

    /**
     * Approach 2: Reading backwards using RandomAccessFile.
     * Best for massive files.
     * Reverse Chunk Read (minimal I/O)
     */
    public static List<String> tailFromFile(String path, int n) throws IOException {
        List<String> lines = new ArrayList<>();
        if(n <= 0) return lines;

        try(RandomAccessFile f = new RandomAccessFile(path, "r")){

            long size = f.length();
            final int CHUNK = 4096;
            byte[] buf = new byte[0];

            int newLines = 0;
            long pos = size;

            while(pos > 0 && newLines <= n){
                int step = (int) Math.max(CHUNK, pos); // step required to read the next chunk
                pos -= step;
                f.seek(pos); // seek to the new position
                byte[] chunk = new byte[step]; // buffer to store the current chunk
                f.readFully(chunk);

                byte[] merged = new byte[step + buf.length]; // buffer to store the merged chunk
                System.arraycopy(chunk, 0, merged, 0, step);
                System.arraycopy(buf, 0, merged, step, buf.length);
                buf = merged;
                for(byte ch: chunk) if(ch == '\n') newLines++;
            }

            // getline-style split: a trailing '\n' does not create an extra empty line
            String s = new String(buf, StandardCharsets.UTF_8);

            int start = 0;
            for(int i = 0; i < s.length(); i++){
             if(s.charAt(i) == '\n') lines.add(s.substring(start, i)); start = i + 1;
            }

            if(start < s.length()) lines.add(s.substring(start));
            if(lines.size() > n) lines = new ArrayList<>(lines.subList(lines.size() - n, lines.size()));
        }
        return lines;
    }
}