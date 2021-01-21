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
        primaryStage.setScene(new Scene(root, 1080, 800));
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
        // If not, create it.
        if (!folderExists("songs")) {
            if (!createFolder("songs")) {
                System.out.println("There was an error creating the \"songs\" folder.");
            }
        }

        // Sync songs folder with the database
        syncDatabaseSongsFolder();

        // Initialize JavaFX GUI
        launch(args);
    }


    /**
     * Syncs the Database and the songs folder
     * Songs that are no longer in the songs folder will be removed from the database
     * All playlists with deleted songs, will have the deleted songs removed from the playlists
     * Adds all songs in the songs folder to the database, that haven't been added yet.
     * (Not done in the order written in this comment)
     */
    public static void syncDatabaseSongsFolder() {

        // Get ArrayList of all songs in database
        ArrayList<String> songsInDatabase = new ArrayList<>();

        // Execute the query
        DB.selectSQL("SELECT fldFilePath FROM tblSongs");

        // Iterate over every song found in the database, and add them to the above arraylist
        int count = 0;
        do{
            String data = DB.getDisplayData();

            // Checks if there is any more data
            if (data.equals(DB.NOMOREDATA)){
                if (count == 0) {
                    System.out.println("\nNo songs were found in the database");
                }
                break;
            }else{
                // Add the song to the ArrayList of songs in the database
                count++;
                songsInDatabase.add(data.replaceFirst("\\s++$", ""));
            }
        } while(true);

        // 1. Songs that are in the DB but no longer in the songs folder are removed from all playlists!
        // 2. Then they're also deleted from the tblSongs table.
        ArrayList<String> songsNoLongerInDatabase = (ArrayList<String>) songsInDatabase.clone();
        songsNoLongerInDatabase.removeAll(songsList);

        // Check if we even have anything to remove
        if (songsNoLongerInDatabase.size() != 0) {
            // Delete statement, to remove from the tblSongs table
            StringBuilder sb = new StringBuilder();

            // Select statement, to get the ID's of all songs to be removed
            StringBuilder sbIds = new StringBuilder();

            // Add the start of the queries to the stringbuilders
            sb.append("DELETE FROM tblSongs WHERE fldFilePath IN ('");
            sbIds.append("SELECT fldSongId FROM tblSongs WHERE fldFilePath IN ('");

            // Add the songs to be removed, to the queries
            for (int i = 0; i < songsNoLongerInDatabase.size(); i++) {
                sb.append(songsNoLongerInDatabase.get(i));
                sbIds.append(songsNoLongerInDatabase.get(i));
                sb.append('\'');
                sbIds.append('\'');

                // Make sure we have no trailing commas
                if (i < songsNoLongerInDatabase.size()-1) {
                    sb.append(", '");
                    sbIds.append(", '");
                }
            }

            // Finished both queries
            sb.append(')');
            sbIds.append(')');

            // Remember, we haven't executed any of the queries yet.
            // They are ready to be used in the right order though!

            // ArrayList to store the IDs of the songs to be removed
            ArrayList<Integer> songIdsToDelete = new ArrayList<>();

            System.out.println(sbIds.toString());

            // Run sbIds query (To get IDs of songs to be removed)
            DB.selectSQL(sbIds.toString());
            count = 0;
            do{
                // Get next row from data
                String data = DB.getDisplayData();

                // Checks if there is any more data
                if (data.equals(DB.NOMOREDATA)) {
                    // If we never got any data, let the developer know.
                    if (count == 0) {
                        System.out.println("\nThere are no songs to be deleted");
                    }
                    break;
                } else {
                    count++;
                    // Add the ID to the songIdsToDelete ArrayList
                    songIdsToDelete.add(Integer.parseInt(data.replaceFirst("\\s++$", ""))); // Not good that DB.getDisplayData() gives us strings
                }
            } while(true);

            // We can reuse the sbIds variable (Garbage collector will remove the old StringBuilder old from memory)
            sbIds = new StringBuilder();

            // Add the start of the query to the StringBuilder instance
            sbIds.append("DELETE FROM tblSongsPlaylist WHERE fldSongId IN (");

            // Add every ID to the query
            for (int i = 0; i < songIdsToDelete.size(); i++) {
                sbIds.append(songIdsToDelete.get(i));

                // Avoid trailing commas
                if (i < songsNoLongerInDatabase.size()-1) {
                    sbIds.append(", ");
                }
            }

            // Finish the query
            sbIds.append(')');

            // Delete the songs from tblSongPlaylists
            System.out.println(sbIds.toString());
            DB.deleteSQL(sbIds.toString());

            // Delete the songs from tblSongs
            System.out.println(sb.toString());
            DB.deleteSQL(sb.toString());
        }



        // Add the remaining songs from the songs folder into the database

        // ArrayList of songs that haven't been added to the database yet.
        ArrayList<String> songsNotAddedToDatabase = (ArrayList<String>) songsList.clone();

        // Since the ArrayList was initialized with all songs in the SongList,
        // we have to remove all songs that are already in the database
        // (We have a list of songs already in the database from earlier)
        songsNotAddedToDatabase.removeAll(songsInDatabase);

        // Check if we have any songs to add to the database
        if (songsNotAddedToDatabase.size() != 0) {

            // Store the query to be executed (songs to add to the tblSongs table)
            StringBuilder tempInsertStatement = new StringBuilder();

            // Start the INSERT query
            tempInsertStatement.append("INSERT INTO tblSongs (fldSongTitle, fldFilePath, fldArtists) VALUES ");

            // Add every song (that will be added) to the query.
            for (int i = 0; i < songsNotAddedToDatabase.size(); i++) {
                tempInsertStatement.append("('");
                tempInsertStatement.append(songsNotAddedToDatabase.get(i)); // Song title
                tempInsertStatement.append("', '");
                tempInsertStatement.append(songsNotAddedToDatabase.get(i)); // Song path (relative to the songs folder)
                tempInsertStatement.append("', '");
                tempInsertStatement.append("N/A')"); // N/A artist, we don't have that from file metadata yet

                // Avoid trailing commas
                if (i < songsNotAddedToDatabase.size() - 1) {
                    tempInsertStatement.append(',');
                }
            }

            // Add the new songs to the database
            System.out.println(tempInsertStatement.toString());

            // ERROR: "Could not find stored procedure" ??
            DB.insertSQL(tempInsertStatement.toString());
        }
    }

    /**
     * This function is meant to be run only when the program is started.
     * It will query the database for the name of all
     */
    public static ArrayList<String> getPlaylistsFromDatabase() {
        ArrayList<String> tempPlaylists = new ArrayList<>();

        DB.selectSQL("SELECT fldPlaylistName FROM tblPlaylists");

        int count = 0;
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

        // Returns the ArrayList of songs that are in the songs folder
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
