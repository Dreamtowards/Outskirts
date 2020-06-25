#version 330 core
layout (location = 0) in vec2 position;
layout (location = 1) in vec2 texCoords;

out vec2 textureCoords;
out vec2 fragPos;

uniform mat2 transMatrix = mat2(1,0,0,1);
uniform vec2 posOffset;
uniform vec2 posScale;

uniform vec2 texOffset;
uniform vec2 texScale;

void main() {

    gl_Position = vec4((transMatrix * position.xy) * posScale + posOffset, 0, 1.0);

    textureCoords = texCoords * texScale + texOffset;

    fragPos = vec2(position.x, -position.y) / 2.0f; // 0-1 from left-top
}