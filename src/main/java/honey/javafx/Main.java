package honey.javafx;

import java.io.IOException;

import honey.Honey;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Honey using FXML.
 */
public class Main extends Application {
    private static final String DATA_PATH = "data/honey.txt";
    private Honey honey = new Honey(DATA_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setHoney(honey); // inject the Honey instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


