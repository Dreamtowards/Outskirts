#version 330 core
layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_texCoord;
layout (location = 2) in vec3 in_normal;
layout (location = 3) in vec3 in_tangent;
layout (location = 4) in vec3 in_acctri_brcd;   // baraycentric coord of tri 3vert.
layout (location = 5) in vec3 in_acctri_mtid;   // material Id of tri 3vert.

out VS_OUT {
    vec3 FragPos;
    vec3 vNorm;
    vec2 vTexCoord;
} vs_out;

out vec3 AccTri_BrCd;
flat out vec3 AccTri_MtId;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {

    vec4 worldposition = modelMatrix * vec4(in_position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldposition;
    vs_out.FragPos = worldposition.xyz;

    vec3 N = normalize(vec3(modelMatrix * vec4(in_normal, 0.0)));
    vs_out.vNorm = N;

    vs_out.vTexCoord = in_texCoord;

    AccTri_BrCd = in_acctri_brcd;
    AccTri_MtId = in_acctri_mtid;
}
