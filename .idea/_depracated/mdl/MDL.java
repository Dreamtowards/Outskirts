package outskirts.client.mdl;

import outskirts.util.IOUtils;
import outskirts.util.StringUtils;
import outskirts.util.Validate;
import outskirts.util.vector.Quaternion;
import outskirts.util.vector.Vector3f;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public final class MDL {

    private static final String MDL_INDICES = "INDICES";
    private static final String MDL_LAYOUTS = "LAYOUTS";

    private static final String MDL_EX_SKELETONS = "+SKELETONS"; // or +JOINTS.? no. joints no enought HighLevel/ SpecAspect, like Common Concepts.


    public static MDLData loadMDL(InputStream inputStream) {
        MDLData mdldata = new MDLData();

        load(inputStream, (field, flines) -> {
            if (field.equals(MDL_INDICES)) {
                assert flines.size() == 1;
                String[] sp = StringUtils.explodeSpaces(flines.get(0));
                int[] indices = new int[sp.length];
                for (int i = 0; i < indices.length; i++) {
                    indices[i] = Integer.parseInt(sp[i]);
                }
                mdldata.indices = indices;
            } else if (field.equals(MDL_LAYOUTS)) {
                assert flines.size() >= 1;
                for (String ln : flines) {
                    String[] sp = StringUtils.explodeSpaces(ln);
                    float[] vdata = new float[sp.length];
                    for (int i = 0; i < vdata.length; i++) {
                        vdata[i] = Float.parseFloat(sp[i]);
                    }
                    mdldata.layouts.add(vdata);
                }
            } else {
               throw new IllegalFieldException();
            }
        });
        return mdldata;
    }

    public static void saveMDL(MDLData vtxdata, OutputStream outputStream) {
        StringBuilder sb = new StringBuilder();

        sb.append(MDL_INDICES).append('\n');
        for (int i = 0;i < vtxdata.indices.length;i++) {
            sb.append(vtxdata.indices[i]);
            if (i < vtxdata.indices.length-1)
                sb.append(' ');
        }
        sb.append('\n');

        sb.append(MDL_LAYOUTS).append('\n');
        for (float[] vdata : vtxdata.layouts) {
            for (int i = 0;i < vdata.length;i++) {
                sb.append(vdata[i]);
                if (i < vdata.length-1)
                    sb.append(' ');
            }
            sb.append('\n');
        }

        save(sb, outputStream);
    }

    public static final class MDLData {
        public int[] indices;
        public List<float[]> layouts = new ArrayList<>();  // VAO AttributeList

        public SkeJoint[] _skeletons;

        public static final class SkeJoint { // SkeletonJoint
            public int idx;  // index of the Joint in Jointlist(sorted list, order by nameId ASC.)  i.e. "Local Joint ID", associated with Any-Joint-Names in the Jointlist.
            public String nameId;
            public int parentIdx; // -1 when is rootJoint.
            public Vector3f translation;
            public Quaternion orientation;

            public List<SkeJoint> _children = new ArrayList<>();
            private static SkeJoint buildRootJoint(SkeJoint[] jointlist) { // for a jointlist, only can execute once. (else'll modify children.
                SkeJoint rootJoint = null;
                for (SkeJoint joint : jointlist) {
                    if (joint.parentIdx == -1) {
                        rootJoint = joint;
                    } else {
                        jointlist[joint.parentIdx]._children.add(joint);
                    }
                }
                Validate.notNull(rootJoint, "Could not find root joint.");
                return rootJoint;
            }
        }
    }




    private static final String MDLANIM_KEYFRAMES = "KEYFRAMES";




    private static void load(InputStream inputStream, BiConsumer<String, List<String>> onField) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String currField = null;
            List<String> fieldLines = new ArrayList<>();

            String line;
            while ((line=br.readLine()) != null) {
                int _cmi = line.indexOf('#');
                if (_cmi != -1) // reduce comments.
                    line = line.substring(0, _cmi);
                if ((line=line.trim()).isEmpty()) // reduce blank line.
                    continue;

                if (isMDLField(line)) {  // FIELD HEADER
                    if (currField != null) {  // another field.
                        onField.accept(currField, fieldLines);
                        fieldLines.clear();
                    }
                    currField = line;
                } else {  // FIELD CONTENT
                    if (currField == null)
                        throw new IllegalStateException("Content of Undefined Field.");
                    fieldLines.add(line);
                }
            }
            if (currField != null) {  // another field. /last field
                onField.accept(currField, fieldLines);
                fieldLines.clear();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed read.", ex);
        }
    }

    private static void save(StringBuilder sb, OutputStream outputStream) {
        try {
            IOUtils.write(new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)), outputStream);
        } catch (IOException ex) {
            throw new RuntimeException("Failed write to.", ex);
        }
    }

    private static boolean isMDLField(String s) {
        return s.equals(MDL_INDICES) || s.equals(MDL_LAYOUTS) || s.equals(MDL_EX_SKELETONS);
    }



}
