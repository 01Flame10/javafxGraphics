package sample.graphical.entity.graphical;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import lombok.Builder;
import lombok.Data;
import sample.configuration.CanvasParametersWrapper;
import sample.graphical.entity.PointHolder;
import sample.graphical.xml.Parser;

import java.lang.reflect.Modifier;
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

    public GraphicalPicture(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        System.out.println("Parse");
        Parser parser = new Parser();
        parser.parse(this);
    }

    @Override
    public void draw(Canvas canvas, CanvasParametersWrapper parameters) {
        double lineWidth = canvas.getGraphicsContext2D().getLineWidth();
        canvas.getGraphicsContext2D().setLineWidth(5);

        int deltaX = centerX - canonicalCenterX;
        int deltaY = centerY - canonicalCenterY;
        graphicalLineSections.forEach(o -> {
            GraphicalLineSection section = o.clone();
            section.setStartX(section.getStartX() + deltaX);
            section.setStartY(section.getStartY() + deltaY);
            section.setEndX(section.getEndX() + deltaX);
            section.setEndY(section.getEndY() + deltaY);
            section.draw(canvas, parameters);
        });
        canvas.getGraphicsContext2D().setLineWidth(lineWidth);
    }

    public static ObservableList<String> parametersToObservableList() {
        return FXCollections.observableArrayList(
                Arrays.stream(GraphicalPicture.class.getDeclaredFields())
                        .filter(field -> !Modifier.isFinal(field.getModifiers()))
                        .map(field -> field.getName() + " =")
                        .collect(Collectors.toList()));
    }

    @Override
    public PointHolder getRotationPoint() {
        return PointHolder.builder()
                .x(centerX)
                .y(centerY)
                .build();
    }

    @Override
    public GraphicalObject clone() {
        GraphicalPicture picture = GraphicalPicture.builder()
                .centerY(getCenterY())
                .centerX(getCenterX())
                .build();

        picture.addPointsToList(getGraphicalLineSections());
        return picture;
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
    public int getMinXCoordinate() {
        return graphicalLineSections.stream().mapToInt(GraphicalLineSection::getMaxXCoordinate).min().orElse(0);
    }

    @Override
    public int getMinYCoordinate() {
        return graphicalLineSections.stream().mapToInt(GraphicalLineSection::getMaxYCoordinate).min().orElse(0);
    }

    @Override
    public String toString() {
        return "GraphicalPicture{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                '}';
    }

}
