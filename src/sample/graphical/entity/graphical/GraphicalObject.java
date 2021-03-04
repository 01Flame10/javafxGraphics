package sample.graphical.entity.graphical;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import lombok.NoArgsConstructor;
import sample.configuration.CanvasParametersWrapper;
import sample.graphical.entity.PointHolder;


@NoArgsConstructor
public abstract class GraphicalObject implements Cloneable {
    public void draw(Canvas canvas, CanvasParametersWrapper parameters) {
//        throw new ExecutionControl.NotImplementedException("Not implemented");
    }

    public boolean validate() {
        return false;
    }

    public static ObservableList<String> parametersToObservableList() {
        return FXCollections.observableArrayList("No params");
    }

    public PointHolder getRotationPoint() {
        return PointHolder.builder()
                .x(0)
                .y(0)
                .build();
    }

    public GraphicalObject prepare(CanvasParametersWrapper parameters) {
        return GraphicalPoint.builder()
                .x(0)
                .y(0)
                .build();
    }

    public int getMaxXCoordinate() {
        return 0;
    }

    public int getMaxYCoordinate() {
        return 0;
    }

    public int getMinXCoordinate() {
        return 0;
    }

    public int getMinYCoordinate() {
        return 0;
    }

    @Override
    public GraphicalObject clone() {
        try {
            return (GraphicalObject) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
