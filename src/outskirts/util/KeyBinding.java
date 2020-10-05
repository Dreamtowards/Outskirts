package outskirts.util;

import outskirts.client.Outskirts;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;

@SideOnly(Side.CLIENT)
public final class KeyBinding implements Registrable {

    public static final int TYPE_KEYBOARD = 0;
    public static final int TYPE_MOUSE = 1;

    public interface OnInputListener {
        /**
         * @param keyState true if keydown, false if keyup
         */
        void onInput(boolean keyState);
    }

    public static final Registry<KeyBinding> REGISTRY = new Registry.RegistrableRegistry<>();

    private String registryID;
    private int keyCode;
    private final int defaultKeyCode;
    private String category;
    private int deviceType;

    private KeyBinding.OnInputListener onInputListener = keyState -> {};

    public KeyBinding(String registryID, int defaultKeyCode, int deviceType, String category) {
        this.registryID = new ResourceLocation(registryID).toString();
        this.keyCode = defaultKeyCode;
        this.defaultKeyCode = defaultKeyCode;
        this.deviceType = deviceType;
        this.category = category;

        //Auto Register
        REGISTRY.register(this);
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public int getDefaultKeyCode() {
        return defaultKeyCode;
    }

    public String getCategory() {
        return category;
    }

    public boolean isKeyDown() {
        if (deviceType == TYPE_KEYBOARD) {
            return Outskirts.isKeyDown(keyCode);
        } else if (deviceType == TYPE_MOUSE) {
            return Outskirts.isMouseDown(keyCode);
        } else {
            return false;
        }
    }

    public KeyBinding setOnInputListener(OnInputListener listener) {
        this.onInputListener = listener;
        return this;
    }

    public static void postInput(int keyCode, boolean keyState, int deviceType) {
        for (KeyBinding keyBinding : REGISTRY.values()) {
            if (deviceType == keyBinding.deviceType && keyCode == keyBinding.keyCode) {
                keyBinding.onInputListener.onInput(keyState);
            }
        }
    }
}
