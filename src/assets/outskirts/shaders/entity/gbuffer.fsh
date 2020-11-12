#version 330 core
layout (location = 0) out vec4 gPositionDepth;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpecular;

in vec3 FragPos;
in vec3 vNorm;
in vec2 vTexCoord;

uniform sampler2D mtlDiffuseMap;
uniform sampler2D mtlSpecularMap;

const float P_NEAR = 0.1f;
const float P_FAR  = 1000.0f;

float inverseLerp(float, float, float);
float lineardepth(float);

void main() {

    if (texture(mtlDiffuseMap, vTexCoord).a == 0) {
        discard;
    }

    gPositionDepth.xyz = FragPos;
    gPositionDepth.w = lineardepth(gl_FragCoord.z);

    gNormal = vNorm;

    gAlbedoSpecular.rgb = texture(mtlDiffuseMap, vTexCoord).rgb;
    gAlbedoSpecular.a = texture(mtlSpecularMap, vTexCoord).r;
}


float inverseLerp(float t, float start, float end) {
    return (t - start) / (end - start);
}
float lineardepth(float pprojdepth) {  // for perspective projection
    float z = pprojdepth * 2.0 - 1.0; // back to NDC
    float pDepth = (2.0 * P_NEAR * P_FAR) / (P_FAR + P_NEAR - z * (P_FAR - P_NEAR)); // [near, far]
    return inverseLerp(pDepth, P_NEAR, P_FAR); // [0,1] linear.
}
//float LinearizeDepth(float depth) {
//    float z = depth * 2.0 - 1.0; // Back to NDC
//    return (2.0 * NEAR * FAR) / (FAR + NEAR - z * (FAR - NEAR));
//}