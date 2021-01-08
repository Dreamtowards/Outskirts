#version 330 core
layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_texCoord;
layout (location = 2) in vec3 in_normal;
layout (location = 3) in vec3 in_tangent;
layout (location = 4) in vec3 in_acctri_barycd;   // baraycentric coord of tri 3vert.
layout (location = 5) in vec3 in_acctri_blxid;    // block Id of tri 3vert

out vec3 FragPos;
out vec3 vNorm;
out vec2 vTexCoord;
out vec3 ACCTRI_BARYCD;
flat out vec3 ACCTRI_BLXID;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {

    vec4 worldposition = modelMatrix * vec4(in_position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldposition;
    FragPos = worldposition.xyz;


    vec3 N = normalize(vec3(modelMatrix * vec4(in_normal, 0.0)));
    vNorm = N;

    ACCTRI_BARYCD = in_acctri_barycd;
    ACCTRI_BLXID = in_acctri_blxid;

    vTexCoord = in_texCoord;
}
