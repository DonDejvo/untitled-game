#type vertex
#version 330 core

layout (location=0) in vec2 a_pos;
layout (location=1) in vec2 a_uv;
layout (location=2) in vec4 a_color;

uniform mat4 u_projection;

out vec2 v_uv;
out vec4 v_color;

void main() {
    v_uv = a_uv;
    v_color = a_color;
    gl_Position = u_projection * vec4(a_pos, 0.0, 1.0);
}

#type fragment
#version 330 core

precision mediump float;

in vec2 v_uv;
in vec4 v_color;

uniform sampler2D u_tex;

out vec4 color;

void main() {
    vec4 tex_color = texture2D(u_tex, v_uv);
    color = v_color * tex_color;
}