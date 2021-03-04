package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;
import lombok.SneakyThrows;
import sample.configuration.CanvasParametersWrapper;
import sample.configuration.CanvasPositionParameters;
import sample.configuration.CanvasRotationParameters;
import sample.configuration.CanvasScaleParameters;
import sample.graphical.algorithm.QuickHull;
import sample.graphical.entity.PointHolder;
import sample.graphical.entity.graphical.*;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller implements Initializable {

    private Map<String, ObservableList<String>> objectsFields;
    private Map<String, GraphicalObject> objectNamesToClassReference;
    private GraphicalObject currentObject;
    private List<GraphicalObject> objectList;
    private ParameterEditAction parameterEditAction = new ParameterEditAction();
    private CanvasParametersWrapper parameters = CanvasParametersWrapper.builder()
            .scaleParameters(CanvasScaleParameters.builder().scale(1.0).build())
            .positionParameters(CanvasPositionParameters.builder()
                    .offset(PointHolder.builder().x(0).y(0).build())
                    .start(PointHolder.builder().x(0).y(0).build())
                    .build())
            .rotationParameters(CanvasRotationParameters.builder()
                    .rotationCenter(PointHolder.builder().x(0).y(0).build())
                    .rotationDegrees(0.0).build())
            .build();

    private DecimalFormat doubleFormatter = new DecimalFormat("#.#");


    private static final int GRID_INTERVALS = 10;
    private static final double SCROLL_SENSITIVE_MULTIPLICATION = 0.0025;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        objectsFields = new HashMap<>();
        objectsFields.put("Point", GraphicalPoint.parametersToObservableList());
        objectsFields.put("Line", GraphicalLine.parametersToObservableList());
        objectsFields.put("Line(section)", GraphicalLineSection.parametersToObservableList());
        objectsFields.put("Circle", GraphicalCircle.parametersToObservableList());
        objectsFields.put("Watch", GraphicalPicture.parametersToObservableList());

        objectNamesToClassReference = new HashMap<>();
        objectNamesToClassReference.put("Point", GraphicalPoint.builder().build());
        objectNamesToClassReference.put("Line", GraphicalLine.builder().build());
        objectNamesToClassReference.put("Line(section)", GraphicalLineSection.builder().build());
        objectNamesToClassReference.put("Circle", GraphicalCircle.builder().build());
        objectNamesToClassReference.put("Watch", GraphicalPicture.builder().build());

        objectList = new ArrayList<>();

        graphTable.getGraphicsContext2D().setFont(new Font(Font.getDefault().getName(), 10));

        scrollPanel.widthProperty().addListener(event -> {
            graphTable.setWidth(scrollPanel.getWidth());
            redrawElements(objectList);
        });
        scrollPanel.heightProperty().addListener(event -> {
            graphTable.setHeight(scrollPanel.getHeight());
            redrawElements(objectList);
        });

        redrawElements(objectList);
        graphTable.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                GraphicalObject object = GraphicalPoint.builder()
                        .x((int) (parameters.getPositionParameters().getOffset().getX() + event.getX() / (parameters.getScaleParameters().getScale())))
                        .y((int) (parameters.getPositionParameters().getOffset().getY() + (graphTable.getHeight() - event.getY()) / (parameters.getScaleParameters().getScale())))
                        .build();

                if (objectList.stream().anyMatch(o -> o.equals(object))) {
                    executionErrorsLabel.setText("Object with these params already exists");
                } else {
                    objectList.add(object);
                    objectsPlacedList.getItems().add(object.toString());

                    object.draw(graphTable, parameters);
                }
            }
        });

        objectsToPlaceList.setItems(FXCollections.observableArrayList(objectsFields.keySet()));
        objectsToPlaceList.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                    objectsParametersList.setItems(objectsFields.get(observableValue.getValue()));
                });

        rotationSlider.valueProperty().addListener((changed, oldValue, newValue) -> {
//            executeGoalButton.setText("Rotate (" + new DecimalFormat("#.##").format(newValue) + "°)");
            parameters.getRotationParameters().setRotationDegrees(newValue.doubleValue());
            int index = objectsPlacedList.getSelectionModel().getSelectedIndex();
            if (index == -1) {
                executionErrorsLabel.setText("No targets selected for rotation");
            } else {
                executionErrorsLabel.setText("");
                parameters.getRotationParameters().setRotationCenter(objectList.get(index).getRotationPoint());
                redrawElements(rotationModeCheckbox.isSelected() ? objectList : Collections.singletonList(objectList.get(index)));
            }
        });

        graphTable.setOnScroll(event -> {
                    if (event.getDeltaY() != 0) {
                        if (parameters.getScaleParameters().getScale() < 0.01 && event.getDeltaY() < 0) {
                            executionErrorsLabel.setText("Stop scaling. Its stupid.");
                        } else {
                            executionErrorsLabel.setText("");
                            parameters.getScaleParameters().setScale((parameters.getScaleParameters().getScale() + event.getDeltaY() * SCROLL_SENSITIVE_MULTIPLICATION)); // parameters.getScaleParameters().getScale());
                            redrawElements(objectList);
                        }
                    }
                }
        );

        graphTable.setOnMousePressed(event -> {
            parameters.getPositionParameters().getStart().setX(event.getX() - parameters.getPositionParameters().getOffset().getX());
            parameters.getPositionParameters().getStart().setY(event.getY() - parameters.getPositionParameters().getOffset().getY());
        });

        graphTable.setOnMouseDragged(event -> {
            if (parameters.getPositionParameters().getStart().getX() != event.getX()
                    || parameters.getPositionParameters().getStart().getY() != event.getY()) {
                parameters.getPositionParameters().getOffset().setX(event.getX() - parameters.getPositionParameters().getStart().getX());
                parameters.getPositionParameters().getOffset().setY(event.getY() - parameters.getPositionParameters().getStart().getY());
                redrawElements(objectList);
            }
        });

        executeGoalButton.setVisible(false);
    }

    @FXML
    private Button highlightElementButton;

    @FXML
    private Button executeGoalButton;

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
    private Label executionErrorsLabel;

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
    private Slider rotationSlider;

    @FXML
    private CheckBox rotationModeCheckbox;

    @FXML
    public void onCreateCanvas() {

    }


    @FXML
    public void onObjectsToPlaceListReload() {

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
        graphTable.getGraphicsContext2D().setFill(Color.RED);
        graphTable.getGraphicsContext2D().setStroke(Color.RED);
        objectList.get(objectsPlacedList.getSelectionModel().getSelectedIndex()).draw(graphTable, parameters);
        graphTable.getGraphicsContext2D().setFill(Color.BLACK);
        graphTable.getGraphicsContext2D().setStroke(Color.BLACK);
    }

    @FXML
    public void onExecuteGoal() {
        parameters.getRotationParameters().setRotationCenter(
                objectsPlacedList.getSelectionModel().getSelectedIndex() == -1 ?
                        PointHolder.builder().x(0).y(0).build() :
                        objectList.get(objectsPlacedList.getSelectionModel().getSelectedIndex()).getRotationPoint());

        redrawElements(objectList);
    }

    @FXML
    public void onEditElement() {
        currentObject = objectList.get(objectsPlacedList.getSelectionModel().getSelectedIndex());
        List<String> stringList = new ArrayList<>();
        Arrays.stream(currentObject.getClass().getDeclaredFields())
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        System.out.println(field.getName() + " - " + field.getModifiers() + " / " + Modifier.FINAL);
                        stringList.add(field.getName() + " = " + field.get(currentObject));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        objectsParametersList.setItems(FXCollections.observableList(stringList));

        createElementButton.setText("Edit");
        createElementButton.setOnAction(event -> {
            if (objectList.stream().filter(o -> o.equals(currentObject)).count() > 1) {
                parameterErrorField.setText("Another object with same params already exists");
            } else {
                objectsPlacedList.getItems().set(objectsPlacedList.getSelectionModel().getSelectedIndex(), currentObject.toString());
                objectsParametersList.setItems(FXCollections.emptyObservableList());
                createElementButton.setText("Create");
                createElementButton.setOnAction(anotherEvent -> onDrawElement());

                redrawElements(objectList);
            }
        });
    }

    @FXML
    public void obDeleteElement() {
        objectList.remove(objectsPlacedList.getSelectionModel().getSelectedIndex());
        objectsPlacedList.setItems(
                FXCollections.observableList(objectList.stream().map(Object::toString).collect(Collectors.toList())));

        redrawElements(objectList);
    }

    @FXML
    public void onDeleteAllElements() {
        objectList.clear();
        objectsPlacedList.getItems().clear();

        redrawElements(objectList);
    }

    @SneakyThrows
    @FXML
    public void onDrawElement() {
        if (currentObject.validate()) {
            if (objectList.stream().anyMatch(o -> o.equals(currentObject))) {
                parameterErrorField.setText("Object with these params already exists");
            } else {
                objectList.add(currentObject.clone());
                objectsPlacedList.getItems().add(currentObject.toString());

                currentObject.draw(graphTable, parameters);
                currentObject = objectNamesToClassReference.get(objectsToPlaceList.getSelectionModel().getSelectedItem());
            }
        } else {
            parameterErrorField.setText("Errors in parameters` values, check if they are above zero");
        }
    }

    @FXML
    private void resizeElements() {
        double resizeScaleX = objectList.stream().mapToDouble(GraphicalObject::getMaxXCoordinate).max().orElse(0);
        double resizeScaleY = objectList.stream().mapToDouble(GraphicalObject::getMaxYCoordinate).max().orElse(0);
        if (resizeScaleX * resizeScaleY == 0) {
            fixInErrorTextHolder.setText("Nothing to fit in");
        } else {
            double newScale = Math.min(graphTable.getHeight(), graphTable.getWidth()) / Math.max(resizeScaleX, resizeScaleY);
            fixInErrorTextHolder.setText("Fitted in [~" + new DecimalFormat("#.##").format(newScale) + "]");
//            parameters.getPositionParameters().getOffset().setX(objectList.stream().mapToDouble(GraphicalObject::getMinXCoordinate).min().orElse(0));
//            parameters.getPositionParameters().getOffset().setY(objectList.stream().mapToDouble(GraphicalObject::getMinYCoordinate).min().orElse(0));
            rescale(newScale);
        }
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

    @FXML
    private void onShowAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Piece of software");
        alert.setContentText("Program that can draw\nwatches with lines. " +
                "\nSlider can pe used to rotate\nsystem or the selected element" +
                "\nPoints can be placed with Create-Edit\nmenu and mouse right-click.");

        alert.showAndWait();
    }

    private void rescale(double newScale) {
        parameters.getScaleParameters().setScale(newScale);
        scaleValueInput.setPromptText("Scale: " + newScale);
        redrawElements(objectList);
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
                            parameterErrorField.setText("");
                        } catch (Exception e) {
                            parameterErrorField.setText("Invalid value of " + field.getName() + ": " + e.getMessage());
                        }
                    });
        }
    }

    private void redrawElements(List<GraphicalObject> list) {
        if (parameters.getScaleParameters().getScale() < 0.01) {
            executionErrorsLabel.setText("Stop scaling. Its stupid.");
            return;
        }
        graphTable.getGraphicsContext2D().clearRect(0, 0, graphTable.getWidth(), graphTable.getHeight());
        System.out.println("scale " + parameters.getScaleParameters().getScale());
        double lineParameter = GRID_INTERVALS * parameters.getScaleParameters().getScale();
        double counter = parameters.getPositionParameters().getOffset().getY();
        double counterIncrement = 10;

        if (lineParameter < 5) {
            lineParameter /= parameters.getScaleParameters().getScale();
            counterIncrement /= parameters.getScaleParameters().getScale();
        }


        graphTable.getGraphicsContext2D().setStroke(Color.DARKGRAY);
        for (double i = 0; i < graphTable.getWidth(); i += lineParameter) {
            graphTable.getGraphicsContext2D().strokeLine(i, 0, i, graphTable.getHeight());
        }

        graphTable.getGraphicsContext2D().setStroke(Color.DARKGRAY);
        for (double i = graphTable.getHeight(); i > 0; i -= lineParameter) {
//            if (counter < 0 && counter + counterIncrement > 0)
//                graphTable.getGraphicsContext2D().setStroke(Color.RED);
//            else
//                graphTable.getGraphicsContext2D().setStroke(Color.DARKGRAY);

            graphTable.getGraphicsContext2D().strokeLine(0, i, graphTable.getWidth(), i);
            graphTable.getGraphicsContext2D().strokeText(doubleFormatter.format(counter), 0, i);

            counter += counterIncrement;
        }

        list.forEach(graphicalObject -> graphicalObject.draw(graphTable, parameters));

    }

    private void drawHullsOnFound(GraphicalPoint point1, GraphicalPoint point2, Supplier<Stream<Pair<GraphicalPoint, Integer>>> resultsSupplier) {
        redrawElements(objectList);
        drawHull(resultsSupplier.get()
                .filter(o -> o.getValue() < 0)
                .map(Pair::getKey)
                .collect(Collectors.toList()), point1);
        drawHull(resultsSupplier.get()
                .filter(o -> o.getValue() > 0)
                .map(Pair::getKey)
                .collect(Collectors.toList()), point2);
    }

    private void drawHull(List<GraphicalPoint> points, GraphicalPoint additionalPoint) {
        points.add(additionalPoint);
        QuickHull quickHull = new QuickHull(points.toArray(new GraphicalPoint[0]));
        graphTable.getGraphicsContext2D().beginPath();
        quickHull.getHullPointsAsVector().forEach(o ->
                graphTable.getGraphicsContext2D().lineTo(o.getX() * parameters.getScaleParameters().getScale(),
                        graphTable.getHeight() - o.getY() * parameters.getScaleParameters().getScale()));

        graphTable.getGraphicsContext2D().closePath();
//        graphTable.getGraphicsContext2D().setFill(Color.GRAY);
//        graphTable.getGraphicsContext2D().setStroke(Color.DARKGRAY);
        graphTable.getGraphicsContext2D().stroke();
    }


}
