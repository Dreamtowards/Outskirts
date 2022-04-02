

namespace std::io;

static class Files {

    static void delete(Path p);
    static void deleteIfExists(Path p);

    static void move(Path src, Path dst);
    static void copy(Path src, Path dst);

    static void createFile(Path p);
    static void createDirectory(Path p);

    static List<Path> list(Path dir);

    static bool isDirectory(Path p);
    static bool isHidden(Path p);

    static bool exists(Path p);
    static int size(Path p);

    static long getLastModifiedTime(Path p);
    static void setLastModifiedTime(Path p, long timestamp);

    // creationTime


    static void write(Path p, List<byte> bytes, bool append);

}
