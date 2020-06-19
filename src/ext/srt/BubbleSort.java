package ext.srt;

//1
public class BubbleSort extends Sort {

    @Override
    public void sort(int[] array) {
        for (int i = 0;i < array.length;i++) {
            for (int j = i + 1;j < array.length;j++) {
                if (array[j] < array[i]) {
                    swap(array, i, j);
                }
            }
        }
    }

}
