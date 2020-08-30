package ext;

import ext.srt.QuickSort;
import ext.srt.Sort;
import outskirts.event.Event;
import outskirts.event.EventHandler;
import outskirts.event.Events;
import outskirts.event._asminvok.ASMInvoker;
import outskirts.event.gui.GuiEvent;
import outskirts.init.Init;
import outskirts.util.IOUtils;
import outskirts.util.ReflectionUtils;
import outskirts.util.SystemUtils;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

//        InputStream is = new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/assets/outskirts/materials/transres/model.dae");
//
//        LOGGER.info(DaeLoader.loadDAE(is));
//        Matrix4f transmat = Maths.createModelMatrix(new Vector3f(0, 0, 10), new Vector3f(1,1,1), Matrix3f.rotate(Maths.PI/2, Vector3f.UNIT_Y, null), null);

//        LOGGER.info(Matrix4f.transform(transmat, new Vector4f(0,0,1, 1)));

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

//        float f2 = Maths.sqrt(100f);
//
//        f=0;
//        speed("sqrt(i)", 10_000_000, l -> {
//            f += Maths.sqrt(l);
//        });
//        f=0;
//        speed("1/i", 10_000_000, l -> {
//            f += 1f/l;
//        });

//        Integer[] i1 = {1,2,3,4,5,6,7,8};
//        Integer[] i2 = {7,8,9};
//        LOGGER.info(CollectionUtils.insertionSort(i1, i2, 0));



//        InputStream is = new FileInputStream("/Users/dreamtowards/Projects/Outskirts/.idea/out/production/Outskirts/outskirts/event/asminvoke/inst/IvkInstan.class");
//        byte[] bcode = IOUtils.toByteArray(is);



//        while ((bcode=ASMInvoker.replaceBytesOnce(new byte[]{}, new byte[]{}, bcode)) != null);

//        Object obj = ASMInvoker.CLSLOADER.define("outskirts.event.asminvoke.inst.IvkInstan", bcode).newInstance();
//
//        ((ASMInvoker)obj)
//                .invoke(new AnExampEHandlerClass(), new GuiEvent());
//
//
//        LOGGER.info(
//                new JSONObject(
//                        "{akey: 12.3i}"
//                )
//        );


//        RigidBody rb = new RigidBody();
//        rb.setCollisionShape(new BoxShape(1,2,3));
//        rb.transform().origin.y = 100;
//
//        Map mp = SAVERS.RIGIDBODY.write(rb, new DATObject());
//
//        System.out.println(new JSONObject(mp).toString(4));


//        Test inst = new Test();
//        GuiEvent event = new GuiEvent();
//
//        Events.EVENT_BUS.register(inst);
//
//        speed("bus", 1_000_000, i -> {
//
//            Events.EVENT_BUS.post(event);
//        });
//        speed("java", 1_000_000, i -> {
//
//            inst.handler(event);
//        });





    }


    @EventHandler
    private void handler(GuiEvent event) {

        Math.cos(100);
        Math.cos(100);
        Math.cos(100);
        Math.cos(100);
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
        for (int i = 0;i < Math.max(1, (int)(N/10));i++) {
            func.accept(1); //warm up
        }
        long s = System.nanoTime();
        for (long i = 0;i < N;i++) {
            func.accept(i);
        }
        long el = System.nanoTime() - s;
        LOGGER.info("'{}' used {}ms in {} calls", name, el/1_000_000f, N);
    }

}
