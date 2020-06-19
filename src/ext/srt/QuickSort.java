package ext.srt;

public class QuickSort extends Sort {

    @Override
    public void sort(int[] array) {
        quickSort(array, 0, array.length-1);
    }

    private void quickSort(int[] array, int left, int right) {
        if (left < right) {
            int l = partition(array, left, right);
            quickSort(array, left, l-1);
            quickSort(array, l+1, right);
        }
    }

    private int partition(int[] array, int left, int right) {
        int pivot = array[left];
        while (left < right) {
            while (left < right && array[right] > pivot)
                right--;
            array[left] = array[right];

            while (left < right && array[left] <= pivot)
                left++;
            array[right] = array[left];
        }
        array[left] = pivot;
        return left;
    }

}
