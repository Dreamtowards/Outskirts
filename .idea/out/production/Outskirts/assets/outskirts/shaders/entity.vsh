#version 330 core
layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_texCoords;
layout (location = 2) in vec3 in_normals;
layout (location = 3) in vec3 in_tangents;

out vec2 OriginalTexCoord;
out vec3 SurfaceNormal;
out vec3 FragPos;
out mat3 invTBN;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform mat4 shadowspaceMatrix;
out vec4 shadowspaceFragCoord;  // [-1.0, 1.0] ProjectionSpace. a point in lightspace, which corresponding this vertex/frag pos.

void main() {

    vec4 worldposition = modelMatrix * vec4(in_position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldposition;

    FragPos = worldposition.xyz;

    shadowspaceFragCoord = shadowspaceMatrix * vec4(FragPos, 1.0);

    vec3 N = normalize(vec3(modelMatrix * vec4(in_normals,  0.0f)));  // FragNormal
    vec3 T = normalize(vec3(modelMatrix * vec4(in_tangents, 0.0f)));
    vec3 B = normalize(cross(N, T));

    invTBN = transpose(mat3(
        T.x, B.x, N.x,
        T.y, B.y, N.y,
        T.z, B.z, N.z
    ));

    SurfaceNormal = N;

    OriginalTexCoord = in_texCoords;
}