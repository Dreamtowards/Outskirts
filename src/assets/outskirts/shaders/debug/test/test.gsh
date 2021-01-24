#version 330 core
layout (points) in;
layout (triangle_strip, max_vertices = 5) out;

void main() {
    float sz = 0.2;
    vec4 pos = gl_in[0].gl_Position;

    gl_Position = pos + vec4(-sz, -sz, 0, 0);
    EmitVertex();
    gl_Position = pos + vec4(sz,  -sz, 0, 0);
    EmitVertex();
    gl_Position = pos + vec4(-sz,  sz, 0, 0);
    EmitVertex();
    gl_Position = pos + vec4(sz,  sz, 0, 0);
    EmitVertex();
    gl_Position = pos + vec4(0,  sz+sz, 0, 0);
    EmitVertex();
    EndPrimitive();
}