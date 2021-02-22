package sample.graphical.entity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import lombok.Builder;
import lombok.Data;
import sample.graphical.GraphicalObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class GraphicalPicture extends GraphicalObject {
    public final static int canonicalCenterX = 366;
    public final static int canonicalCenterY = 478;

    private int centerX;
    private int centerY;

    private final List<GraphicalLineSection> graphicalLineSections = new ArrayList<>();

    public void addPointsToList(List<GraphicalLineSection> points) {
        graphicalLineSections.addAll(points);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        double lineWidth = canvas.getGraphicsContext2D().getLineWidth();
        canvas.getGraphicsContext2D().setLineWidth(5);
        graphicalLineSections.forEach(o -> {
            // TODO improve
            o.setStartX(o.getStartX() - centerX + canonicalCenterX);
            o.setStartY(o.getStartY() - centerY + canonicalCenterY);
            o.setEndX(o.getEndX() - centerX + canonicalCenterX);
            o.setEndY(o.getEndY() - centerY + canonicalCenterY);
            o.draw(canvas);
        });
        canvas.getGraphicsContext2D().setLineWidth(lineWidth);
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
        return true;
    }

    @Override
    public int getMaxXCoordinate() {
        return graphicalLineSections.stream().mapToInt(GraphicalLineSection::getMaxXCoordinate).max().orElse(0);
    }

    @Override
    public int getMaxYCoordinate() {
        return graphicalLineSections.stream().mapToInt(GraphicalLineSection::getMaxYCoordinate).max().orElse(0);
    }

    @Override
    public String toString() {
        return "GraphicalPicture{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                '}';
    }

}
