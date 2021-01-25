#version 330 core
layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in VS_OUT {
    vec3 FragPos;
    vec3 vNorm;
    vec2 vTexCoord;
    float vMtlId;
} vs_out[];

out vec3 FragPos;
out vec3 vNorm;
out vec2 vTexCoord;

out vec3 TriWeight;
flat out vec3 TriMtlId;

void main() {
    vec3 TMI = vec3(vs_out[0].vMtlId, vs_out[1].vMtlId, vs_out[2].vMtlId);

    gl_Position = gl_in[0].gl_Position;
    FragPos = vs_out[0].FragPos;
    vNorm = vs_out[0].vNorm;
    vTexCoord = vs_out[0].vTexCoord;
    TriWeight = vec3(1, 0, 0);
    TriMtlId = TMI;
    EmitVertex();

    gl_Position = gl_in[1].gl_Position;
    FragPos = vs_out[1].FragPos;
    vNorm = vs_out[1].vNorm;
    vTexCoord = vs_out[1].vTexCoord;
    TriWeight = vec3(0, 1, 0);
    TriMtlId = TMI;
    EmitVertex();

    gl_Position = gl_in[2].gl_Position;
    FragPos = vs_out[2].FragPos;
    vNorm = vs_out[2].vNorm;
    vTexCoord = vs_out[2].vTexCoord;
    TriWeight = vec3(0, 0, 1);
    TriMtlId = TMI;
    EmitVertex();

    EndPrimitive();

}