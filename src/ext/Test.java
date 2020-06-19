package ext;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ext.srt.QuickSort;
import ext.srt.Sort;
import org.json.JSONObject;
import outskirts.client.Outskirts;
import outskirts.client.gui.screen.tools.GuiScreen3DVertices;
import outskirts.event.EventHandler;
import outskirts.event.client.WindowResizedEvent;
import outskirts.util.*;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Vector3f;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import static java.lang.Math.floor;
import static java.lang.Math.random;
import static outskirts.util.logging.Log.LOGGER;

public class Test {

    static List<Vector3f> ls = new LinkedList<>();
    static Object[] stObj = new Object[10];

    static float f = 1;

    public static void main(String[] args) throws Exception {

//        LOGGER.info(Float.MIN_VALUE);
//        LOGGER.info(Vector3f.dot(new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1.0f, -0.06548178f, 49.0f)) > Float.MIN_VALUE);

//        Vector3f A = new Vector3f(1402.0005f, -0.03347206f, -1401.9995f); // C-A-D
//        Vector3f B = new Vector3f(-1402.0007f, -39.94113f, -1402.0005f);
//        Vector3f C = new Vector3f(-1402.0006f, 0.058893204f, 1401.9995f);
//
//        Vector3f AB = new Vector3f(B).sub(A);
//        Vector3f AC = new Vector3f(C).sub(A);
//
//        Vector3f ABCNorm = Vector3f.cross(AB, AC, null).normalize();
//
//        Vector3f AO = new Vector3f().sub(A);
//
//        LOGGER.info(ABCNorm);
//        LOGGER.info(Vector3f.dot(ABCNorm, AO)); // when < 0, "beyond" the Origin.
//        LOGGER.info(Vector3f.dot(ABCNorm, A));
//        LOGGER.info(Vector3f.dot(ABCNorm, B));
//        LOGGER.info(Vector3f.dot(ABCNorm, C));

        FileUtils.walk(new File("."), f -> {
            if (f.getName().endsWith(".DS_Store")) {
                LOGGER.info(f);
                try {
                    FileUtils.delete(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private static void test_alloc_stack() {

        long sum = 0;

        long s = System.currentTimeMillis();

        for(int i=0;i<1_000_000_00;i++){
            Matrix3f m1 = new Matrix3f();
            m1.m00 = 2;
            Matrix3f m2 = new Matrix3f();
            m2.m00 = 1;
            Matrix3f mr = Matrix3f.sub(m1, m2, null);

            sum += (long)mr.m00;
//            stObj[0] = m1;
        }

        System.out.println(System.currentTimeMillis() - s);
        System.out.println(sum);
    }
    static class vec3 {
        float x, y, z;
        vec3(float x, float y, float z) {this.x = x; this.y = y; this.z = z;}
    }

    /**
     * r = d+ -(d•n)*2*n
     */
    public static vec3 reflect(vec3 norm, vec3 vec) {
        float t = -(vec.x*norm.x + vec.y*norm.y + vec.z*norm.z); // -(d•n)
        return new vec3(
                vec.x + t*2*norm.x,
                vec.y + t*2*norm.y,
                vec.z + t*2*norm.z
        );
    }

















    private static void writeVerts(Iterable<GuiScreen3DVertices.Vert> verts) throws IOException {
        IOUtils.write(
                new ByteArrayInputStream(GuiScreen3DVertices.Vert.writeToJSON(verts).toString(4).getBytes()),
                new FileOutputStream("vert.json"));
    }



    private static float rand() {
        return (float)random();
    }


    @EventHandler(scheduler = Outskirts.class)
    public void abc(WindowResizedEvent event) {

        Log.info("Resized. " + Thread.currentThread());
    }


    private static void sorttest() {

        int[] arr = genarr(20);//{9, 8, 7, 6, 5, 0, 3, 2, 1, 4};
        Log.info(Arrays.toString(arr));

        Sort sort = new QuickSort();

        sort.sort(arr);

        Log.info(Arrays.toString(arr));
    }

    private static int[] genarr(int len) {
        int[] arr = new int[len];
        for (int i = 0;i < len;i++)
            arr[i] = i;
        for (int i = 0;i < len;i++)
            Sort.swap(arr, i, (int)(random() * len));
        return arr;
    }

    private static void speed(String name, long N, LongConsumer func) {
        func.accept(1); //warm up
        long s = System.nanoTime();
        for (long i = 0;i < N;i++) {
            func.accept(i);
        }
        long el = System.nanoTime() - s;
        LOGGER.info("'{}' used {}ms in {} calls", name, el/1_000_000f, N);
    }

}
