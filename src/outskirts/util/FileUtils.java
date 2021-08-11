package outskirts.util;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class FileUtils {

    public static final int KB = 1024;
    public static final int MB = 1024 * KB;  // 1048576L
    public static final long GB = 1024L * MB;  // 1073741824L
    public static final long TB = 1024 * GB;  // 1099511627776L
    public static final long PB = 1024 * TB;  // 1125899906842624L
    public static final long EB = 1024 * PB;  // 1152921504606846976L

    private static final long FILE_COPY_BUFFER_SIZE = 30 * MB;

    public static File getTempDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    public static File getUserDirectory() {
        return new File(System.getProperty("user.home"));
    }

    private static final DecimalFormat DATSIZE_FORMAT = new DecimalFormat("#.##");
    public static String toDisplaySize(long byteCount) {
        float UP_THRESHOLD = 1.25f;
        if (byteCount < KB *UP_THRESHOLD) {
            return byteCount + " bytes";
        } else if (byteCount < MB *UP_THRESHOLD) {
            return DATSIZE_FORMAT.format((float)byteCount / KB) + " KB";
        } else if (byteCount < GB *UP_THRESHOLD) {
            return DATSIZE_FORMAT.format((float)byteCount / MB) + " MB";
        } else if (byteCount < TB *UP_THRESHOLD) {
            return DATSIZE_FORMAT.format((float)byteCount / GB) + " GB";
        } else if (byteCount < PB *UP_THRESHOLD) {
            return DATSIZE_FORMAT.format((float)byteCount / TB) + " TB";
        } else if (byteCount < EB *UP_THRESHOLD) {
            return DATSIZE_FORMAT.format((float)byteCount / PB) + " PB";
        } else {
            return DATSIZE_FORMAT.format((float)byteCount / EB) + " EB";
        }
    }

    public static void walk(File directory, Consumer<File> visitor, boolean child) {
        Validate.isTrue(directory.isDirectory(), "Not is a directory.");

        for (File file : Objects.requireNonNull(directory.listFiles()))
        {
            visitor.accept(file);

            if (child && file.isDirectory()) {
                walk(file, visitor, true);
            }
        }
    }

    public static void walk(File directory, Consumer<File> visitor) {
        walk(directory, visitor, true);
    }

    public static List<File> listFiles(File directory, Predicate<File> filter, boolean child) {
        List<File> result = new LinkedList<>();
        walk(directory, file -> {
            if (filter.test(file)) {
                result.add(file);
            }
        }, child);
        return result;
    }

    public static File[] listFiles(File directory) {
        return Objects.requireNonNull(directory.listFiles(), "Failed to list directory.");
    }

    public static void mkdirs(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new IOException("File " + directory + " exists but not is a directory. Unable to create directory.");
            }
        } else if (!directory.mkdirs() && !directory.isDirectory()) {
            throw new IOException("Unable to create directory " + directory);
        }
    }

    private static void copyFile(File src, File dest) throws IOException {
        Validate.isTrue(src.isFile() && (!dest.exists() || dest.isFile()), "when copyFile(), src and dest must is file");

        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(dest);
        FileChannel input = fis.getChannel();
        FileChannel output = fos.getChannel();

        long size = input.size();
        long position = 0;

        while (position < size) {
            long count = Math.min(size - position, FILE_COPY_BUFFER_SIZE);
            position += output.transferFrom(input, position, count);
        }

        IOUtils.closeQuietly(output, input, fos, fis);

        if (src.length() != dest.length()) {
            throw new IOException("Failed to copy full contents from '" + src + "' to '" + dest + "' Expected length: " + src.length() + " Actual: " + dest.length());
        }
    }

    /**
     * only-support:
     *  src-file -> dest-file
     *  src-dir  -> dest-dir   copy src-dir inner files/dirs to dest-dir inner
     */
    public static void copy(File src, File dest, boolean preserveFileDate) throws IOException {
        Validate.isTrue(!src.getCanonicalPath().equals(dest.getCanonicalPath()), "Source '" + src + "' and destination '" + dest + "' are the same");

        if (src.isFile())
        {
            copyFile(src, dest);
        }
        else if (src.isDirectory())
        {
            mkdirs(dest);

            for (File child : Objects.requireNonNull(src.listFiles())) {
                copy(child, new File(dest, child.getName()), preserveFileDate);
            }
        }
        else
        {
            throw new UnsupportedOperationException("src must be a File or Directory.");
        }

        if (preserveFileDate) {
            dest.setLastModified(src.lastModified());
        }
    }

    public static void copy(File src, File dest) throws IOException {
        copy(src, dest, true);
    }

    public static void move(File src, File dest, boolean preserveFileDate) throws IOException {
        copy(src, dest, preserveFileDate);
        delete(src);
    }

    public static void move(File src, File dest) throws IOException {
        move(src, dest, true);
    }

    public static void delete(File dest) throws IOException {
        if (!dest.exists())
            throw new FileNotFoundException("File " + dest + " does not exist");

        if (dest.isDirectory()) {
            for (File child : Objects.requireNonNull(dest.listFiles())) {
                delete(child);
            }
        }
        if (!dest.delete()) {
            throw new IOException("Unable to delete: " + dest);
        }
    }

    public static long sizeOf(File dest) {
        Validate.isTrue(dest.exists(), "File " + dest + " does not exist");

        if (dest.isDirectory()) {
            long total = 0;
            for (File child : Objects.requireNonNull(dest.listFiles())) {
                total += sizeOf(child);
            }
            return total;
        } else {
            return dest.length();
        }
    }

    public static FileInputStream openFileStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("File not found.", ex);
        }
    }
}
