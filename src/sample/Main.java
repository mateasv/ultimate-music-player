package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

// Test commit

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static ArrayList<Song> songArrayList = new ArrayList<Song>();
    public static ArrayList<Playlist> listOfPlaylists = new ArrayList<Playlist>();

    public static void main(String[] args) {
        launch(args);

        listOfPlaylists.add(new Playlist("whatevs", songArrayList));

    }

    public static ArrayList removePlaylist() {
        listOfPlaylists.remove(1);
        return listOfPlaylists;

    }
}
