#version 330 core

in vec2 TexCoord;
in vec3 SurfaceNormal;

out vec4 FragColor;

uniform sampler2D diffuseMap;

const vec3 lightdir = vec3(-0.577, -0.577, -0.577);

void main() {

    vec4 diffColor = texture(diffuseMap, TexCoord);

    float diffBrightness = max(dot(-lightdir, SurfaceNormal), 0.0) * 0.7 + 0.4;

    FragColor = diffColor;
}