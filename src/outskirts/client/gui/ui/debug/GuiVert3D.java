package outskirts.client.gui.ui.debug;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.*;
import outskirts.physics.collision.broadphase.bounding.AABB;
import outskirts.util.*;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiVert3D extends Gui {

    public static GuiVert3D INSTANCE = new GuiVert3D();

    public static class Vert {
        public String name = "";
        public Vector3f position = new Vector3f();
        public Vector4f color = new Vector4f(Colors.WHITE);
        public Vert[] connect = new Vert[0];
        public String[] connectNames = new String[0];

        public Vert() {}

        public Vert(String name, Vector3f position, Vector4f color) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(position);
            Objects.requireNonNull(color);
            this.name = name;
            this.position = position;
            this.color = color;
        }

        private static Vert readFromJSON(JSONObject json) {
            Vert vert = new Vert();
            vert.position = vec3(json.getString("position"));
            if (json.has("name"))
                vert.name = json.getString("name");
            if (json.has("color"))
                vert.color = vec4(json.getString("color"));
            if (json.has("connect")) {
                vert.connectNames = StringUtils.explode(json.getString("connect"), ",");
                for (int i = 0;i < vert.connectNames.length;i++)
                    vert.connectNames[i] = vert.connectNames[i].trim();
            }
            return vert;
        }

        public static JSONArray writeToJSON(Iterable<Vert> iterable) {
            JSONArray arr = new JSONArray();
            for (Vert vert : iterable) {
                JSONObject jobj = new JSONObject();
                jobj.put("position", vert.position.x+","+vert.position.y+","+vert.position.z);
                if (vert.name != null && !vert.name.isEmpty())
                    jobj.put("name", vert.name);
                if (!vert.color.equals(Colors.WHITE))
                    jobj.put("color", vert.color.x+","+vert.color.y+","+vert.color.z+","+vert.color.w);
                if (vert.connectNames.length != 0) {
                    StringBuilder sb = new StringBuilder();
                    for (String c : vert.connectNames)
                        sb.append(c).append(",");
                    jobj.put("connect", sb.toString());
                }
                arr.put(jobj);
            }
            return arr;
        }
    }

    public List<Vert> vertices = new CopyOnWriteArrayList();//new CopyOnIterateArrayList<>();


    private GuiSlider _VertDisplayNumControl = addGui(new GuiSlider()); {
        _VertDisplayNumControl.setWidth(400);
        _VertDisplayNumControl.setUserMinMaxValue(0, 200);
        _VertDisplayNumControl.addValueChangedListener(e -> {
//            _VertDisplayNumControl.setText("DisplayNum: "+(int)_VertDisplayNumControl.getCurrentUserValue());
        });
        _VertDisplayNumControl.setValue(1f);
    }

    public static Vert addVert(String name, Vector3f position, Vector4f color) {
        Vert v = new Vert(name, position, color);
        INSTANCE.vertices.add(v);
        return v;
    }
    public static Vert addVert(String name, Vector3f position, Vector4f color, String[] connectNames) {
        Vert v = addVert(name, position, color);
        v.connectNames = connectNames;
        return v;
    }
    public static Vert addVert(String name, Vector3f position, Vector4f color, Vert[] connect) {
        Vert v = addVert(name, position, color);
        v.connect = connect;
        return v;
    }

    public static List<Vert> addNorm(String name, Vector3f p, Vector3f norm, Vector4f color) {
        Vert v = addVert("", p, color);
        Vert vNorm = addVert(name, new Vector3f(p).add(norm), color, new Vert[]{v});
        return Arrays.asList(v, vNorm);
    }
    public static List<Vert> addTri(String prefix, Vector3f v0, Vector3f v1, Vector3f v2, Vector4f color, Vector3f norm) {
        List<Vert> added = new ArrayList<>();
        Vert vert1 = addVert(prefix+".v0", v0, color);
        Vert vert2 = addVert(prefix+".v1", v1, color, new Vert[]{vert1});
        Vert vert3 = addVert(prefix+".v2", v2, color, new Vert[]{vert1, vert2});
        added.add(vert1);
        added.add(vert2);
        added.add(vert3);
        if (norm != null) {
            Vector3f ABCCenter = new Vector3f().add(v0).add(v1).add(v2).scale(1/3f);
            added.addAll(addNorm(prefix+".norm", ABCCenter, norm, color));
        }
        return added;
    }
    public static List<Vert> addAABB(String name, AABB aabb, Vector4f color) {
        Vert v0 = addVert(name, new Vector3f(aabb.min.x, aabb.min.y, aabb.min.z), color);
        Vert v1 = addVert(name, new Vector3f(aabb.max.x, aabb.min.y, aabb.min.z), color, new Vert[]{v0});
        Vert v2 = addVert(name, new Vector3f(aabb.max.x, aabb.min.y, aabb.max.z), color, new Vert[]{v1});
        Vert v3 = addVert(name, new Vector3f(aabb.min.x, aabb.min.y, aabb.max.z), color, new Vert[]{v2, v0});
        Vert v4 = addVert(name, new Vector3f(aabb.min.x, aabb.max.y, aabb.min.z), color, new Vert[]{v0});
        Vert v5 = addVert(name, new Vector3f(aabb.max.x, aabb.max.y, aabb.min.z), color, new Vert[]{v4,v1});
        Vert v6 = addVert(name, new Vector3f(aabb.max.x, aabb.max.y, aabb.max.z), color, new Vert[]{v5,v2});
        Vert v7 = addVert(name, new Vector3f(aabb.min.x, aabb.max.y, aabb.max.z), color, new Vert[]{v6,v4,v3});
        return Arrays.asList(v0,v1,v2,v3,v4,v5,v6,v7);
    }


    public GuiVert3D() {

        addKeyboardListener(e -> {
            if (e.getKeyState() && e.getKey() == GLFW.GLFW_KEY_I) {
                vertices.clear();
                try {
                    JSONArray array = new JSONArray(IOUtils.toString(new FileInputStream("vert.json")));
                    for (int i = 0;i < array.length();i++) {
                        JSONObject vertJSON = array.getJSONObject(i);
                        vertices.add(Vert.readFromJSON(vertJSON));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Log.LOGGER.info("Imported.");
            }
            if (e.getKeyState() && e.getKey() == GLFW.GLFW_KEY_P) {
                int i = 0;
                for (Vert v : vertices)
                    Log.LOGGER.info("v{}: name:{},  pos:{},  ", i++, v.name, v.position);
            }
        });

        addOnDrawListener(e -> {

            drawString("vts: "+vertices.size(), getX(), getY()+100, Colors.WHITE);

            // draw vertices
            int i = 0;
            for (Vert vert : vertices) {
                if (_VertDisplayNumControl.getCurrentUserValue() < _VertDisplayNumControl.getUserMaxValue())
                    if (i++ > _VertDisplayNumControl.getCurrentUserValue()) continue;
                Gui.drawWorldpoint(vert.position, (x, y) -> {
                    drawRect(vert.color, x, y, 4, 4);
                    if (vert.name.contains("[v]"))
                        drawString(vert.name.replace("[v]", " (" + vert.position + ")"), x, y, vert.color);
                    else if (Outskirts.isKeyDown(GLFW.GLFW_KEY_V))
                        drawString(vert.name +vert.position, x, y, vert.color);
                    else
                        drawString(vert.name, x, y, vert.color);
                });

                List<Vert> conns = new ArrayList<>(Arrays.asList(vert.connect));
                for (Vert v : vertices) {
                    if (CollectionUtils.contains(vert.connectNames, v.name))
                        conns.add(v);
                }
                // LINE self->conn
                for (Vert conn : conns) {
                    Outskirts.renderEngine.getModelRenderer().drawLine(vert.position, conn.position, vert.color);
                }
                // TRIANGLE conn->nx_conn->self
                for (int j = 0;j < conns.size();j++) {
                    if (conns.size()==1) break;
                    if (conns.size()==2&&j==1) break; // dont want render "back-side" of a triangle.
                    Vert v0 = conns.get(j);
                    Vert v1 = conns.get(j+1==conns.size()?0:j+1);
                    Outskirts.renderEngine.getModelRenderer().drawTriangleFace(vert.position, v0.position, v1.position, new Vector4f(vert.color).setW(0.05f));
                }
            }

            // coordinate basis lines
            for (int sy = -0;sy <= 0;sy++) {
                int size = 5;
                for (int sx = -size;sx <= size;sx++) {
                    Outskirts.renderEngine.getModelRenderer().drawLine(new Vector3f(sx, sy, size), new Vector3f(sx, sy, -size), Colors.BLACK40);
                }
                for (int sz = -size;sz <= size;sz++) {
                    Outskirts.renderEngine.getModelRenderer().drawLine(new Vector3f(size, sy, sz), new Vector3f(-size, sy, sz), Colors.BLACK40);
                }
            }
            Outskirts.renderEngine.getModelRenderer().drawLine(new Vector3f(-10,0,0), new Vector3f(10,0,0), Colors.FULL_R);
            Outskirts.renderEngine.getModelRenderer().drawLine(new Vector3f(0,-10,0), new Vector3f(0,10,0), Colors.FULL_G);
            Outskirts.renderEngine.getModelRenderer().drawLine(new Vector3f(0,0,-10), new Vector3f(0,0,10), Colors.FULL_B);

        });
    }

    private static Vector3f vec3(String str) {
        Vector3f vec = new Vector3f();
        String[] s = StringUtils.explode(str, ",");
        for (int i = 0;i < s.length;i++) {
            if (i==0)
                vec.x = Float.parseFloat(s[i]);
            if (i==1)
                vec.y = Float.parseFloat(s[i]);
            if (i==2)
                vec.z = Float.parseFloat(s[i]);
        }
        return vec;
    }

    private static Vector4f vec4(String str) {
        Vector4f vec = new Vector4f();
        String[] s = StringUtils.explode(str, ",");
        for (int i = 0;i < s.length;i++) {
            if (i==0)
                vec.x = Float.parseFloat(s[i]);
            if (i==1)
                vec.y = Float.parseFloat(s[i]);
            if (i==2)
                vec.z = Float.parseFloat(s[i]);
            if (i==3)
                vec.w = Float.parseFloat(s[i]);
        }
        return vec;
    }

}
