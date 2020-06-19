package ext.srt;

//2
public class SelectionSort extends Sort {

    @Override
    public void sort(int[] array) {
        for (int i = 0;i < array.length;i++) {
            int min = i;
            for (int j = i + 1;j < array.length;j++) {
                if (array[j] < array[min]) {
                    min = j;
                }
            }
            swap(array, i, min);
        }
    }

}
