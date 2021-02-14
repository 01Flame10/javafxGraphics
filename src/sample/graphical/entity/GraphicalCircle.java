package sample.graphical.entity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import lombok.Builder;
import lombok.NoArgsConstructor;
import sample.graphical.GraphicalObject;

import java.util.Arrays;
import java.util.stream.Collectors;

@Builder
public class GraphicalCircle extends GraphicalObject {
    private int centerX;
    private int centerY;
    private int radius;

    public static ObservableList<String> parametersToObservableList() {
        return FXCollections.observableArrayList(
                Arrays.stream(GraphicalCircle.class.getDeclaredFields())
                        .map(field -> field.getName() + " = ")
                        .collect(Collectors.toList()));
    }

    @Override
    public void draw(GraphicsContext context) {
        context.fillOval(centerX, centerY, radius, radius);
    }

    @Override
    public boolean validate() {
        return centerX > 0 && centerY > 0 && radius > 0;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", radius=" + radius +
                '}';
    }
}
