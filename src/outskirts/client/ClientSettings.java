package outskirts.client;

import org.json.JSONArray;
import org.json.JSONObject;
import outskirts.client.gui.Gui;
import outskirts.client.gui.compoents.GuiMap;
import outskirts.client.gui.debug.GuiDebugV;
import outskirts.client.gui.debug.GuiVert3D;
import outskirts.client.gui.ex.GuiIngame;
import outskirts.util.*;
import outskirts.util.vector.Vector3f;

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

import static org.lwjgl.input.Keyboard.*;
import static outskirts.util.logging.Log.LOGGER;

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
                    case "mods": // or extensions .?
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
         * the arguments like --width 730 --height "500" --fullscreen --some-path "with blanks"
         * system already parsed the "" Quoted strings.
         */
        private static Map<String, String> parse(String[] args) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0;i < args.length;) {
                assert args[i].startsWith("--") : "Illegal program argument key: "+args[i]+". dosent startsWith '--'";
                String key = args[i++].substring(2);
                String value = null;
                if (i < args.length && !args[i].startsWith("--")) {
                    value = args[i++];
                }
                map.put(key, value);
            }
            return map;
        }
    }


    public static int FPS_CAPACITY = 60; // cap <= 0 means not lim

    public static boolean ENABLE_FA = false; //Texture Filter Anisotropic



    public static float GUI_SCALE = 1f;



    public static float MOUSE_SENSITIVITY = .25f;




    public static final KeyBinding KEY_USE = new KeyBinding("key.use", 1, KeyBinding.TYPE_MOUSE, "categories.gameplay");
    public static final KeyBinding KEY_ATTACK = new KeyBinding("key.attack", 0, KeyBinding.TYPE_MOUSE, "categories.gameplay");

    public static final KeyBinding KEY_WALK_FORWARD = new KeyBinding("key.forward", KEY_W, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_WALK_BACKWARD = new KeyBinding("key.backward", KEY_S, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_WALK_LEFT = new KeyBinding("key.left", KEY_A, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_WALK_RIGHT = new KeyBinding("key.right", KEY_D, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_JUMP = new KeyBinding("key.jump", KEY_SPACE, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_SNEAK = new KeyBinding("key.sneak", KEY_LSHIFT, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");

    public static final KeyBinding KEY_HOTBAR1 = new KeyBinding("key.hotbar1", KEY_1, KeyBinding.TYPE_KEYBOARD, "categories.gameplay").setOnInputListener(keyState -> changeHotbarSlotWhenKeyDown(keyState, 0));
    public static final KeyBinding KEY_HOTBAR2 = new KeyBinding("key.hotbar2", KEY_2, KeyBinding.TYPE_KEYBOARD, "categories.gameplay").setOnInputListener(keyState -> changeHotbarSlotWhenKeyDown(keyState, 1));
    public static final KeyBinding KEY_HOTBAR3 = new KeyBinding("key.hotbar3", KEY_3, KeyBinding.TYPE_KEYBOARD, "categories.gameplay").setOnInputListener(keyState -> changeHotbarSlotWhenKeyDown(keyState, 2));
    public static final KeyBinding KEY_HOTBAR4 = new KeyBinding("key.hotbar4", KEY_4, KeyBinding.TYPE_KEYBOARD, "categories.gameplay").setOnInputListener(keyState -> changeHotbarSlotWhenKeyDown(keyState, 3));
    public static final KeyBinding KEY_HOTBAR5 = new KeyBinding("key.hotbar5", KEY_5, KeyBinding.TYPE_KEYBOARD, "categories.gameplay").setOnInputListener(keyState -> changeHotbarSlotWhenKeyDown(keyState, 4));
    public static final KeyBinding KEY_HOTBAR6 = new KeyBinding("key.hotbar6", KEY_6, KeyBinding.TYPE_KEYBOARD, "categories.gameplay").setOnInputListener(keyState -> changeHotbarSlotWhenKeyDown(keyState, 5));
    public static final KeyBinding KEY_HOTBAR7 = new KeyBinding("key.hotbar7", KEY_7, KeyBinding.TYPE_KEYBOARD, "categories.gameplay").setOnInputListener(keyState -> changeHotbarSlotWhenKeyDown(keyState, 6));
    public static final KeyBinding KEY_HOTBAR8 = new KeyBinding("key.hotbar8", KEY_8, KeyBinding.TYPE_KEYBOARD, "categories.gameplay").setOnInputListener(keyState -> changeHotbarSlotWhenKeyDown(keyState, 7));
    public static final KeyBinding KEY_HOTBAR9 = new KeyBinding("key.hotbar9", KEY_9, KeyBinding.TYPE_KEYBOARD, "categories.gameplay").setOnInputListener(keyState -> changeHotbarSlotWhenKeyDown(keyState, 8));

    private static void changeHotbarSlotWhenKeyDown(boolean keyState, int slot) {
        if (keyState) Outskirts.getPlayer().setHotbarSlot(slot);
    }



    private static final File OPTION_FILE = new File("options.dat");

    public static void loadOptions() {
        try {
            JSONObject json = new JSONObject(IOUtils.toString(new FileInputStream(OPTION_FILE)));

            FPS_CAPACITY = json.getInt("fps_cap");

            LOGGER.info("Loaded ClientSettings options. ({})", OPTION_FILE);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load ClientSettings options.", ex);
        }
    }

    public static void saveOptions() {
        try {
            JSONObject json = new JSONObject();

            json.put("fps_cap", FPS_CAPACITY);

            IOUtils.write(json.toString(4), OPTION_FILE);
            LOGGER.info("Saved ClientSettings options. ({})", OPTION_FILE);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save ClientSettings options.", ex);
        }
    }
}
