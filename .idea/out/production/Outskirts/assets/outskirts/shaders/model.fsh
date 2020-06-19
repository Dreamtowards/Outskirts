#version 330 core

in vec2 textureCoords;

out vec4 FragColor;

uniform sampler2D textureSampler;

uniform vec4 colorMultiply;

void main() {


    FragColor = texture(textureSampler, textureCoords) * colorMultiply;

}