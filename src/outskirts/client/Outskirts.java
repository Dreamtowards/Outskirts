package outskirts.client;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import outskirts.client.audio.AudioEngine;
import outskirts.client.gui.Gui;
import outskirts.client.gui.screen.*;
import outskirts.client.gui.screen.tools.GuiScreen3DVertices;
import outskirts.client.material.Model;
import outskirts.client.render.Camera;
import outskirts.client.render.Light;
import outskirts.client.render.renderer.RenderEngine;
import outskirts.entity.EntityGeoShape;
import outskirts.entity.player.EntityPlayerSP;
import outskirts.event.Events;
import outskirts.event.client.WindowResizedEvent;
import outskirts.event.client.input.*;
import outskirts.init.Init;
import outskirts.init.Models;
import outskirts.init.Textures;
import outskirts.mod.Mods;
import outskirts.physics.collision.narrowphase.collisionalgorithm.gjk.Gjk;
import outskirts.physics.collision.shapes.ConvexShape;
import outskirts.physics.collision.shapes.concave.TriangleMeshShape;
import outskirts.physics.collision.shapes.convex.*;
import outskirts.physics.dynamics.RigidBody;
import outskirts.physics.extras.quickhull.QuickHull;
import outskirts.util.*;
import outskirts.util.concurrent.Scheduler;
import outskirts.util.profiler.Profiler;
import outskirts.util.vector.Vector3f;
import outskirts.world.WorldClient;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static outskirts.client.GameSettings.GUI_SCALE;
import static outskirts.util.logging.Log.LOGGER;

public class Outskirts {

    private float dScroll; // wheelDXY
    private float mouseDX;
    private float mouseDY;
    private float mouseX;
    private float mouseY;
    private int width; // screen coordinate (not Framebuffer coordinate, not GUI_SCALE
    private int height;
    private long window; // glfwWindow
    private float osContentScale; // ContentScale of OS = fb_size / window_size

    private static Outskirts INSTANCE;

    public static RenderEngine renderEngine;

    private AudioEngine audioEngine;

    private boolean running;

    private GuiScreenRoot rootGUI = new GuiScreenRoot();
    private LinkedList<Gui> screenStack = new LinkedList<>(Collections.singletonList(Gui.EMPTY));

    private WorldClient world;

    private Camera camera = new Camera();
    private GameTimer timer = new GameTimer();
    private RayPicker rayPicker = new RayPicker();

    private EntityPlayerSP player;

    private Thread thread = Thread.currentThread();
    private Scheduler scheduler = new Scheduler(thread);

    private Profiler profiler = new Profiler().setEnable(false);

    public void run() {
        try
        {
            this.startGame();

            while (this.running)
            {
                this.runGameLoop();
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            this.destroy();
        }
    }

    private void startGame() throws Throwable {

        // init, load Modules
        for (String e : GameSettings.ProgramArguments.EXTENSIONS) {
            Mods.registerInit(new File(e));
        }

        this.running = true;
        Outskirts.INSTANCE = this;
        GameSettings.loadOptions();
        this.createDisplay();

        renderEngine = new RenderEngine();
        audioEngine = new AudioEngine();

        {   // set window to screen center and perform init-onResize()
            GLFWVidMode wVidmode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
            int wWidth = GameSettings.ProgramArguments.WIDTH, wHeight = GameSettings.ProgramArguments.HEIGHT;
            glfwSetWindowPos(window, wVidmode.width() / 2 - wWidth / 2, wVidmode.height() / 2 - wHeight / 2); // make window center
            glfwSetWindowSize(window, wWidth, wHeight); // for init onResize() in macOS
            glfwShowWindow(window);

            this.updateDisplay(); // update CONTENT_SCALE for correct init viewport (before init-round onResize()/glfwPollEvents()
        }

        Init.registerAll(Side.CLIENT);

        player = new EntityPlayerSP();
        camera.getCameraUpdater().setOwnerEntity(player);

        rootGUI.setIngameGUI(new GuiScreenInGame());
        startScreen(GuiScreenMainMenu.INSTANCE);

        getIngameGUI().addGui(GuiScreen3DVertices._TMP_DEF_INST.setVisible(false));

//        GuiScreenHawks screenHawks = new GuiScreenHawks();
//        GuiHawksWindow w = new GuiHawksWindow();
//        w.setWidth(100).setHeight(100);
//        w.setTitle("The Window Title");
//        w.getGuiMain().addGui(new GuiHawksSysCMD());
//        screenHawks.windows.addGui(w);
//        startScreen(screenHawks);



//        Events.EVENT_BUS.post(new InitializedEvent());

    }

    private void runGameLoop() throws Throwable { profiler.push("rt");

        timer.update();

        scheduler.processTasks();

        profiler.setEnable(true);
        profiler.push("runTick");
        while (timer.pollFullTick())
        {
            this.runTick();
        }
        profiler.pop("runTick");
        profiler.setEnable(false);

        profiler.push("glfwPollEvents");
        glfwPollEvents();
        profiler.pop("glfwPollEvents");

        camera.getCameraUpdater().update();

        if (world != null)
            rayPicker.update();

        // Render Phase
        profiler.push("render");
        renderEngine.prepare();

        profiler.push("world");
        if (world != null) {
            renderEngine.render(world);
        }
        profiler.pop("world");

        profiler.push("gui");
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        {
            profiler.push("ingame");
            rootGUI.getIngameGUI().onDraw();
            profiler.pop().push("currScreen");
            rootGUI.getCurrentScreen().onDraw();
            profiler.pop();
        }
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        profiler.pop("gui");
        profiler.pop("render");

        profiler.push("updateDisplay");
        this.updateDisplay();
        profiler.pop("updateDisplay");
        profiler.pop("rt");
//        GuiScreen3DVertices._TMP_DEF_INST.vertices.clear();
    }

    /**
     * the "On-Floor-Items non stand stable" bug.
     * appear when:
     * if (manifold instanceof [Item, Floor(invMass~=0)] (i.e Manifold Item-as-bodyA, Floor-as-bodyB) ) {   // wait.. mass? or "area"..??
     *     if ( x &&(also a little ||) y < 0 )
     *         bug appear.
     * }
     *
     */

    public static void setWorld(WorldClient world) {
        INSTANCE.world = world;
        if (world == null)
            return;
//        INSTANCE.world.addEntity(getPlayer());

        Light lightSun = new Light();
        lightSun.getPosition().set(100, 100, 100);
        lightSun.getDirection().set(-1, -1, -1).normalize();
        lightSun.getColor().set(5, 5, 5);
        Light.calculateApproximateAttenuation(100, lightSun.getAttenuation());
        INSTANCE.world.lights.add(lightSun);

        getPlayer().getMaterial()
                .setShininess(18).setSpecularStrength(0.1f)
                .setModel(Models.GEO_CUBE);


//        Events.EVENT_BUS.register(KeyboardEvent.class, e -> {
//            if (e.getKeyState() && e.getKey() == GLFW_KEY_B) {
//                setPauseWorld(0.5f);

                GuiScreen3DVertices._TMP_DEF_INST.vertices.clear();

//                Random rand = new Random();
//                Vector3f[] vertices = new Vector3f[60];
//                for (int i = 0;i < vertices.length;i++) {
//                    vertices[i] = new Vector3f(rand.nextFloat()*200, rand.nextFloat()*250, rand.nextFloat()*200);
//
//                    GuiScreen3DVertices.addVert("", vertices[i], Colors.WHITE);
//                }

//                Model model = Loader.loadOBJ(new Identifier("materials/nonbox.obj").getInputStream(), dat -> {
//                    Set<Vector3f> set = new HashSet<>();
//                    for (int idx : dat.indices) {
//                        Vector3f v = new Vector3f(
//                                dat.positions[idx*3],
//                                dat.positions[idx*3+1],
//                                dat.positions[idx*3+2]
//                        );
//                        set.add(v);
//                        GuiScreen3DVertices.addVert("", v, Colors.WHITE);
//                    }
//                    Set<Vector3f> hull = new QuickHull().quickHull(set.toArray(new Vector3f[0]));
//                    getPlayer().getRigidBody().setCollisionShape(new PolygonConvexShape(hull));
//                });
//                getPlayer().getMaterial().setModel(model);
//            }
//            if (e.getKeyState() && e.getKey() == GLFW_KEY_K)
//                QuickHull.canContinue = true;
//        });

//        if (true)return;

        for (int i = 0;i < 4;i++) {
            for (int j = 0;j < 4;j++) {
                for (int k = 0;k < 4;k++) {
                    EntityGeoShape entity = new EntityGeoShape(new BoxShape(new Vector3f(2, 2, 2)));
//                    EntityGeoShape entity = new EntityGeoShape(new ShapeSphere(2));
                    INSTANCE.world.addEntity(entity);
                    RigidBody body = entity.getRigidBody();
                    body.getGravity().set(0, -20f, 0);
                    body.transform().origin.set(i*4, 50+ k*4, j*4);
                    body.setMass(20);
                    RigidBody.updateShapeInertia(body);
                    entity.getMaterial()
                            .setDiffuseMap(Textures.FRONT)
                            .setSpecularMap(Textures.CONTAINER_SPEC)
                            .setShininess(100);
                }
            }
        }


//        EntityMaterialDisplay entityNsuit = new EntityMaterialDisplay();
//        INSTANCE.world.addEntity(entityNsuit);
//        entityNsuit.tmp_boxSphere_scale.scale(6);
//        entityNsuit.getRigidBody().setCollisionShape(new ShapeBox(new Vector3f(0,0,0)));
//        entityNsuit.getRigidBody().setMass(0);
//        entityNsuit.getRigidBody().getGravity().set(0, 0, 0);
//        entityNsuit.getRigidBody().transform().origin.set(-10, 0, -10);
//        entityNsuit.getRigidBody().setRestitution(0.5f);
//        entityNsuit.getMaterial()
//                .setModel(Loader.loadOBJ(new Identifier("materials/tree1/Tree.obj").getInputStream()))
//                .setDiffuseMap(Loader.loadTexture(new Identifier("materials/tree1/bark_0021.jpg").getInputStream()));
//        RigidBody.updateShapeInertia(entityNsuit.getRigidBody());
//
//
//        EntityMaterialDisplay entityNorm = new EntityMaterialDisplay();
//        INSTANCE.world.addEntity(entityNorm);
//        entityNorm.tmp_boxSphere_scale.scale(6);
//        entityNorm.getRigidBody().setCollisionShape(new ShapeBox(entityNorm.tmp_boxSphere_scale));
//        entityNorm.getRigidBody().setMass(0);
//        entityNorm.getRigidBody().getGravity().set(0, 0, 0);
//        entityNorm.getRigidBody().transform().origin.set(0, 40, 0);
//        entityNorm.getRigidBody().setRestitution(0.5f);
//        entityNorm.getMaterial()
//                .setModel(Models.GEO_CUBE)
//                .setDiffuseMap(Loader.loadTexture(new Identifier("materials/brickwall.png").getInputStream()))
//                .setNormalMap(Loader.loadTexture(new Identifier("materials/brickwall_normal.png").getInputStream()))
//                .setNormalMap(Loader.loadTexture(new Identifier("materials/bricks2_normal.png").getInputStream()))
//                .setDisplacementMap(Loader.loadTexture(new Identifier("materials/bricks2_disp.png").getInputStream()))
//                .setShininess(35);
//        RigidBody.updateShapeInertia(entityNorm.getRigidBody());


        EntityGeoShape eFloor = new EntityGeoShape(new BoxShape(new Vector3f(100,10,100)));
        INSTANCE.world.addEntity(eFloor);
        eFloor.getRigidBody().getGravity().set(0,0,0);
        eFloor.getRigidBody().transform().origin.set(0, -10, 0);
        eFloor.getRigidBody().setMass(8000*0f).setRestitution(0.1f);
        RigidBody.updateShapeInertia(eFloor.getRigidBody());
//        eFloor.tmp_boxSphere_scale.set(1,1,1);
        eFloor.getMaterial()
//                .setModel(Loader.loadOBJ(new Identifier("materials/mount/part.obj").getInputStream(), mdat -> {
//                    eFloor.getRigidBody().setCollisionShape(new TriangleMeshShape(mdat.indices, mdat.positions));
//                }))
                .setDiffuseMap(Textures.WOOD1)
                .setSpecularStrength(0.1f).setShininess(20);

        Vector3f[] v = new Vector3f[]{new Vector3f(0,0,-1), new Vector3f(-1,0,1), new Vector3f(1,0,1)};

        Model model = Loader.loadModelWithTangent(new int[]{0,1,2},new float[]{v[0].x,v[0].y,v[0].z,v[1].x,v[1].y,v[1].z,v[2].x,v[2].y,v[2].z},new float[]{0,0,0.5f,1,1,0},new float[]{0,1,0,0,1,0,0,1,0});

//        getPlayer().getMaterial().setModel(model); //Loader.loadOBJ(new Identifier("materials/_capsule.obj").getInputStream())
        getPlayer().getMaterial().setModel(Models.GEO_SPHERE);
        getPlayer().getMaterial().setDiffuseMap(Textures.CONTAINER);
//        getPlayer().getRigidBody().setCollisionShape(new TriangleShape());
//        getPlayer().getRigidBody().setCollisionShape(new BoxShape(new Vector3f(1, 1, 1)));
        getPlayer().getRigidBody().setCollisionShape(new SphereShape(3));
        getPlayer().tmp_boxSphere_scale.set(1,1,1).scale(3);
        getPlayer().getRigidBody().transform().set(Transform.IDENTITY);
//        getPlayer().getRigidBody().transform().origin.set(0,50,50);
        getPlayer().getRigidBody().getGravity().set(0, -20, 0).scale(1);
        getPlayer().getRigidBody().getAngularVelocity().scale(0);
        getPlayer().getRigidBody().getLinearVelocity().scale(0);
        getPlayer().getRigidBody().setMass(20).setFriction(0.5f);//.setLinearDamping(0.01f);
        RigidBody.updateShapeInertia(getPlayer().getRigidBody());


        float[] PREV_FOV = {0};
        Events.EVENT_BUS.register(KeyboardEvent.class, e -> {
            if (e.getKeyState()) {
                if (e.getKey() == GLFW_KEY_C)
                    GuiScreen3DVertices._TMP_DEF_INST.vertices.clear();
                if (e.getKey() == GLFW_KEY_T) {
//                    getPlayer().getRigidBody().getAngularVelocity().add(0, 1000, 0);
//                CollisionAlgorithm ca = new CollisionAlgorithmConvexConvex();
//                ca.detectCollision(bodyA.getRigidBody(), getPlayer().getRigidBody(), new CollisionManifold(bodyA.getRigidBody(), getPlayer().getRigidBody()));

                    new Thread(() -> {
                        GuiScreen3DVertices._TMP_DEF_INST.vertices.clear();

                        Gjk gjk = new Gjk();

                        List<Gjk.SupportPoint> slx = gjk.detectCollision(eFloor.getRigidBody(), getPlayer().getRigidBody());

                        if (slx != null) {
                            GuiScreen3DVertices.addTri("ABC", slx.get(0).point, slx.get(1).point, slx.get(2).point, Colors.YELLOW, null);
                            GuiScreen3DVertices.addTri("ABD", slx.get(0).point, slx.get(1).point, slx.get(3).point, Colors.YELLOW, null);
                            GuiScreen3DVertices.addTri("BCD", slx.get(1).point, slx.get(2).point, slx.get(3).point, Colors.YELLOW, null);
                            GuiScreen3DVertices.addTri("CAD", slx.get(2).point, slx.get(0).point, slx.get(3).point, Colors.YELLOW, null);
                        }

                        LOGGER.info("simplex: {}", slx);
                    }).start();
                }
                if (e.getKey() == GLFW_KEY_1) {
                    getCamera().getPosition().set(0, 0, 10);
                }

                if (e.getKey() == GLFW_KEY_B) {
                    GuiScreen3DVertices._TMP_DEF_INST.vertices.clear();
                    Set<Vector3f> vs = new HashSet<>();

                    final int i = 3;
                    for (int x = -i;x <= i;x++) {
                        for (int y = -i;y <= i;y++) {
                            for (int z = -i;z <= i;z++) {
                                if (x==0 && y==0 && z==0)
                                    continue;
                                Vector3f d = new Vector3f(x, y, z).normalize();
                                ConvexShape shape = (ConvexShape)getPlayer().getRigidBody().getCollisionShape();
                                Gjk.SupportPoint sp = Gjk.getSupportPoint(eFloor.getRigidBody(), getPlayer().getRigidBody(), d);
                                vs.add(sp.point);
//                                GuiScreen3DVertices.addVert("", shape.getFarthestPoint(d, new Vector3f(), getPlayer().getRigidBody().transform()), Colors.WHITE);
                            }
                        }
                    }

                    new QuickHull().quickHull(vs.toArray(new Vector3f[0]));
                }
            }

            if (e.getKey() == GLFW_KEY_C) {
                if (e.getKeyState()){
                    PREV_FOV[0] = GameSettings.FOV;
                    GameSettings.FOV = 30;
                } else {
                    GameSettings.FOV = PREV_FOV[0];
                }
            }
        });
    }
    public static void setPauseWorld(float speed) {
//        if (speed == 0) speed = 0.000001f;
        getCamera().getCameraUpdater().setOwnerEntity(speed==1.0f ? getPlayer() : null);
        getWorld().tmpTickFactor = speed;
    }

    private void runTick() {

        if (getWorld() != null) {
            if (currentScreen() == Gui.EMPTY) {
                float lv =1;
                if (Outskirts.isKeyDown(GLFW_KEY_F))
                    lv *= 6;
                if (GameSettings.KEY_WALK_FORWARD.isKeyDown()) player.walkStep(lv, 0);
                if (GameSettings.KEY_WALK_BACKWARD.isKeyDown()) player.walkStep(lv, Maths.PI);
                if (GameSettings.KEY_WALK_LEFT.isKeyDown()) player.walkStep(lv, Maths.PI/2);
                if (GameSettings.KEY_WALK_RIGHT.isKeyDown()) player.walkStep(lv, -Maths.PI/2);
                if (GameSettings.KEY_JUMP.isKeyDown()) player.walkStep(lv, new Vector3f(0, 1, 0));
                if (GameSettings.KEY_SNEAK.isKeyDown()) player.walkStep(lv, new Vector3f(0, -1, 0));
            }


            profiler.push("world");
            world.onTick();
            profiler.pop();
        }
    }

    private void destroy() {

        GameSettings.saveOptions();

        Loader.destroy();

        audioEngine.destroy();

        GL.setCapabilities(null);
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public static Gui currentScreen() {
        return INSTANCE.rootGUI.getCurrentScreen();
    }
    private static void setCurrentScreen(Gui screen) {
        // Mouse.setGrabbed(screen == null);
        // Keyboard.enableRepeatEvents(screen != null);
        glfwSetInputMode(INSTANCE.window, GLFW_CURSOR, screen == Gui.EMPTY ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
        INSTANCE.rootGUI.setCurrentScreen(screen);
    }

    public static void startScreen(GuiScreen screen) {
        Validate.isTrue(screen != null && screen != Gui.EMPTY, "illegal screen.");
        INSTANCE.screenStack.push(screen);
        setCurrentScreen(screen);
    }
    public static void closeScreen() {
        INSTANCE.screenStack.pop();
        setCurrentScreen(INSTANCE.screenStack.peek());
    }
    public static void closeAllScreen() {
        while (currentScreen() != Gui.EMPTY) {
            closeScreen();
        }
    }

    public static boolean isRunning() {
        return INSTANCE.running;
    }

    public static void shutdown() {
        INSTANCE.running = false;
    }

    public static Profiler getProfiler() {
        return INSTANCE.profiler;
    }

    public static Scheduler getScheduler() {
        return INSTANCE.scheduler;
    }

    public static GuiScreenInGame getIngameGUI() {
        return (GuiScreenInGame)INSTANCE.rootGUI.getIngameGUI();
    }

    public static EntityPlayerSP getPlayer() {
        return INSTANCE.player;
    }

    public static RayPicker getRayPicker() {
        return INSTANCE.rayPicker;
    }

    public static WorldClient getWorld() {
        return INSTANCE.world;
    }

    public static float getDelta() {
        return INSTANCE.timer.getDelta();
    }

    public static Camera getCamera() {
        return INSTANCE.camera;
    }


    // the DX/DY is high level in actually operation, in MacOSX DX'll automaclly be attachs by holding Shift key. but in windows not.
    public static float getDScroll() {
        return INSTANCE.dScroll;
    }

    public static float getMouseDX() {
        return INSTANCE.mouseDX / GUI_SCALE;
    }
    public static float getMouseDY() {
        return INSTANCE.mouseDY / GUI_SCALE;
    }

    public static float getMouseX() {
        return INSTANCE.mouseX / GUI_SCALE;
    }
    public static float getMouseY() {
        return INSTANCE.mouseY / GUI_SCALE;
    }

    public static float getWidth() {
        return INSTANCE.width / GUI_SCALE;
    }
    public static float getHeight() {
        return INSTANCE.height / GUI_SCALE;
    }

    public static boolean isCtrlKeyDown() {
        if (SystemUtils.IS_OS_MAC)
            return glfwGetKey(INSTANCE.window, GLFW_KEY_LEFT_SUPER) == GLFW_PRESS || glfwGetKey(INSTANCE.window, GLFW_KEY_RIGHT_SUPER) == GLFW_PRESS;
        else
            return glfwGetKey(INSTANCE.window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS || glfwGetKey(INSTANCE.window, GLFW_KEY_RIGHT_CONTROL) == GLFW_PRESS;
    }
    public static boolean isShiftKeyDown() {
        return glfwGetKey(INSTANCE.window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS || glfwGetKey(INSTANCE.window, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS;
    }
    public static boolean isAltKeyDown() {
        return glfwGetKey(INSTANCE.window, GLFW_KEY_LEFT_ALT) == GLFW_PRESS || glfwGetKey(INSTANCE.window, GLFW_KEY_RIGHT_ALT) == GLFW_PRESS;
    }

    public static boolean isMouseDown(int button) {
        return glfwGetMouseButton(INSTANCE.window, button) == GLFW_PRESS;
    }
    public static boolean isKeyDown(int key) {
        return glfwGetKey(INSTANCE.window, key) == GLFW_PRESS;
    }

    // there is not requires AWT. useful in OSX
    public static String getClipboard() {
        return glfwGetClipboardString(INSTANCE.window);
    }
    public static void setClipboard(String s) {
        glfwSetClipboardString(INSTANCE.window, s);
    }

    // framebuffer_size = window_size * os_content_scale = (guiCoords * GUI_SCALE)==window_size * os_content_scale
    public static float toFramebufferCoords(float guiCoords) {
        return guiCoords * GUI_SCALE * INSTANCE.osContentScale;
    }

    private void createDisplay() throws IOException {

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW.");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE); // MAC OSX requires

        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);

        window = glfwCreateWindow(1, 1, "ENGINE", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create window.");

//        glfwSetWindowIcon(window, new GLFWImage.Buffer(
//                Loader.loadTextureData(Loader.loadPNG(new Identifier("textures/gui/icons/icon_32x32.png").getInputStream()), false)
//        ));

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        GL.createCapabilities();

        glfwSetWindowSizeCallback(window, this::onWindowResized);
        glfwSetCursorPosCallback(window, this::onMouseMove);
        glfwSetScrollCallback(window, this::onScroll);
        glfwSetMouseButtonCallback(window, this::onMouseButton);
        glfwSetKeyCallback(window, this::onKeyboard);
        glfwSetCharCallback(window, this::onCharInput);

        LOGGER.info("OperationSystem {} {}, rt {} {}, VM {} v{}",
                System.getProperty("os.name"), System.getProperty("os.version"),
                System.getProperty("java.version"), System.getProperty("os.arch"),
                System.getProperty("java.vm.name"), System.getProperty("java.vm.version"));
    }

    private void updateDisplay() {

        if (glfwWindowShouldClose(window)) {
            Outskirts.shutdown();
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer scaleX = stack.callocFloat(1);
            FloatBuffer scaleY = stack.callocFloat(1);
            glfwGetWindowContentScale(INSTANCE.window, scaleX, scaleY);
            Validate.isTrue(scaleX.get(0) == scaleY.get(0), "Unsupported scale type: x != y");
            osContentScale = scaleX.get(0);
        }

        // clear curr delta in frame tail
        dScroll = 0;
        mouseDX = 0;
        mouseDY = 0;

        glfwSwapBuffers(window);

        // SystemUtils.nanosleep();
        // Display.sync(GameSettings.FPS_CAPACITY);
    }


    /**
     * Window Creation, Window Resized, Window toggleFullscreen
     */
    private void onWindowResized(long w, int nWidth, int nHeight) {
        Events.EVENT_BUS.post(new WindowResizedEvent());

        this.width = nWidth;
        this.height = nHeight;

        glViewport(0, 0, (int)Outskirts.toFramebufferCoords(Outskirts.getWidth()), (int)Outskirts.toFramebufferCoords(Outskirts.getHeight()));
    }

    private void onMouseMove(long w, double nX, double nY) {
        float oldmx = mouseX, oldmy = mouseY;
        mouseX =  (float) nX;
        mouseY =  (float) nY;
        mouseDX = mouseX - oldmx;
        mouseDY = mouseY - oldmy;
        Events.EVENT_BUS.post(new MouseMoveEvent());
        rootGUI.broadcaseEvent(new MouseMoveEvent());
    }

    private void onScroll(long w, double dX, double dY) {
        dScroll = (float)(dX + dY);
        Events.EVENT_BUS.post(new MouseScrollEvent());
        rootGUI.broadcaseEvent(new MouseScrollEvent());

    }

    private void onMouseButton(long w, int button, int action, int mods) {
        Events.EVENT_BUS.post(new MouseButtonEvent(button, action));
        rootGUI.broadcaseEvent(new MouseButtonEvent(button, action));

        KeyBinding.postInput(button, action==GLFW_PRESS, KeyBinding.TYPE_MOUSE);
    }

    private void onKeyboard(long w, int key, int scancode, int action, int mods) {
        if (action == GLFW_REPEAT)
            return;
        Events.EVENT_BUS.post(new KeyboardEvent(key, action));
        rootGUI.broadcaseEvent(new KeyboardEvent(key, action));

        KeyBinding.postInput(key, action==GLFW_PRESS, KeyBinding.TYPE_KEYBOARD);
    }

    private void onCharInput(long w, int ch) {
        Events.EVENT_BUS.post(new CharInputEvent(ch));
        rootGUI.broadcaseEvent(new CharInputEvent(ch));
    }
}
