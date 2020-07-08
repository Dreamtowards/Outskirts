package outskirts.client.animation.dae;

import outskirts.util.CollectionUtils;
import outskirts.util.Maths;
import outskirts.util.StringUtils;
import outskirts.util.XML;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Quaternion;
import outskirts.util.vector.Vector3f;

import java.io.InputStream;
import java.util.*;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

/**
 * Unsupported:
 * <asset>, <library_cameras>, <library_lights>, <library_images>, <library_materials>, <library_effects>, <scene>, <extra>.
 *
 * Loadup part:
 * <library_geometries>, <library_controllers>, <library_visual_scenes>, <library_animations>.
 */
public class DaeLoader {

    private static final List<String> SUPPORTED_VERSIONS = Arrays.asList("1.4.0", "1.4.1");

    public static DaeData loadDAE(InputStream inputStream) {
        XML COLLADA = new XML(inputStream);  assert SUPPORTED_VERSIONS.contains(COLLADA.getAttributes().get("version")) : "Unsupported DAE Version.";
        DaeData daedata = new DaeData();

        XML _library_geometries = COLLADA.getChild("library_geometries");
        XML _library_controllers = COLLADA.getChild("library_controllers");
        XML _library_visual_scenes = COLLADA.getChild("library_visual_scenes");
        XML _library_animations = COLLADA.getChild("library_animations");

        daedata.meshs = MeshLoad.loadMeshs(_library_geometries, _library_controllers);

        daedata.joints = JointsLoad.loadJoints(_library_visual_scenes);

        daedata.anim1 = AnimLoad.loadAnim(_library_animations);

        return daedata;
    }

    public static final class DaeData {

        public MeshData meshs;

        public static final class MeshData {
            public int[] indices;
            public float[] positions;     // vec3
            public float[] textureCoords; // vec2
            public float[] normals;       // vec3

            public float[] jointsIDs;     // vec?3. but x,y,z all are non-fraction (just like int).
            public float[] jointsWeights; // vec?3. sum(vec3.xyz)==1.
        }

        // validate: parentIdx < idx for vaild-order. (sometimes parent needs been operate before children.
        public JointInfo[] joints; // rootjoint=i_0.

        public static final class JointInfo { // tree or children... tree for Clearity.
            public String name;  // nameId
            public int parentIdx;
            public Matrix4f bindTransform;

            private static void validateJoints(JointInfo[] joints) {
                assert joints[0].parentIdx == -1;
                for (int i = 0;i < joints.length;i++)
                    assert joints[i].parentIdx < i;
            }
        }

        public Map<String, JointKeyframeData[]> anim1;  // <joint_name:String, JointAnim>

        public static final class JointKeyframeData { // JointKeyframe
            public float timestamp;
            public Vector3f translation;
            public Quaternion orientation;
        }

    }


    private static class MeshLoad {

        private static final int MAX_WEIGHT_JOINTS = 3;

        //    @SuppressWarnings("all")
        private static DaeData.MeshData loadMeshs(XML _library_geometries, XML _library_controllers) {

            XML nMesh = _library_geometries.getChild("geometry").getChild("mesh");
            XML nTriangles = nMesh.getChild("polylist");

            List<Integer> indices = new ArrayList<>();
            List<mstVertex> vertices = new ArrayList<>();

            // read Table
            float[] tbPositions = GetFloatArrayById(nMesh, nMesh.getChild("vertices").getChild("input").getAttributes().get("source").substring(1));
            float[] tbTexCoords = GetFloatArrayById(nMesh, nTriangles.getChild("input", "semantic", "TEXCOORD").getAttributes().get("source").substring(1));
            float[] tbNormals = GetFloatArrayById(nMesh, nTriangles.getChild("input", "semantic", "NORMAL").getAttributes().get("source").substring(1));
            for (int i = 0; i < tbPositions.length / 3; i++) {
                vertices.add(new mstVertex(i, -1, -1));
            }

            {   // process vertices indices
                int vTypes = nTriangles.getChildren("input").size();
                String[] mulIdxArr = nTriangles.getChild("p").getTextContent().split(" ");
                for (int i = 0; i < mulIdxArr.length; i += vTypes) {
                    int posIdx = parseInt(mulIdxArr[i]);
                    int normIdx = parseInt(mulIdxArr[i + 1]);
                    int texIdx = parseInt(mulIdxArr[i + 2]);

                    mstVertex bv = vertices.get(posIdx);
                    if (bv.textureIdx == -1) { // init the vert.
                        bv.textureIdx = texIdx;
                        bv.normalIdx = normIdx;
                        indices.add(posIdx);
                    } else {
                        meshAddVert(bv, texIdx, normIdx, vertices, indices);
                    }
                }
            }


            int[][] tbJointIDs       = new int[tbPositions.length/3][];
            float[][] tbJointWeights = new float[tbPositions.length/3][];
            {   // EXT load Joints Mesh/Skin Data
                XML nSkin = _library_controllers.getChild("controller").getChild("skin");

                // build Table
                float[] rawTbWeights = GetFloatArrayById(nSkin, nSkin.getChild("vertex_weights").getChild("input", "semantic", "WEIGHT").getAttributes().get("source").substring(1));
                String[] mulIdxArr = StringUtils.explodeSpaces(nSkin.getChild("vertex_weights").getChild("v").getTextContent()); // vJointIdWeightArr
                String[] vcount = StringUtils.explodeSpaces(nSkin.getChild("vertex_weights").getChild("vcount").getTextContent());

                int i_mulIdx = 0;
                for (int vi = 0; vi < vcount.length; vi++) {  // vcount is aligned to tbPositions/3.
                    int jointsCount = parseInt(vcount[vi]);
                    int[] ids = new int[jointsCount];
                    float[] weights = new float[jointsCount];
                    for (int j = 0; j < jointsCount; j++) {
                        ids[j] = parseInt(mulIdxArr[i_mulIdx]);
                        weights[j] = rawTbWeights[parseInt(mulIdxArr[i_mulIdx + 1])];
                        i_mulIdx += 2;
                    }
                    tbJointIDs[vi] = ids;
                    tbJointWeights[vi] = weights;
                }
            }


            // to Arrays.
            int[] out_indices = CollectionUtils.toArrayi(indices);
            float[] out_positions = new float[indices.size() * 3];
            float[] out_textureCoords = new float[indices.size() * 2];
            float[] out_normals = new float[indices.size() * 3];
            float[] out_jointIDs = new float[vertices.size() * MAX_WEIGHT_JOINTS];
            float[] out_jointWeights = new float[vertices.size() * MAX_WEIGHT_JOINTS];
            for (int i = 0; i < vertices.size(); i++) {
                mstVertex v = vertices.get(i);

                out_positions[i * 3] = tbPositions[v.positionIdx * 3];
                out_positions[i * 3 + 1] = tbPositions[v.positionIdx * 3 + 1];
                out_positions[i * 3 + 2] = tbPositions[v.positionIdx * 3 + 2];

                out_textureCoords[i * 2] = tbTexCoords[v.textureIdx * 2];
                out_textureCoords[i * 2 + 1] = 1f-tbTexCoords[v.textureIdx * 2 + 1];  // cancel 1-

                out_normals[i * 3] = tbNormals[v.normalIdx * 3];
                out_normals[i * 3 + 1] = tbNormals[v.normalIdx * 3 + 1];
                out_normals[i * 3 + 2] = tbNormals[v.normalIdx * 3 + 2];

                for (int j = 0;j < Math.min(tbJointIDs[v.positionIdx].length, MAX_WEIGHT_JOINTS);j++) {
                    out_jointIDs    [i*MAX_WEIGHT_JOINTS+j] =     tbJointIDs[v.positionIdx][j];
                    out_jointWeights[i*MAX_WEIGHT_JOINTS+j] = tbJointWeights[v.positionIdx][j];
                }
            }

            DaeData.MeshData mdat = new DaeData.MeshData();
            mdat.indices=out_indices;
            mdat.positions=out_positions; mdat.textureCoords=out_textureCoords; mdat.normals=out_normals;
            mdat.jointsIDs=out_jointIDs; mdat.jointsWeights=out_jointWeights;
            return mdat;
        }

        private static void meshAddVert(mstVertex bv, int texIdx, int normIdx, List<mstVertex> vertices, List<Integer> indices) {
            if (bv.textureIdx == texIdx && bv.normalIdx == normIdx) {
                indices.add(bv.extenVertexIdx == -1 ? bv.positionIdx : bv.extenVertexIdx);
                return;
            }
            if (bv.extenVertex == null) {
                bv.extenVertex = new mstVertex(bv.positionIdx, texIdx, normIdx);
                vertices.add(bv.extenVertex);
                bv.extenVertex.extenVertexIdx = vertices.size() - 1;
                indices.add(bv.extenVertex.extenVertexIdx);
            } else {
                meshAddVert(bv.extenVertex, texIdx, normIdx, vertices, indices);
            }
        }

        private static class mstVertex {
            int positionIdx;
            int textureIdx;
            int normalIdx;

            int extenVertexIdx = -1;
            mstVertex extenVertex;

            mstVertex(int positionIdx, int textureIdx, int normalIdx) {
                this.positionIdx = positionIdx;
                this.textureIdx = textureIdx;
                this.normalIdx = normalIdx;
            }
        }
    }

    private static class JointsLoad {

        private static DaeData.JointInfo[] loadJoints(XML _library_visual_scenes) {
            List<DaeData.JointInfo> joints = new ArrayList<>();

            loadJointData(_library_visual_scenes.getChild("visual_scene").getChild("node", "id", "Armature").getChild("node"),
                    -1, joints, new Matrix4f());

            // the order may could dismatch with the Vertex-Data vertex-Joint-ID.?
//            DaeData.JointInfo.validateJoints(joints);
            return joints.toArray(new DaeData.JointInfo[0]);
        }

        private static void loadJointData(XML nNodeJoint, int parentIdx, List<DaeData.JointInfo> dest, Matrix4f parentTrans) {
            DaeData.JointInfo joint = new DaeData.JointInfo();
            joint.name = nNodeJoint.getAttribute("id");
            joint.parentIdx = parentIdx;
            joint.bindTransform = utLoadMatrix4x4(nNodeJoint.getChild("matrix").getTextContent());
            Matrix4f.mul(parentTrans, joint.bindTransform, joint.bindTransform); // modelspace.
            dest.add(joint);
            int idx = dest.size()-1;

            for (XML nchild : nNodeJoint.getChildren("node")) {
                loadJointData(nchild, idx, dest, joint.bindTransform);
            }
        }
    }

    private static class AnimLoad {

        private static Map<String, DaeData.JointKeyframeData[]> loadAnim(XML _library_animations) {
            List<XML> nJAnimLs = _library_animations//.getChildren().get(0)
                    .getChildren("animation");
            Map<String, DaeData.JointKeyframeData[]> anim = new HashMap<>();

            for (XML nJAnim : nJAnimLs) {
                String jointId = nJAnim.getChild("channel").getAttribute("target").split("/")[0];

                float[] tbTimestamps = GetFloatArrayById(nJAnim, nJAnim.getChild("sampler").getChild("input", "semantic", "INPUT").getAttribute("source").substring(1));
                float[] tbKeyframes = GetFloatArrayById(nJAnim, nJAnim.getChild("sampler").getChild("input", "semantic", "OUTPUT").getAttribute("source").substring(1));
                assert tbTimestamps.length == tbKeyframes.length/16;

                DaeData.JointKeyframeData[] keyframes = new DaeData.JointKeyframeData[tbTimestamps.length];
                for (int i = 0;i < keyframes.length;i++) {
                    DaeData.JointKeyframeData kf = new DaeData.JointKeyframeData();
                    kf.timestamp = tbTimestamps[i];

                    kf.translation = new Vector3f(tbKeyframes[i*16+3], tbKeyframes[i*16+7], tbKeyframes[i*16+11]);
                    kf.orientation = Quaternion.fromMatrix(new Matrix3f(
                            tbKeyframes[i*16], tbKeyframes[i*16+1], tbKeyframes[i*16+2],
                            tbKeyframes[i*16+4], tbKeyframes[i*16+5],tbKeyframes[i*16+6],
                            tbKeyframes[i*16+8],tbKeyframes[i*16+9],tbKeyframes[i*16+10]
                    ), null);
                    keyframes[i] = kf;
                }
                anim.put(jointId, keyframes);
            }
            return anim;
        }
    }



    // the Get is Good.
    private static float[] GetFloatArrayById(XML sourceContainer, String sourceId) {
        String[] s = StringUtils.explodeSpaces(sourceContainer.getChild("source", "id", sourceId).getChild("float_array").getTextContent());  // to float opt.
        float[] arr = new float[s.length];
        for (int i = 0;i < s.length;i++) {
            arr[i] = parseFloat(s[i]);
        }
        return arr;
    }
    private static String[] GetNameArrayById(XML sourceContainer, String sourceId) {
        return StringUtils.explodeSpaces(sourceContainer.getChild("source", "id", sourceId).getChild("Name_array").getTextContent());
    }

    private static Matrix4f utLoadMatrix4x4(String tx16f) {
        return Matrix4f.load(null, StringUtils.readNumbers(tx16f, new float[16]));
    }
}
