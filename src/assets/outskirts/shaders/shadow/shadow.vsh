#version 330 core
#define SHADOW_MAP_BIAS 0.8
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoord;

out vec2 TexCoord;

uniform mat4 lightspaceMatrix;
uniform mat4 modelMatrix;

void main() {

    gl_Position = lightspaceMatrix * modelMatrix * vec4(position, 1.0f);
    TexCoord = textureCoord;

    float dist = length(gl_Position.xy);
    float distortFactor = (1.0 - SHADOW_MAP_BIAS) + dist * SHADOW_MAP_BIAS;
    gl_Position.xy /= distortFactor;
}