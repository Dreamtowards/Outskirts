package ext;

import ext.srt.QuickSort;
import ext.srt.Sort;
import outskirts.client.Outskirts;
import outskirts.client.animation.loader.dae.DaeLoader;
import outskirts.client.gui.debug.GuiVert3D;
import outskirts.client.material.ModelData;
import outskirts.event.EventHandler;
import outskirts.event.client.WindowResizedEvent;
import outskirts.util.*;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.io.*;
import java.util.*;
import java.util.function.LongConsumer;

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


//        Ett e = new Ett("EttTitle123", "SomeAUTHOr", 123, "SomeTime", "ABC123", 0.567f, Arrays.asList("user1", "userABC", "user567"));
//
//        Map mp = new HashMap();
//
//        e.onWrite(mp);
//
//        LOGGER.info(mp);
//
//        SNL.write(new FileOutputStream("ett.snl"), mp);

//        Map mpRead = DST.read(new FileInputStream("ett.snl"));
//        LOGGER.info(mpRead);
//
//        Ett e2 = new Ett();
//        e2.onRead(mpRead);
//
//        LOGGER.info(e2.toString());

//        LOGGER.info();
//        int v = 100;
//
//        SomeClas someClas = new SomeClas();
//        Class<?> cls = SomeClas.class;
//        f=0;
//        speed("accessClassGetter", 100_000_000, i -> {
//            if (v == someClas.metho())
//                f++;
//        });
//
//        f=0;
//        speed("hashCodeAccess", 100_000_000, i -> {
//            if (v == someClas.hashCode())
//                f++;
//        });
//
//        f=0;
//        speed("ClassHashCodeAccess", 100_000_000, i -> {
//            if (v == someClas.getClass().hashCode())
//                f++;
//        });
//
//        f=0;
//        speed("cachedLocalClassHashCodeAccess", 100_000_000, i -> {
//            if (v == cls.hashCode())
//                f++;
//        });
//
//        Class loc = someClas.getClass();
//        f=0;
//        speed("ClassEq", 100_000_000, i -> {
//            if (cls == loc)
//                f++;
//        });


//        for (int i = 2;i < 100;i++) {
//            boolean is = true;
//            for (int ti = 2; ti < i; ti++) {
//                if (Maths.frac((float)i/ti)==0) {
//                    is=false;
//                    break;
//                }
//            }
//            if (is)LOGGER.info(i);
//        }

//        new Thread(() -> {
//            try {
//                int[] da_sum = {0};
//
//                String line;
//                BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
//                while ((line=br.readLine()) != null) {
//                    float f = Float.parseFloat(line);
//                    if (f == 0) break;
//                    da_sum[0]+=f;
//                    LOGGER.info("inputed: {}, sum: {}", f, da_sum[0]);
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            } catch (NumberFormatException ex) {
//                LOGGER.warn("Bad input: require num.");
//            }
//        }).start();


//        Matrix3f R = Matrix3f.rotate(2, new Vector3f(1,1,1).normalize(), null);
//
//        Matrix3f I = Matrix3f.scale(new BoxShape(1,2,3).calculateLocalInertia(10, new Vector3f()), new Matrix3f());
//
//        Matrix3f RT = new Matrix3f(R).transpose();
//
//        LOGGER.info("I: {}", I);
//
//        LOGGER.info("R*I*R^T: {}", Matrix3f.mul(Matrix3f.mul(R, I, null), RT, null));


//        MDLLoader.loadVtx(Loader.class.getResourceAsStream("/outskirts/client/mdl/model.mdlvtx"));
//        long s;
//        ModelData modelData;
//
//        s=System.currentTimeMillis();
//        modelData = OBJFileLoader.loadOBJ(new Identifier("materials/aya091/091_W_Aya_10K.obj").getInputStream());
//        LOGGER.info("AssocFind OptED OBJ Loader: {}ms", System.currentTimeMillis()-s);
//        LOGGER.info("posHash {}, posLen: {}", Arrays.hashCode(modelData.positions), modelData.positions.length);
//        LOGGER.info("texHash {}, texLen: {}", Arrays.hashCode(modelData.textureCoords), modelData.textureCoords.length);
//
//        s=System.currentTimeMillis();
//        modelData = SimpleOBJLoader.loadOBJ(new Identifier("materials/aya091/091_W_Aya_10K.obj").getInputStream());
//        LOGGER.info("Basic OBJ Loader: {}ms", System.currentTimeMillis()-s);
//        LOGGER.info("posHash {}, posLen: {}", Arrays.hashCode(modelData.positions), modelData.positions.length);
//        LOGGER.info("texHash {}, texLen: {}", Arrays.hashCode(modelData.textureCoords), modelData.textureCoords.length);
//
//        MDL.MDLData mdlData = new MDL.MDLData();
//        mdlData.indices = modelData.indices;
//        mdlData.layouts.add(modelData.positions);
//        mdlData.layouts.add(modelData.textureCoords);
//        mdlData.layouts.add(modelData.normals);
//
//        s=System.currentTimeMillis();
//        MDL.saveMDL(mdlData, new FileOutputStream("mdl-fts/aya091_10K.mdl"));
//        LOGGER.info("\n\nBasic MDL Saver: {}ms", System.currentTimeMillis()-s);
//
//        s=System.currentTimeMillis();
//        MDL.MDLData loadedMDLD = MDL.loadMDL(new FileInputStream("mdl-fts/aya091_10K.mdl"));
//        LOGGER.info("Basic MDL Loader: {}ms", System.currentTimeMillis()-s);
//        LOGGER.info("posHash {}", Arrays.hashCode(loadedMDLD.layouts.get(0)));
//        LOGGER.info("texHash {}", Arrays.hashCode(loadedMDLD.layouts.get(1)));

//        XML nd1 = new XML("<ele1 attr1='attrValue1'><a></a><b>Some of Texts</b></ele1>");
//        System.out.println(nd1.toString(0));
//        System.out.println(nd1.toString());  // default indentFactor: 4.
//
//        XML tn2 = new XML(new FileInputStream("testxml.xml"));
//        System.out.println(tn2.toString(2));
//
//        System.out.println("attrs: "+tn2.getChildren().get(2).getAttributes());

        InputStream is = new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/assets/outskirts/materials/transres/model.dae");
//
//        LOGGER.info(DaeLoader.loadDAE(is));
        Matrix4f transmat = Maths.createModelMatrix(new Vector3f(0, 0, 10), new Vector3f(1,1,1), Matrix3f.rotate(Maths.PI/2, Vector3f.UNIT_Y, null), null);

        LOGGER.info(Matrix4f.transform(transmat, new Vector4f(0,0,1, 1)));

//        LOGGER.info(r==null?"null":s.substring(r[0], r[1]));

//        ModelData mdat;
//        long s;
//
//        s=System.currentTimeMillis();
//        mdat = OBJFileLoader.loadOBJ(new Identifier("materials/aya091/091_W_Aya_30K.obj").getInputStream());
//        LOGGER.info("PRE-OBJFileLoader: {}ms. idc: {}", System.currentTimeMillis()-s, mdat.indices.length);
//
//        s=System.currentTimeMillis();
//        mdat = OBJFileLoader.loadOBJ(new Identifier("materials/aya091/091_W_Aya_30K.obj").getInputStream());
//        LOGGER.info("OBJFileLoader: {}ms. idc: {}", System.currentTimeMillis()-s, mdat.indices.length);
//
//        s=System.currentTimeMillis();
//        mdat = OBJLoader.loadOBJ(new Identifier("materials/aya091/091_W_Aya_30K.obj").getInputStream());
//        LOGGER.info("PRE-OBJLoader: {}ms. idc: {}", System.currentTimeMillis()-s, mdat.indices.length);
//
//        s=System.currentTimeMillis();
//        mdat = OBJLoader.loadOBJ(new Identifier("materials/aya091/091_W_Aya_30K.obj").getInputStream());
//        LOGGER.info("OBJLoader: {}ms. idc: {}", System.currentTimeMillis()-s, mdat.indices.length);
    }



    private static class SomeClas {

        public int someInt = 123;

        public int metho() {return someInt;}
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

















    private static void writeVerts(Iterable<GuiVert3D.Vert> verts) throws IOException {
        IOUtils.write(
                new ByteArrayInputStream(GuiVert3D.Vert.writeToJSON(verts).toString(4).getBytes()),
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
