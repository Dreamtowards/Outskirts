#version 330 core

in vec2 TexCoord;

out vec4 FragColor;

uniform sampler2D ssaoMap;

void main() {
    int R = 1;

    vec2 texelSize = 1.0 / textureSize(ssaoMap, 0);

    float sum = 0;
    for (int i = -R;i <= R;i++) {
        for (int j = -R;j <= R;j++) {
            sum += texture(ssaoMap, TexCoord + vec2(i, j) * texelSize).r;
        }
    }

    FragColor = vec4(sum / ((R*2+1)*(R*2+1)));
    FragColor.a = 1.0;
}