package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import sample.graphical.GraphicalObject;

import java.util.ArrayList;
import java.util.List;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();

        Path path = new Path();

        //Moving to the starting point
        MoveTo moveTo = new MoveTo(0, 0);

        //Creating 1st line
        LineTo line1 = new LineTo(321, 161);

        //Creating 2nd line
        LineTo line2 = new LineTo(126,232);

        //Creating 3rd line
        LineTo line3 = new LineTo(232,52);

        //Creating 4th line
        LineTo line4 = new LineTo(269, 250);

        LineTo line5 = new LineTo(108, 71);

        path.getElements().add(moveTo);
        path.getElements().addAll(line1);

        Group root = new Group(path);

        stage.setTitle("Drawing an arc through a path");

//        TextField xCoordField

        Button buttonLeft = new Button("Move Left");
        buttonLeft.setOnAction(event -> {
            line1.setX(line1.getX() + 10);
//            buttonLeft.setText("THIS BUTTON IS TESTED");
        });Button buttonRight = new Button("Move Right");
        buttonRight.setOnAction(event -> {
            line1.setX(line1.getX() - 10);

//            buttonRight.setText("THIS BUTTON IS TESTED");
        });

        HBox hbox = new HBox(buttonLeft, buttonRight, objectListView, root);
        Scene scene = new Scene(hbox, 600, 300);

        stage.setScene(scene);
        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
