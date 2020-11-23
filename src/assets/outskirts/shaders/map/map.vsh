#version 330 core
layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_textureCoord;

out vec2 TexCoord;
out vec3 FragPos;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {

    gl_Position = projectionMatrix * viewMatrix * vec4(in_position, 1.0);
    FragPos = in_position;

    TexCoord = in_textureCoord;
}