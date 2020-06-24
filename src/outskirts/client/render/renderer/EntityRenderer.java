package outskirts.client.render.renderer;

import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.material.Material;
import outskirts.client.material.Model;
import outskirts.client.render.Camera;
import outskirts.client.render.Illuminable;
import outskirts.client.render.Light;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.entity.Entity;
import outskirts.init.Textures;
import outskirts.util.Colors;
import outskirts.util.Maths;
import outskirts.util.ResourceLocation;
import outskirts.util.logging.Log;
import outskirts.util.vector.Matrix4f;
import outskirts.util.vector.Vector3f;
import outskirts.util.vector.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * EntityRenderer. a high-level renderer, for Entity
 */
public class EntityRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new ResourceLocation("shaders/entity.vsh").getInputStream(),
            new ResourceLocation("shaders/entity.fsh").getInputStream()
    );

    public EntityRenderer() {
        //init Texture-Units
        shader.useProgram();
        shader.setInt("material.diffuseSampler", 0);
        shader.setInt("material.specularSampler", 1);
        shader.setInt("material.emissionSampler", 2);
        shader.setInt("material.normalSampler", 3);
        shader.setInt("material.displacementSampler", 4);

        shader.setInt("environmentSampler", 5);
        shader.setInt("shadowdepthmapSampler", 6);
    }

    private static final int RENDER_LIGHTS = 64;
    private String[] uniform_lights$color = createUniformNameArray("lights[%s].color", RENDER_LIGHTS);
    private String[] uniform_lights$position = createUniformNameArray("lights[%s].position", RENDER_LIGHTS);
    private String[] uniform_lights$attenuation = createUniformNameArray("lights[%s].attenuation", RENDER_LIGHTS);
    private String[] uniform_lights$spotDirection = createUniformNameArray("lights[%s].spotDirection", RENDER_LIGHTS);
    private String[] uniform_lights$coneAngleInner = createUniformNameArray("lights[%s].coneAngleInnerCos", RENDER_LIGHTS);
    private String[] uniform_lights$coneAngleOuter = createUniformNameArray("lights[%s].coneAngleOuterCos", RENDER_LIGHTS);

    private Matrix4f MAT_MODELMAT_TRANS = new Matrix4f();

    public void render(List<Entity> entities, List<Light> lights) {

        shader.useProgram();

        shader.setMatrix4f("projectionMatrix", Outskirts.renderEngine.getProjectionMatrix());
        shader.setMatrix4f("viewMatrix", Outskirts.renderEngine.getViewMatrix());

        shader.setVector3f("cameraPosition", Outskirts.getCamera().getPosition());

        {   // setup lights
            int lightCount = Math.min(lights.size(), RENDER_LIGHTS);
            shader.setInt("lightCount", lightCount);

            for (int i = 0;i < lightCount;i++) {
                Light light = lights.get(i);
                shader.setVector3f(uniform_lights$color[i], light.getColor());
                shader.setVector3f(uniform_lights$position[i], light.getPosition());
                shader.setVector3f(uniform_lights$attenuation[i], light.getAttenuation());
                shader.setVector3f(uniform_lights$spotDirection[i], light.getDirection());

                shader.setFloat(uniform_lights$coneAngleInner[i], (float)Math.cos(light.getConeAngleInner()));
                shader.setFloat(uniform_lights$coneAngleOuter[i], (float)Math.cos(light.getConeAngleOuter()));
            }
        }
        glActiveTexture(GL_TEXTURE5);
        glBindTexture(GL_TEXTURE_CUBE_MAP, Outskirts.renderEngine.getSkyboxRenderer().textureCubemap.textureID());

        glActiveTexture(GL_TEXTURE6);
        glBindTexture(GL_TEXTURE_2D, Outskirts.renderEngine.getShadowRenderer().getDepthMapTexture().textureID());
        shader.setMatrix4f("shadowspaceMatrix", Outskirts.renderEngine.getShadowRenderer().getShadowspaceMatrix());

        for (Entity entity : entities) {
            if (entity == Outskirts.getCamera().getCameraUpdater().getOwnerEntity() && Outskirts.getCamera().getCameraUpdater().getCameraDistance() == 0)
                continue;

            Material material = entity.getMaterial();
            Model model = material.getModel();

            glBindVertexArray(model.vaoID());

            shader.setFloat("material.specularStrength", material.getSpecularStrength());
            shader.setFloat("material.shininess", material.getShininess());

            shader.setFloat("material.displacementScale", material.getDisplacementScale());

            shader.setMatrix4f("modelMatrix", Maths.createModelMatrix(entity.getPosition(), entity.tmp_boxSphere_scale, entity.getRotation(), MAT_MODELMAT_TRANS));

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, material.getDiffuseMap().textureID());
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, material.getSpecularMap().textureID());
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, material.getEmissionMap().textureID());
            glActiveTexture(GL_TEXTURE3);
            glBindTexture(GL_TEXTURE_2D, material.getNormalMap().textureID());
            glActiveTexture(GL_TEXTURE4);
            glBindTexture(GL_TEXTURE_2D, material.getDisplacementMap().textureID());

            glDrawElements(GL_TRIANGLES, model.vertexCount(), GL_UNSIGNED_INT, 0);
        }

        glBindVertexArray(0);
    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }
}
