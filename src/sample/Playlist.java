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

    /**
     * Method to append a song to the end of this playlist
     * on pressed button add song to the end of the arraylist
     */
    public void addToPlaylist(Song song){
        playlist.add(song);
    }

    /**
     * Method to remove songs from a playlist
     * on pressed button, get the arraylist number of of the song and remove it.
     *
     * @param index Index of the song to be removed (0 = first song in the playlist)
     */
    public void removeFromPlaylist(int index){
        playlist.remove(index);

    }

    /**
     * Method to delete a playlist
     * on pressed button, delete the contents of the chosen arraylist.
     * Delete the chosen arraylist from the list of playlists
     */
    public void deletePlaylist(){
        playlist.clear();
    }
}
