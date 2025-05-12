import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class IndexFileManager {
    private static final int BLOCK_SIZE = 512;
    private static final String MAGIC = "4348PRJ3";

    public static void create(String fileName) throws IOException {
        File file = new File(fileName);
        if (file.exists()) throw new IOException("Index file already exists.");

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(MAGIC.getBytes(StandardCharsets.US_ASCII), 0, block, 0, 8);
            ByteBuffer buffer = ByteBuffer.allocate(16);
            buffer.putLong(0); // Root ID = 0
            buffer.putLong(1); // Next Block ID = 1
            System.arraycopy(buffer.array(), 0, block, 8, 16);
            raf.write(block);
        }
    }
}
