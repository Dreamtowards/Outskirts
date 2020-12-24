#version 330 core
layout (location = 0) in vec3 in_position;

flat out vec3 ndcFragPos;

void main() {


    gl_Position = vec4(in_position, 1.0);

    ndcFragPos = in_position;
}