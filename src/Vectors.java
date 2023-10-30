/*
 * utility class for 3d vectors
 */
class vec3 implements Comparable<vec3> {
    public double x, y, z;
    
    public vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public vec3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    
    public static vec3 add(vec3 a, vec3 b) {
        return new vec3(a.x + b.x, a.y + b.y, a.z + b.z);
    }
    
    public static vec3 subtract(vec3 a, vec3 b) {
        return new vec3(a.x - b.x, a.y - b.y, a.z - b.z);
    }
    
    public static vec3 multiply(vec3 v, double scalar) {
        return new vec3(v.x * scalar, v.y * scalar, v.z * scalar);
    }
    
    public static vec3 divide(vec3 v, double scalar) {
        return new vec3(v.x / scalar, v.y / scalar, v.z / scalar);
    }
    
    public static vec3 cross(vec3 a, vec3 b) {
        return new vec3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }
    
    public static vec3 normalize(vec3 v) {
        double length = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        return new vec3(v.x / length, v.y / length, v.z / length);
    }
    
    public int compareTo(vec3 other) {
        if (x < other.x) return -1;
        if (x > other.x) return 1;
        if (y < other.y) return -1;
        if (y > other.y) return 1;
        if (z < other.z) return -1;
        if (z > other.z) return 1;
        return 0;
    }
    
    public vec3 add(vec3 other) {
        return new vec3(x + other.x, y + other.y, z + other.z);
    }
    
    public vec3 subtract(vec3 other) {
        return new vec3(x - other.x, y - other.y, z - other.z);
    }
    
    public vec3 multiply(double scalar) {
        return new vec3(x * scalar, y * scalar, z * scalar);
    }
    
    public vec3 divide(double scalar) {
        return new vec3(x / scalar, y / scalar, z / scalar);
    }
    
    public vec3 cross(vec3 other) {
        return new vec3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }
    
    public vec3 normalize() {
        double length = Math.sqrt(x * x + y * y + z * z);
        return new vec3(x / length, y / length, z / length);
    }
}

class vec2 implements Comparable<vec2> {
    public double x, y;
    
    public vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public vec2() {
        this.x = 0;
        this.y = 0;
    }
    
    public static vec2 add(vec2 a, vec2 b) {
        return new vec2(a.x + b.x, a.y + b.y);
    }
    
    public static vec2 subtract(vec2 a, vec2 b) {
        return new vec2(a.x - b.x, a.y - b.y);
    }
    
    public static vec2 multiply(vec2 v, double scalar) {
        return new vec2(v.x * scalar, v.y * scalar);
    }
    
    public static vec2 divide(vec2 v, double scalar) {
        return new vec2(v.x / scalar, v.y / scalar);
    }
    
    public static vec2 normalize(vec2 v) {
        double length = Math.sqrt(v.x * v.x + v.y * v.y);
        return new vec2(v.x / length, v.y / length);
    }
    
    public int compareTo(vec2 other) {
        if (x < other.x) return -1;
        if (x > other.x) return 1;
        if (y < other.y) return -1;
        if (y > other.y) return 1;
        return 0;
    }
    
    public vec2 add(vec2 other) {
        return new vec2(x + other.x, y + other.y);
    }
    
    public vec2 subtract(vec2 other) {
        return new vec2(x - other.x, y - other.y);
    }
    
    public vec2 multiply(double scalar) {
        return new vec2(x * scalar, y * scalar);
    }
    
    public vec2 divide(double scalar) {
        return new vec2(x / scalar, y / scalar);
    }
    
    public vec2 normalize() {
        double length = Math.sqrt(x * x + y * y);
        return new vec2(x / length, y / length);
    }
}