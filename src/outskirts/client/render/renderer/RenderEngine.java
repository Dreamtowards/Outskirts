package outskirts.client.render.renderer;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import outskirts.client.ClientSettings;
import outskirts.client.Outskirts;
import outskirts.client.render.Framebuffer;
import outskirts.client.render.renderer.debug.visualgeo.DebugVisualGeoRenderer;
import outskirts.client.render.renderer.gui.FontRenderer;
import outskirts.client.render.renderer.gui.GuiRenderer;
import outskirts.client.render.renderer.map.MapRenderer;
import outskirts.client.render.renderer.particle.ParticleRenderer;
import outskirts.client.render.renderer.post.PostRenderer;
import outskirts.client.render.renderer.shadow.ShadowRenderer;
import outskirts.client.render.renderer.skybox.SkyboxRenderer;
import outskirts.client.render.renderer.ssao.SSAORenderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.event.Events;
import outskirts.util.Maths;
import outskirts.util.vector.Matrix4f;
import outskirts.world.World;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static outskirts.util.logging.Log.LOGGER;

public final class RenderEngine {

    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();

    private EntityRenderer entityRenderer = new EntityRenderer();
    private GuiRenderer guiRenderer = new GuiRenderer();
    private FontRenderer fontRenderer = new FontRenderer();
    private ModelRenderer modelRenderer = new ModelRenderer();
    private ShadowRenderer shadowRenderer = new ShadowRenderer();
//    private SkyboxRenderer skyboxRenderer = new SkyboxRenderer();
    private ParticleRenderer particleRenderer = new ParticleRenderer();
    private PostRenderer postRenderer = new PostRenderer();
    private SSAORenderer ssaoRenderer = new SSAORenderer();
    private MapRenderer mapRenderer = new MapRenderer();
    private DebugVisualGeoRenderer debugVisualGeoRenderer = new DebugVisualGeoRenderer();

    public Framebuffer gBufferFBO = Framebuffer.glfGenFramebuffer()
            .bindPushFramebuffer()
            .resize(1280, 720)
            .attachTextureColor(0, GL_RGBA16F) // position,depth  ?todo: RGB?RGBA
            .attachTextureColor(1, GL_RGB16F) // normal
            .attachTextureColor(2, GL_RGBA) // diffuse
            .initMRT()
            .attachRenderbufferDepthStencil()
            .checkFramebufferStatus()
            .popFramebuffer();

    public Framebuffer ssaoFBO = Framebuffer.glfGenFramebuffer()
            .bindPushFramebuffer()
            .resize(640, 360)
            .attachTextureColor(0, GL_RGB)
            .checkFramebufferStatus()
            .popFramebuffer();
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
        LOGGER.info("RenderEngine initialized. GL_I: {} - {} | {}", glGetString(GL_VENDOR), glGetString(GL_RENDERER), glGetString(GL_VERSION));
        LOGGER.info("LWJGL {}, GLFWL NONE.", Sys.getVersion());

//        Events.EVENT_BUS.register(WindowResizedEvent.class, e -> updateRenderQuality());
    }

    public void prepare() {
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS); // DEF

//        glEnable(GL_STENCIL_TEST);
//        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDisable(GL_CULL_FACE);
        glCullFace(GL_BACK); // DEF
        glFrontFace(GL_CCW); // DEF

        RenderEngine.checkGlError("prepare");

        // todo: split out. refreshViewMatrix(), refreshProjectionMatrix(), or just refreshViewProjectionMatrix().
        // projection matrix almost only needs been update when, FOV changed, width/height changed.. one of those args changed..
        // but the calculation is very lightweight. and good at in-time update. like arbitrary to set FOV.. at anytime and dosen't needs manually update (the projmatrix).
        Maths.createPerspectiveProjectionMatrix(Maths.toRadians(getFov()), Outskirts.getWidth(), Outskirts.getHeight(), ClientSettings.NEAR_PLANE, ClientSettings.FAR_PLANE, getProjectionMatrix());
//        Maths.createOrthographicProjectionMatrix(Outskirts.getWidth()*f, Outskirts.getHeight()*f, ClientSettings.FAR_PLANE, getProjectionMatrix());

        Maths.createViewMatrix(Outskirts.getCamera().getPosition(), Outskirts.getCamera().getRotation(), Outskirts.renderEngine.getViewMatrix());

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



        gBufferFBO.bindPushFramebuffer();
            prepare();
            glDisable(GL_BLEND);
            entityRenderer.renderGBuffer(world.getEntities());
            glEnable(GL_BLEND);
        gBufferFBO.popFramebuffer();

//        ssaoFBO.bindPushFramebuffer();
//            prepare();
//            ssaoRenderer.renderSSAO(gBufferFBO.colorTextures(0), gBufferFBO.colorTextures(1));
//        ssaoFBO.popFramebuffer();

//        ssaoBlurFBO.bindPushFramebuffer();
//            prepare();
//            ssaoRenderer.renderSSAOBlur(ssaoFBO.colorTextures(0));
//        ssaoBlurFBO.popFramebuffer();


        prepare();
        glClearColor(0.5f, 0.6f, 0.8f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        entityRenderer.renderCompose(gBufferFBO, world.lights);
//        skyboxRenderer.render();

//        Gui.drawTexture(ssaoBlurFBO.colorTextures(0), Outskirts.getRootGUI());
//        postRenderer.render(ssaoFBO.colorTextures(0));

    }

//    public void updateRenderQuality() {
//        Outskirts.renderEngine.getWorldFramebuffer().bindPushFramebuffer();
//        Outskirts.renderEngine.getWorldFramebuffer().resize(
//                (int)(Outskirts.toFramebufferCoords(Outskirts.getWidth())*RENDERE_QUALITY),
//                (int)(Outskirts.toFramebufferCoords(Outskirts.getHeight())*RENDERE_QUALITY));
//        Outskirts.renderEngine.getWorldFramebuffer().popFramebuffer();
//    }

    public static void checkGlError(String msg) {
        int i = glGetError();
        if (i != 0) {
            LOGGER.warn("######## GL Error ########");
            LOGGER.warn("{}: {}.", msg, i);
        }
    }





    private float fov = 70;
    private boolean vsync;

    public void setVSync(boolean enable) {
        vsync = enable;
        Display.setVSyncEnabled(enable);
    }
    public boolean isVSync() {
        return vsync;
    }

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
    public ShadowRenderer getShadowRenderer() {
        return shadowRenderer;
    }
//    public SkyboxRenderer getSkyboxRenderer() {
//        return skyboxRenderer;
//    }
    public ParticleRenderer getParticleRenderer() {
        return particleRenderer;
    }
    public MapRenderer getMapRenderer() {
        return mapRenderer;
    }
    public DebugVisualGeoRenderer getDebugVisualGeoRenderer() {
        return debugVisualGeoRenderer;
    }
}
