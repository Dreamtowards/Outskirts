#version 330 core

in vec2 TexCoord;

out vec4 FragColor;

uniform sampler2D textureSampler;

uniform float exposure;

void main() {

    vec3 color = texture(textureSampler, TexCoord).rgb;

    color = vec3(1.0) - exp(-color * exposure);

//    float gamma = 2.2;
//    color = pow(color, vec3(1.0 / gamma));

    FragColor = vec4(color, 1.0);
}