package honey.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;


public class DialogBox extends HBox {

    private Label text;
    private ImageView displayPicture;

    public DialogBox(String s, Image i) {
        text = new Label(s);
        displayPicture = new ImageView(i);

        //Styling the dialog box
        text.setWrapText(true);
        text.setMinHeight(Region.USE_PREF_SIZE);
        text.setPrefHeight(Region.USE_COMPUTED_SIZE);
        text.setMaxHeight(Region.USE_COMPUTED_SIZE);
        text.getStyleClass().add("label"); // Apply CSS class for styling
        // Set text width to be responsive to parent container
        text.maxWidthProperty().bind(this.widthProperty().multiply(0.75));
        displayPicture.setFitWidth(100.0);
        displayPicture.setFitHeight(100.0);
        displayPicture.setId("displayPicture");
        this.setAlignment(Pos.TOP_RIGHT);

        // Load CSS stylesheet
        this.getStylesheets().add(getClass().getResource("/css/dialog-box.css").toExternalForm());

        this.getChildren().addAll(text, displayPicture);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
        text.getStyleClass().add("reply-label");
    }

    public static DialogBox getUserDialog(String s, Image i) {
        return new DialogBox(s, i);
    }

    public static DialogBox getHoneyDialog(String s, Image i) {
        var db = new DialogBox(s, i);
        db.flip();
        return db;
    }

}
