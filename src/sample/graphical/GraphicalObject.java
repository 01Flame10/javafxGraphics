package sample.graphical;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import jdk.jshell.spi.ExecutionControl;
import lombok.Data;
import lombok.NoArgsConstructor;
import sample.graphical.entity.GraphicalPoint;


@NoArgsConstructor
public abstract class GraphicalObject {
    public void draw(GraphicsContext context) {
//        throw new ExecutionControl.NotImplementedException("Not implemented");
    }

    public boolean validate() {
        return false;
    }

    public static ObservableList<String> parametersToObservableList() {
        return FXCollections.observableArrayList("No params");
    }


}
