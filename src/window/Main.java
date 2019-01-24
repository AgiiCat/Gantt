package window;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane root = FXMLLoader.load(getClass().getResource("mainwindow.fxml"));

        Scene mainScene = new Scene(root, 960, 540);
        mainScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Gantt chart by Jakub Buziewicz");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


    public static void main(String[] args) { Application.launch(args); }
}
