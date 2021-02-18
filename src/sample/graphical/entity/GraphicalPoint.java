package sample.graphical.entity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import lombok.Builder;
import lombok.Data;
import sample.graphical.GraphicalObject;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@Builder
public class GraphicalPoint extends GraphicalObject {
    private static final double DRAW_RADIUS = 5.0D;

    private int x;
    private int y;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.getGraphicsContext2D().fillOval((x * canvas.getScaleX() - DRAW_RADIUS / 2),
                (canvas.getHeight() - y * canvas.getScaleY() - DRAW_RADIUS / 2),
                DRAW_RADIUS, DRAW_RADIUS);
    }

    public static ObservableList<String> parametersToObservableList() {
        return FXCollections.observableArrayList(
                Arrays.stream(GraphicalPoint.class.getDeclaredFields())
                        .filter(field -> !field.getName().equals("DRAW_RADIUS"))
                        .map(field -> field.getName() + " =")
                        .collect(Collectors.toList()));
    }

    @Override
    public boolean validate() {
        return x > 0 && y > 0;
    }

    @Override
    public int getMaxXCoordinate() {
        return x;
    }

    @Override
    public int getMaxYCoordinate() {
        return y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public GraphicalObject clone() throws CloneNotSupportedException {
        return GraphicalPoint.builder()
                .x(this.x)
                .y(this.y)
                .build();
    }
}
