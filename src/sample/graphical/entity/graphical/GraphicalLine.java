package sample.graphical.entity.graphical;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import lombok.Builder;
import lombok.Data;
import sample.configuration.CanvasParametersWrapper;
import sample.graphical.entity.PointHolder;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
public class GraphicalLine extends GraphicalObject {
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public static ObservableList<String> parametersToObservableList() {
        return FXCollections.observableArrayList(
                Arrays.stream(GraphicalLine.class.getDeclaredFields())
                        .map(field -> field.getName() + " =")
                        .collect(Collectors.toList()));
    }

    @Override
    public void draw(Canvas canvas, CanvasParametersWrapper parameters) {
        GraphicalLine section = this.prepare(parameters);
        canvas.getGraphicsContext2D().strokeLine(section.getStartX() * parameters.getScaleParameters().getScale(),
                canvas.getHeight() - section.getStartY() * parameters.getScaleParameters().getScale(),
                section.getEndX() * parameters.getScaleParameters().getScale(),
                canvas.getHeight() - section.getEndY() * parameters.getScaleParameters().getScale());
    }

    @Override
    public PointHolder getRotationPoint() {
        return PointHolder.builder()
                .x(Math.abs(startX - endX))
                .y(Math.abs(startY - endY))
                .build();
    }

    @Override
    public GraphicalLine prepare(CanvasParametersWrapper parameters) {
        GraphicalLine section = GraphicalLine.builder().build();
        GraphicalPoint startPoint = GraphicalPoint.builder()
                .x(this.getStartX())
                .y(this.getStartY())
                .build().prepare(parameters);

        GraphicalPoint endPoint = GraphicalPoint.builder()
                .x(this.getEndX())
                .y(this.getEndY())
                .build().prepare(parameters);

        section.setStartX(startPoint.getX());
        section.setStartY(startPoint.getY());
        section.setEndX(endPoint.getX());
        section.setEndY(endPoint.getY());
        return section;
    }

    @Override
    public boolean validate() {
        return startX > 0 && startY > 0 &&
                endX > 0 && endY > 0 &&
                !(startX == endX && startY == endY);
    }

    @Override
    public int getMaxXCoordinate() {
        return Math.max(startX, endX);
    }

    @Override
    public int getMaxYCoordinate() {
        return Math.max(startY, endY);
    }

    @Override
    public int getMinXCoordinate() {
        return Math.min(startX, endX);
    }

    @Override
    public int getMinYCoordinate() {
        return Math.min(startY, endY);
    }

    @Override
    public String toString() {
        return "Line{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                '}';
    }

    @Override
    public GraphicalObject clone() {
        return GraphicalLine.builder()
                .startX(this.startX)
                .startY(this.startY)
                .endX(this.endX)
                .endY(this.endY)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphicalLine that = (GraphicalLine) o;
        return startX == that.startX &&
                startY == that.startY &&
                endX == that.endX &&
                endY == that.endY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startX, startY, endX, endY);
    }
}
