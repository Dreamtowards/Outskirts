#version 330 core

struct Light {
    vec3 color;
    vec3 position;
    vec3 attenuation;
    vec3 spotDirection;
    float coneAngleInnerCos; // angle cos
    float coneAngleOuterCos;
};
struct Material {
    sampler2D diffuseSampler; // Diffuse Map Sampler. Surface Texture.
    sampler2D specularSampler;
    sampler2D emissionSampler;
    sampler2D normalSampler;
    sampler2D displacementSampler;
    // Diffuse, Specular, Normal, Bump, Displace

    float specularStrength;  // 0.5;
    float shininess;         // 32;

    float displacementScale; // 1.0;
};

in vec2 OriginalTexCoord;
in vec3 SurfaceNormal;
in vec3 FragPos;
in mat3 invTBN;

in vec4 shadowspaceFragCoord;
uniform sampler2D shadowdepthmapSampler;

out vec4 FragColor;

uniform vec3 cameraPosition;

uniform Material material;

uniform Light lights[64];
uniform int lightCount;

uniform samplerCube environmentSampler;

const float GAMMA = 0.01;  // GAMMA
const float P_NEAR = 0.1f;
const float P_FAR  = 1000.0f;

float inverseLerp(float t, float start, float end) {
    return (t - start) / (end - start);
}
bool isTexCoordOutOfBound(vec2 tc) {
    return tc.x < 0.0 || tc.x > 1.0 || tc.y < 0.0 || tc.y > 1.0;
}

float calculateShadowFactor();

mat3 computeLighting(vec3, vec3);

vec3 calculateNormal(vec2);

// Steep Parallax Mapping. + Parallax Occlusion Mapping.
vec2 calculateParallaxMapping(vec3 fragToCamera) {
//    if (texture(material.displacementSampler, OriginalTexCoord).r == 0)  // quick exit.
//        return OriginalTexCoord;
    vec3 tangentFragToCamera = (transpose(invTBN) * fragToCamera);  // TangentSpace.

    const float numLayers = 10;                // number of depth layer
    const float layerDepth = 1.0 / numLayers;

    float height = texture(material.displacementSampler, OriginalTexCoord).r;

    vec2 P = tangentFragToCamera.xy * material.displacementScale;  // the (max) TextureCoordinate Offset
    vec2 deltaP = P / numLayers;

    // Steep Parallax Mapping. uses "Almost Legal" Fixed-Layer-Depth.
    int currentLayer = 0;
    vec2 currentLayerTexCoord = OriginalTexCoord;  // current offseted TexCoord

    while (currentLayer*layerDepth < texture(material.displacementSampler, currentLayerTexCoord).r) {
        currentLayerTexCoord -= deltaP;
        currentLayer++;
    }

    // Parallax Occilision Mapping. lerp currentLayer with prevLayer.
    vec2 prevLayerTexCoord = currentLayerTexCoord + deltaP;

    // abs_diff of {curr/prev}LayerFixedDepth and {curr/prev}LayerActuallyDepthMapValue
    float currDepdV  = currentLayer*layerDepth - texture(material.displacementSampler, currentLayerTexCoord).r;
    float prevDepdV =                            texture(material.displacementSampler,    prevLayerTexCoord).r - (currentLayer-1)*layerDepth;
    float t = currDepdV / (prevDepdV + currDepdV);

    vec2 lerpedTexCoord = currentLayerTexCoord + (prevLayerTexCoord-currentLayerTexCoord)*t;

    if (isTexCoordOutOfBound(lerpedTexCoord))
        discard;

    return lerpedTexCoord;
}

void main() {

    vec3 fragToCamera = normalize(cameraPosition - FragPos);

    // Parallax Mapping
    vec2 TexCoord = calculateParallaxMapping(fragToCamera);

    // Normal
    vec3 FragNormal = calculateNormal(TexCoord);

    // Lighting
    mat3 ltRst = computeLighting(FragNormal, fragToCamera);
    vec3 totalDiffuse  = ltRst[0];
    vec3 totalSpecular = ltRst[1];

    // Shadow factoring
    float shadowFactor = max(1.0 - (calculateShadowFactor() * 0.65f), 0.3);
    totalDiffuse *= shadowFactor;
    totalSpecular *= shadowFactor;

    FragColor = vec4(totalDiffuse, 1.0)  * texture(material.diffuseSampler, TexCoord)  +
                vec4(totalSpecular, 1.0) * texture(material.specularSampler, TexCoord) +
                                           texture(material.emissionSampler, TexCoord);

    FragColor.a = min(FragColor.a, 1.0);
//    FragColor.rgb = vec3(FragColor.a);
//    FragColor*=100;
//    vec3 cameraReflection = reflect(-fragToCamera, surfaceNormal);
//    vec3 cameraRefraction = refract(-fragToCamera, surfaceNormal, 1.00 / 1.3);
//    FragColor = texture(environmentSampler, cameraRefraction);// * texture(material.specularSampler, textureCoords);


}

vec3 calculateNormal(vec2 TexCoord) {
    vec4 normMapColor = texture(material.normalSampler, TexCoord);  // read NormalMap.
    if (normMapColor.a != 0) {  // had NormalMap Data.
        return invTBN * normalize(normMapColor.rgb * 2.0f - 1.0f);
    }
    return SurfaceNormal;
}

float calculateShadowFactor() { // 1.0: full-shadow, 0.0: none-shadow
    vec3 shadowFragCoord = shadowspaceFragCoord.xyz / shadowspaceFragCoord.w; // [-1, 1]. perspective devidation.
         shadowFragCoord = shadowFragCoord * 0.5f + 0.5f; // [0, 1]
    if (isTexCoordOutOfBound(shadowFragCoord.xy))
        return 0.0;
    float fragDepth = shadowFragCoord.z;  // far > near.
    float shadowFactor = 0;
    vec2 mapSize = textureSize(shadowdepthmapSampler, 0);
    int SAMPLES = 1;  // (x*2+1)^2
    float bias = 0.0002f;  // 0.0003f;
    for (int x = -SAMPLES;x <= SAMPLES;x++) {
        for (int y = -SAMPLES;y <= SAMPLES;y++) {
            float pcfClosestDepth = texture(shadowdepthmapSampler, shadowFragCoord.xy + vec2(x,y)/mapSize).r;  // in shadowspace
            shadowFactor += fragDepth - bias > pcfClosestDepth ? 1.0 : 0.0;
        }
    }
    return shadowFactor / ((SAMPLES*2.0f+1.0f)*SAMPLES*2.0f+1.0f);
}


mat3 computeLighting(vec3 FragNormal, vec3 fragToCamera) {

    // Lighting
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for (int i = 0;i < lightCount;i++) {
        Light light = lights[i];

        vec3 ambient = (light.color * GAMMA) / lightCount;

        // Diffuse
        vec3 fragToLight = normalize(light.position - FragPos); // direcion Frag -> Light
        vec3 diffuse = max(dot(fragToLight, FragNormal), 0.0) * light.color;

        // Specular. Blinn-Phong Model
        vec3 halfwayVec = normalize(fragToLight + fragToCamera);
        float spec = pow(max(dot(halfwayVec, FragNormal),0.0), material.shininess);
        vec3 specular = material.specularStrength * spec * light.color;

        // Attenuation
        float lightDistance = length(light.position - FragPos);
        float attenuation = 1.0 / (light.attenuation.x +
                                   light.attenuation.y *lightDistance +
                                   light.attenuation.z *lightDistance*lightDistance);

        // SpotLight
        float spotStrength = 1; // may should uses "attenuation" ..?
        if (light.coneAngleInnerCos != 3.1415f || light.coneAngleOuterCos != 3.1415f) { // enable SpotLight
            spotStrength = dot(light.spotDirection, -fragToLight);
            spotStrength = clamp(inverseLerp(spotStrength, light.coneAngleOuterCos, light.coneAngleInnerCos), 0.0, 1.0);
        }

        totalDiffuse  += diffuse  * attenuation * spotStrength + ambient;
        totalSpecular += specular * attenuation * spotStrength;
    }

    return mat3(totalDiffuse, totalSpecular, vec3(0));
}








float linearDepth(float pprojdepth) {  // for perspective projection
    float z = pprojdepth * 2.0 - 1.0; // back to NDC
    float pDepth = (2.0 * P_NEAR * P_FAR) / (P_FAR + P_NEAR - z * (P_FAR - P_NEAR)); // [near, far]
    return inverseLerp(pDepth, P_NEAR, P_FAR);
}