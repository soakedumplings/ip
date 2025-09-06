package honey.javafx;

import honey.Honey;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {
    String dataPath = "data/honey.txt";
    private Honey honey = new Honey(dataPath);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setHoney(honey);  // inject the Honey instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


