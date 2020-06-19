package ext.srt;

//4
public class ShellSort extends Sort {

    @Override
    public void sort(int[] array) {
        for (int gap = array.length;gap > 0;gap /= 2) {
            for (int i = gap;i < array.length;i++) {
                for (int j = i;j >= gap && array[j-gap] > array[j];j-=gap) {
                    swap(array, j-gap, j);
                }
            }
        }
    }

//    Optimized ver
//    @Override
//    public void sort(int[] array) {
//        for (int gap = array.length;gap > 0;gap /= 2) {
//            for (int i = gap;i < array.length;i++) {
//                int tmp = array[i];
//                int j;
//                for (j = i;j >= gap && array[j-gap] > tmp;j-=gap) {
//                    array[j] = array[j-gap];
//                }
//                array[j] = tmp;
//            }
//        }
//    }

}
