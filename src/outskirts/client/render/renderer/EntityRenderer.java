package outskirts.client.render.renderer;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.render.renderer.preferences.RenderPerferences;
import outskirts.client.render.Model;
import outskirts.client.render.TextureAtlas;
import outskirts.client.render.Framebuffer;
import outskirts.client.render.lighting.Light;
import outskirts.client.render.renderer.post.PostRenderer;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.entity.Entity;
import outskirts.init.MaterialTextures;
import outskirts.util.Colors;
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.ResourceLocation;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec3;
import static outskirts.client.render.isoalgorithm.sdf.Vectors.vec4;
import static outskirts.util.Colors.fromRGB;

/**
 * EntityRenderer. a high-level renderer, for Entity
 */
public class EntityRenderer extends Renderer {

    private ShaderProgram shaderGeometry = new ShaderProgram(
            new Identifier("shaders/entity/geometry.vsh").getInputStream(),
            new Identifier("shaders/entity/geometry.fsh").getInputStream(),
            new Identifier("shaders/entity/geometry.gsh").getInputStream()
    );
    private ShaderProgram shaderCompose = new ShaderProgram(
            new ResourceLocation("shaders/entity/compose.vsh").getInputStream(),
            new ResourceLocation("shaders/entity/compose.fsh").getInputStream()
    );

    public static Object[][] skyColorTable = new Object[][] {
            {-1, fromRGB(151,108,92), fromRGB(46,70,116), fromRGB(78,83,96)},
            {6000, fromRGB(152,207,233), fromRGB(34,99,175), fromRGB(0,45,84)},
//            {12000, fromRGB(98,52,60), fromRGB(44,107,132), fromRGB(37,40,51)},
            {14000, fromRGB(0,6,14), fromRGB(0,15,31), fromRGB(1,1,1)},
            {22000, fromRGB(0,6,14), fromRGB(0,15,31), fromRGB(1,1,1)},
            {24000, fromRGB(151,108,92), fromRGB(46,70,116), fromRGB(78,83,96)},  // same as -1.
    };

    public static void setColors() {
        float time = Outskirts.getWorld().daytime;

        Object[] prev=null, next=null;
        for (int i = 0;i < skyColorTable.length;i++) {
            float keytime = (int)skyColorTable[i][0];
            if (keytime >= time) {
                prev = skyColorTable[i-1];
                next = skyColorTable[i];
                break;
            }
        }
        float t = Maths.inverseLerp(time, (int)prev[0], (int)next[0]);

        Vector4f.lerp(t, (Vector4f)prev[1], (Vector4f)next[1], bgColor);
        Vector4f.lerp(t, (Vector4f)prev[2], (Vector4f)next[2], skyColor);
        Vector4f.lerp(t, (Vector4f)prev[3], (Vector4f)next[3], voidColor);
    }

    public static Vector4f bgColor = new Vector4f(fromRGB(152,207,233));
    public static Vector4f skyColor = new Vector4f(fromRGB(34,99,175));
    public static Vector4f voidColor = new Vector4f(fromRGB(0,45,84));

    public static float fogDensity = 0.01f;
    public static float fogGradient = 1.5f;

    public EntityRenderer() {
        //init Texture-Units

        shaderGeometry.useProgram();
        shaderGeometry.setInt("diffuseMap", 0);
        shaderGeometry.setInt("specularMap", 1);
        shaderGeometry.setInt("normalMap", 2);
        shaderGeometry.setInt("displacementMap", 3);
        // emissionMap, environmentMap

        shaderCompose.useProgram();
        shaderCompose.setInt("gPositionDepth", 0);
        shaderCompose.setInt("gNormal", 1);
        shaderCompose.setInt("gAlbedoSpecular", 2);
        shaderCompose.setInt("ssaoBlurMap", 3);

        shaderCompose.setInt("shadowdepthMap", 6);
    }


    private static final int RENDER_LIGHTS = 64;
    private String[] uniform_lights$color = createUniformNameArray("lights[%s].color", RENDER_LIGHTS);
    private String[] uniform_lights$position = createUniformNameArray("lights[%s].position", RENDER_LIGHTS);
    private String[] uniform_lights$attenuation = createUniformNameArray("lights[%s].attenuation", RENDER_LIGHTS);
    private String[] uniform_lights$direction = createUniformNameArray("lights[%s].direction", RENDER_LIGHTS);
    private String[] uniform_lights$coneAngleInner = createUniformNameArray("lights[%s].coneAngleInnerCos", RENDER_LIGHTS);
    private String[] uniform_lights$coneAngleOuter = createUniformNameArray("lights[%s].coneAngleOuterCos", RENDER_LIGHTS);

    private Matrix4f MAT_MODELMAT_TRANS = new Matrix4f();


    public void renderGBuffer(List<Entity> entities) {

        shaderGeometry.useProgram();

        int i = 0;
        for (TextureAtlas.Fragment frag : MaterialTextures.DIFFUSE_ATLAS.fragments()) {
            shaderGeometry.setVector4f("mtlfrags["+i+"]", vec4(frag.OFFSET.x, frag.OFFSET.y, frag.SCALE.x, frag.SCALE.y));
            i++;
        }

        shaderGeometry.setMatrix4f("projectionMatrix", Outskirts.renderEngine.getProjectionMatrix());
        shaderGeometry.setMatrix4f("viewMatrix", Outskirts.renderEngine.getViewMatrix());

        for (Entity entity : entities) {
            Model model = entity.getModel();
            RenderPerferences renderPerferences = entity.getRenderPerferences();

            glBindVertexArray(model.vaoID());

            shaderGeometry.setMatrix4f("modelMatrix", Maths.createModelMatrix(entity.position(), entity.tmp_boxSphere_scale, entity.rotation(), MAT_MODELMAT_TRANS));

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, renderPerferences.getDiffuseMap().textureID());
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, renderPerferences.getSpecularMap().textureID());
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, renderPerferences.getNormalMap().textureID());
            glActiveTexture(GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, renderPerferences.getDisplacementMap().textureID());

            glDrawElements(GL_TRIANGLES, model.vertexCount(), GL_UNSIGNED_INT, 0);
        }
        glBindVertexArray(0);
    }

    public void renderCompose(Framebuffer gfbo, List<Light> lights) {

        shaderCompose.useProgram();

        shaderCompose.setVector3f("CameraPos", Outskirts.getCamera().getPosition());

        shaderCompose.setFloat("fogDensity", fogDensity);
        shaderCompose.setFloat("fogGradient", fogGradient);
        shaderCompose.setVector3f("fogColor", vec3(bgColor));

        {   // setup lights
            int lightCount = Math.min(lights.size(), RENDER_LIGHTS);
            shaderCompose.setInt("lightCount", lightCount);
            for (int i = 0;i < lightCount;i++) {
                Light light = lights.get(i);
                shaderCompose.setVector3f(uniform_lights$color[i], light.color());
                shaderCompose.setVector3f(uniform_lights$position[i], light.position());
                shaderCompose.setVector3f(uniform_lights$attenuation[i], light.attenuation());
                shaderCompose.setVector3f(uniform_lights$direction[i], light.direction());
                shaderCompose.setFloat(uniform_lights$coneAngleInner[i], (float)Math.cos(light.coneAngle().x));
                shaderCompose.setFloat(uniform_lights$coneAngleOuter[i], (float)Math.cos(light.coneAngle().y));
            }
        }

        shaderCompose.setMatrix4f("shadowspaceMatrix", Outskirts.renderEngine.getShadowRenderer().getShadowspaceMatrix());
        glActiveTexture(GL_TEXTURE6);
        glBindTexture(GL_TEXTURE_2D, Outskirts.renderEngine.getShadowRenderer().getDepthMapTexture().textureID());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, gfbo.colorTextures(0).textureID());
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, gfbo.colorTextures(1).textureID());
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, gfbo.colorTextures(2).textureID());
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, Outskirts.renderEngine.ssaoFBO.colorTextures(0).textureID());

        PostRenderer.doDrawQuad();
    }

    @Override
    public ShaderProgram getShader() {
        throw new UnsupportedOperationException();
    }
}
