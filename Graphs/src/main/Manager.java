package main;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

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

        //javafx.scene.text.Font.loadFonts(Manager.class.getResource("/assets/maths").toExternalForm(),1);
       // Font.
        Parent root = FXMLLoader.load(Manager.class.getResource(
                "MainScene.fxml"));
        stage.setTitle("Graph-tool");

        root.getStylesheets().add(getClass().getResource("MyStyle.css").toString());
        stage.setScene(new Scene(root));

        stage.setMinWidth(900);
        stage.setMinHeight(640);

        stage.getScene().setOnKeyReleased(Controller.shortCuts);

        FileManager.setStage(stage);


        stage.show();
    }
}
