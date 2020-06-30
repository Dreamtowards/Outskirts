#version 330 core

const int MAX_JOINTS = 50;

layout (location = 0) in vec3 positions;
layout (location = 1) in vec2 textureCoords;
layout (location = 2) in vec3 normals;
layout (location = 3) in vec3 jointIdx;
layout (location = 4) in vec3 jointWeights;

out vec2 TexCoord;
out vec3 SurfaceNormal;

uniform mat4 jointTransforms[MAX_JOINTS];
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {

    vec4 modelPosition = vec4(0.0f);
    vec4 modelNormal = vec4(0.0f);

    for (int i = 0;i < 3;i++) {
        mat4 jointTransMat = jointTransforms[int(jointIdx[i])];

        vec4 jointTransPos = jointTransMat * vec4(positions, 1.0);
        modelPosition += jointTransPos * jointWeights[i];

        vec4 jointTransNorm = jointTransMat * vec4(normals, 0.0);
        modelNormal += jointTransNorm * jointWeights[i];
    }
//    modelPosition = vec4(modelPosition.x*5,modelPosition.y*5,modelPosition.z*5, modelPosition.w);

    gl_Position = projectionMatrix * viewMatrix * modelPosition;

    TexCoord = textureCoords;
    SurfaceNormal = normalize(modelNormal.xyz);
}