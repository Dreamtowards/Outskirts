package ext.srt;

public class MergeSort extends Sort {

    @Override
    public void sort(int[] array) {
        int[] tmp = new int[array.length];

        sort(array, 0, array.length-1, tmp, 0);
    }

    private void sort(int[] array, int left, int right, int[] tmp, int dep) {
        if (left < right) {
            int mid = (left + right) / 2;
            sort(array, left, mid, tmp, dep+1);
            sort(array, mid+1, right, tmp, dep+1);
            merge(array, left, mid, right, tmp);
        }
    }

    private void merge(int[] array, int left, int mid, int right, int[] tmp) {
        int i = left;
        int j = mid + 1;
        int t = 0;
        while (i <= mid && j <= right) {
            if (array[i] < array[j]) {
                tmp[t++] = array[i++];
            } else {
                tmp[t++] = array[j++];
            }
        }

        while (i <= mid)
            tmp[t++] = array[i++];

        while (j <= right)
            tmp[t++] = array[j++];

        t = 0;
        while (left <= right)
            array[left++] = tmp[t++];
    }

}
