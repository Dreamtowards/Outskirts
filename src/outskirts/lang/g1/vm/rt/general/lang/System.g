package general.lang;


class System {

    public in;
    public out;
    public err;

    public static Console console();


    public static long currentTimeMillis();

    public static long nanoTime();


    public stativ int hashCode(Object o);

    public static void arraycopy(array src, int srcpos, array dest, int destpos, int length);

    public static Map<String, String> getProperties();

    public static Map<String, String> getenv();


    long pointer(any obj) {

    }
    any reference(long pointer) {

    }


    // BEGIN RUNTIME

    public static exit(int status);

    public static void addShutdownHook(Thread hook);

    public static Process exec(String[] cmd, File dir, Map<String, String> envp);


    public static int availableProcessors();

    public static long maxMemory();

    public static long usedMemory();


    public static void gc();

    public static void load(String s);
    public static void loadLibrary(String s);


    // END RUNTIME
}