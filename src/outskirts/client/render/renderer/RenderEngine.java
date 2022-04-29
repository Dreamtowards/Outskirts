package outskirts.client.render.renderer;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.main.TmpExtTest;
import outskirts.client.render.Framebuffer;
import outskirts.client.render.Frustum;
import outskirts.client.render.Texture;
import outskirts.client.render.chunk.ChunkRenderDispatcher;
import outskirts.client.render.renderer.debug.visualgeo.DebugVisualGeoRenderer;
import outskirts.client.render.renderer.gui.FontRenderer;
import outskirts.client.render.renderer.gui.GuiRenderer;
import outskirts.client.render.renderer.map.MapRenderer;
import outskirts.client.render.renderer.particle.ParticleRenderer;
import outskirts.client.render.renderer.post.PostRenderer;
import outskirts.client.render.renderer.shadow.ShadowRenderer;
import outskirts.client.render.renderer.sky.SkyRenderer;
import outskirts.client.render.renderer.skybox.SkyboxRenderer;
import outskirts.client.render.renderer.ssao.SSAORenderer;
import outskirts.entity.Entity;
import outskirts.event.Events;
import outskirts.event.client.WindowResizedEvent;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix3f;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import outskirts.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static outskirts.client.Outskirts.GUI_FBO_SIZE_FACTOR;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.client.render.renderer.EntityRenderer.*;
import static outskirts.util.logging.Log.LOGGER;

public final class RenderEngine {

    static {
        Texture.init();
    }

    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();

    private Matrix4f cacheProjectionViewMatrix = new Matrix4f();
    private Frustum frustum = new Frustum();

    public static float NEAR_PLANE = 0.1f;
    public static float FAR_PLANE = 1000f;

    public static boolean dbg_EnableFrustumUpdate = true;

    private EntityRenderer entityRenderer = new EntityRenderer();
    private GuiRenderer guiRenderer = new GuiRenderer();
    private FontRenderer fontRenderer = new FontRenderer();
    private ModelRenderer modelRenderer = new ModelRenderer();
//    private ShadowRenderer shadowRenderer = new ShadowRenderer();
//    private SkyboxRenderer skyboxRenderer = new SkyboxRenderer();
//    private ParticleRenderer particleRenderer = new ParticleRenderer();
//    private PostRenderer postRenderer = new PostRenderer();
//    private SSAORenderer ssaoRenderer = new SSAORenderer();
//    private MapRenderer mapRenderer = new MapRenderer();
    private DebugVisualGeoRenderer debugVisualGeoRenderer = new DebugVisualGeoRenderer();
    private SkyRenderer skyRenderer = new SkyRenderer();

    private ChunkRenderDispatcher chunkRenderDispatcher = new ChunkRenderDispatcher();

    public Framebuffer gBufferFBO = Framebuffer.glfGenFramebuffer()
            .pushFramebuffer()
            .resize(1280, 720)
            .attachTextureColor(0, GL_RGBA16F) // position,depth  ?todo: RGB?RGBA
            .attachTextureColor(1, GL_RGBA)   // diffuse
            .attachTextureColor(2, GL_RGB16F) // normal
            .initMRT()
            .attachRenderbufferDepthStencil()
            .checkFramebufferStatus()
            .popFramebuffer();



    public Framebuffer fbGUI = Framebuffer.glfGenFramebuffer()
            .pushFramebuffer()
            .attachTextureColor(0, GL_RGBA)
            .checkFramebufferStatus()
            .popFramebuffer();

//    public Framebuffer ssaoFBO = Framebuffer.glfGenFramebuffer()
//            .pushFramebuffer()
//            .resize(640, 360)
//            .attachTextureColor(0, GL_RGB)
//            .checkFramebufferStatus()
//            .popFramebuffer();

//    public Framebuffer ssaoBlurFBO = Framebuffer.glfGenFramebuffer()
//            .bindPushFramebuffer()
//            .resize(1280, 720)
//            .attachTextureColor(0, GL_RGB)
//            .checkFramebufferStatus()
//            .popFramebuffer();

//    private Framebuffer worldFramebuffer = Framebuffer.glfGenFramebuffer()
//            .bindPushFramebuffer()
//            .resize(1920, 1080)
//            .attachTextureColor(0, GL_RGB16F)
//            .attachRenderbufferDepthStencil()
//            .checkFramebufferStatus()
//            .popFramebuffer();



    public RenderEngine() {
        LOGGER.info("RenderEngine initialized. GL_I: {} | {}, {}", glGetString(GL_VERSION), glGetString(GL_RENDERER), glGetString(GL_VENDOR));
        LOGGER.info("LWJGL {}, GLFW {}.", Version.getVersion(), GLFW.glfwGetVersionString());

        Events.EVENT_BUS.register(WindowResizedEvent.class, e -> {
//            Log.LOGGER.info("Resize Gui FBO");
            fbGUI
                    .pushFramebuffer()
                    .resize((int)(Outskirts.getWidth()* GUI_FBO_SIZE_FACTOR), (int)(Outskirts.getHeight()* GUI_FBO_SIZE_FACTOR))
                    .checkFramebufferStatus()
                    .popFramebuffer();
            Gui.requestLayout();
        });
    }

    public void prepare() {
        glClearColor(0, 0.4f, 0.4f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS); // DEF

//        glEnable(GL_STENCIL_TEST);
//        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK); // DEF
        glFrontFace(GL_CCW); // DEF

        RenderEngine.checkGlError("prepare");


    }

    // update when view-aspect, FOV, near/far plane changed.
    private void refreshProjectionMatrix() {
        Maths.createPerspectiveProjectionMatrix(Maths.toRadians(getFov()), Outskirts.getWidth(), Outskirts.getHeight(), NEAR_PLANE, FAR_PLANE, projectionMatrix);
        // Maths.createOrthographicProjectionMatrix(Outskirts.getWidth()*f, Outskirts.getHeight()*f, ClientSettings.FAR_PLANE, getProjectionMatrix());
    }

    private void refreshViewMatrix() {
        Maths.createViewMatrix(Outskirts.getCamera().getPosition(), Outskirts.getCamera().getRotation(), viewMatrix);
    }

    private void refreshViewFrustum() {
        Matrix4f.mul(projectionMatrix, viewMatrix, cacheProjectionViewMatrix);
        getViewFrustum().set(cacheProjectionViewMatrix);
    }


    public void render(World world) {

//        shadowRenderer.renderDepthMap(world.getEntities());


//        worldFramebuffer.bindPushFramebuffer();
//            prepare();
////        glDisable(GL_CULL_FACE);
//            entityRenderer.render(world.getEntities(), world.lights);
////            Particle p = new Particle();
////            p.getPosition().set(getPlayer().getPosition());
////            p.setTexture(Outskirts.renderEngine.getWorldFramebuffer().colorTextures(0));
////            Outskirts.renderEngine.getParticleRenderer().render(Collections.singletonList(p));
////        glEnable(GL_CULL_FACE);
//        worldFramebuffer.popFramebuffer();


        refreshProjectionMatrix();
        refreshViewMatrix();
        if (dbg_EnableFrustumUpdate)
            refreshViewFrustum();


        List<Entity> entities = new ArrayList<>();
        for (Entity entity : world.getEntities()) {
            if (entity == Outskirts.getCamera().getOwnerEntity() && Outskirts.getCamera().getCameraDistance() == 0)
                continue;
            // Frustum Culling.
            if (getViewFrustum().intersects(entity.getRigidBody().getAABB())) {
                entities.add(entity);
            }
        }

        gBufferFBO.pushFramebuffer();
            prepare();
            glDisable(GL_BLEND);
            entityRenderer.renderGBuffer(entities);
            glEnable(GL_BLEND);
        gBufferFBO.popFramebuffer();

//        ssaoFBO.pushFramebuffer();
//            prepare();
//            ssaoRenderer.renderSSAO(gBufferFBO.colorTextures(0), gBufferFBO.colorTextures(1));
//        ssaoFBO.popFramebuffer();

//        ssaoBlurFBO.bindPushFramebuffer();
//            prepare();
//            ssaoRenderer.renderSSAOBlur(ssaoFBO.colorTextures(0));
//        ssaoBlurFBO.popFramebuffer();

//        skyColor.set(Colors.fromRGB(149, 185, 214));
//        LOGGER.info(skyColor);

        prepare();
        glClearColor(bgColor.x, bgColor.y, bgColor.z, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        entityRenderer.renderCompose(gBufferFBO, Collections.singletonList(TmpExtTest.theLight));
//            skyboxRenderer.render();

//        glDisable(GL_CULL_FACE);

        skyRenderer.render(skyHei, 0,
                vec3(skyColor));
        skyRenderer.render(-skyHei, 0,
                vec3(voidColor));

        Vector3f sunpos = vec3(100, 0, 0);

        Matrix3f.transform(Matrix3f.rotate((Maths.PI*2) * (Outskirts.getWorld().daytime / 24000f), Vector3f.UNIT_Z, null),
                sunpos);

        modelRenderer.render(ModelRenderer.MODEL_CUBE, Texture.UNIT,
                vec3(Outskirts.getCamera().getPosition()).add(sunpos), vec3(2), Matrix3f.IDENTITY, Colors.RED,
                true,
                true,
                GL_TRIANGLES);

//        EntityRenderer.setColors();

//        Gui.drawTexture(ssaoBlurFBO.colorTextures(0), Outskirts.getRootGUI());
//        postRenderer.render(ssaoFBO.colorTextures(0));

    }

    public static float skyHei = 100;






    private float fov = 70;

    public float getFov() {
        return fov;
    }
    public void setFov(float fov) {
        this.fov = fov;
    }





    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Frustum getViewFrustum() {
        return frustum;
    }

    public ChunkRenderDispatcher getChunkRenderDispatcher() {
        return chunkRenderDispatcher;
    }

    public static void checkGlError(String msg) {
        int i = glGetError();
        if (i != 0) {
            LOGGER.warn("######## GL Error ########");
            LOGGER.warn("{}: {}.", msg, i);
        }
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
//    public ShadowRenderer getShadowRenderer() {
//        return shadowRenderer;
//    }
//    public SkyboxRenderer getSkyboxRenderer() {
//        return skyboxRenderer;
//    }
//    public ParticleRenderer getParticleRenderer() {
//        return particleRenderer;
//    }
//    public MapRenderer getMapRenderer() {
//        return mapRenderer;
//    }
    public DebugVisualGeoRenderer getDebugVisualGeoRenderer() {
        return debugVisualGeoRenderer;
    }
}
