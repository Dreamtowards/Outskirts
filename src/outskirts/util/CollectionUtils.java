package outskirts.util;

import outskirts.util.function.TriConsumer;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class CollectionUtils {

    //populateMap() or asMap() ?
    public static <K, V> Map<K, V> asMap(Map<K, V> dest, Iterable<K> keys, Iterable<V> values) {
        Iterator<V> vIter = values.iterator();

        for (K key : keys) {
            dest.put(key, vIter.next());
        }

        if (vIter.hasNext())
            throw new NoSuchElementException();

        return dest;
    }

    /**
     * @param kvs Keys & Values
     */
    public static <K, V> Map<K, V> asMap(Map<Object, Object> dest, Object... kvs) {
        Validate.isTrue(kvs.length % 2 == 0, "Keys and Values must be paired.");

        for (int i = 0;i < kvs.length;) {
            dest.put(kvs[i++], kvs[i++]);
        }

        return (Map<K, V>)dest;
    }

    public static <K, V> Map<K, V> asMap(Object... kvs) {
        return asMap(new HashMap<>(), kvs);
    }


    public static <E> List<E> asList(List<E> dest, E... elements) {
        dest.addAll(Arrays.asList(elements));
        return dest;
    }

    public static float[] toArrayf(List<Float> list) {
        float[] array = new float[list.size()];
        int i = 0;
        for (Float data : list) {
            array[i++] = data;
        }
        return array;
    }

    public static int[] toArrayi(List<Integer> ls) {
        int[] arr = new int[ls.size()];
        int i = 0;
        for (Integer v : ls)
            arr[i++] = v;
        return arr;
    }

    public static <T> T[] toArray(List<T> ls) {
        return (T[])ls.toArray();
    }

    public static int indexOf(Object[] array, Object find, int fromIndex, int toIndex) {
        if (find == null) { // may should't have null find..? with equals find (equals is high level than ==, ==null)
            for (int i = fromIndex; i < toIndex; i++)
                if (array[i] == null)
                    return i;
        } else {
            for (int i = fromIndex; i < toIndex; i++)
                if (find.equals(array[i]))
                    return i;
        }
        return -1;
    }
    public static int indexOf(Object[] array, Object find, int fromIndex) {
        return indexOf(array, find, fromIndex, array.length);
    }
    public static int indexOf(Object[] array, Object find) {
        return indexOf(array, find, 0, array.length);
    }

    public static int indexOf(Object[] array, Object[] search, int fromIndex) {
        for (int i = fromIndex;i <= array.length-search.length;i++) {
            boolean found = true;
            for (int j = 0;j < search.length;j++) {
                if (!array[i+j].equals(search[j])) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }
    public static int indexOf(byte[] array, byte[] search, int fromIndex) {
        for (int i = fromIndex;i <= array.length-search.length;i++) {
            boolean found = true;
            for (int j = 0;j < search.length;j++) {
                if (array[i+j] != search[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    public static boolean contains(Object[] array, Object find, int fromIndex, int toIndex) {
        return indexOf(array, find, fromIndex, toIndex) != -1;
    }
    public static boolean contains(Object[] array, Object find, int fromIndex) {
        return indexOf(array, find, fromIndex) != -1;
    }
    public static boolean contains(Object[] array, Object find) {
        return indexOf(array, find) != -1;
    }


    public static <T> T get(T[] array, Predicate<T> predicate) {
        for (T e : array) {
            if (predicate.test(e)) {
                return e;
            }
        }
        return null;
    }

    public static <T> T[] fill(T[] array, Supplier<T> supplier) {
        for (int i = 0;i < array.length;i++)
            array[i] = supplier.get();
        return array;
    }

    public static <T> T[] subarray(T[] array, int beginIndex, int endIndex) {
        T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), endIndex - beginIndex);
        System.arraycopy(array, beginIndex, result, 0, result.length);
        return result;
    }
    public static <T> T[] subarray(T[] array, int beginIndex) {
        return subarray(array, beginIndex, array.length);
    }

    //this custom should be in library..? RAND is too high level
    public static <T> T[] shuffle(T[] arr, Random rnd) {
        for (int i = arr.length;i > 1;i--) {
            swap(arr, i-1, rnd.nextInt(i));
        }
        return arr;
    }
    public static int[] shufflei(int[] arr, Random rnd) {
        for (int i = arr.length;i > 1;i--) {
            swapi(arr, i-1, rnd.nextInt(i));
        }
        return arr;
    }

    public static <T> T[] swap(T[] array, int i1, int i2) {
        T obj1 = array[i1];
        array[i1] = array[i2];
        array[i2] = obj1;
        return array;
    }
    public static int[] swapi(int[] array, int i1, int i2) {
        int obj1 = array[i1];
        array[i1] = array[i2];
        array[i2] = obj1;
        return array;
    }

    public static <T> List<T> swap(List<T> list, int i1, int i2) {
        T obj1 = list.get(i1);
        list.set(i1, list.get(i2));
        list.set(i2, obj1);
        return list;
    }

    public static <K, V> String toString(Map<K, V> map, String delimiter, TriConsumer<StringBuilder, K, V> accumulator) {
        StringBuilder sb = new StringBuilder("{");
        int counter = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            accumulator.accept(sb, entry.getKey(), entry.getValue());
            counter++;
            if (counter != map.size()) {
                sb.append(delimiter);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public static <E> String toString(List<E> list, String delimiter) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (E e : list) {
            sb.append(e.toString());
            if (++i != list.size()) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    private static <T> void forEach(T[] array, Consumer<T> c) {
        for (T e : array)
            c.accept(e);
    }

    public static <T> T[] reverse(T[] arr) {
        for (int i = 0;i < arr.length/2;i++) {
            swap(arr, i, arr.length-1-i);
        }
        return arr;
    }

    // actually not good. not common, but useful in Generate FakeIndices(EBO)
    public static int[] range(int len) {
        return range(0, len);
    }
    public static int[] range(int from, int len) {
        int[] arr = new int[len];
        for (int i = 0;i < arr.length;i++)
            arr[i] = from+i;
        return arr;
    }

    /**
     * @param list both "src" AND "dest"
     */
    public static <T> List<T> quickSort(List<T> list, Comparator<T> c) {
        QuickSort.quickSort(list, 0, list.size()-1, c);
        return list;
    }

    /**
     * QuickSort is not stable-sort, but not needs extra array-alloc than Arrays.sort(T[])'s MargeSort
     */
    private static class QuickSort {

        private static <T> void quickSort(List<T> list, int left, int right, Comparator<T> c) {
            int i = left;
            int j = right;
            T pivot = list.get((left + right) / 2);

            // partition
            do {
                while (c.compare(list.get(i), pivot) < 0) i++;
                while (c.compare(pivot, list.get(j)) < 0) j--;

                if (i <= j) {
                    swap(list, i, j);
                    i++;
                    j--;
                }
            } while (i <= j);

            // recursion
            if (left < j) {
                quickSort(list, left, j, c);
            }
            if (i < right) {
                quickSort(list, i, right, c);
            }
        }
    }

    public static <T> List<T> insertionSort(List<T> list, Comparator<T> c) {
        InsertionSort.insertionSort(list, c);
        return list;
    }

    public static <T> T[] insertionSort(T[] arr, Comparator<T> c) {
        InsertionSort.insertionSort(arr, c);
        return arr;
    }

    /**
     * for some inner algorithm/impl. like in sorted-list-adding..
     */
    private static class InsertionSort {

        private static <T> void insertionSort(List<T> list, Comparator<T> c) {
            for (int i = 1;i < list.size();i++) {
                T key = list.get(i);
                T tmp;
                int j = i;
                while (j >= 1 && c.compare(tmp=list.get(j-1), key) > 0) {
                    list.set(j, tmp);
                    j--;
                }
                list.set(j, key);
            }
        }

        private static <T> void insertionSort(T[] arr, Comparator<T> c) {
            for (int i = 1;i < arr.length;i++) {
                T key = arr[i];
                T tmp;
                int j = i;
                while (j >= 1 && c.compare(tmp=arr[j-1], key) > 0) {
                    arr[j] = tmp;
                    j--;
                }
                arr[j] = key;
            }
        }

    }
}
