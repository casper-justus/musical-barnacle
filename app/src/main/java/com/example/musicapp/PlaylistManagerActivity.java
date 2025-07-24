package com.example.musicapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class PlaylistManagerActivity extends AppCompatActivity {

    private RecyclerView playlistsRecyclerView;
    private PlaylistAdapter playlistAdapter;
    private List<Playlist> playlists;
    private FloatingActionButton addPlaylistFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_manager);

        playlistsRecyclerView = findViewById(R.id.playlists_recycler_view);
        addPlaylistFab = findViewById(R.id.add_playlist_fab);

        playlists = new ArrayList<>();
        playlists.add(new Playlist("My Playlist 1"));
        playlists.add(new Playlist("My Playlist 2"));
        playlists.add(new Playlist("My Playlist 3"));

        playlistAdapter = new PlaylistAdapter(playlists);
        playlistsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playlistsRecyclerView.setAdapter(playlistAdapter);

        addPlaylistFab.setOnClickListener(v -> {
            // TODO: Implement add playlist functionality
        });
    }
}
