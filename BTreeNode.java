import java.io.*;
import java.nio.ByteBuffer;

public class BTreeNode {
    public static final int DEGREE = 10;
    public static final int MAX_KEYS = 2 * DEGREE - 1;
    public static final int MAX_CHILDREN = 2 * DEGREE;
    public static final int NODE_SIZE = 512;

    public long blockId;
    public long parentId;
    public int numKeys;
    public long[] keys = new long[MAX_KEYS];
    public long[] values = new long[MAX_KEYS];
    public long[] children = new long[MAX_CHILDREN];
    public boolean isLeaf = true;

    public BTreeNode(long blockId) {
        this.blockId = blockId;
        this.parentId = 0;
        this.numKeys = 0;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(NODE_SIZE);
        buffer.putLong(blockId);
        buffer.putLong(parentId);
        buffer.putLong(numKeys);

        for (int i = 0; i < MAX_KEYS; i++) buffer.putLong(keys[i]);
        for (int i = 0; i < MAX_KEYS; i++) buffer.putLong(values[i]);
        for (int i = 0; i < MAX_CHILDREN; i++) buffer.putLong(children[i]);

        return buffer.array();
    }

    public static BTreeNode fromBytes(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        BTreeNode node = new BTreeNode(buffer.getLong());
        node.parentId = buffer.getLong();
        node.numKeys = (int) buffer.getLong();
        for (int i = 0; i < MAX_KEYS; i++) node.keys[i] = buffer.getLong();
        for (int i = 0; i < MAX_KEYS; i++) node.values[i] = buffer.getLong();
        for (int i = 0; i < MAX_CHILDREN; i++) node.children[i] = buffer.getLong();
        node.isLeaf = node.children[0] == 0;
        return node;
    }
}
