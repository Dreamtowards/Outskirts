package outskirts.client;

import org.json.JSONArray;
import org.json.JSONObject;
import outskirts.client.gui.Gui;
import outskirts.client.gui.debug.GuiDebugCommon;
import outskirts.client.gui.debug.GuiVert3D;
import outskirts.client.gui.screen.GuiScreenMainMenu;
import outskirts.client.gui.screen.GuiScreenPause;
import outskirts.util.IOUtils;
import outskirts.util.KeyBinding;
import outskirts.util.StringUtils;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static outskirts.util.logging.Log.LOGGER;

//todo: ClientSettings
public final class ClientSettings {

    // CommandLineArguments or ProgramArguments ..?
    public static class ProgramArguments {

        public static int WIDTH = 700;

        public static int HEIGHT = 500;

        public static String UUID;

        public static String TOKEN;

        public static List<String> EXTENSIONS = new ArrayList<>();

        public static void readArguments(String[] args) {
            parse(args).forEach((key, value) -> {
                switch (key) {
                    case "width":
                        WIDTH = Integer.parseInt(value);
                        break;
                    case "height":
                        HEIGHT = Integer.parseInt(value);
                        break;
                    case "uuid":
                        UUID = value;
                        break;
                    case "token":
                        TOKEN = value;
                        break;
                    case "extensions":
                        for (String e : StringUtils.explode(value, ";"))
                            if (!e.isEmpty())
                                EXTENSIONS.add(e);
                        break;
                    default:
                        LOGGER.warn("Unknown program argument: {}", key);
                        break;
                }
            });
        }

        /**
         * the arguments like --width="730" --height="500" --fullscreen
         */
        private static Map<String, String> parse(String[] args) {
            Map<String, String> map = new HashMap<>();
            for (String item : args) {
                if (!item.startsWith("--")) {
                    LOGGER.warn("Illegal program argument: \"{}\", skipped.", item);
                    continue;
                }
                if (item.contains("=")) {
                    String key = item.substring("--".length(), item.indexOf("="));
                    String value = item.substring(item.indexOf("=") + 1);
                    map.put(key, value);
                } else {
                    String key = item.substring("--".length());
                    map.put(key, null);
                }
            }
            return map;
        }
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface Save
    {
        String value();
    }



    @Save("fps_cap")
    public static int FPS_CAPACITY = 60; // cap <= 0 means not lim

    public static boolean ENABLE_VSYNC = false;

    public static boolean ENABLE_FA = false; //Texture Filter Anisotropic

    public static float FOV = 70;

    public static float NEAR_PLANE = 0.1f;

    public static float FAR_PLANE = 10000f;



    //public static float GUI_SCALE = 1f;
//    public static final Vector2f CONTENT_SCALE = new Vector2f(); // contentscale = fb_size / window_size

    // GUI_COORD_SCALE   guiCoords to windowCoords scale.
    public static float GUI_SCALE = 1f;




    public static int PICKER_DEPTH = 4;

    public static float MOUSE_SENSITIVITY = 0.5f;


    // KEY DEBUGS

    private static final KeyBinding KEY_VERT3D = new KeyBinding("key.debug.vert3d", GLFW_KEY_V, KeyBinding.TYPE_KEYBOARD, "categories.debug").setOnInputListener(keyState -> {
        if (keyState)
            Gui.toggleVisible(GuiVert3D.INSTANCE);
    });

    private static final KeyBinding KEY_COMMDEBUG = new KeyBinding("key.debug.comm", GLFW_KEY_F3, KeyBinding.TYPE_KEYBOARD, "categories.debug").setOnInputListener(keyState -> {
        if (keyState)
            Gui.toggleVisible(GuiDebugCommon.INSTANCE);
    });


    public static final KeyBinding KEY_ESC = new KeyBinding("key.esc", GLFW_KEY_ESCAPE, KeyBinding.TYPE_KEYBOARD, "categories.misc").setOnInputListener(keyState -> {
        if (keyState) {
            if (Outskirts.getWorld() != null) {
                if (Outskirts.currentScreen() == null) {
                    Outskirts.startScreen(GuiScreenPause.INSTANCE);
                } else {
                    Outskirts.closeScreen();
                }
            } else {
                if (Outskirts.currentScreen() != GuiScreenMainMenu.INSTANCE) {
                    Outskirts.closeScreen();
                }
            }
        }
    });

    public static final KeyBinding KEY_GUI_DEBUG = new KeyBinding("key.debug", GLFW_KEY_F3, KeyBinding.TYPE_KEYBOARD, "categories.misc").setOnInputListener(keyState -> {
        if (keyState) {
//            Gui.toggleVisible(GuiScreenInGame.INSTANCE.getGuiScreenDebug());
        }
    });


    public static final KeyBinding KEY_USE = new KeyBinding("key.use", GLFW_MOUSE_BUTTON_RIGHT, KeyBinding.TYPE_MOUSE, "categories.gameplay").setOnInputListener(keyState -> {
        if (keyState && Outskirts.getWorld() != null && Outskirts.currentScreen() == null) {


        }
    });


    public static final KeyBinding KEY_ATTACK = new KeyBinding("key.attack", GLFW_MOUSE_BUTTON_LEFT, KeyBinding.TYPE_MOUSE, "categories.gameplay").setOnInputListener(keyState -> {
        if (keyState && Outskirts.getWorld() != null && Outskirts.currentScreen() == null) {


        }
    });

    public static final KeyBinding KEY_WALK_FORWARD = new KeyBinding("key.forward", GLFW_KEY_W, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_WALK_BACKWARD = new KeyBinding("key.backward", GLFW_KEY_S, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_WALK_LEFT = new KeyBinding("key.left", GLFW_KEY_A, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_WALK_RIGHT = new KeyBinding("key.right", GLFW_KEY_D, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_JUMP = new KeyBinding("key.jump", GLFW_KEY_SPACE, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_SNEAK = new KeyBinding("key.sneak", GLFW_KEY_LEFT_SHIFT, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");



    private static final File OPTION_FILE = new File("options.dat");

    public static void loadOptions() {
        try {
            JSONObject json = new JSONObject(IOUtils.toString(new FileInputStream(OPTION_FILE)));

            loadJSON(ClientSettings.class, json);

            LOGGER.info("Loaded GameSettings options. ({})", OPTION_FILE);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load GameSettings options.", ex);
        }
    }

    public static void saveOptions() {
        try {
            JSONObject json = new JSONObject();

            saveJSON(ClientSettings.class, json);

            IOUtils.write(new ByteArrayInputStream(json.toString(4).getBytes()), new FileOutputStream(OPTION_FILE));

            LOGGER.info("Saved GameSettings options. ({})", OPTION_FILE);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save GameSettings options.", ex);
        }
    }


    private static void loadJSON(Class clazz, JSONObject json) throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            Save annotation = field.getAnnotation(Save.class);
            if (annotation == null || !json.has(annotation.value()))
                continue;

            Object option = json.get(annotation.value());
            Class<?> fieldtype = field.getType();
            Object value = null;

            if (fieldtype == Integer.TYPE) {
                value = Integer.parseInt(option.toString());
            } else if (fieldtype == Long.TYPE) {
                value = Long.parseLong(option.toString());
            } else if (fieldtype == Float.TYPE) {
                value = Float.parseFloat(option.toString());
            } else if (fieldtype == Boolean.TYPE) {
                value = Boolean.parseBoolean(option.toString());
            } else if (fieldtype == String.class) {
                value = option.toString();
            } else if (fieldtype == JSONObject.class || fieldtype == JSONArray.class) {
                value = option;
            } else {
                throw new UnsupportedOperationException("Unsupported field type (" + fieldtype + ")");
            }

            field.set(clazz, value);
        }
    }

    private static void saveJSON(Class clazz, JSONObject json) throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            Save annotation = field.getAnnotation(Save.class);
            if (annotation == null) continue;

            String key = annotation.value();
            Object value = field.get(clazz);
            Object option = null;

            if (value instanceof JSONObject || value instanceof JSONArray) {
                option = value;
            } else {
                option = value.toString();
            }

            json.put(key, option);
        }
    }
}