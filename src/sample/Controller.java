package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import lombok.SneakyThrows;
import sample.graphical.GraphicalObject;
import sample.graphical.entity.GraphicalCircle;
import sample.graphical.entity.GraphicalLine;
import sample.graphical.entity.GraphicalLineSection;
import sample.graphical.entity.GraphicalPoint;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    Map<String, ObservableList<String>> objectsFields;
    Map<String, GraphicalObject> objectNamesToClassReference;
    GraphicalObject currentObject;
    List<GraphicalObject> objectList;

    boolean inited = false;

    private static final int GRID_INTERVALS = 10;

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
    private Button fitAllObjectsButton;

    @FXML
    private Button applyNewScaleButton;

    @FXML
    private Label parameterErrorField;

    @FXML
    private Label fixInErrorTextHolder;

    @FXML
    private TextField parameterValueInput;

    @FXML
    private TextField scaleValueInput;

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

        if (!inited) {
            redrawElements();
            scaleValueInput.setText("1.0");
            inited = true;
        }
    }


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
//                    parameterValueInput.setOnAction(event -> System.out.println("ENTER TAPPED"));
                    setParameterValueButton.setOnAction(event -> Arrays.stream(currentObject.getClass().getDeclaredFields())
                            .filter(field -> observableValue.getValue().startsWith(field.getName()))
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
                            }));
                });
    }

    @FXML
    public void onHighlightElement() {
//        graphTable.getGraphicsContext2D().;
//        graphTable.scaleXProperty().set(2);
//        graphTable.scaleYProperty().set(2);
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

    @SneakyThrows
    @FXML
    public void onDrawElement() {
        if (currentObject.validate()) {
            objectList.add(currentObject.clone());
            objectsPlacedList.getItems().add(currentObject.toString());

            currentObject.draw(graphTable);
            currentObject = objectNamesToClassReference.get(objectsToPlaceList.getSelectionModel().getSelectedItem());
        } else {
            parameterErrorField.setText("Errors in parameters");
        }
    }

    @FXML
    private void resizeElements() {
        double resizeScaleX = objectList.stream().mapToDouble(GraphicalObject::getMaxXCoordinate).max().orElse(0);
        double resizeScaleY = objectList.stream().mapToDouble(GraphicalObject::getMaxYCoordinate).max().orElse(0);
        if (resizeScaleX * resizeScaleY == 0) {
            fixInErrorTextHolder.setText("Nothing to fit in");
        } else {
            fixInErrorTextHolder.setText("Fitted in");
//            System.out.println("//>> Math.max(resizeScaleX, resizeScaleY) = " + Math.max(resizeScaleX, resizeScaleY));
//            System.out.println("//>> Math.min(graphTable.getScaleX(), graphTable.getScaleY()) = " + Math.min(graphTable.getHeight(), graphTable.getWidth()));
            double newScale = Math.min(graphTable.getHeight(), graphTable.getWidth()) / Math.max(resizeScaleX, resizeScaleY);
            rescale(newScale);
        }
    }

    private void redrawElements() {
//        System.out.println(">> redrawing with scale " + graphTable.getScaleX());
        graphTable.getGraphicsContext2D().clearRect(0, 0, graphTable.getWidth(), graphTable.getHeight());

        graphTable.getGraphicsContext2D().setStroke(Color.DARKGRAY);
        for (double i = 0; i < graphTable.getWidth(); i += GRID_INTERVALS)
            graphTable.getGraphicsContext2D().strokeLine(i, GRID_INTERVALS, i, graphTable.getHeight());

        graphTable.getGraphicsContext2D().setStroke(Color.DARKGRAY);
        for (double i = graphTable.getHeight(); i > 0; i -= GRID_INTERVALS)
            graphTable.getGraphicsContext2D().strokeLine(GRID_INTERVALS, i, graphTable.getWidth(), i);

        objectList.forEach(graphicalObject -> graphicalObject.draw(graphTable));
        objectList.forEach(System.out::println);
    }

    @FXML
    private void applyNewScale() {
        try {
            double newScale = Double.parseDouble(scaleValueInput.getText());
            rescale(newScale);
        } catch (Exception e) {
            fixInErrorTextHolder.setText(e.getMessage());
        }
    }

    private void rescale(double newScale) {
        graphTable.setScaleX(newScale);
        graphTable.setScaleY(newScale);
        scaleValueInput.setPromptText("Scale: " + newScale);
//        graphTable.setOnDragDetected(event -> event.get);
        System.out.println(">> moved");
        redrawElements();
        graphTable.getGraphicsContext2D().moveTo(0,0);
    }
}
