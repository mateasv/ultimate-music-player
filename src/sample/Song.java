package sample;


import java.io.*;

public class Song {

    private String title, artist, genre, path;
    private File song;


    private Song(String songTitle, String artistName, String songGenre, String filePath){
            this.title = songTitle;
            this.artist = artistName;
            this.genre = songGenre;
            this.path = filePath;
    }

    //Find song metadata
    public void getSongData(){
        song = new File(System.getProperty("user.dir"));

    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getPath() {
        return path;
    }


}