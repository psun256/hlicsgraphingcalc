#version 330 core

varying vec3 color;

void main() {
    gl_FragColor = vec4(0.6 * color, 1.0);
}