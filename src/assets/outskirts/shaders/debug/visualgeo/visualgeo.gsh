#version 330 core
layout (triangles) in;
layout (line_strip, max_vertices = 9) out;

in vec3 pNorm[3];

out vec4 vColor;

uniform vec4 normColor;
uniform vec4 borderColor;

void GenNormLine(int i) {

    gl_Position = gl_in[i].gl_Position;
    vColor = normColor;
    EmitVertex();

    gl_Position = gl_in[i].gl_Position + vec4(pNorm[i] * .2f, 0);
    vColor = normColor;
    EmitVertex();

    EndPrimitive();
}

void main() {

    GenNormLine(0);
    GenNormLine(1);
    GenNormLine(2);

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