#version 330 core

out vec4 FragColor;

in vec2 TexCoord;
in vec3 FragPos;

uniform sampler2D diffuseMap;

void main() {

    float heightGray = 0.65f + FragPos.y / 48.0f;

    FragColor = texture(diffuseMap, TexCoord) * heightGray;
}