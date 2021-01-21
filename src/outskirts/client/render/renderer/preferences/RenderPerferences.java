package outskirts.client.render.renderer.preferences;

import outskirts.client.render.Texture;

//todo: problem: Texture/Model should like Vector/mat that 100% reuseable.? or just Unmodifiable...
public final class RenderPerferences {

    private Texture diffuseMap = Texture.UNIT; // Diffuse Map. the Surface Texture.
    private Texture emissionMap = Texture.ZERO;
    private Texture normalMap = Texture.ZERO;

    private Texture specularMap = Texture.UNIT; // Specular Map
    private float specularStrength = 0f;
    private float shininess = 1;

    private Texture displacementMap = Texture.ZERO;  // Parallax Impl. actually is a DepthMap. displacement'll not Convex up, but Concave down.
    private float displacementScale = 0.1f;

    public Texture getDiffuseMap() {
        return diffuseMap;
    }
    public RenderPerferences setDiffuseMap(Texture diffuseMap) {
        this.diffuseMap = diffuseMap;
        return this;
    }

    public Texture getEmissionMap() {
        return emissionMap;
    }
    public RenderPerferences setEmissionMap(Texture emissionMap) {
        this.emissionMap = emissionMap;
        return this;
    }

    public Texture getNormalMap() {
        return normalMap;
    }
    public RenderPerferences setNormalMap(Texture normalMap) {
        this.normalMap = normalMap;
        return this;
    }


    public Texture getSpecularMap() {
        return specularMap;
    }
    public RenderPerferences setSpecularMap(Texture specularMap) {
        this.specularMap = specularMap;
        return this;
    }
    public float getSpecularStrength() {
        return specularStrength;
    }
    public RenderPerferences setSpecularStrength(float specularStrength) {
        this.specularStrength = specularStrength;
        return this;
    }
    public float getShininess() {
        return shininess;
    }
    public RenderPerferences setShininess(float shininess) {
        this.shininess = shininess;
        return this;
    }

    public Texture getDisplacementMap() {
        return displacementMap;
    }
    public RenderPerferences setDisplacementMap(Texture displacementMap) {
        this.displacementMap = displacementMap;
        return this;
    }
    public float getDisplacementScale() {
        return displacementScale;
    }
    public RenderPerferences setDisplacementScale(float displacementScale) {
        this.displacementScale = displacementScale;
        return this;
    }

    public final RenderPerferences set(RenderPerferences m) {

        setDiffuseMap(m.getDiffuseMap());
        setEmissionMap(m.getEmissionMap());
        setNormalMap(m.getNormalMap());

        setSpecularStrength(m.getSpecularStrength());
        setShininess(m.getShininess());
        setSpecularMap(m.getSpecularMap());

        setDisplacementMap(m.getDisplacementMap());
        setDisplacementScale(m.getDisplacementScale());

        return this;
    }
}
