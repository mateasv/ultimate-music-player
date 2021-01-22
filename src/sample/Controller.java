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

import static sample.Main.getPlaylistsFromDatabase;
import static sample.Main.getSongsFromPlaylist;

public class Controller implements Initializable {

    // Variables used throughout multiple methods
    private String currentlyPlaying = "";
    private MediaPlayer player = null;
    private String currentSelectedSong = null;
    private String currentSelectedPlaylist = "";
    private int queuePosition = 0;

    ObservableList<String> items = FXCollections.observableArrayList(sample.Main.songsList);

    @FXML
    ListView<String> songsList = new ListView<>(items);

    @FXML
    ListView<String> playlistList = new ListView<>();

    @FXML
    ListView<String> queueList = new ListView<>();

    @FXML
    ListView<String> searchResultsList = new ListView<>();

    @FXML
    TextField textFieldSearch = new TextField();

    @FXML
    Button playBtn = new Button();

    @FXML
    Button nextBtn = new Button();

    @FXML
    Button prevBtn = new Button();

    @FXML
    Button searchBtn = new Button();

    @FXML
    Label lblCurrentlyPlaying = new Label();

    @FXML
    TextField textFieldPlaylistName = new TextField();

    /**
     * Will take the text from textFieldSearch and search and find
     * all matching songs and/or artists.
     */
    public void searchBtnClicked() {
        String srch = textFieldSearch.getText().toLowerCase();
        searchResultsList.getItems().clear();
        for (String songName : songsList.getItems()) {
            if (songName.toLowerCase().contains(srch)) {
                searchResultsList.getItems().add(songName);
            }
        }
    }

    /**
     * Handles all clicks in the searchResultsList Listview
     * It will play the song that have been clicked
     */
    public void searchResultsListClicked() {
        currentSelectedSong = searchResultsList.getSelectionModel().getSelectedItem();

        // Clicking the list without clicking on an item is just ignored
        if (currentSelectedSong == null) {
            return;
        }

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
     *
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

        // Clicking the playlistList without clicking on an item is just ignored
        if (currentSelectedSong == null) {
            return;
        }

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
     * Clicked event on the playlistList ListView.
     * When a playlist is clicked twice, the songs
     * from the playlist will be added to the queue.
     * @param e Event data
     */
    @FXML
    public void playlistListClicked(Event e) {
        // Get the current selected item from the playlist ListView
        String tmpCrnt = playlistList.getSelectionModel().getSelectedItem();

        // Clicking the playlistList without clicking on an item is just ignored
        if (tmpCrnt == null) {
            return;
        }

        // Makes sure that you have double clicked
        if (!currentSelectedPlaylist.equals(tmpCrnt)) {
            currentSelectedPlaylist = playlistList.getSelectionModel().getSelectedItem();
            return;
        }

        // Reset so you have to doubleclick
        currentSelectedPlaylist = "";

        // Import songs from the database
        ArrayList<String> tmpSongList = getSongsFromPlaylist(tmpCrnt);
        if (tmpSongList == null) {
            return;
        }
        queueList.getItems().addAll(tmpSongList);
    }

    /**
     * Will load the given songPath (parameter), initialize a new
     * MediaPlayer, and start the new song. We also check the status
     * of the previous player to avoid issues.
     *
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
     *
     * @param e Event data
     */
    public void createPlaylistBtnClicked(Event e) {
        // The playlist name given by the user.
        String writtenPlaylistName = textFieldPlaylistName.getText();

        // Check if the user wrote anything
        // Do nothing if no playlist name is provided
        if (writtenPlaylistName.length() == 0) {
            return;
        }

        // temporary local variable to track whether
        // we found a playlist with the same name.
        boolean playlistExists = false;

        // Check if a playlist with the given name already exists
        // (The UI is already synced with the database, no need to check the db)
        for (String playlistName : playlistList.getItems()) {
            if (writtenPlaylistName.equals(playlistName)) {
                return;
            }
        }

        // If we are here in the code, we know that a playlist with
        // the given name does not already exist
        playlistList.getItems().add(writtenPlaylistName);

        // Add the playlist to the database
        DB.insertSQL("INSERT INTO tblPlaylists (fldPlaylistName) VALUES ('" + writtenPlaylistName + "')");
    }

    /**
     * Adds the currently playing song to the selected playlist.
     *
     * @param e Event data
     */
    public void addSongToPlaylistClicked(Event e) {
        String selItem = playlistList.getSelectionModel().getSelectedItem();

        // Check if any playlist is selected.
        if (selItem == null) {
            System.out.println("No playlist selected");
            return;
        }

        // This is true if the user haven't played a song yet.
        if (currentlyPlaying.length() == 0) {
            return;
        }

        //Checks if the song is already in the playlist
        int songID = 0;
        DB.selectSQL("SELECT COUNT(fldSongID) FROM tblSongsPlaylist WHERE fldSongId = (SELECT fldSongId FROM tblSongs WHERE fldFilePath = '" + currentlyPlaying + "') AND fldPlaylistId = (SELECT fldPlaylistId FROM tblPlaylists WHERE fldPlaylistName= '" + selItem + "')");
        songID = Integer.parseInt(DB.getData());
        clearData();

        // 0 = not in playlist. 1+ = the song is in the playlist
        if(songID == 0) {

            // Should work, haven't tried since songs aren't added to the database yet!
            // TODO: Make this work with special characters, such as '
            currentlyPlaying = currentlyPlaying.replace("\'", "\\\'");
            DB.insertSQL("INSERT INTO tblSongsPlaylist (fldSongId, fldPlaylistId) VALUES ((SELECT fldSongId FROM tblSongs WHERE fldFilePath = '" + currentlyPlaying + "'), (SELECT fldPlaylistId FROM tblPlaylists WHERE fldPlaylistName = '" + selItem + "'))");
            //System.out.println("INSERT INTO tblSongsPlaylist (fldSongId, fldPlaylistId) VALUES ((SELECT fldSongId FROM tblSongs WHERE fldFilePath = '" + currentlyPlaying + "'), (SELECT fldPlaylistId FROM tblPlaylists WHERE fldPlaylistName = '" + selItem + "'))");

        } else {
            System.out.println("That song is already in the playlist");
        }
    }

    /**
     * Removes the currently playling song from the selected playlist.
     * @param e Event data
     */
    public void removeSongFromPlaylistClicked(Event e) {
        // Get the current selected playlist
        String selItem = playlistList.getSelectionModel().getSelectedItem();

        // Check if any playlist is selected.
        if (selItem == null) {
            System.out.println("No playlist selected");
            return;
        }

        // This is true if the user haven't played a song yet.
        if (currentlyPlaying.length() == 0) {
            return;
        }

        // Delete all rows from tblSongsPlaylist, where the fldSongId is the song
        // currently playing, and fldPlaylistId is the currently selected playlist.
        DB.deleteSQL("DELETE FROM tblSongsPlaylist WHERE fldSongId = (SELECT fldSongId FROM tblSongs WHERE fldFilePath = '" + currentlyPlaying + "') AND fldPlaylistId = (SELECT fldPlaylistId FROM tblPlaylists WHERE fldPlaylistName= '" + selItem + "')");
    }

    /**
     * Plays the previous song from the queue. If we are already
     * at the first song, then nothing will happen.
     *
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
     *
     * @param e Event data
     */
    public void nextBtnClicked(Event e) {
        // Make sure we aren't already at the last song in the queue
        if (queuePosition == queueList.getItems().size() - 1) {
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

        // Remove the playlist from the UI
        playlistList.getItems().remove(playlistList.getSelectionModel().getSelectedIndex());

        // Delete all songs in the playlist. No point in storing which songs were stored in a playlist that no longer exists
        DB.deleteSQL("DELETE FROM tblSongsPlaylist WHERE fldPlaylistId = (SELECT fldPlaylistId FROM tblPlaylists WHERE fldPlaylistName = '" + selItem + "')");

        // Delete the playlist from the database.
        DB.deleteSQL("DELETE FROM tblPlaylists WHERE fldPlaylistName = '" + selItem + "'");
    }

    /**
     * Adds a song (String) to the bottom of the queue list.
     *
     * @param songName String, the exact name of the song.
     */
    public void addSongToQueue(String songName) {
        queueList.getItems().add(songName);
        selectSongInQueue(queueList.getItems().size() - 1);
        queuePosition = queueList.getItems().size() - 1;
    }

    /**
     * Selects a song in the queue list at a given index.
     * By select, we mean as if a user clicked on it.
     *
     * @param index int - index of item to select
     */
    public void selectSongInQueue(int index) {
        // Do nothing if the given index is higher than the count of songs in the queue
        if (queueList.getItems().size() - 1 < index) {
            return;
        }

        // Scroll to and select the new song in the queue
        queueList.scrollTo(index);
        queueList.getSelectionModel().select(index);
        queueList.getFocusModel().focus(index);
    }

    /**
     * Tommy's do while loop put in a method so we can use it through out the code and easily clear
     * the DB.Select statements
     */
    public static void clearData() {
        do {
            String data = DB.getDisplayData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                System.out.print(data);
            }
        } while (true);
    }

    /**
     * Adds all songs to the song list in the UI on program run
     *
     * @param location  unused URL parameter
     * @param resources unused ResourceBundle parameter
     */
    public void initialize(URL location, ResourceBundle resources) {
        // Adds all songs to the songsList when the UI initializes
        songsList.setItems(items);

        // Adds all playlists to the UI
        playlistList.setItems(FXCollections.observableArrayList(getPlaylistsFromDatabase()));
    }
}
