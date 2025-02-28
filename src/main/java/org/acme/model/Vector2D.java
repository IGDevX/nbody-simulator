package org.acme.model;

import lombok.Getter;

@Getter
public class Vector2D {
    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    public Vector2D normalize() {
        double magnitude = magnitude();
        return new Vector2D(x / magnitude, y / magnitude);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D subtract(Vector2D position) {
        return new Vector2D(x - position.x, y - position.y);
    }

    public Vector2D divide(double mass) {
        return new Vector2D(x / mass, y / mass);
    }

    public Vector2D scale(double v) {
        return new Vector2D(x * v, y * v);
    }
}
