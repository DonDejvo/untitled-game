#type vertex
#version 330 core

layout (location=0) in vec3 a_pos;
layout (location=1) in vec2 a_uv;
layout (location=2) in vec3 a_normal;

uniform mat4 u_PVM;
uniform mat4 u_view;
uniform mat4 u_model;

out vec3 v_world_pos;
out vec3 v_view_pos;
out vec2 v_uv;
out vec3 v_normal;

void main() {
    v_world_pos = (u_model * vec4(a_pos, 1.0)).xyz;
    v_view_pos = (u_view * vec4(v_world_pos, 1.0)).xyz;
    v_uv = a_uv;
    v_normal = mat3(u_model) * a_normal;
    gl_Position = u_PVM * vec4(a_pos, 1.0);
}

#type fragment
#version 330 core

precision mediump float;

in vec3 v_world_pos;
in vec3 v_view_pos;
in vec2 v_uv;
in vec3 v_normal;

uniform sampler2D u_tex;
uniform vec4 u_color;

uniform vec3 u_spot_lights[192];
uniform int u_spot_lights_count;
uniform vec3 u_ambient_color;

out vec4 color;

void main() {
    vec4 tex_color = texture2D(u_tex, v_uv);

    color = tex_color * u_color;

    vec3 result = vec3(0.0);

    for(int i = 0; i < u_spot_lights_count * 3; i += 3) {
        vec3 light_pos = u_spot_lights[i];
        vec3 light_color = u_spot_lights[i + 1];
        float light_radius_min = u_spot_lights[i + 2].x;
        float light_radius_max = u_spot_lights[i + 2].y;
        float theta = dot(v_normal, normalize(light_pos - v_world_pos));
        float distance = length(light_pos - v_world_pos);
        float intensity = 1 - (distance - light_radius_min) / (light_radius_max - light_radius_min);
        intensity = clamp(intensity, 0.0, 1.0);
        result += max(theta, 0.0) * intensity * light_color;
    }

    result += u_ambient_color;

    color.rgb *= result;
}