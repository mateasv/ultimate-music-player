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

    //Find song metadata
    public void getSongData(){
        song = new File(System.getProperty("user.dir"));

    }

    /*
    @return the title of the song
     */
    public String getTitle() {
        return title;
    }

    /*
    @return the artist that made the song
     */
    public String getArtist() {
        return artist;
    }

    /*
    @return the file path
     */
    public String getPath() {
        return path;
    }


}