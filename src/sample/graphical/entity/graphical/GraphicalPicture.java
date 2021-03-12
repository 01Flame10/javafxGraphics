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

    private double scaleX;
    private double scaleY;
    private int pointX;
    private int pointY;

    private final List<GraphicalLineSection> graphicalLineSections = new ArrayList<>();

    public void addPointsToList(List<GraphicalLineSection> points) {
        graphicalLineSections.addAll(points);
    }

    public GraphicalPicture(int centerX, int centerY, double scaleX, double scaleY, int pointX, int pointY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.pointX = pointX;
        this.pointY = pointY;
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
            section.setStartX((int) ((section.getStartX() + deltaX) * scaleX + (1 - scaleX) * pointX));
            section.setStartY((int) ((section.getStartY() + deltaY) * scaleY + (1 - scaleY) * pointY));
            section.setEndX((int) ((section.getEndX() + deltaX) * scaleX + (1 - scaleX) * pointX));
            section.setEndY((int) ((section.getEndY() + deltaY) * scaleY + (1 - scaleY) * pointY));
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
                .scaleX(scaleX)
                .scaleY(scaleY)
                .pointX(pointX)
                .pointY(pointY)
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
                ", scaleX=" + scaleX +
                ", scaleY=" + scaleY +
                ", scaleX=" + pointX +
                ", scaleY=" + pointY +
                ", graphicalLineSections=" + graphicalLineSections +
                '}';
    }
}
