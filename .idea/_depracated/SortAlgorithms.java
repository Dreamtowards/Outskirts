package testing;

import outskirts.util.logging.Log;

import java.util.Arrays;

public class SortAlgorithms {

    private static int[] ARRAY = new int[] {3, 2, 4, 6, 1, 9, 5, 7, 8, 0};

    private static void swap(int[] array, int index1, int index2) {
        int element1 = array[index1];
        array[index1] = array[index2];
        array[index2] = element1;
    }

    private static void shellSort(int[] array) {
        for (int gap = array.length;gap > 0;gap /= 3) {
            for (int i = gap;i < array.length;i++) {
                for (int j = i;j >= gap && array[j-gap] > array[j];j -= gap) {
                    swap(array, j, j-gap);
                }
            }
        }
    }

    private static void insertionSort(int[] array) {
        for (int i = 0;i < array.length;i++) {
            for (int j = i;j > 0 && array[j-1] > array[j];j--) {
                swap(array, j, j-1);
            }
        }
    }

    private static void selectionSort(int[] array) {
        for (int i = 0;i < array.length;i++) {
            int min = i;
            for (int j = i;j < array.length;j++) {
                if (array[j] < array[min]) {
                    min = j;
                }
            }
            swap(array, i, min);
        }
    }

    private static void bubbleSort(int[] array) {
        for (int i = 0;i < array.length;i++) {
            for (int j = i;j < array.length;j++) {
                if (array[j] < array[i]) {
                    swap(array, i, j);
                }
            }
        }
    }

    public static void main(String[] args) {

        int[] arr = gen_arr(10000, 1000);

//        Log.info("Before: " + Arrays.toString(arr));

        test(() -> {
            bubbleSort(arr);
        });
    }

    private static int[] gen_arr(int len, int max) {
        int[] arr = new int[len];
        for (int i = 0;i < len;i++)
            arr[i] = (int)(Math.random() * max);
        return arr;
    }

    private static void test(Runnable runnable) {
        long s = System.currentTimeMillis();

        runnable.run();

        Log.info("test finish. used time: %s ms", System.currentTimeMillis() - s);
    }
}
