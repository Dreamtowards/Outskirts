#version 330 core
layout (location = 0) in vec2 positions_in;
layout (location = 1) in vec2 texCoords_in;

out vec2 TexCoord;

void main() {

    gl_Position = vec4(positions_in, 0.0, 1.0);

    TexCoord = texCoords_in;
}
