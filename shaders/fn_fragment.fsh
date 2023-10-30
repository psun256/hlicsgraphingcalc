#version 330 core

varying vec3 pos;
varying vec3 normal;

uniform vec3 scaleMajor;

void main() {
    gl_FragColor = vec4(0.5 * (abs(normal) + vec3(0.5)), 1.0);
    // scale is the major grid size, so we want to draw black lines for those, and then more transparent lines for the minor grid
    vec3 scaleMinor = scaleMajor / 10.0;
    vec3 distMajor = mod(pos, scaleMajor);
    vec3 distMinor = mod(pos, scaleMinor);

    // if the distance to the nearest major grid line is less than 0.008, draw a black line
    if (abs(distMajor.x) < 0.008 * length(normal.yz) * scaleMajor.x ||
        abs(distMajor.y) < 0.008 * length(normal.xz) * scaleMajor.y ||
        abs(distMajor.z) < 0.008 * length(normal.yx) * scaleMajor.z) {

        gl_FragColor = vec4(0.1, 0.1, 0.1, 1.0);
    }
    // if the distance to the nearest minor grid line is less than 0.005, draw a more transparent black linee
    if (abs(distMinor.s) < 0.005 * length(normal.yz) * scaleMajor.x ||
        abs(distMinor.y) < 0.005 * length(normal.xz) * scaleMajor.y ||
        abs(distMinor.z) < 0.005 * length(normal.yx) * scaleMajor.z) {

        gl_FragColor = 0.5 * gl_FragColor + 0.5 * vec4(0.1, 0.1, 0.1, 0.5);
    }
}