#version 330 core
layout (triangles) in;
layout (line_strip, max_vertices = 9) out;

in vec3 pNorm[3];
in vec2 TexCoord[3];

out vec4 vColor;

uniform vec4 normColor;
uniform vec4 borderColor;

void GenLine(int i, vec3 dir, vec4 color) {

    gl_Position = gl_in[i].gl_Position;
    vColor = color;
    EmitVertex();

    gl_Position = gl_in[i].gl_Position + vec4(dir * .2f, 0);
    vColor = color;
    EmitVertex();

    EndPrimitive();
}

vec3 computeTangent(vec3 P1, vec3 P2, vec3 P3, vec2 UV1, vec2 UV2, vec2 UV3) {
    vec3 E1 = P2 - P1;
    vec3 E2 = P3 - P1;
    vec2 D1 = UV2 - UV1;
    vec2 D2 = UV3 - UV1;
    float f = 1.0f / (D1.x*D2.y - D2.x*D1.y);
    vec3 T = vec3(f*(D2.y*E1.x-D1.y*E2.x),
                  f*(D2.y*E1.y-D1.y*E2.y),
                  f*(D2.y*E1.z-D1.y*E2.z));
    return normalize(T);
}

void main() {

    GenLine(0, pNorm[0], normColor);
    GenLine(1, pNorm[1], normColor);
    GenLine(2, pNorm[2], normColor);

//    vec3 T = computeTangent(vec3(gl_in[0].gl_Position), vec3(gl_in[1].gl_Position), vec3(gl_in[2].gl_Position),
//                                 TexCoord[0],                TexCoord[1],                TexCoord[2]);
//    GenLine(0, T, vec4(1, 0, 0, 1));
//    GenLine(1, T, vec4(1, 0, 0, 1));
//    GenLine(2, T, vec4(1, 0, 0, 1));

    gl_Position = gl_in[0].gl_Position;
    vColor = borderColor;
    EmitVertex();
    gl_Position = gl_in[1].gl_Position;
    vColor = borderColor;
    EmitVertex();
    gl_Position = gl_in[2].gl_Position;
    vColor = borderColor;
    EmitVertex();
    EndPrimitive();
}