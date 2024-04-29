#type vertex
#version 330 core

layout (location=0) in vec3 a_pos;
layout (location=1) in vec4 a_color;

uniform mat4 u_PV;

out vec4 v_color;

void main() {
    v_color = a_color;
    gl_Position = u_PV * vec4(a_pos, 1.0);
}

#type fragment
#version 330 core

precision mediump float;

in vec4 v_color;

out vec4 color;

void main() {
    color = v_color;
}