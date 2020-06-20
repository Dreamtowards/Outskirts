package outskirts.client.render.renderer;

import org.lwjgl.Version;
import outskirts.client.GameSettings;
import outskirts.client.Outskirts;
import outskirts.client.render.shadow.ShadowRenderer;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix4f;
import outskirts.world.World;

import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.opengl.GL11.*;
import static outskirts.util.logging.Log.LOGGER;

public final class RenderEngine {

    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();

    private EntityRenderer entityRenderer = new EntityRenderer();
    private GuiRenderer guiRenderer = new GuiRenderer();
    private FontRenderer fontRenderer = new FontRenderer();
    private ModelRenderer modelRenderer = new ModelRenderer();
    private TerrainRenderer terrainRenderer = new TerrainRenderer();
    private ShadowRenderer shadowRenderer = new ShadowRenderer();
    private SkyboxRenderer skyboxRenderer = new SkyboxRenderer();

    public RenderEngine() {
        LOGGER.info("RenderEngine initialized. GL_I: {} - {} | {}", glGetString(GL_VENDOR), glGetString(GL_RENDERER), glGetString(GL_VERSION));
        LOGGER.info("LWJGL {}, GLFWL {}", Version.getVersion(), glfwGetVersionString());
    }

    public void prepare() {
        glClearColor(0.45f, 0.45f, 0.45f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS); // DEF

        glEnable(GL_STENCIL_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK); // DEF
        glFrontFace(GL_CCW); // DEF

//        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        // projection matrix almost only needs been update when, FOV changed, width/height changed.. one of those args changed..
        // but the calculation is very lightweight. and good at in-time update. like arbitrary to set FOV.. at anytime and dosen't needs manually update (the projmatrix).
        Maths.createPerspectiveProjectionMatrix(Maths.toRadians(GameSettings.FOV), Outskirts.getWidth(), Outskirts.getHeight(), GameSettings.NEAR_PLANE, GameSettings.FAR_PLANE, getProjectionMatrix());
//        Maths.createOrthographicProjectionMatrix(Outskirts.getWidth(), Outskirts.getHeight(), 1000, renderEngine.getProjectionMatrix());
    }

    public void render(World world) {

        shadowRenderer.renderDepthMap(world.getEntities());

//        glDisable(GL_CULL_FACE);
        entityRenderer.render(world.getEntities(), world.lights);
//        glEnable(GL_CULL_FACE);

        terrainRenderer.render(world.getTerrains());

//        skyboxRenderer.render();
    }


    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }


    public EntityRenderer getEntityRenderer() {
        return entityRenderer;
    }

    public GuiRenderer getGuiRenderer() {
        return guiRenderer;
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    public ModelRenderer getModelRenderer() {
        return modelRenderer;
    }

    public TerrainRenderer getTerrainRenderer() {
        return terrainRenderer;
    }

    public ShadowRenderer getShadowRenderer() {
        return shadowRenderer;
    }

    public SkyboxRenderer getSkyboxRenderer() {
        return skyboxRenderer;
    }
}
