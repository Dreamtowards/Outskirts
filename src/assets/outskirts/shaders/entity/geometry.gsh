#version 330 core
layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in VS_OUT {
    vec3 FragPos;
    vec3 vNorm;
    vec2 vTexCoord;
} vs_out[];
in vec3 AccTri_BrCd[];
flat in vec3 AccTri_MtId[];

out vec3 FragPos;
out vec3 vNorm;
out vec2 vTexCoord;
out vec3 ACCTRI_BARYCD;
flat out vec3 ACCTRI_BLXID;

void main() {

    gl_Position = gl_in[0].gl_Position;
    FragPos = vs_out[0].FragPos;
    vNorm = vs_out[0].vNorm;
    vTexCoord = vs_out[0].vTexCoord;
    ACCTRI_BARYCD = AccTri_BrCd[0];
    ACCTRI_BLXID = AccTri_MtId[0];
    EmitVertex();

    gl_Position = gl_in[1].gl_Position;
    FragPos = vs_out[1].FragPos;
    vNorm = vs_out[1].vNorm;
    vTexCoord = vs_out[1].vTexCoord;
    ACCTRI_BARYCD = AccTri_BrCd[1];
    ACCTRI_BLXID = AccTri_MtId[1];
    EmitVertex();

    gl_Position = gl_in[2].gl_Position;
    FragPos = vs_out[2].FragPos;
    vNorm = vs_out[2].vNorm;
    vTexCoord = vs_out[2].vTexCoord;
    ACCTRI_BARYCD = AccTri_BrCd[2];
    ACCTRI_BLXID = AccTri_MtId[2];
    EmitVertex();

    EndPrimitive();

}