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
    public static void insert(String fileName, long key, long value) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            raf.seek(0);
            byte[] header = new byte[BLOCK_SIZE];
            raf.readFully(header);

            String magic = new String(header, 0, 8, StandardCharsets.US_ASCII);
            if (!magic.equals(MAGIC)) throw new IOException("Invalid index file.");

            ByteBuffer hdr = ByteBuffer.wrap(header);
            hdr.position(8);
            long rootId = hdr.getLong();
            long nextBlockId = hdr.getLong();

            BTree tree = new BTree(fileName, raf, rootId, nextBlockId);
            tree.insert(key, value);
        }
    }

    public static void search(String fileName, long key) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
            raf.seek(0);
            byte[] header = new byte[BLOCK_SIZE];
            raf.readFully(header);

            String magic = new String(header, 0, 8, StandardCharsets.US_ASCII);
            if (!magic.equals(MAGIC)) throw new IOException("Invalid index file.");

            ByteBuffer hdr = ByteBuffer.wrap(header);
            hdr.position(8);
            long rootId = hdr.getLong();
            long nextBlockId = hdr.getLong();

            BTree tree = new BTree(fileName, raf, rootId, nextBlockId);
            Long result = tree.search(key);
            if (result != null) System.out.println(key + ": " + result);
            else System.out.println("Key not found.");
        }
    }

    public static void load(String fileName, String csvFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2) continue;
                insert(fileName, Long.parseLong(parts[0]), Long.parseLong(parts[1]));
            }
        }
    }

    public static void print(String fileName) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
            raf.seek(0);
            byte[] header = new byte[BLOCK_SIZE];
            raf.readFully(header);

            String magic = new String(header, 0, 8, StandardCharsets.US_ASCII);
            if (!magic.equals(MAGIC)) throw new IOException("Invalid index file.");

            ByteBuffer hdr = ByteBuffer.wrap(header);
            hdr.position(8);
            long rootId = hdr.getLong();
            long nextBlockId = hdr.getLong();

            BTree tree = new BTree(fileName, raf, rootId, nextBlockId);
            tree.printTree();
        }
    }

    public static void extract(String fileName, String outFile) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r");
             BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {

            byte[] header = new byte[BLOCK_SIZE];
            raf.readFully(header);
            String magic = new String(header, 0, 8, StandardCharsets.US_ASCII);
            if (!magic.equals(MAGIC)) throw new IOException("Invalid index file.");

            ByteBuffer hdr = ByteBuffer.wrap(header);
            hdr.position(8);
            long rootId = hdr.getLong();
            long nextBlockId = hdr.getLong();

            BTree tree = new BTree(fileName, raf, rootId, nextBlockId);
            tree.extractTree(writer);
        }
    }
}
