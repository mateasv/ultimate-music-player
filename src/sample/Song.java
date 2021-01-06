package sample;


import java.io.*;

public class Song {

    private String title, artist, path;
    private File song;


    public Song(String songTitle, String artistName, String filePath){
            this.title = songTitle;
            this.artist = artistName;
            this.path = filePath;
    }

    /**
     * Find song metadata
     */
    public void getSongData(){
        song = new File(System.getProperty("user.dir"));

    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }


}