package ext.srt;

public abstract class Sort {

    public abstract void sort(int[] array);

    public static void swap(int[] array, int i1, int i2) {
        int obj1 = array[i1];
        array[i1] = array[i2];
        array[i2] = obj1;
    }

    public static String repeat(int count, String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < count;i++)
            sb.append(s);
        return sb.toString();
    }
}
