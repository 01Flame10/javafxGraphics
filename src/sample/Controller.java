package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import sample.graphical.GraphicalObject;
import sample.graphical.entity.GraphicalCircle;
import sample.graphical.entity.GraphicalLine;
import sample.graphical.entity.GraphicalLineSection;
import sample.graphical.entity.GraphicalPoint;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    Map<String, ObservableList<String>> objectsFields;
    Map<String, GraphicalObject> objectNamesToClassReference;
    GraphicalObject currentObject;
    List<GraphicalObject> objectList;
    ParameterEditAction parameterEditAction = new ParameterEditAction();


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

        objectList = new ArrayList<>();
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
    private Label parameterErrorField;

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
    private ScrollPane scrollPanel;

    @FXML
    public void onCreateCanvas() {
        scrollPanel.widthProperty().addListener(event -> {
            graphTable.setWidth(scrollPanel.getWidth());
            redrawElements();
        });
        scrollPanel.heightProperty().addListener(event -> {
            graphTable.setHeight(scrollPanel.getHeight());
            redrawElements();
        });
    }


    @FXML
    public void onObjectsToPlaceListReload() {
        objectsToPlaceList.setItems(FXCollections.observableArrayList(objectsFields.keySet()));
        objectsToPlaceList.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                    objectsParametersList.setItems(objectsFields.get(observableValue.getValue()));
                });
    }

    @FXML
    public void onObjectToPlaceSelected() {
        createElementButton.setText("Create");
        createElementButton.setOnAction(anotherEvent -> onDrawElement());
        currentObject = objectNamesToClassReference.get(objectsToPlaceList.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void onParameterSelected() {
        objectsParametersList.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                    parameterValueInput.setPromptText("Value of " + observableValue.getValue());
                    parameterValueInput.setOnAction(parameterEditAction);
                    setParameterValueButton.setOnAction(parameterEditAction);
                });
    }

    @FXML
    public void onHighlightElement() {
//        graphTable.getGraphicsContext2D().;
    }

    @FXML
    public void onEditElement() {
        currentObject = objectList.get(objectsPlacedList.getSelectionModel().getSelectedIndex());
        List<String> stringList = new ArrayList<>();
        Arrays.stream(currentObject.getClass().getDeclaredFields())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        stringList.add(field.getName() + " = " + field.get(currentObject));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        objectsParametersList.setItems(FXCollections.observableList(stringList));

        createElementButton.setText("Edit");
        createElementButton.setOnAction(event -> {
            objectsPlacedList.getItems().set(objectsPlacedList.getSelectionModel().getSelectedIndex(), currentObject.toString());
            objectsParametersList.setItems(FXCollections.emptyObservableList());
            createElementButton.setText("Create");
            createElementButton.setOnAction(anotherEvent -> onDrawElement());

            redrawElements();
        });
    }

    @FXML
    public void obDeleteElement() {
        objectList.remove(objectsPlacedList.getSelectionModel().getSelectedIndex());
        objectsPlacedList.setItems(
                FXCollections.observableList(objectList.stream().map(Object::toString).collect(Collectors.toList())));

        redrawElements();
    }

    @FXML
    public void onDrawElement() {
        if (currentObject.validate()) {
            objectList.add(currentObject);
            objectsPlacedList.getItems().add(currentObject.toString());

            currentObject.draw(graphTable.getGraphicsContext2D());
            currentObject = objectNamesToClassReference.get(objectsToPlaceList.getSelectionModel().getSelectedItem());
        } else {
            parameterErrorField.setText("Errors in parameters");
        }
    }

    private void redrawElements() {
        graphTable.getGraphicsContext2D().clearRect(0, 0, graphTable.getWidth(), graphTable.getHeight());
        objectList.forEach(graphicalObject -> graphicalObject.draw(graphTable.getGraphicsContext2D()));
    }

    private class ParameterEditAction implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Arrays.stream(currentObject.getClass().getDeclaredFields())
                    .filter(field -> objectsParametersList.getSelectionModel().getSelectedItem().startsWith(field.getName()))
                    .forEach(field -> {
                        try {
                            field.setAccessible(true);
                            field.set(currentObject, Integer.parseInt(parameterValueInput.getText().trim()));
                            objectsParametersList.getItems()
                                    .set(objectsParametersList.getSelectionModel().getSelectedIndex(),
                                            field.getName() + " = " + parameterValueInput.getText());
                        } catch (Exception e) {
                            parameterErrorField.setText(e.getMessage());
                        }
                    });
        }
    }
}
