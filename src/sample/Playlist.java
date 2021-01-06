package sample;

import java.util.ArrayList;

public class Playlist  {

    String playlistName;
    ArrayList<Song> playlist;


    public Playlist(String playlistName, ArrayList<Song> playlist) {
        this.playlistName = playlistName;
        this.playlist = playlist;


    }


    //method to create a new playlist
    //o pressed button, create a new playlist and give it a name
//    public ArrayList<Song> newPlaylist(){
//
//        return new ArrayList<Song>();
//
//    }

    //create song
    public Song createSong(String songTitle, String artistName, String songGenre, String filePath){
        Song song = new Song(songTitle, artistName, songGenre, filePath);
        return song;
    }

    //method to add songs to a playlist
    //on pressed button add song to the end of the arraylist
    public void addToPlaylist(){
        playlist.add(createSong("baby", "kris", "rock", "songs"));

    }

    //method to remove songs from a playlist
    //on pressed button, get the arraylist number of of the song and remove it.
    public void removeFromPlaylist(){
        playlist.remove(1);

    }

    //method to delete a playlist
    //on pressed button, delete the contents of the chosen arraylist.
    //delete the chosen arraylist from the list of playlists
    public  void deletePlaylist(){
        playlist.clear();


    }
}
