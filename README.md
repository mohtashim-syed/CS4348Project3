# B-Tree Index File Manager ReadMe File

## Project Overview
This Java application is a command-line tool for managing B-Tree-based index files using file-backed data structures. Supports persistent B-Tree storage with minimal memory usage.

## File Structure
- `Main.java`: Entry point for CLI commands
- `IndexFileManager.java`: Handles file-level commands and validation
- `BTree.java`: Core B-Tree logic (insert, search, print, extract)
- `BTreeNode.java`: Node structure with 512-byte serialization

## Index File Format
- Fixed block size: **512 bytes**
- Header (Block 0):
  - Magic number `"4348PRJ3"` (8 bytes)
  - Root block ID (8 bytes)
  - Next block ID (8 bytes)
- Each B-Tree node fits in one block:
  - Metadata + 19 keys + 19 values + 20 child pointers

## Supported Commands
```
java Main create <index_file>
java Main insert <index_file> <key> <value>
java Main search <index_file> <key>
java Main load <index_file> <csv_file>
java Main print <index_file>
java Main extract <index_file> <output_csv>
```

## Example
```bash
java Main create test.idx
java Main insert test.idx 10 100
java Main search test.idx 10
java Main load test.idx input.csv
java Main print test.idx
java Main extract test.idx output.csv
```

## Features
- File-persistent B-Tree structure
- Degree-10 B-Tree with node splitting
- Efficient memory usage
- Command-line driven
- CSV-based bulk loading and export
