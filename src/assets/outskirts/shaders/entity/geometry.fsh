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

uniform sampler2D diffuseMap;
uniform sampler2D specularMap;
uniform sampler2D normalMap;

uniform sampler2D mlerpdetMap;

uniform vec4 mtlfrags[256];

const float P_NEAR = 0.1f;
const float P_FAR  = 1000.0f;

float inverseLerp(float, float, float);
float lineardepth(float);
float mod(float, float);
vec2 modv2u(vec2);

vec3 triplanarSample(vec4 txfrag, vec2[3] uvPlanar, vec3 pweight) {
    return texture(diffuseMap, txfrag.xy+uvPlanar[0]*txfrag.zw).rgb * pweight.x +
           texture(diffuseMap, txfrag.xy+uvPlanar[1]*txfrag.zw).rgb * pweight.y +
           texture(diffuseMap, txfrag.xy+uvPlanar[2]*txfrag.zw).rgb * pweight.z;
}

vec3 UnpackTNorm(vec2 uv) {
    return normalize(texture(normalMap, uv).rgb * 2.0 - 1.0);
}

void main() {

//    if (texture(mtlDiffuseMap, vTexCoord).a == 0) {
//        discard;
//    }
    vec3 FragNorm = vNorm;

    // Triplanar Planar UVs. Shared.
    vec2 p_uv[3];
    p_uv[0] = modv2u(vec2(-FragPos.z, FragPos.y));
    p_uv[1] = modv2u(vec2(FragPos.x, -FragPos.z));
    p_uv[2] = modv2u(FragPos.xy);

    vec3 p_weight = abs(FragNorm);  p_weight /= p_weight.x + p_weight.y + p_weight.z;

    if (texture(normalMap, vec2(0)).a != 0) {  // Valid Normal Map.
        if (TBN != mat3(0)) { // UV Mesh.
            FragNorm = TBN * UnpackTNorm(vTexCoord);
        } else {
            // Triplanar Normal Mapping. use of Whiteout Blend. based on UDN-blend.

            vec3 tnormX = UnpackTNorm(p_uv[0]);
            vec3 tnormY = UnpackTNorm(p_uv[1]);
            vec3 tnormZ = UnpackTNorm(p_uv[2]);

            tnormX = vec3(tnormX.xy + vNorm.zy, abs(tnormX.z) * vNorm.x);
            tnormY = vec3(tnormY.xy + vNorm.xz, abs(tnormY.z) * vNorm.y);
            tnormZ = vec3(tnormZ.xy + vNorm.xy, abs(tnormZ.z) * vNorm.z);

            FragNorm = normalize(
                vec3(tnormX.zy, -tnormX.x) * p_weight.x +
                vec3(tnormY.xz, -tnormY.y) * p_weight.y +
                vec3(tnormZ.xyz)           * p_weight.z
            );
        }
    }



    gPositionDepth.xyz = FragPos;
    gPositionDepth.w = lineardepth(gl_FragCoord.z);

    gNormal = FragNorm;

    gAlbedoSpecular.a = texture(specularMap, vTexCoord).r;

    if (TBN != mat3(0)) {
        gAlbedoSpecular.rgb = texture(diffuseMap, vTexCoord).rgb;
    } else {

        vec4 v1frag = mtlfrags[int(TriMtlId.x)];
        vec4 v2frag = mtlfrags[int(TriMtlId.y)];
        vec4 v3frag = mtlfrags[int(TriMtlId.z)];

        gAlbedoSpecular.rgb = triplanarSample(v1frag, p_uv, p_weight) * TriWeight.x +
                              triplanarSample(v2frag, p_uv, p_weight) * TriWeight.y +
                              triplanarSample(v3frag, p_uv, p_weight) * TriWeight.z;


//        float gray = texture(mlerpdetMap, p_uv[1]).r / 3;  // chunky shaped border.
//        if (gray < TriWeight.x) {
//            gAlbedoSpecular.rgb = triplanarSample(v1frag, p_uv, p_weight);
//        } else if (gray < TriWeight.y) {
//            gAlbedoSpecular.rgb = triplanarSample(v2frag, p_uv, p_weight);
//        } else if (gray < TriWeight.z) {
//            gAlbedoSpecular.rgb = triplanarSample(v3frag, p_uv, p_weight);
//        } else {
//            discard;
//        }

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