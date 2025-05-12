public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Invalid arguments.");
            return;
        }

        String command = args[0];
        String fileName = args[1];

            switch (command) {
                case "create":
                    IndexFileManager.create(fileName);
                    break;
                case "insert":
                    long key = Long.parseLong(args[2]);
                    long value = Long.parseLong(args[3]);
                    IndexFileManager.insert(fileName, key, value);
                    break;
                case "search":
                    key = Long.parseLong(args[2]);
                    IndexFileManager.search(fileName, key);
                    break;
                case "load":
                    IndexFileManager.load(fileName, args[2]);
                    break;
                case "print":
                    IndexFileManager.print(fileName);
                    break;
                case "extract":
                    IndexFileManager.extract(fileName, args[2]);
                    break;
                default:
                    System.err.println("Unknown command.");
            }
    }
}
