#version 330 core
layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_texCoord;

out vec2 TexCoord;
out vec3 FragPos;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {

    vec4 worldposisiton = modelMatrix * vec4(in_position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldposisiton;

    FragPos = worldposisiton.xyz;
    TexCoord = in_texCoord;

}