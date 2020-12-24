#version 330 core

flat in vec3 ndcFragPos;

out vec4 FragColor;

void main() {


    FragColor = vec4(ndcFragPos, 1.0);

}