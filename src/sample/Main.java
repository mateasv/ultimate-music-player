package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
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
        primaryStage.setScene(new Scene(root, 450, 350));
        primaryStage.show();
    }



    // ArrayList of the current playlists
    public static ArrayList<Playlist> listOfPlaylists = new ArrayList<>();


    public static void main(String[] args) {

        // Make sure the "songs" folder exists
        if (!folderExists("songs")) {
            createFolder("songs");
        }

        // Initialize JavaFX GUI
        launch(args);

        // Add a new playlist
        listOfPlaylists.add(new Playlist("whatevs", new ArrayList<>()));

        // Add a song to the first playlist
        listOfPlaylists.get(0).addToPlaylist(new Song("baby", "kris", "songs"));

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
     * Method to check if the "songs" folder exists.
     *
     * @param folderName The name of the folder to check for
     * @return boolean, true if the folder exists
     */
    public static boolean folderExists(String folderName) {
        // new File instance for the current directory
        File dir = new File(System.getProperty("user.dir"));

        String[] contents = dir.list();
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
        // new File instance for the current directory
        File dir = new File(System.getProperty("user.dir"));

        String path = "\\" + folderName;
        boolean created;
        dir = new File(dir + path);
        created = dir.mkdir();

        return created; // True = created successfully
    }
}
