#version 330 core

out vec4 FragColor;

in vec2 TexCoord;

uniform sampler2D diffuseMap;

void main() {
    if (texture(diffuseMap, TexCoord).a == 0)
        discard;
    // gl_FragDepth = gl_FragCoord.z;  // OpenGL already did. in Early-Depth-Test.
}