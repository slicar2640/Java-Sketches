package sketch.util;

public class Vector {
  public float x, y;

  public Vector(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Vector set(float x, float y) {
    this.x = x;
    this.y = y;
    return this;
  }

  public Vector set(Vector v) {
    this.x = v.x;
    this.y = v.y;
    return this;
  }

  public Vector add(float x, float y) {
    this.x += x;
    this.y += y;
    return this;
  }

  public Vector add(Vector other) {
    this.x += other.x;
    this.y += other.y;
    return this;
  }

  public Vector sub(float x, float y) {
    this.x -= x;
    this.y -= y;
    return this;
  }

  public Vector sub(Vector other) {
    this.x -= other.x;
    this.y -= other.y;
    return this;
  }

  public Vector mult(float x, float y) {
    this.x *= x;
    this.y *= y;
    return this;
  }

  public Vector mult(Vector other) {
    this.x *= other.x;
    this.y *= other.y;
    return this;
  }

  public Vector mult(float f) {
    this.x *= f;
    this.y *= f;
    return this;
  }

  public Vector div(float x, float y) {
    this.x /= x;
    this.y /= y;
    return this;
  }

  public Vector div(Vector other) {
    this.x /= other.x;
    this.y /= other.y;
    return this;
  }

  public Vector div(float f) {
    this.x /= f;
    this.y /= f;
    return this;
  }

  public float dist(Vector other) {
    return (float) Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
  }

  public float dist(float x, float y) {
    return (float) Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
  }

  public float mag() {
    return (float) Math.sqrt(x * x + y * y);
  }

  public float magSq() {
    return x * x + y * y;
  }

  public Vector normalize() {
    float mag = mag();
    x /= mag;
    y /= mag;
    return this;
  }

  public float dot(Vector other) {
    return x * other.x + y * other.y;
  }

  public float heading() {
    return (float) Math.atan2(y, x);
  }

  public Vector copy() {
    return new Vector(x, y);
  }

  public String toString() {
    return "(%.2f, %.2f)".formatted(x, y);
  }

  public static Vector add(Vector a, Vector b) {
    return new Vector(a.x + b.x, a.y + b.y);
  }

  public static Vector sub(Vector a, Vector b) {
    return new Vector(a.x - b.x, a.y - b.y);
  }

  public static Vector mult(Vector a, Vector b) {
    return new Vector(a.x * b.x, a.y * b.y);
  }

  public static Vector mult(Vector v, float f) {
    return new Vector(v.x * f, v.y * f);
  }

  public static Vector div(Vector a, Vector b) {
    return new Vector(a.x / b.x, a.y / b.y);
  }

  public static Vector div(Vector v, float f) {
    return new Vector(v.x / f, v.y / f);
  }

  public static float dist(Vector a, Vector b) {
    return (float) Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
  }

  public static float dist(float x1, float y1, float x2, float y2) {
    return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
  }

  public static Vector lerp(Vector a, Vector b, float t) {
    return new Vector(MathUtils.lerp(a.x, b.x, t), MathUtils.lerp(a.y, b.y, t));
  }

  public static Vector random2D() {
    double angle = Math.random() * 2 * Math.PI;
    return new Vector((float) (Math.cos(angle)), (float) Math.sin(angle));
  }

  public static void random2D(Vector p) {
    double angle = Math.random() * 2 * Math.PI;
    p.set((float) (Math.cos(angle)), (float) Math.sin(angle));
  }

  public static float dot(Vector a, Vector b) {
    return a.x * b.x + a.y * b.y;
  }

  public static Vector fromAngle(float angle) {
    return new Vector((float) Math.cos(angle), (float) Math.sin(angle));
  }

  public static Vector fromAngle(float angle, float length) {
    return new Vector(length * (float) Math.cos(angle), length * (float) Math.sin(angle));
  }
}
