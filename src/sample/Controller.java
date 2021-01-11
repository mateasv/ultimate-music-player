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
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    // Variables used throughout multiple methods
    private String currentlyPlaying = "";
    private MediaPlayer player = null;
    private String currentSelectedSong = null;
    private int queuePosition = 0;

    ObservableList<String> items = FXCollections.observableArrayList (sample.Main.songsList);

    @FXML
    ListView<String> songsList = new ListView<>(items);

    @FXML
    ListView<String> playlistList = new ListView<>();

    @FXML
    ListView<String> queueList = new ListView<>();

    @FXML
    TextField textFieldSearch = new TextField();

    @FXML
    Button playBtn = new Button();

    @FXML
    Button nextBtn = new Button();

    @FXML
    Button prevBtn = new Button();

    @FXML
    Label lblCurrentlyPlaying = new Label();

    @FXML
    TextField textFieldPlaylistName = new TextField();

    /**
     * Handles all "On Mouse Clicked" events
     * Will Start playing, Pause the song or resume the media
     * based on the current player status.
     *
     * @param e Event data
     */
    @FXML
    public void playBtnClicked(Event e) {
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
     * Handles all "On Mouse Clicked" events
     * Will stop playing the current song
     * @param e Event data
     */
    @FXML
    public void stopBtnClicked(Event e) {
        // Nothing to stop if the player does not exist.
        if (player == null) {
            return;
        }

        // Properly stops the media player
        switch (player.getStatus()) {
            case READY:
            case STOPPED:
                // There is nothing to stop
                break;
            case STALLED:
            case PAUSED:
            case PLAYING:
                playBtn.setText("⏵");
                player.stop();
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

        // Makes sure that you have double clicked
        if (!currentSelectedSong.equals(currentlyPlaying)) {
            currentlyPlaying = currentSelectedSong;
            return;
        }

        // Adds the song to the queue and plays it.
        addSongToQueue(currentSelectedSong);

        // Play the song.
        playSong(currentSelectedSong);
    }

    /**
     * Will load the given songPath (parameter), initialize a new
     * MediaPlayer, and start the new song. We also check the status
     * of the previous player to avoid issues.
     * @param songPath String, the name of the song file. The rest of the file path will be added by the method.
     */
    public void playSong(String songPath) {
        lblCurrentlyPlaying.setText("Currently Playing: " + songPath);
        // Check if file exists
        //media = new Media("\\songs\\" + currentSelectedSong.replace(" ", "%20"));
        //System.out.println(("file:///" + new File(System.getProperty("user.dir")) + "/songs/" + currentSelectedSong.replace(" ", "%20")).replace("\\", "/"));
        // Initialize


        // If we have never used the player before, then we can
        // just start skip this and start playing.
        // If we have used the player before, then make sure
        // that it's ready to play new media.
        if (player != null) {
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
        }

        // Load the new (or first) media to play!
        Media media = new Media(("file:///" + new File(System.getProperty("user.dir")) + "/songs/" + songPath.replace(" ", "%20")).replace("\\", "/"));
        player = new MediaPlayer(media);

        // Change the "START" button to "PAUSE"
        playBtn.setText("⏸");

        // Play!
        player.play();
    }

    /**
     * Creates a new playlist with the name written in the
     * 'textFieldPlaylistName' text field. Nothing happens
     * if a playlist with the same name already exists.
     * This playlist will also automatically be added to
     * the database.
     * @param e Event data
     */
    public void createPlaylistBtnClicked(Event e) {
        // Check if the user wrote anything
        if (textFieldPlaylistName.getText().length() == 0) {
            return;
        }

        // temporary local variable to track whether
        // we found a playlist with the same name.
        boolean playlistExists = false;

        // Check if a playlist with the given name already exists
        // (The UI is already synced with the database, no need to check the db)
        for (String playlistName : playlistList.getItems()) {
            if (textFieldPlaylistName.getText().equals(playlistName)) {
                return;
            }
        }

        // If we are here in the code, we know that a playlist with
        // the given name does not already exist
        playlistList.getItems().add(textFieldPlaylistName.getText());

        // Add a new playlist
        sample.Main.listOfPlaylists.add(new Playlist(textFieldPlaylistName.getText(), new ArrayList<>()));

        // 1. Add the playlist to the database!
        // TODO: INSERT INTO tblPlaylists (fldPlaylistName) VALUES ('PLAYLIST_NAME_HERE');
        // INSERT INTO table_name (column1, column2, column3, ...) VALUES (value1, value2, value3, ...);
    }

    /**
     * Adds the currently playing song to the selected playlist.
     * @param e Event data
     */
    public void addSongToPlaylistClicked(Event e) {
        String selItem = playlistList.getSelectionModel().getSelectedItem();

        // Temporary for testing
        if (selItem == null) {
            System.out.println("No playlist selected");
        } else {
            System.out.println("Playlist " + selItem + " selected!");
        }

        // TODO: Add the song to the playlist:
        // sample.Main.listOfPlaylists.get(0).addToPlaylist(new Song("baby", "kris", "songs"));
        // TODO: Also update the database
    }

    /**
     * Plays the previous song from the queue. If we are already
     * at the first song, then nothing will happen.
     * @param e Event data
     */
    public void prevBtnClicked(Event e) {
        // Make sure we aren't already at the first song in the queue
        if (queuePosition == 0) {
            return;
        }

        // Update the queue tracker
        queuePosition -= 1;

        // Scroll to and select the new song in the queue
        selectSongInQueue(queuePosition);

        // Update the "Currently Playing" text in the UI
        lblCurrentlyPlaying.setText("Currently Playing: " + queueList.getItems().get(queuePosition));

        // Play the song!
        playSong(queueList.getItems().get(queuePosition));
    }

    /**
     * Plays the next song in the queue. If we are already
     * at the last song, then nothing will happen.
     * @param e Event data
     */
    public void nextBtnClicked(Event e) {
        // Make sure we aren't already at the last song in the queue
        if (queuePosition == queueList.getItems().size()-1) {
            return;
        }

        // Update the queue tracker
        queuePosition += 1;

        // Scroll to and select the new song in the queue
        selectSongInQueue(queuePosition);

        // Update the "Currently Playing" text in the UI
        lblCurrentlyPlaying.setText("Currently Playing: " + queueList.getItems().get(queuePosition));

        // Play the song!
        playSong(queueList.getItems().get(queuePosition));
    }

    /**
     * Will delete the currently selected menu.
     * Nothing happens if no playlist is selected.
     * Will automatically update the database!
     */
    public void deletePlaylistBtnClicked() {
        // Get current selected playlist, null if none.
        String selItem = playlistList.getSelectionModel().getSelectedItem();

        // Checks if the user selected any playlist, if no playlist
        // is selected, then we do not know which playlist to delete.
        if (selItem == null) {
            return;
        }

        System.out.println(selItem);

        // selItem = Playlist name
        //
        // 1. Delete playlist from the UI
        // 2. Delete all songs related to the deleted playlist
        // TODO: DELETE * FROM tblSongsPlaylist WHERE fldPlaylistId = (SELECT fldPlaylistId FROM tblPlaylists WHERE fldPlaylistName = 'PLAYLIST_NAME_HERE');
        // 3. Delete the playlist from the database
        // TODO: DELETE * FROM tblPlaylists WHERE fldPlaylistName = 'PLAYLIST_NAME_HERE';
    }

    // Currently not in use, will add a song to a given playlist
    // All created playlists can be found at ArrayList<Playlist> sample.Main.listOfPlaylists
    // Make sure to update the database so we always are in sync!
    public void addSongToPlaylist(String playlistName) {
        // Add a song to the first playlist
        sample.Main.listOfPlaylists.get(0).addToPlaylist(new Song("baby", "kris", "songs"));
    }

    /**
     * Adds a song (String) to the bottom of the queue list.
     * @param songName String, the exact name of the song.
     */
    public void addSongToQueue(String songName) {
        queueList.getItems().add(songName);
        selectSongInQueue(queueList.getItems().size()-1);
        queuePosition = queueList.getItems().size()-1;
    }

    /**
     * Selects a song in the queue list at a given index.
     * By select, we mean as if a user clicked on it.
     * @param index int - index of item to select
     */
    public void selectSongInQueue(int index) {
        // Do nothing if the given index is higher than the count of songs in the queue
        if (queueList.getItems().size()-1 < index) {
            return;
        }

        // Scroll to and select the new song in the queue
        queueList.scrollTo(index);
        queueList.getSelectionModel().select(index);
        queueList.getFocusModel().focus(index);
    }

    /**
     * Adds all songs to the song list in the UI on program run
     *
     * @param location unused URL parameter
     * @param resources unused ResourceBundle parameter
     */
    public void initialize(URL location, ResourceBundle resources) {
        // Adds all songs to the songsList when the UI initializes
        songsList.setItems(items);
        // TODO: Get all playlists from the database and add them to the UI
        //       and ArrayList<Playlist> sample.Main.listOfPlaylists
    }
}
