import java.io.*;
import java.util.*;

public class BTree {
    private final RandomAccessFile raf;
    private long rootBlockId;
    private long nextBlockId;
    private final String fileName;

    public BTree(String fileName, RandomAccessFile raf, long rootBlockId, long nextBlockId) {
        this.fileName = fileName;
        this.raf = raf;
        this.rootBlockId = rootBlockId;
        this.nextBlockId = nextBlockId;
    }

    private BTreeNode readNode(long blockId) throws IOException {
        byte[] data = new byte[BTreeNode.NODE_SIZE];
        raf.seek(blockId * BTreeNode.NODE_SIZE);
        raf.readFully(data);
        return BTreeNode.fromBytes(data);
    }

    private void writeNode(BTreeNode node) throws IOException {
        raf.seek(node.blockId * BTreeNode.NODE_SIZE);
        raf.write(node.toBytes());
    }

    public void insert(long key, long value) throws IOException {
        if (rootBlockId == 0) {
            BTreeNode root = new BTreeNode(nextBlockId++);
            root.keys[0] = key;
            root.values[0] = value;
            root.numKeys = 1;
            rootBlockId = root.blockId;
            writeNode(root);
            updateHeader();
        } else {
            BTreeNode root = readNode(rootBlockId);
            if (root.numKeys == BTreeNode.MAX_KEYS) {
                BTreeNode newRoot = new BTreeNode(nextBlockId++);
                newRoot.isLeaf = false;
                newRoot.children[0] = root.blockId;
                splitChild(newRoot, 0, root);
                insertNonFull(newRoot, key, value);
                rootBlockId = newRoot.blockId;
                writeNode(newRoot);
                updateHeader();
            } else {
                insertNonFull(root, key, value);
                writeNode(root);
            }
        }
    }

    private void insertNonFull(BTreeNode node, long key, long value) throws IOException {
        int i = node.numKeys - 1;
        if (node.isLeaf) {
            while (i >= 0 && key < node.keys[i]) {
                node.keys[i + 1] = node.keys[i];
                node.values[i + 1] = node.values[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.values[i + 1] = value;
            node.numKeys++;
        } else {
            while (i >= 0 && key < node.keys[i]) i--;
            i++;
            BTreeNode child = readNode(node.children[i]);
            if (child.numKeys == BTreeNode.MAX_KEYS) {
                splitChild(node, i, child);
                if (key > node.keys[i]) i++;
                child = readNode(node.children[i]);
            }
            insertNonFull(child, key, value);
            writeNode(child);
        }
    }

    private void splitChild(BTreeNode parent, int index, BTreeNode fullChild) throws IOException {
        BTreeNode newChild = new BTreeNode(nextBlockId++);
        newChild.isLeaf = fullChild.isLeaf;
        newChild.numKeys = BTreeNode.DEGREE - 1;

        for (int j = 0; j < BTreeNode.DEGREE - 1; j++) {
            newChild.keys[j] = fullChild.keys[j + BTreeNode.DEGREE];
            newChild.values[j] = fullChild.values[j + BTreeNode.DEGREE];
        }
        if (!fullChild.isLeaf) {
            for (int j = 0; j < BTreeNode.DEGREE; j++) {
                newChild.children[j] = fullChild.children[j + BTreeNode.DEGREE];
            }
        }

        fullChild.numKeys = BTreeNode.DEGREE - 1;

        for (int j = parent.numKeys; j >= index + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[index + 1] = newChild.blockId;

        for (int j = parent.numKeys - 1; j >= index; j--) {
            parent.keys[j + 1] = parent.keys[j];
            parent.values[j + 1] = parent.values[j];
        }

        parent.keys[index] = fullChild.keys[BTreeNode.DEGREE - 1];
        parent.values[index] = fullChild.values[BTreeNode.DEGREE - 1];
        parent.numKeys++;

        writeNode(fullChild);
        writeNode(newChild);
    }

    private void updateHeader() throws IOException {
        raf.seek(8);
        raf.writeLong(rootBlockId);
        raf.writeLong(nextBlockId);
    }

    public Long search(long key) throws IOException {
        return searchRecursive(readNode(rootBlockId), key);
    }

    private Long searchRecursive(BTreeNode node, long key) throws IOException {
        int i = 0;
        while (i < node.numKeys && key > node.keys[i]) i++;
        if (i < node.numKeys && key == node.keys[i]) return node.values[i];
        if (node.isLeaf) return null;
        return searchRecursive(readNode(node.children[i]), key);
    }

    public void printTree() throws IOException {
        printRecursive(readNode(rootBlockId));
    }

    private void printRecursive(BTreeNode node) throws IOException {
        for (int i = 0; i < node.numKeys; i++) {
            if (!node.isLeaf) printRecursive(readNode(node.children[i]));
            System.out.println(node.keys[i] + ": " + node.values[i]);
        }
        if (!node.isLeaf) printRecursive(readNode(node.children[node.numKeys]));
    }

    public void extractTree(BufferedWriter writer) throws IOException {
        extractRecursive(readNode(rootBlockId), writer);
    }

    private void extractRecursive(BTreeNode node, BufferedWriter writer) throws IOException {
        for (int i = 0; i < node.numKeys; i++) {
            if (!node.isLeaf) extractRecursive(readNode(node.children[i]), writer);
            writer.write(node.keys[i] + "," + node.values[i]);
            writer.newLine();
        }
        if (!node.isLeaf) extractRecursive(readNode(node.children[node.numKeys]), writer);
    }
}
