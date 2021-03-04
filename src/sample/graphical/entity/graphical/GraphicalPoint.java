package sample.graphical.entity.graphical;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import lombok.Builder;
import lombok.Data;
import sample.configuration.CanvasParametersWrapper;
import sample.graphical.entity.PointHolder;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
public class GraphicalPoint extends GraphicalObject {
    private static final double DRAW_RADIUS = 5.0D;

    private int x;
    private int y;

    @Override
    public void draw(Canvas canvas, CanvasParametersWrapper parameters) {
       GraphicalPoint preparedPoint = prepare(parameters);
        canvas.getGraphicsContext2D().fillOval( parameters.getPositionParameters().getOffset().getX() + preparedPoint.getX() * parameters.getScaleParameters().getScale(),
                parameters.getPositionParameters().getOffset().getY() + canvas.getHeight() - preparedPoint.getY() * parameters.getScaleParameters().getScale(),
                DRAW_RADIUS, DRAW_RADIUS);
    }

    public static ObservableList<String> parametersToObservableList() {
        return FXCollections.observableArrayList(
                Arrays.stream(GraphicalPoint.class.getDeclaredFields())
                        .filter(field -> !Modifier.isFinal(field.getModifiers()))
                        .map(field -> field.getName() + " =")
                        .collect(Collectors.toList()));
    }

    public GraphicalPoint prepare(CanvasParametersWrapper parameters) {
        double coordX = coordinateToDrawable(x, parameters);
        double coordY = coordinateToDrawable(y, parameters);
        double pointX = coordinateToDrawable(parameters.getRotationParameters().getRotationCenter().getX(), parameters);
        double pointY = coordinateToDrawable(parameters.getRotationParameters().getRotationCenter().getY(), parameters);
        double radians = (Math.PI / 180) * parameters.getRotationParameters().getRotationDegrees();

        return GraphicalPoint.builder()
                .x((int) (pointX + (coordX - pointX) * Math.cos(radians) - (coordY - pointY) * Math.sin(radians)))
                .y((int) (pointY + (coordX - pointX) * Math.sin(radians) + (coordY - pointY) * Math.cos(radians)))
                .build();
    }

    @Override
    public PointHolder getRotationPoint() {
        return PointHolder.builder()
                .x(this.x)
                .y(this.y)
                .build();
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
    public int getMinXCoordinate() {
        return x;
    }

    @Override
    public int getMinYCoordinate() {
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
    public GraphicalPoint clone() {
        return GraphicalPoint.builder()
                .x(this.x)
                .y(this.y)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphicalPoint point = (GraphicalPoint) o;
        return x == point.x &&
                y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    private double coordinateToDrawable(double value, CanvasParametersWrapper parameters) {
        return value - DRAW_RADIUS / 2;
    }

}
