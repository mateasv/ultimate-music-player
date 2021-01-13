package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;

public class Main extends Application {

    /**
     * Method used by JavaFX to initialize the GUI
     *
     * @param primaryStage unknown, handled by JavaFX
     * @throws Exception Exception in case of a runtime error
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("player.fxml"));
        primaryStage.setTitle("Ultimate Music Player");
        primaryStage.setScene(new Scene(root, 750, 450));
        primaryStage.show();
    }

    // new File instance for the current directory
    private static File currentDir = new File(System.getProperty("user.dir"));

    // Current song list
    public static ArrayList<String> songsList = getSongsInFolder("songs");

    // ArrayList of the current playlists
    public static ArrayList<Playlist> listOfPlaylists = new ArrayList<>();

    public static void main(String[] args) {



        // Make sure the "songs" folder exists
        if (!folderExists("songs")) {
            if (!createFolder("songs")) {
                System.out.println("There was an error creating the \"songs\" folder.");
            }
        }

        //getSongsInFolder("songs");

        // TEMPORARY
        DB.selectSQL("SELECT * FROM tblSongs");

        int count = 0;
        final int COLUMN_COUNT = 4;
        do{
            String data = DB.getDisplayData();

            // Checks if there is any more data
            if (data.equals(DB.NOMOREDATA)){
                if (count == 0) {
                    System.out.println("\nNo results matching your search term...");
                }
                break;
            }else{
                // Makes a new line if we have a new row
                if ((++count-1) % COLUMN_COUNT == 0) {
                    System.out.print("\n| ");
                }
                System.out.print(data.replaceFirst("\\s++$", "") + " | ");
            }
        } while(true);

        // Initialize JavaFX GUI
        launch(args);
    }

    /**
     * This function is meant to be run only when the program is started.
     * It will query the database for the name of all
     */
    public static ArrayList<String> getPlaylistsFromDatabase() {
        ArrayList<String> tempPlaylists = new ArrayList<>();

        DB.selectSQL("SELECT fldPlaylistName FROM tblPlaylists");

        int count = 0;
        final int COLUMN_COUNT = 1;
        do{
            String data = DB.getDisplayData();

            // Checks if there is any more data
            if (data.equals(DB.NOMOREDATA)) {
                if (count == 0) {
                    System.out.println("\nThere are no playlists stored in the database");
                }
                break;
            } else {
                count++;
                tempPlaylists.add(data.replaceFirst("\\s++$", ""));
            }
        } while(true);

        return tempPlaylists;
    }

    public static ArrayList<String> getSongsFromPlaylist(String playlistName) {
        ArrayList<String> tempPlaylists = new ArrayList<>();

        DB.selectSQL("SELECT fldFilePath FROM tblSongs WHERE fldSongId IN (SELECT fldSongId FROM tblSongsPlaylist WHERE fldPlaylistId = (SELECT fldPlaylistId FROM tblPlaylists WHERE fldPlaylistName = '" + playlistName + "'));");

        int count = 0;
        final int COLUMN_COUNT = 1;
        do{
            String data = DB.getDisplayData();

            // Checks if there is any more data
            if (data.equals(DB.NOMOREDATA)) {
                if (count == 0) {
                    System.out.println("\nNo songs in this playlist...");
                    return null;
                }
                break;
            } else {
                count++;
                tempPlaylists.add(data.replaceFirst("\\s++$", ""));
            }
        } while(true);

        return tempPlaylists;
    }

    /**
     * This method will removed a playlist of the arraylist of playlists.
     *
     * @param index Index of the playlist to be removed
     */
    public static void removePlaylist(int index) {
        listOfPlaylists.remove(index);
    }

    /**
     * This method will return a String array of discovered songs in the "songs" folder.
     *
     * @param folderName The folder to look in
     * @return String array of discovered songs
     */
    public static ArrayList<String> getSongsInFolder(String folderName) {
        // Local String ArrayList which will be returned once this function is finished successfully.
        ArrayList<String> tempSongList = new ArrayList<>();

        // Declares and initializes a String array of files in the given folder (May be null)
        String[] currentFolderFile = new File(currentDir + "\\" + folderName).list();

        // Checks if currentFolderFile to avoid a potential NullPointerException
        if (currentFolderFile == null) {
            System.out.println("Info: No songs were found in the \"songs\" folder.");
            return new ArrayList<>();
        }

        // Add all .mp3 and .wav files to the tempSongList, which will be returned
        for (String song : currentFolderFile) {
            if (song.endsWith(".mp3") || song.endsWith(".wav")) {
                //System.out.println("New song discovered: " + song);
                tempSongList.add(song);
            }
        }

        return tempSongList;
    }



    /**
     * Method to check if the "songs" folder exists.
     *
     * @param folderName The name of the folder to check for
     * @return boolean, true if the folder exists
     */
    public static boolean folderExists(String folderName) {
        String[] contents = currentDir.list();
        assert contents != null;
        for(String content: contents){
            if(content.equals(folderName)){
                // The folder exists!
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a folder with the specified name
     *
     * @param folderName The name of the folder to create
     * @return boolean, true if the folder was created successfully
     */
    public static boolean createFolder(String folderName){
        String path = "\\" + folderName;
        boolean created;
        currentDir = new File(currentDir + path);
        created = currentDir.mkdir();

        return created; // True = created successfully
    }
}
