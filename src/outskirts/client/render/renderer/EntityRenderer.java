package outskirts.client.render.renderer;

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
import outskirts.util.Identifier;
import outskirts.util.Maths;
import outskirts.util.ResourceLocation;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector4f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec3;
import static outskirts.client.render.isoalgorithm.sdf.VecCon.vec4;
import static outskirts.util.logging.Log.LOGGER;

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

    public EntityRenderer() {
        //init Texture-Units
//        shader.useProgram();
//        shader.setInt("material.diffuseSampler", 0);
//        shader.setInt("material.specularSampler", 1);
//        shader.setInt("material.emissionSampler", 2);
//        shader.setInt("material.normalSampler", 3);
//        shader.setInt("material.displacementSampler", 4);
//
//        shader.setInt("environmentSampler", 5);
//        shader.setInt("shadowdepthmapSampler", 6);

        shaderGeometry.useProgram();
        shaderGeometry.setInt("mtlDiffuseMap", 0);
        shaderGeometry.setInt("mtlSpecularMap", 1);

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

//    public void render(List<Entity> entities, List<Light> lights) {
//
//        shader.useProgram();
//
//        shader.setMatrix4f("projectionMatrix", Outskirts.renderEngine.getProjectionMatrix());
//        shader.setMatrix4f("viewMatrix", Outskirts.renderEngine.getViewMatrix());
//
//        shader.setVector3f("cameraPosition", Outskirts.getCamera().getPosition());
//
//        {   // setup lights
//            int lightCount = Math.min(lights.size(), RENDER_LIGHTS);
//            shader.setInt("lightCount", lightCount);
//
//            for (int i = 0;i < lightCount;i++) {
//                Light light = lights.get(i);
//                shader.setVector3f(uniform_lights$color[i], light.getColor());
//                shader.setVector3f(uniform_lights$position[i], light.getPosition());
//                shader.setVector3f(uniform_lights$attenuation[i], light.getAttenuation());
//                shader.setVector3f(uniform_lights$direction[i], light.getDirection());
//
//                shader.setFloat(uniform_lights$coneAngleInner[i], (float)Math.cos(light.getConeAngleInner()));
//                shader.setFloat(uniform_lights$coneAngleOuter[i], (float)Math.cos(light.getConeAngleOuter()));
//            }
//        }
//        glActiveTexture(GL_TEXTURE5);
//        glBindTexture(GL_TEXTURE_CUBE_MAP, Outskirts.renderEngine.getSkyboxRenderer().textureCubemap.textureID());
//
//        glActiveTexture(GL_TEXTURE6);
//        glBindTexture(GL_TEXTURE_2D, Outskirts.renderEngine.getShadowRenderer().getDepthMapTexture().textureID());
//        shader.setMatrix4f("shadowspaceMatrix", Outskirts.renderEngine.getShadowRenderer().getShadowspaceMatrix());
//
//        for (Entity entity : entities) {
//            if (entity == Outskirts.getCamera().getCameraUpdater().getOwnerEntity() && Outskirts.getCamera().getCameraUpdater().getCameraDistance() == 0)
//                continue;
//            Model model = entity.getModel();
//
//            glBindVertexArray(model.vaoID());
//
//            Material material = entity.getMaterial();
//
//            shader.setFloat("material.specularStrength", material.getSpecularStrength());
//            shader.setFloat("material.shininess", material.getShininess());
//
//            shader.setFloat("material.displacementScale", material.getDisplacementScale());
//
//            shader.setMatrix4f("modelMatrix", Maths.createModelMatrix(entity.getPosition(), entity.tmp_boxSphere_scale, entity.getRotation(), MAT_MODELMAT_TRANS));
//
//            glActiveTexture(GL_TEXTURE0);
//            glBindTexture(GL_TEXTURE_2D, material.getDiffuseMap().textureID());
//            glActiveTexture(GL_TEXTURE1);
//            glBindTexture(GL_TEXTURE_2D, material.getSpecularMap().textureID());
//            glActiveTexture(GL_TEXTURE2);
//            glBindTexture(GL_TEXTURE_2D, material.getEmissionMap().textureID());
//            glActiveTexture(GL_TEXTURE3);
//            glBindTexture(GL_TEXTURE_2D, material.getNormalMap().textureID());
//            glActiveTexture(GL_TEXTURE4);
//            glBindTexture(GL_TEXTURE_2D, material.getDisplacementMap().textureID());
//
//            glDrawElements(GL_TRIANGLES, model.vertexCount(), GL_UNSIGNED_INT, 0);
//        }
//
//        glBindVertexArray(0);
//    }

    public void renderGBuffer(List<Entity> entities) {

        shaderGeometry.useProgram();

        int i = 0;
        for (TextureAtlas.Fragment frag : MaterialTextures.TEXTURE_ATLAS.fragments()) {
            shaderGeometry.setVector4f("mtlfrags["+i+"]", vec4(frag.OFFSET.x, frag.OFFSET.y, frag.SCALE.x, frag.SCALE.y));
            i++;
        }

        shaderGeometry.setMatrix4f("projectionMatrix", Outskirts.renderEngine.getProjectionMatrix());
        shaderGeometry.setMatrix4f("viewMatrix", Outskirts.renderEngine.getViewMatrix());

        for (Entity entity : entities) {
            if (entity == Outskirts.getCamera().getCameraUpdater().getOwnerEntity() && Outskirts.getCamera().getCameraUpdater().getCameraDistance() == 0)
                continue;
            Model model = entity.getModel();
            RenderPerferences renderPerferences = entity.getRenderPerferences();

            glBindVertexArray(model.vaoID());

            shaderGeometry.setMatrix4f("modelMatrix", Maths.createModelMatrix(entity.position(), entity.tmp_boxSphere_scale, entity.rotation(), MAT_MODELMAT_TRANS));

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, renderPerferences.getDiffuseMap().textureID());
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, renderPerferences.getSpecularMap().textureID());

            glDrawElements(GL_TRIANGLES, model.vertexCount(), GL_UNSIGNED_INT, 0);
        }
        glBindVertexArray(0);
    }

    public void renderCompose(Framebuffer gfbo, List<Light> lights) {

        shaderCompose.useProgram();

        shaderCompose.setVector3f("CameraPos", Outskirts.getCamera().getPosition());

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
