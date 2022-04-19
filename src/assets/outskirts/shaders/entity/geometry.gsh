#version 330 core
layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in VS_OUT {
    vec3 FragPos;
    vec3 vNorm;
    vec2 vTexCoord;
    // float vMtlId;
} vs_out[];

out vec3 FragPos;
out vec3 vNorm;
out vec2 vTexCoord;
// out mat3 TBN;

// out vec3 TriWeight;
// flat out vec3 TriMtlId;

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
    // vec3 TMI = vec3(vs_out[0].vMtlId, vs_out[1].vMtlId, vs_out[2].vMtlId);
    // if (vs_out[0].vTexCoord != vs_out[1].vTexCoord) {  // Valid TexCoord. TBN Setup.
    //     vec3 N = vs_out[0].vNorm;
    //     vec3 T = computeTangent(vs_out[0].FragPos,   vs_out[1].FragPos,   vs_out[2].FragPos,
    //                             vs_out[0].vTexCoord, vs_out[1].vTexCoord, vs_out[2].vTexCoord);
    //     vec3 B = normalize(cross(N, T));
    //     TBN = mat3(
    //         T.x, T.y, T.z,
    //         B.x, B.y, B.z,
    //         N.x, N.y, N.z
    //     );
    // } else {  // Triplanar method.Triplanar Normal Mapping.  No directly TexCoord.
    //     TBN = mat3(0);  // Disable TBN.
    // }

    gl_Position = gl_in[0].gl_Position;
    FragPos = vs_out[0].FragPos;
    vNorm = vs_out[0].vNorm;
    vTexCoord = vs_out[0].vTexCoord;
    //TriWeight = vec3(1, 0, 0);
    //TriMtlId = TMI;
    EmitVertex();

    gl_Position = gl_in[1].gl_Position;
    FragPos = vs_out[1].FragPos;
    vNorm = vs_out[1].vNorm;
    vTexCoord = vs_out[1].vTexCoord;
    //TriWeight = vec3(0, 1, 0);
    //TriMtlId = TMI;
    EmitVertex();

    gl_Position = gl_in[2].gl_Position;
    FragPos = vs_out[2].FragPos;
    vNorm = vs_out[2].vNorm;
    vTexCoord = vs_out[2].vTexCoord;
    //TriWeight = vec3(0, 0, 1);
    //TriMtlId = TMI;
    EmitVertex();

    EndPrimitive();

}