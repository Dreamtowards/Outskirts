#version 330 core

in vec2 textureCoords;
in vec2 fragPos;

out vec4 FragColor;


uniform sampler2D textureSampler;
uniform vec4 colorMultiply;

uniform float renderrespect; // width/height
uniform float roundradius;

float lenSq(vec2 v) {
    return dot(v, v);
}

void main() {

    if (roundradius != 0) {
        vec2 p = vec2(fragPos.x, fragPos.y/renderrespect);
        float rsp = renderrespect;
        float sizeY = 1.0/renderrespect;
        float rSq = roundradius*roundradius;

        if (p.x < roundradius && p.y < roundradius) { // left-top
            if (lenSq(p - vec2(roundradius)) > rSq)
                discard;
        } else if (p.x < roundradius && p.y > sizeY-roundradius) { // left-bottom
            if (lenSq(p - vec2(roundradius, sizeY-roundradius)) > rSq)
                discard;
        } else if (p.x > 1.0f-roundradius && p.y < roundradius) { // right-top
            if (lenSq(p - vec2(1.0f-roundradius, roundradius)) > rSq)
                discard;
        } else if (p.x > 1.0f-roundradius && p.y > sizeY-roundradius) { // right-bottom
            if (lenSq(p - vec2(1.0f-roundradius, sizeY-roundradius)) > rSq)
                discard;
        }
    }

    FragColor = texture(textureSampler, textureCoords) * colorMultiply;

}