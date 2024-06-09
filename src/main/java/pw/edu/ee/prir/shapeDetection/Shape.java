package pw.edu.ee.prir.shapeDetection;

import org.opencv.core.Scalar;

public enum Shape {
    RECTANGLE("rectangle", new Scalar(0, 255, 0)),
    TRIANGLE("triangle", new Scalar(255, 51, 51)),
    CIRCLE("circle", new Scalar(0, 255, 255));

    private final String name;
    private final Scalar color;

    Shape(String name, Scalar color) {
        this.name = name;
        this.color = color;
    }

    public Scalar color() {
        return color;
    }

    @Override
    public String toString() {
        return name;
    }
}
