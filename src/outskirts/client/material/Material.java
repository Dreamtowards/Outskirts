package outskirts.client.material;

public final class Material {

    private Model model;     // the model actualy is not really a part of Material.

    private Texture diffuseMap = Texture.UNIT; // Diffuse Map. the Surface Texture.
    private Texture emissionMap = Texture.ZERO;
    private Texture normalMap = Texture.ZERO;

    private Texture specularMap = Texture.UNIT; // Specular Map
    private float specularStrength = 0.5f;
    private float shininess = 1;

    private Texture displacementMap = Texture.ZERO;  // Parallax Impl. actually is a DepthMap. displacement'll not Convex up, but Concave down.
    private float displacementScale = 0.1f;

    public Model getModel() {
        return model;
    }
    public Material setModel(Model model) {
        this.model = model;
        return this;
    }

    public Texture getDiffuseMap() {
        return diffuseMap;
    }
    public Material setDiffuseMap(Texture diffuseMap) {
        this.diffuseMap = diffuseMap;
        return this;
    }

    public Texture getEmissionMap() {
        return emissionMap;
    }
    public Material setEmissionMap(Texture emissionMap) {
        this.emissionMap = emissionMap;
        return this;
    }

    public Texture getNormalMap() {
        return normalMap;
    }
    public Material setNormalMap(Texture normalMap) {
        this.normalMap = normalMap;
        return this;
    }


    public Texture getSpecularMap() {
        return specularMap;
    }
    public Material setSpecularMap(Texture specularMap) {
        this.specularMap = specularMap;
        return this;
    }
    public float getSpecularStrength() {
        return specularStrength;
    }
    public Material setSpecularStrength(float specularStrength) {
        this.specularStrength = specularStrength;
        return this;
    }
    public float getShininess() {
        return shininess;
    }
    public Material setShininess(float shininess) {
        this.shininess = shininess;
        return this;
    }

    public Texture getDisplacementMap() {
        return displacementMap;
    }
    public Material setDisplacementMap(Texture displacementMap) {
        this.displacementMap = displacementMap;
        return this;
    }
    public float getDisplacementScale() {
        return displacementScale;
    }
    public Material setDisplacementScale(float displacementScale) {
        this.displacementScale = displacementScale;
        return this;
    }

    public final Material set(Material m) {
        setModel(m.getModel());

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
