package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import sample.graphical.GraphicalObject;
import sample.graphical.entity.GraphicalCircle;
import sample.graphical.entity.GraphicalLine;
import sample.graphical.entity.GraphicalLineSection;
import sample.graphical.entity.GraphicalPoint;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    //    ObservableList<String> objectsDataToAdd = FXCollections.observableArrayList("Point", "Line", "Line(section)", "Circle");
    Map<String, ObservableList<String>> objectsFields;
    Map<String, GraphicalObject> objectNamesToClassReference;
    GraphicalObject currentObject;
    List<GraphicalObject> objectList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        objectsFields = new HashMap<>();
        objectsFields.put("Point", GraphicalPoint.parametersToObservableList());
        objectsFields.put("Line", GraphicalLine.parametersToObservableList());
        objectsFields.put("Line(section)", GraphicalLineSection.parametersToObservableList());
        objectsFields.put("Circle", GraphicalCircle.parametersToObservableList());

        objectNamesToClassReference = new HashMap<>();
        objectNamesToClassReference.put("Point", GraphicalPoint.builder().build());
        objectNamesToClassReference.put("Line", GraphicalLine.builder().build());
        objectNamesToClassReference.put("Line(section)", GraphicalLineSection.builder().build());
        objectNamesToClassReference.put("Circle", GraphicalCircle.builder().build());
    }

    @FXML
    private Button highlightElementButton;

    @FXML
    private Button deleteElementButton;

    @FXML
    private Button editElementButton;

    @FXML
    private Button createElementButton;

    @FXML
    private Button setParameterValueButton;

    @FXML
    private TextField parameterValueInput;

    @FXML
    private ListView<String> objectsPlacedList;

    @FXML
    private ListView<String> objectsToPlaceList;

    @FXML
    private ListView<String> objectsParametersList;

    @FXML
    private Canvas graphTable;

    @FXML
    public void onObjectsToPlaceListReload() {
        objectsToPlaceList.setItems(FXCollections.observableArrayList(objectsFields.keySet()));
        objectsToPlaceList.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                    objectsParametersList.setItems(objectsFields.get(observableValue.getValue()));
                    currentObject = objectNamesToClassReference.get(observableValue.getValue());
                });
    }

    @FXML
    public void onParameterSelected() {
        objectsParametersList.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                    parameterValueInput.setPromptText("Value of " + observableValue.getValue());
                    setParameterValueButton.setOnAction(event -> Arrays.stream(currentObject.getClass().getDeclaredFields())
                            .filter(field -> observableValue.getValue().startsWith(field.getName()))
                            .forEach(field -> {
                                try {
                                    field.setAccessible(true);
                                    field.set(currentObject, Integer.parseInt(parameterValueInput.getText()));
                                    objectsParametersList.getItems()
                                            .set(objectsParametersList.getSelectionModel().getSelectedIndex(),
                                                    field.getName() + " = " + parameterValueInput.getText());
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }

                            }));

                });
    }

    @FXML
    public void onHighlightElement() {

    }

    @FXML
    public void onDrawElement() {
        objectsParametersList.getSelectionModel().selectedItemProperty().addListener(observable -> {

//            String selectedItem = objectsParametersList.getSelectionModel().getSelectedItem();
//            int index = objectsParametersList.getSelectionModel().getSelectedIndex();
//            list.remove(index);
//            list.add("+" + index + "+");
//            System.out.println("index " + index);
        });
        GraphicalLine.builder()
                .startX(50).startY(50)
                .endX(100).endY(100)
                .build().draw(graphTable.getGraphicsContext2D());
        System.out.println(">> highlighting");
        graphTable.getGraphicsContext2D().strokeLine(200, 50, 300, 150);
    }
}
