#version 330 core

in vec2 TexCoord;

out vec4 FragColor;

const int NUM_SAMPLES = 64;
uniform vec3 KERNEL_SAMPLES[NUM_SAMPLES];
uniform vec2 texRandTanScale;
uniform sampler2D texRandTan;

uniform sampler2D gPositionDepth;
uniform sampler2D gNormal;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

const float P_NEAR = 0.1f;
const float P_FAR  = 1000.0f;
float inverseLerp(float t, float start, float end) {
    return (t - start) / (end - start);
}
float lineardepth(float pprojdepth) {  // for perspective projection
    float z = pprojdepth * 2.0 - 1.0; // back to NDC
    float pDepth = (2.0 * P_NEAR * P_FAR) / (P_FAR + P_NEAR - z * (P_FAR - P_NEAR)); // [near, far]
    return inverseLerp(pDepth, P_NEAR, P_FAR); // [0,1] linear.
}
bool isTexCoordOutOfBound(vec2 tc) {
    return tc.x < 0.0 || tc.x > 1.0 || tc.y < 0.0 || tc.y > 1.0;
}

void main() {

    if (texture(gPositionDepth, TexCoord).w == 1.0) {  // discard the non-rendered areas.
        discard;
    }

    vec3 FragNorm = texture(gNormal, TexCoord).xyz;
    vec3 FragPos  = texture(gPositionDepth, TexCoord).xyz;

    vec3 tangentSrc = texture(texRandTan, TexCoord * texRandTanScale).xyz;
    tangentSrc.xy = tangentSrc.xy * 2.0 - 1.0;
//    tangentSrc = vec3(1,0,0);
    vec3 othoTangent = normalize(tangentSrc - (FragNorm * dot(FragNorm, tangentSrc)));
    vec3 biTangent = cross(FragNorm, othoTangent);
    mat3 TBN = mat3(othoTangent, biTangent, FragNorm);

    vec4 lastProjSample;
    float occlusion = 0;
    for (int i = 0;i < NUM_SAMPLES;i++) {
        float radius = 1;
        vec3 wSample = FragPos + (TBN * KERNEL_SAMPLES[i]) * radius;
        vec4 projSample = projectionMatrix * viewMatrix * vec4(wSample, 1.0);
        projSample.xyz /= projSample.w;
        projSample.xyz = projSample.xyz * 0.5f + 0.5f;  // the Z!?
        lastProjSample = projSample;

        float samplepDepth = lineardepth(projSample.z);
        float surfaceDepth = texture(gPositionDepth, projSample.xy).w;
        if (isTexCoordOutOfBound(projSample.xy)) {  // fix screen-border when tex-wrap is not CLAMP_TO_BORDER.
            surfaceDepth = 1;
        }

        float rangef = smoothstep(0.0, 1.0, radius / abs(samplepDepth - surfaceDepth));
        occlusion += (surfaceDepth < samplepDepth ? 1.0 : 0.0);// * rangef;
    }
    occlusion /= NUM_SAMPLES;

    FragColor = vec4(vec3(1.0 - occlusion), 1.0);

//    FragColor = vec4(FragPos, 1.0);
//    FragColor = vec4(vec3(lineardepth(lastProjSample.z)), 1.0);
//    FragColor = lastProjSample.z <= 0 ? vec4(1) : vec4(0);
}