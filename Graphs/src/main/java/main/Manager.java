package main;

import java.io.IOException;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class Manager extends Application {

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        javafx.scene.text.Font.loadFont(Manager.class.
            getResource("/assets/jlm_cmex10.ttf").toExternalForm(), 1);

        javafx.scene.text.Font.loadFont(Manager.class.
            getResource("/assets/jlm_cmmi10.ttf").toExternalForm(), 1);
        javafx.scene.text.Font.loadFont(Manager.class.
            getResource("/assets/jlm_cmsy10.ttf").toExternalForm(), 1);
        javafx.scene.text.Font.loadFont(Manager.class.
            getResource("/assets/jlm_cmr10.ttf").toExternalForm(), 1);

        javafx.scene.text.Font.loadFont(Manager.class.
            getResource("/assets/jlm_fcmrpg.ttf").toExternalForm(), 1);
        javafx.scene.text.Font.loadFont(Manager.class.
            getResource("/assets/jlm_eufb10.ttf").toExternalForm(), 1);
        javafx.scene.text.Font.loadFont(Manager.class.
            getResource("/assets/jlm_special.ttf").toExternalForm(), 1);

        Parent root = FXMLLoader.load(Manager.class.getResource(
            "/MainScene.fxml"));
        stage.setTitle("Graph-tool");

        root.getStylesheets().add(getClass().getResource("/MyStyle.css").toString());
        stage.setScene(new Scene(root));

        stage.setMinWidth(900);
        stage.setMinHeight(640);

        stage.getScene().setOnKeyReleased(Controller.shortCuts);

        FileManager.setStage(stage);

        stage.show();
    }
}

