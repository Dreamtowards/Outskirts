package outskirts.client.render.chunk;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.Model;
import outskirts.client.render.VertexBuffer;
import outskirts.util.Ref;
import outskirts.util.SystemUtil;

import java.util.function.Supplier;

public class ChunkModelBuildWorker implements Runnable {

    private Supplier<RenderSection> taskpoller;

    public ChunkModelBuildWorker(Supplier<RenderSection> taskpoller) {
        this.taskpoller = taskpoller;
    }

    @Override
    public void run() {
        while (true) {
            RenderSection rs = taskpoller.get();
            if (rs != null) {
                Ref<int[]> vertm = Ref.wrap();
                VertexBuffer vbuf = ChunkMeshGen.buildModel(rs, vertm);
                if (vbuf == null)
                    continue;

                float[] VBRCD = genVertBaryCoord(vbuf.positions.size()/3);
                float[] VMTID = genVertMaterialId(vbuf.positions.size()/3, vertm.value);

                Outskirts.getScheduler().addScheduledTask(() -> {
                    Model model = Loader.loadModel(3,vbuf.posarr(), 2,vbuf.uvarr(), 3,vbuf.normarr(), 3,vbuf.posarr(), 3,VBRCD, 3,VMTID);
                    rs.setModel(model);
                });
            }

            SystemUtil.sleep(10);
        }
    }

    private static float[] genVertBaryCoord(int vn) {
        float[] barycd = new float[vn*3];
        for (int i = 0;i < vn;i+=3) {
            barycd[i*3]=1;   barycd[i*3+1]=0; barycd[i*3+2]=0;
            barycd[i*3+3]=0; barycd[i*3+4]=1; barycd[i*3+5]=0;
            barycd[i*3+6]=0; barycd[i*3+7]=0; barycd[i*3+8]=1;
        }
        return barycd;
    }

    private static float[] genVertMaterialId(int vn, int[] vertm) {
        float[] vmids = new float[vn*3];
        for (int i = 0;i < vn;i+=3) {
            int v1Id = vertm[i];
            int v2Id = vertm[i+1];
            int v3Id = vertm[i+2];
            for (int j=0;j<3;j++) {
                vmids[i*3+j*3] =   v1Id;
                vmids[i*3+j*3+1] = v2Id;
                vmids[i*3+j*3+2] = v3Id;
            }
        }
        return vmids;
    }

//    private static Vector3f[] CUBE_FACES_DIRS = new Vector3f[] {
//            new Vector3f( 1,0,0),
//            new Vector3f(-1,0,0),
//            new Vector3f(0, 1,0),
//            new Vector3f(0,-1,0),
//            new Vector3f(0,0, 1),
//            new Vector3f(0,0,-1),
//    };

//    public static void putCubeVtx(VertexBuffer vbuf, Vector3f blockpos, World world, TextureAtlas.Fragment txFrag) {
//        for (int i = 0;i < CUBE_FACES_DIRS.length;i++) {
//            Vector3f faceDir = CUBE_FACES_DIRS[i];
//            Block neibgherBlock = world.getBlock((int)(blockpos.x + faceDir.x), (int)(blockpos.y+faceDir.y), (int)(blockpos.z+faceDir.z));
//            if (neibgherBlock == null || neibgherBlock.isTranslucent()) {
//                putCubeFace(vbuf, blockpos, i, txFrag, Matrix3f.IDENTITY, Vector3f.ZERO);
//            }
//        }
//    }

//    public static void putCubeFace(VertexBuffer vbuf, Vector3f blockpos, int iface, TextureAtlas.Fragment txFrag, Matrix3f rot, Vector3f preposoff) {
//        final int FACE_FN = 6*3;  // numFloat components of a QuadFace(2triangles)  i.e. 2*3*vec3 = 6*vec3 floats.
//        float[] rawpos = ModelRenderer.MODEL_CUBE.attribute(0).data;
//        float[] rawtx = ModelRenderer.MODEL_CUBE.attribute(1).data;
//        float[] rawnorm = ModelRenderer.MODEL_CUBE.attribute(2).data;
//        for (int i = 0;i < 6;i++) {
//            Vector3f pos = new Vector3f(
//                    rawpos[iface*FACE_FN+i*3],
//                    rawpos[iface*FACE_FN+i*3+1],
//                    rawpos[iface*FACE_FN+i*3+2]);
//            pos.add(preposoff);
//            Matrix3f.transform(rot, pos);
//            pos.add(1,1,1).scale(1/2f);  // to [0, 1]
//            pos.add(Maths.mod(blockpos.x, 16), blockpos.y, Maths.mod(blockpos.z, 16));
//            vbuf.positions.add(pos.x); vbuf.positions.add(pos.y); vbuf.positions.add(pos.z);
//
//            Vector3f norm = new Vector3f(rawnorm[iface*FACE_FN+i*3], rawnorm[iface*FACE_FN+i*3+1], rawnorm[iface*FACE_FN+i*3+2]);
//            vbuf.normals.add(norm.x);vbuf.normals.add(norm.y);vbuf.normals.add(norm.z);
//
//            vbuf.textureCoords.add(rawtx[i*2]*txFrag.SCALE.x + txFrag.OFFSET.x);
//            vbuf.textureCoords.add(rawtx[i*2+1]*txFrag.SCALE.y + txFrag.OFFSET.y);
//        }
//    }



//    public static void putTallgrassCrossFaces(VertexBuffer vbuf, Vector3f blockpos, TextureAtlas.Fragment txFrag) {
//
//        Matrix3f rot = new Matrix3f();
//
//        Matrix3f.rotate(Maths.PI/4, Vector3f.UNIT_Y, rot);
//        putCubeFace(vbuf, blockpos, 0, txFrag, rot, new Vector3f(-1,0,0));
//
//        Matrix3f.rotate(-Maths.PI/4, Vector3f.UNIT_Y, rot);
//        putCubeFace(vbuf, blockpos, 0, txFrag, rot, new Vector3f(-1,0,0));
//
//    }
}
