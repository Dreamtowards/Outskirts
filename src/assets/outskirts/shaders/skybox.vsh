#version 330 core

layout (location = 0) in vec3 aPos;

out vec3 TexCoords;

uniform mat4 proj;
uniform mat4 view;

void main() {
    mat4 eview = view;
    eview[3][0] = 0;
    eview[3][1] = 0;
    eview[3][2] = 0;

    TexCoords = aPos;

    gl_Position = proj * eview * vec4(aPos, 1.0);
    gl_Position = gl_Position.xyww;
}