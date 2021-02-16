package View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {

    private static Stage primaryStage;

    static public Stage getPrimaryStage()
    {
        return Main.primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        MyViewController controller = fxmlLoader.getController();

        primaryStage.setTitle("90's Maze Game");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();


        //closing the program from the X button
        primaryStage.setOnCloseRequest(e ->
        {
            e.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Closing Program");
            alert.setHeaderText("You are going to lose the game");
            alert.setContentText("Are you sure you want to exit the program?");
            alert.showAndWait();

            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK)
            {
                controller.getViewModel().stopServers();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
