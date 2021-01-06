package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    ObservableList<String> items = FXCollections.observableArrayList (
            "Test Song 1", "Test Song 2", "Test Song 3", "Test Song 4");

    @FXML
    ListView<String> songsList = new ListView<String>(items);

    @FXML
    TextField textFieldSearch = new TextField();

    @FXML
    Button playBtn = new Button();



    @FXML
    public void playBtnClicked(Event e) {
        playBtn.setText(playBtn.getText().equals("PLAY") ? "PAUSE" : "PLAY");
    }

    public void initialize(URL location, ResourceBundle resources) {
        songsList.setItems(items);
    }
}
