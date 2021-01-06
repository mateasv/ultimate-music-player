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

// Test commit

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("player.fxml"));
        primaryStage.setTitle("Ultimate Music Player");
        primaryStage.setScene(new Scene(root, 450, 350));
        primaryStage.show();
    }


    static File dir = new File(System.getProperty("user.dir"));

    public static ArrayList<Song> songArrayList = new ArrayList<Song>();
    public static ArrayList<Playlist> listOfPlaylists = new ArrayList<Playlist>();

    public static void main(String[] args) {

        createFolder(hasSongsFolder(dir), dir);

        launch(args);

        listOfPlaylists.add(new Playlist("whatevs", songArrayList));

    }

    public static ArrayList removePlaylist() {
        listOfPlaylists.remove(1);
        return listOfPlaylists;

    }

    public static boolean hasSongsFolder(File directory) {
        String[] contents = directory.list();
        assert contents != null;
        for(String content: contents){
            if(content.equals("songs")){
                return true;
            }
        }
        return false;
    }

    public static void createFolder(boolean exists, File directory){
        String path = "\\songs";
        boolean created;
        if(exists){
            System.out.println("Folder already exists");
            return;
        }
            directory = new File(directory + path);
            created = directory.mkdir();
            if(created){
                System.out.println("The folder has been created");
            }
            else{
                System.out.println("The folder could not be created");
            }
    }
}
