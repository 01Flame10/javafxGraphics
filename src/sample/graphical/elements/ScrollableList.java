package sample.graphical.elements;

import javafx.scene.control.ListView;
import sample.graphical.GraphicalObject;

import java.util.ArrayList;
import java.util.List;

public class ScrollableList<T> extends GraphicalObject {
    private List<T> objectList;
    private ListView<String> objectListView;

    public ScrollableList() {
        objectList = new ArrayList<>();
        objectListView = new ListView<>();
    }

    public addObject(T object) {
        objectList.add(object);
        objectListView.
    }


}
