package org.example.hammingcode;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        Stage stage1 = new Stage();
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("GUI2.fxml"));
        Scene scene2 = new Scene(loader.load());
        Controller2 controller2 = loader.getController();
        stage1.setTitle("Receiver");
        stage1.setScene(scene2);
        stage1.show();
        stage1.setOnCloseRequest(event -> Platform.exit());

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("GUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Controller controller = fxmlLoader.getController();
        controller.errorIndexes = controller2.errorIndexes;
        controller.BitsTextedArea =controller2.BitsTextedArea;
        controller.convertMessage = controller2.convertMessage;
        controller.recivedTextArea = controller2.recivedTextArea;
        controller.error = controller2.error;
        stage.setTitle("Sender");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> Platform.exit());
    }

    public static void main(String[] args) {
        launch();
    }
}