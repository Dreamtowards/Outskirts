package ext.srt;

//3
public class InsertionSort extends Sort {

    @Override
    public void sort(int[] array) {
        for (int i = 0;i < array.length;i++) {
            for (int j = i;j >= 1 && array[j-1] > array[j];j--) {
                swap(array, j-1, j);
            }
        }
    }

}
