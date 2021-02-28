#version 330 core

in vec2 TexCoord;
in vec3 FragPos;

out vec4 FragColor;

uniform vec3 fillColor;
uniform vec3 bgColor;
uniform vec3 CamPos;

const float fogNear = 10;
const float fogFar = 1000;

void main() {

    FragColor = vec4(fillColor, 1.0);

    float t = clamp(smoothstep(fogNear, fogFar, length(CamPos - FragPos)), 0.0, 1.0);
    FragColor.rgb = mix(FragColor.rgb, bgColor, t);
}