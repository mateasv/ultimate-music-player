package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;

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

    public static void main(String[] args) {

        createFolder(hasSongsFolder(dir), dir);

        launch(args);
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
