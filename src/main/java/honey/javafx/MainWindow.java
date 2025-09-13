package honey.javafx;

import honey.Honey;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Honey honey;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private Image honeyImage = new Image(this.getClass().getResourceAsStream("/images/Honey.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Honey instance
     */
    public void setHoney(Honey h) {
        honey = h;
        addDialogs(DialogBox.getHoneyDialog(Honey.WELCOME_MESSAGE, honeyImage));
        userInput.requestFocus();
    }

    /**
     * Adds multiple dialog boxes to the dialog container using varargs.
     */
    private void addDialogs(DialogBox... dialogs) {
        dialogContainer.getChildren().addAll(dialogs);
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Honey's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        try {
            String response = honey.getResponse(input);
            if (response.equals(Honey.GOODBYE_MESSAGE)) {
                Platform.exit();
            }
            addDialogs(
                    DialogBox.getUserDialog(input, userImage),
                    DialogBox.getHoneyDialog(response, honeyImage)
            );
        } catch (Exception e) {
            addDialogs(DialogBox.getHoneyDialog(e.getMessage(), honeyImage));
        } finally {
            userInput.clear();
        }
    }
}

