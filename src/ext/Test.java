package ext;

import ext.srt.QuickSort;
import ext.srt.Sort;
import outskirts.event.EventHandler;
import outskirts.event.gui.GuiEvent;
import outskirts.storage.dst.DST;
import outskirts.storage.dst.DSTUtils;
import outskirts.storage.tools.DstJsonConvert;
import outskirts.util.CollectionUtils;
import outskirts.util.HttpUtils;
import outskirts.util.ReflectionUtils;
import outskirts.util.StringUtils;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.LongConsumer;

import static java.lang.Float.NaN;
import static java.lang.Math.random;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec4;
import static outskirts.util.Maths.INFINITY;
import static outskirts.util.logging.Log.LOGGER;

public class Test {

    static List<Vector3f> ls = new LinkedList<>();
    static Object[] stObj = new Object[10];

    static float f = 1;

    public static void main(String[] args) throws Exception {

        // curl 'http://www.jxzyz.cn/user/zan.php' \
        //  -H 'Connection: keep-alive' \
        //  -H 'Accept: application/json, text/javascript, */*; q=0.01' \
        //  -H 'X-Requested-With: XMLHttpRequest' \
        //  -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.192 Safari/537.36' \
        //  -H 'Content-Type: application/x-www-form-urlencoded' \
        //  -H 'Origin: http://www.jxzyz.cn' \
        //  -H 'Referer: http://www.jxzyz.cn/user/ren_xx.php?id=6000399' \
        //  -H 'Accept-Language: zh-CN,zh;q=0.9,en;q=0.8' \
        //  --data-raw 'id=6000399' \
        //  --compressed \
        //  --insecure
        for (int ti = 0;ti < 5;ti++) {
            new Thread(() -> {
                for (int i = 0;i < 2000;i++) {
                    try {
                        String s = HttpUtils.httpPost("http://www.jxzyz.cn/user/zan.php", "id=6000399".getBytes(), CollectionUtils.asMap(

                        ));

                        LOGGER.info(i+": "+s);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }

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

//        for (int i = 0;i < 10;i++) {
//            long st = System.nanoTime();
//            ModelData mdat = OBJLoader.loadOBJ(new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/assets/outskirts/materials/ruum.obj"));
//            LOGGER.info("New: plen={}, phash: {}, t:{}", mdat.positions.length, Arrays.hashCode(mdat.positions), (System.nanoTime() - st) / 1_000_000f);
//
//            st = System.nanoTime();
//            ModelData oldmdat = OldOBJLoader.loadOBJ(new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/assets/outskirts/materials/ruum.obj"));
//            LOGGER.info("Old: len={}, t:{}", oldmdat.positions.length, (System.nanoTime() - st) / 1_000_000f);
//        }
//        LOGGER.info("================\n\n\n");
//
//        long st = System.nanoTime();
//        ModelData mdat = OBJLoader.loadOBJ(new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/assets/outskirts/materials/ruum.obj"));
//        LOGGER.info("New: plen={}, phash: {}, t:{}", mdat.positions.length, Arrays.hashCode(mdat.positions), (System.nanoTime()-st)/1_000_000f);
//
//        st=System.nanoTime();
//        ModelData oldmdat = OldOBJLoader.loadOBJ(new FileInputStream("/Users/dreamtowards/Projects/Outskirts/src/assets/outskirts/materials/ruum.obj"));
//        LOGGER.info("Old: len={}, t:{}", oldmdat.positions.length, (System.nanoTime()-st)/1_000_000f);


//        OBJLoader.saveOBJ(mdat.indices, mdat.positions, mdat.textureCoords, mdat.normals);
//
//        long st = System.nanoTime();
//        String s = OBJLoader.saveOBJ(mdat.indices, mdat.positions, mdat.textureCoords, mdat.normals);
//        System.out.println((System.nanoTime()-st)/1_000_000f);
//
//        IOUtils.write(new ByteArrayInputStream(s.getBytes()), new FileOutputStream("tmpout.obj"));


//        BufferedImage bi = new BufferedImage(1080, 1080, BufferedImage.TYPE_INT_ARGB);
//        NoisePerlin noise = new NoisePerlin();
//
//        for (int x = 0;x < bi.getWidth();x++) {
//            for (int y = 0;y < bi.getHeight();y++) {
//                Vector4f pixcolor = new Vector4f();
//
//                float rawf = noise.noise(x/50f, y/50f);
//                float f = Math.abs(rawf);
//                pixcolor.set(f, f, f, 1f);
//
//                bi.setRGB(x, y, Colors.toARGB(pixcolor));
//            }
//        }
//
//        IOUtils.write(new ByteArrayInputStream(Loader.savePNG(bi)), new FileOutputStream("nis.png"));


//        LOGGER.info(MarchingCubes.tbEdge.length);


//        for (int[] tri : MarchingCubes.tbTri) {
//            System.out.print("{");
//            for (int i = 0;i < tri.length;i+=3) {
//                System.out.print(tri[i]+", "+tri[i+2]+", "+tri[i+1]+", ");
//            }
//            System.out.print("},\n");
//        }

//        LOGGER.info(Maths.mod(1, 1.0f));


//        LOGGER.info(
//                DualContouring.fnorm(DualContouring.F_SPHERE, new Vector3f(1, 0, 0), 0.01f, new Vector3f())
//        );

//        for (int i=0; i<8; i++) {
//            LOGGER.info(new Vector3f(Math.signum(i & 4), Math.signum(i & 2), Math.signum(i & 1)));
//        }
//
//        LOGGER.info((0));
//        byte b = -1;
//        LOGGER.info(b);
//        LOGGER.info((byte)~b);
//        System.exit(1);
//        LOGGER.info(Vector3f.angle(new Vector3f(0.9951376f, -0.09597331f, 0.02214509f),
//                new Vector3f(0.9951376f, -0.09597331f, 0.02214509f)));


//        for (float y=3; y>=0; y--) {
//            out.print("y"+y+" |  ");
//            for (float x=0; x<6; x++) {
//                float v = DualContouringUniformGridDensitySmpl.F_CUBE.sample(x, y, 0);
//
//                out.printf("%4s  ", v);
//            }
//            out.print("\n");
//        }


//        Octree.Leaf lf = new Octree.Leaf(vec3(1,1,1), 10);
//        LOGGER.info("INIT: "+lf.collapsed());
//        for (int i = 0;i < 8;i++) {
//            lf.sign(i, true);
//            LOGGER.info("i "+i+" "+lf.collapsed());
//        }

//        Vector2f v2 = new Vector2f();
//        AABB aabb1 = new AABB(0,0,0,1,1,1);
//        LOGGER.info(Maths.intersectRayAabb(vec3(1,0,0), vec3(0,1,0), aabb1, v2));

//        float INF = Float.POSITIVE_INFINITY;
//        LOGGER.info(AABB.intersects(aabb1, new AABB(0,-INF,0,0,INF,0), 0.001f));
//        System.out.println(" "+Float.POSITIVE_INFINITY*-1);
//        System.out.println(" "+Float.POSITIVE_INFINITY*0);
//        System.exit(0);
//        NoiseGeneratorPerlin nois = new NoiseGeneratorPerlin();
//        TrifFunc D_FUNC = (x,y,z)-> nois.noise(x/8f,y/8f,z/8f);//-DistFunctions.boundingbox(vec3(x,y,z), vec3(3f,2f,4f), .3f);

//        tod0: NORM GEN DBG.
//        LOGGER.info("STRT");

//        Octree node = DCOctreeGen.fromSDF(vec3(-5), 10, D_FUNC, 5);
//        LOGGER.info("Read OBJ.");
//        VertexBuffer inVbuf = OBJLoader.loadOBJ(new FileInputStream("stool.obj"));
//        AABB bd = AABB.bounding(inVbuf.posarr(), null);bd.grow(0.00001f);
//        LOGGER.info("model aabb: "+bd); // AABB[[5.306239, 5.3062587, 0.7999878], [10.693726, 10.693727, 15.199997]]
//
//        LOGGER.info("Sampling mesh");
//        Raycastable mesh = new BvhTriangleMeshShape(CollectionUtils.range(inVbuf.positions.size()/3), inVbuf.posarr());

//        BvhTriangleMeshShape.vb=true;
//        Val t = Val.zero(); Vector3f n = new Vector3f();
//        boolean itst = mesh.raycast(vec3(2,0,0), vec3(-1,0,0), t, n);  // 1, 0,1,0
//        boolean itst = mesh.raycast(vec3(0), Vector3f.fromString("[-0.6666667, -0.33333334, -0.6666667]"), t, n);  // 1, 0,1,0
//        LOGGER.info(" cast result: "+itst+" t="+t.val+", n:"+n); int i = 0;
//        for (float x = -4;x <= 4; x++) {
//            for (float y = -4;y <= 4;y++) {
//                for (float z = -4;z <= 4;z++) {
//                    if (x==0&&y==0&&z==0) continue;
//                    Vector3f dir = vec3(x,y,z).normalize();
//                    assert mesh.raycast(vec3(0), dir, t, n) : "DIR: "+dir+" t:"+t.val;
//                    LOGGER.info(t.val+"  "+dir+"    n: "+n);
////                    LOGGER.info(i++ +"/"+(9*9*9) + " x"+x+" y" + y + " z " + z);
//                }
//            }
//        }
//                System.exit(0);
//        Vector3f min = vec3(-10f); min.set(bd.min);
//        float size = 20; size = Math.max(Math.max(bd.max.x-bd.min.x, bd.max.y-bd.min.y), bd.max.z-bd.min.z);
//        Octree node = DCOctreeSampler.fromMESH(min, size, mesh, 5);
////        node = DCOctreeGen.fromSDF(min, size, D_FUNC, 5);
//        LOGGER.info("Collapse octree.");
//        node = Octree.collapse(node);
//        Octree.dbgprint(node, 0, "");

//        LOGGER.info("CONTOURING.");
//        VertexBuffer vbuf = DualContouring.contouring(node);
//
//        LOGGER.info("Write aabb");
//        Octree.DBG_AABB_LEAF=true;Octree.DBG_AABB_INTERN=false;Octree.DBG_AABB_HERMIT=false;
//        Octree.dbgaabbobj(node, "aabb_l.obj", min, size);
//        Octree.DBG_AABB_LEAF=false;Octree.DBG_AABB_INTERN=true;Octree.DBG_AABB_HERMIT=false;
//        Octree.dbgaabbobj(node, "aabb_i.obj", min, size);
//        Octree.DBG_AABB_LEAF=false;Octree.DBG_AABB_INTERN=false;Octree.DBG_AABB_HERMIT=true;
//        Octree.dbgaabbobj(node, "aabb_h.obj", min, size);
//
//        LOGGER.info("Write Output.");
//        vbuf.inituvnorm();
//        vbuf.tmpsaveobjfile("ms.obj");

//        LOGGER.info(Float.POSITIVE_INFINITY == -Float.NEGATIVE_INFINITY);

//        LOGGER.info(vec4(10, 20 , NaN ,Float.POSITIVE_INFINITY).hashCode());
//        LOGGER.info(vec4(10, 20 , NaN ,Float.POSITIVE_INFINITY).hashCode());

        ArrayList i = ReflectionUtils.newInstance(ArrayList.class);

        LOGGER.info(i);

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
