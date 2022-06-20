package com.example.blacknoteclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blacknoteclone.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    // Variable initialisation
    GridView gridView;
    static ArrayList<String> titleArrayList = new ArrayList<String>();
    static ArrayList<String> contentArrayList = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;

    SharedPreferences sharedPreferences;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Assign bottom nav to variable
        BottomNavigationView bottom_nav_view = findViewById(R.id.bottom_nav_view);

        // Assign listener to bottom nav
        bottom_nav_view.setOnItemSelectedListener(navigationItemSelectedListener);

        sharedPreferences = getApplicationContext().getSharedPreferences(
                "com.example.blacknoteclone",
                MODE_PRIVATE
        );

        // Fetch data
        HashSet<String> titleHashSet = (HashSet<String>) sharedPreferences.getStringSet("title", null);
        HashSet<String> contentHashSet = (HashSet<String>) sharedPreferences.getStringSet("content", null);

        if((titleHashSet != null) && (contentHashSet != null)) {
            titleArrayList = new ArrayList<>(titleHashSet);
            contentArrayList = new ArrayList<>(contentHashSet);
        }

        gridView = binding.notesGrid;

        // Adapter for GridView items
        arrayAdapter = new ArrayAdapter(this, R.layout.notes_grid_item, R.id.note_title, titleArrayList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView noteTitle = view.findViewById(R.id.note_title);
                noteTitle.setText(titleArrayList.get(position).trim());
                return view;
            }
        };

        gridView.setAdapter(arrayAdapter);

        // Opens an activity for actions performed on a grid item
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String s = (String) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra("data",position);
                startActivity(intent);
            }
        });

        // Hold grid item to open delete dialog
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Note")
                        .setMessage("Are you sure?")
                        .setPositiveButton(
                                "Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        titleArrayList.remove(position);
                                        contentArrayList.remove(position);
                                        arrayAdapter.notifyDataSetChanged();
                                        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.blacknoteclone", Context.MODE_PRIVATE);
                                        HashSet<String> titleHashSet = new HashSet<String>(MainActivity.titleArrayList);
                                        HashSet<String> contentHashSet = new HashSet<String>(MainActivity.contentArrayList);
                                        SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
                                        sharedPreferenceEditor.putStringSet("title", titleHashSet);
                                        sharedPreferenceEditor.putStringSet("content", contentHashSet);
                                        sharedPreferenceEditor.apply();
                                        Toast.makeText(getApplicationContext(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        ).setNegativeButton("No",null).show();
                return true;
            }
        });

    }

    // Bottom nav navigation logic gate
    private NavigationBarView.OnItemSelectedListener navigationItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_category:
                    Intent categoryIntent = new Intent(getApplicationContext(), CategoryActivity.class);
                    startActivity(categoryIntent);
                    return true;
                case R.id.nav_search:
                    Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(searchIntent);
                    return true;
                case R.id.nav_add:
                    Intent addIntent = new Intent(getApplicationContext(), AddNoteActivity.class);
                    startActivity(addIntent);
                    return true;
                case R.id.nav_favourites:
                    Intent favIntent = new Intent(getApplicationContext(), FavouritesActivity.class);
                    startActivity(favIntent);
                    return true;
                case R.id.nav_check:
                    Intent dailyCheckIntent = new Intent(getApplicationContext(), DailyCheckActivity.class);
                    startActivity(dailyCheckIntent);
                    return true;
            }
            return false;
        }
    };
}