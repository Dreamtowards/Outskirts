#version 330 core
layout (location = 0) out vec4 gPositionDepth;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpecular;

in vec3 FragPos;
in vec3 vNorm;
in vec2 vTexCoord;
in vec3 TriWeight;
flat in vec3 TriMtlId;

uniform sampler2D mtlDiffuseMap;
uniform sampler2D mtlSpecularMap;

uniform vec4 mtlfrags[256];

const float P_NEAR = 0.1f;
const float P_FAR  = 1000.0f;

float inverseLerp(float, float, float);
float lineardepth(float);
float mod(float, float);
vec2 modv2u(vec2);

vec3 tripSample(vec4 txfrag, vec2[3] uvPlanar, vec3 pweight) {
    return texture(mtlDiffuseMap, txfrag.xy+uvPlanar[0]*txfrag.zw).rgb * pweight.x +
           texture(mtlDiffuseMap, txfrag.xy+uvPlanar[1]*txfrag.zw).rgb * pweight.y +
           texture(mtlDiffuseMap, txfrag.xy+uvPlanar[2]*txfrag.zw).rgb * pweight.z;
}

void main() {

//    if (texture(mtlDiffuseMap, vTexCoord).a == 0) {
//        discard;
//    }

    gPositionDepth.xyz = FragPos;
    gPositionDepth.w = lineardepth(gl_FragCoord.z);

    gNormal = vNorm;

//    gAlbedoSpecular.rgb = texture(mtlDiffuseMap, vTexCoord).rgb;
    gAlbedoSpecular.a = texture(mtlSpecularMap, vTexCoord).r;

    vec4 v1txfrag = mtlfrags[int(TriMtlId.x)];
    vec4 v2txfrag = mtlfrags[int(TriMtlId.y)];
    vec4 v3txfrag = mtlfrags[int(TriMtlId.z)];

    vec2 uvPlanar[3];
    uvPlanar[0] = modv2u(vec2(1.0f-FragPos.z, FragPos.y));
    uvPlanar[1] = modv2u(vec2(FragPos.x, 1.0f-FragPos.z));
    uvPlanar[2] = modv2u(FragPos.xy);
    vec3 pweight = abs(vNorm);

    pweight /= pweight.x + pweight.y + pweight.z;
//    gAlbedoSpecular.rgb =
//            texture(mtlDiffuseMap, uvPlanarX).rgb * pweight.x +
//            texture(mtlDiffuseMap, uvPlanarY).rgb * pweight.y +
//            texture(mtlDiffuseMap, uvPlanarZ).rgb * pweight.z;
    gAlbedoSpecular.rgb = tripSample(v1txfrag, uvPlanar, pweight) * TriWeight.x +
                          tripSample(v2txfrag, uvPlanar, pweight) * TriWeight.y +
                          tripSample(v3txfrag, uvPlanar, pweight) * TriWeight.z;


//    int i = pweight.x > pweight.y ? (pweight.x > pweight.z ? 0 : 2) : (pweight.y > pweight.z ? 1 : 2);
//    gAlbedoSpecular.rgb =
//            i == 0 ? texture(mtlDiffuseMap, uvPlanarX).rgb :
//            i == 1 ? texture(mtlDiffuseMap, uvPlanarY).rgb :
//                     texture(mtlDiffuseMap, uvPlanarZ).rgb;
//    gAlbedoSpecular.rgb =
//            i == 0 ? texture(mtlDiffuseMap, v1txfrag.xy+uvPlanarX*v1txfrag.zw).rgb :
//            i == 1 ? texture(mtlDiffuseMap, v1txfrag.xy+uvPlanarY*v1txfrag.zw).rgb :
//                     texture(mtlDiffuseMap, v1txfrag.xy+uvPlanarZ*v1txfrag.zw).rgb;
}

float mod(float v, float b) {
    return v - (floor(v / b) * b);
}
vec2 modv2u(vec2 v) {
    return vec2(mod(v.x, 1.0f), mod(v.y, 1.0f));
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