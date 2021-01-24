#version 330 core
layout (location = 0) in vec3 in_position;
layout (location = 2) in vec3 in_normal;

out vec3 pNorm;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {

    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(in_position, 1.0);

    pNorm = normalize(vec3(projectionMatrix * viewMatrix * modelMatrix * vec4(in_normal, 0.0)));

}