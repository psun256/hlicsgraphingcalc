#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;

varying vec3 pos;
varying vec3 normal;

uniform mat4 mvp;

void main() {
    // send global position to fragment shader for gridline calculation
    pos = aPos;
    // send normal to fragment shader for gridline width calculation, and for simple and nice colors
    normal = aNormal;

    // transformation from world space to clip space done entirely in vertex shader
    gl_Position = mvp * vec4(aPos, 1.0);
}