#version 330 core
#define SHADOW_MAP_BIAS 0.8

out vec4 FragColor;

in vec2 QuadTexCoord;

uniform sampler2D gPositionDepth;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpecular;

uniform vec3 CameraPos;

struct Light {
    vec3 color;
    vec3 position;
    vec3 attenuation;
    vec3 direction;          // for Spot-Light.
    float coneAngleInnerCos; // cos val. [-1, 1]
    float coneAngleOuterCos;
};
uniform Light lights[64];
uniform int lightCount;

uniform float mtlShininess = 32;
uniform float mtlSpecularStrength;

uniform mat4 shadowspaceMatrix;
uniform sampler2D shadowdepthMap;

uniform sampler2D ssaoBlurMap;

float inverseLerp(float, float, float);

mat3 computeLighting(vec3, vec3, vec3);

float computeShadow(vec3 FragPos);

void main() {
    if (texture(gPositionDepth, QuadTexCoord).a == 1) {
        discard;
    }

    vec3 FragPos = texture(gPositionDepth, QuadTexCoord).xyz;
    vec3 Normal = texture(gNormal, QuadTexCoord).xyz;
    vec3 Albedo = texture(gAlbedoSpecular, QuadTexCoord).rgb;
    float Specularf = texture(gAlbedoSpecular, QuadTexCoord).a;

    vec3 fragToCamera = normalize(CameraPos - FragPos);

    mat3 ltRst = computeLighting(FragPos, Normal, fragToCamera);
    vec3 totalDiffuse = ltRst[0];
    vec3 totalSpecular = ltRst[1];

    float shadow = max(1.0 - computeShadow(FragPos), 0.3);
    totalDiffuse *= shadow;
    totalSpecular *= shadow;

    FragColor.rgb = totalDiffuse  * Albedo +
                    totalSpecular * Specularf;

    FragColor.a = 1.0;
}


mat3 computeLighting(vec3 FragPos, vec3 Normal, vec3 fragToCamera) {
    float occlusionf = texture(ssaoBlurMap, QuadTexCoord).r;

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    for (int i = 0;i < lightCount;i++) {
        Light light = lights[i];

        vec3 ambient = light.color * 0.3 * occlusionf;

        // Diffuse
        vec3 fragToLight = normalize(light.position - FragPos);
        vec3 diffuse = max(dot(Normal, fragToLight), 0.0) * light.color;

        // Specular. Blinn-Phong Model
        vec3 halfwayDir = normalize(fragToLight + fragToCamera);
        float spec = pow(max(dot(halfwayDir, Normal),0.0), mtlShininess);
        vec3 specular = mtlSpecularStrength * spec * light.color;

        // Attenuation
        float lightDistance = length(light.position - FragPos);
        float attenuation = 1.0 / (light.attenuation.x +
                                   light.attenuation.y *lightDistance +
                                   light.attenuation.z *lightDistance*lightDistance);
        // SpotLight
        float spotStrength = 1;
        if (light.coneAngleInnerCos != light.coneAngleOuterCos) { // enable SpotLight
            float fragCos = dot(light.direction, -fragToLight);   // [-1, 1]
            spotStrength = clamp(inverseLerp(fragCos, light.coneAngleOuterCos, light.coneAngleInnerCos), 0.0, 1.0);
        }

        totalDiffuse  += diffuse  * attenuation * spotStrength + ambient;
        totalSpecular += specular * attenuation * spotStrength;
    }
    return mat3(totalDiffuse, totalSpecular, vec3(0.0));
}


bool isTexCoordOutOfBound(vec2 tc) {
    return tc.x < 0.0 || tc.x > 1.0 || tc.y < 0.0 || tc.y > 1.0;
}
float computeShadow(vec3 FragPos) {  // 1.0: full-shadow, 0.0: none-shadow
    vec4 spFragCoord = shadowspaceMatrix * vec4(FragPos, 1.0);       // ProjectionPosition of FragPos on Shadowspace
    spFragCoord.xyz /= spFragCoord.w;  // [-1, 1]. perspective devidation.
    // align the DomeProjection
    float dist = length(spFragCoord.xy);
    float distortFactor = (1.0 - SHADOW_MAP_BIAS) + dist * SHADOW_MAP_BIAS;
    spFragCoord.xy /= distortFactor;

    spFragCoord.xyz  = spFragCoord.xyz * 0.5f + 0.5f;  // [0, 1]
    if (isTexCoordOutOfBound(spFragCoord.xy)) return 0.0;
    float fragDepth = spFragCoord.z;  // far > near.
    float shadowFactor = 0;
    vec2 mapSize = textureSize(shadowdepthMap, 0);
    int SAMPLES = 0;  // (x*2+1)^2
    float bias = 0.0001f;  // 0.0003f;
    for (int x = -SAMPLES;x <= SAMPLES;x++) {
        for (int y = -SAMPLES;y <= SAMPLES;y++) {
            float shadowmapDepth = texture(shadowdepthMap, spFragCoord.xy + vec2(x,y)/mapSize).r;  // in shadowspace
            shadowFactor += fragDepth - bias > shadowmapDepth ? 1.0 : 0.0;
        }
    }
    return shadowFactor / ((SAMPLES*2.0f+1.0f)*(SAMPLES*2.0f+1.0f));
}



float inverseLerp(float t, float start, float end) {
    return (t - start) / (end - start);
}