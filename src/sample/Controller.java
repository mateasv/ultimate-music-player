package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    // Variables used throughout multiple methods
    private String currentlyPlaying = "";
    private MediaPlayer player = null;
    private String currentSelectedSong = null;

    ObservableList<String> items = FXCollections.observableArrayList (sample.Main.songsList);

    @FXML
    ListView<String> songsList = new ListView<>(items);

    @FXML
    TextField textFieldSearch = new TextField();

    @FXML
    Button playBtn = new Button();

    @FXML
    Label lblCurrentlyPlaying = new Label();

    /**
     * Handles all "On Mouse Clicked" events
     * Will Start playing, Pause the song or resume the media
     * based on the current player status.
     *
     * @param e Event data
     */
    @FXML
    public void playBtnClicked(Event e) {
        System.out.println(e);

        // playBtn.setText(playBtn.getText().equals("PLAY") ? "PAUSE" : "PLAY");

        // Don't play anything if the user never selected a song.
        if (currentSelectedSong == null) {
            return;
        }

        // If the player has never been used, then we can just call the playSong method
        // instantly, since it will give the player variable a new MediaPlayer instance
        if (player == null) {
            playSong(currentSelectedSong);
            return;
        }

        // Figure out whether to play/resume/pause
        switch (player.getStatus()) {
            case READY:
            case STALLED:
            case STOPPED:
                playSong(currentSelectedSong);
                break;
            case PAUSED:
                playBtn.setText("⏸");
                player.play();
                break;
            case PLAYING:
                playBtn.setText("⏵");
                player.pause();
                break;
        }
    }

    /**
     * Handles all "On Mouse Clicked" events from the songs list.
     * Since we can't (easily, without regex nor substring) get the
     * song name from the event, we are using the getSelectionModel()
     * method on the songList to instantly get the selected
     * song on a user click.
     *
     * @param e Event data
     */
    @FXML
    public void songsListClick(Event e) {
        currentSelectedSong = songsList.getSelectionModel().getSelectedItem();

        if (!currentSelectedSong.equals(currentlyPlaying)) {
            currentlyPlaying = currentSelectedSong;
            return;
        }

        lblCurrentlyPlaying.setText("Currently Playing: " + currentSelectedSong);
        // Check if file exists
        //media = new Media("\\songs\\" + currentSelectedSong.replace(" ", "%20"));
        //System.out.println(("file:///" + new File(System.getProperty("user.dir")) + "/songs/" + currentSelectedSong.replace(" ", "%20")).replace("\\", "/"));
        // Initialize


        if (player == null) {
            playSong(currentSelectedSong);
            return;
        }

        // Ready? Play the song!
        // Already playing? Stop the previous song!
        switch (player.getStatus()) {
            case READY:
                break;
            case PAUSED:
            case STALLED:
            case STOPPED:
            case PLAYING:
                player.stop();
                break;
        }

        playSong(currentSelectedSong);
    }

    /**
     * Will load the given songPath (parameter), initialize a new
     * MediaPlayer, and start the new song. We do not check the
     * status of the previous player. We do that before calling
     * this method.
     * @param songPath String, the name of the song file. The rest of the file path will be added by the method.
     */
    public void playSong(String songPath) {
        // Load the new (or first) media to play!
        Media media = new Media(("file:///" + new File(System.getProperty("user.dir")) + "/songs/" + songPath.replace(" ", "%20")).replace("\\", "/"));
        player = new MediaPlayer(media);

        // Change the "START" button to "PAUSE"
        playBtn.setText("⏸");

        // Play!
        player.play();
    }

    /**
     * Adds all songs to the song list in the UI on program run
     *
     * @param location unused URL parameter
     * @param resources unused ResourceBundle parameter
     */
    public void initialize(URL location, ResourceBundle resources) {
        songsList.setItems(items);
    }
}
