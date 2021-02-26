#version 330 core
layout (location = 0) out vec4 gPositionDepth;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpecular;

in vec3 FragPos;
in vec3 vNorm;
in vec2 vTexCoord;
in vec3 TriWeight;
flat in vec3 TriMtlId;
in mat3 TBN;

uniform sampler2D diffuseMap;      // rgba
uniform sampler2D specularMap;     // r
uniform sampler2D normalMap;       // rgb
uniform sampler2D displacementMap; // r

uniform vec4 mtlfrags[256];

const float P_NEAR = 0.1f;
const float P_FAR  = 1000.0f;


float inverseLerp(float, float, float);
float lineardepth(float);
float mod(float, float);
vec2 modv2u(vec2);  // MOD vec2 L1-'united'. (just numrical MOD to 0.0-1.0). for working with texture-atlas.

vec3 texture_triplanar(sampler2D tex, vec4 txfrag, vec2[3] p_uv, vec3 pweight) {
    return texture(tex, txfrag.xy +p_uv[0]*txfrag.zw).rgb * pweight.x +
           texture(tex, txfrag.xy +p_uv[1]*txfrag.zw).rgb * pweight.y +
           texture(tex, txfrag.xy +p_uv[2]*txfrag.zw).rgb * pweight.z;
}

vec3 UnpackTNorm(vec2 uv) {
    return normalize(texture(normalMap, uv).rgb * 2.0 - 1.0);
}

void main() {

//    if (texture(mtlDiffuseMap, vTexCoord).a == 0) {
//        discard;
//    }

    gPositionDepth.xyz = FragPos;
    gPositionDepth.w = lineardepth(gl_FragCoord.z);

    gAlbedoSpecular.a = texture(specularMap, vTexCoord).r;


    vec3 FragNorm = vNorm;

    // Triplanar Planar UVs. Shared.
    vec2 p_uv[3];
    p_uv[0] = modv2u(vec2(-FragPos.z, FragPos.y));
    p_uv[1] = modv2u(vec2(FragPos.x, -FragPos.z));
    p_uv[2] = modv2u(FragPos.xy);
    vec3 p_weight = abs(FragNorm);  p_weight /= p_weight.x + p_weight.y + p_weight.z;

    vec4 vfrag[3];
    vfrag[0] = mtlfrags[int(TriMtlId.x)];
    vfrag[1] = mtlfrags[int(TriMtlId.y)];
    vfrag[2] = mtlfrags[int(TriMtlId.z)];

    float h_v0 = texture_triplanar(displacementMap, vfrag[0], p_uv, p_weight).r * TriWeight.x;
    float h_v1 = texture_triplanar(displacementMap, vfrag[1], p_uv, p_weight).r * TriWeight.y;
    float h_v2 = texture_triplanar(displacementMap, vfrag[2], p_uv, p_weight).r * TriWeight.z;
    int h_i = h_v0 > h_v1 ? (h_v0 > h_v2 ? 0 : 2) : (h_v1 > h_v2 ? 1 : 2);

    if (TBN != mat3(0)) { // UV Mesh.
        if (texture(normalMap, vec2(0)).a != 0) {  // valid normalMap.
            FragNorm = TBN * UnpackTNorm(vTexCoord);
        }
    } else {
        // Triplanar Normal Mapping. useof Whiteout Blend. based on UDN-blend.
        // there may have axis dir problem.
        vec3 tnormX = UnpackTNorm(vfrag[h_i].xy +p_uv[0]*vfrag[h_i].zw);
        vec3 tnormY = UnpackTNorm(vfrag[h_i].xy +p_uv[1]*vfrag[h_i].zw);
        vec3 tnormZ = UnpackTNorm(vfrag[h_i].xy +p_uv[2]*vfrag[h_i].zw);

        tnormX = vec3(tnormX.xy + vec2(vNorm.z, vNorm.y), abs(tnormX.z) * vNorm.x);
        tnormY = vec3(tnormY.xy + vec2(vNorm.x, vNorm.z), abs(tnormY.z) * vNorm.y);
        tnormZ = vec3(tnormZ.xy + vNorm.xy, abs(tnormZ.z) * vNorm.z);

        FragNorm = normalize(
            vec3(tnormX.zy, tnormX.x) * p_weight.x +  // when 'rotate' tangent-norm to axis X.
            vec3(tnormY.xz, tnormY.y) * p_weight.y +
            vec3(tnormZ.xyz)          * p_weight.z
        );
    }


    gNormal = FragNorm;

    if (TBN != mat3(0)) {
        gAlbedoSpecular.rgb = texture(diffuseMap, vTexCoord).rgb;
    } else {

//        gAlbedoSpecular.rgb = texture_triplanar(v1frag, p_uv, p_weight) * TriWeight.x +
//                              texture_triplanar(v2frag, p_uv, p_weight) * TriWeight.y +
//                              texture_triplanar(v3frag, p_uv, p_weight) * TriWeight.z;

        gAlbedoSpecular.rgb = texture_triplanar(diffuseMap, vfrag[h_i], p_uv, p_weight);




    }

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