package sample.graphical.entity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sample.graphical.GraphicalObject;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@Builder
public class GraphicalPoint extends GraphicalObject {
    private static final double DRAW_RADIUS = 1.0D;

    private int x;
    private int y;

    @Override
    public void draw(GraphicsContext context) {
        super.draw(context);

        context.fillOval(x, y, DRAW_RADIUS, DRAW_RADIUS);
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
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
