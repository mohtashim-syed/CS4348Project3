# devlog: B-Tree Index File Manager

## Initial Planning & Setup (May 6, 2025 : 16:35)
- Reviewed the project specifications, and B-Tree requirements

## Initial Planning (May 7, 2025 : 11:00)
- Reviewed the project specifications, and B-Tree requirements

## Planning & Setup (May 8, 2025 : 09:00)
- Reviewed the project specifications, and B-Tree requirements
- Planned class structure: `Main.java`, `IndexFileManager.java`, `BTree.java`, `BTreeNode.java`
- Implemented `Main.java` for commands: `create`, `insert`, `search`, `load`, `print`, `extract`
- Wrote initial version of `IndexFileManager.java` with full support for the `create` command using RandomAccessFile and 512-byte header block

---

## Node Serialization & Deserialization (May 9, 2025 : 23:05)
- Implemented `BTreeNode.java`:
  - Node fields: keys, values, children, blockId, parentId, numKeys
  - Serialization: `toBytes()` and `fromBytes()`
- fixed node size of 512 bytes 
- init read/write node logic into `IndexFileManager` for upcoming use

---

## B-Tree Insertione (May 10, 2025 : 19:32)
- Implemented `BTree.java` to manage insertions, handling:
  - Tree creation when root does not exist
  - Root splits on full insertions
  - Recursive insertion into non-full nodes
- `splitChild()` logic to maintain B-Tree properties
- Updated file header to track root and next available blockId after each insertion

---

## Search, Bulk Load, Printing, and Extraction (May 11, 2025 : 7:09)
- Added support for:
  - `search`: recursive traversal using block reads
  - `load`: bulk insert via CSV parsing
  - `print`: in-order traversal of tree using DFS
  - `extract`: dump key-value pairs to a new CSV

---

## Features Summary & Notes:
- B-Tree with 512-byte blocks
- B-Tree with dynamic node splitting
- 1–3 nodes loaded in memory at a time
- CLI: `create`, `insert`, `search`, `load`, `print`, `extract`
- Header block tracks root & next block
- binary storage
