#type vertex
#version 330 core

layout (location=0) in vec3 a_pos;
layout (location=1) in vec2 a_uv;
layout (location=2) in vec4 a_color;

uniform mat4 u_PV;
uniform mat4 u_view;

out vec3 v_view_pos;
out vec2 v_uv;
out vec4 v_color;

void main() {
    v_view_pos = (u_view * vec4(a_pos, 1.0)).xyz;
    v_uv = a_uv;
    v_color = a_color;
    gl_Position = u_PV * vec4(a_pos, 1.0);
}

#type fragment
#version 330 core

precision mediump float;

in vec3 v_view_pos;
in vec2 v_uv;
in vec4 v_color;

uniform sampler2D u_tex;

uniform vec3 u_fog_color;
uniform float u_fog_near;
uniform float u_fog_far;

out vec4 color;

void main() {
    vec4 tex_color = texture2D(u_tex, v_uv);
    color = v_color * tex_color;

    float fog_alpha = tex_color.a;
    vec3 fog_direction = normalize(v_view_pos);
    float fog_depth = length(v_view_pos);

    float fog_factor = smoothstep(u_fog_near, u_fog_far, fog_depth);
    fog_factor = clamp(fog_factor, 0.0, 1.0);

    color = mix(color, vec4(u_fog_color, fog_alpha), fog_factor);
}