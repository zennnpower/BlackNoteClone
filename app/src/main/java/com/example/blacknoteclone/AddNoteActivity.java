package com.example.blacknoteclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blacknoteclone.databinding.ActivityAddNoteBinding;

import java.util.HashSet;

public class AddNoteActivity extends AppCompatActivity {
    // Variable initialisation
    int index;
    Toolbar note_toolbar;
    Toolbar editing_toolbar;
    EditText title_input;
    EditText note_input;
    TextView editing_category;
    TextView editing_title;
    Button note_btn;
    ImageView back_btn;

    SharedPreferences sharedPreferences;
    ActivityAddNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate
        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Assigning views to variables
        index = getIntent().getIntExtra("data", -1);
        title_input = binding.titleInput;
        note_input = binding.noteInput;
        note_toolbar = binding.noteToolbar;
        editing_toolbar = binding.editingToolbar;
        editing_category = binding.editingCategory;
        editing_title = binding.editingTitle;
        back_btn = binding.backButton;
        note_btn = binding.noteBtn;

        // Back button to go back to home activity
        back_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // If index exists in SharedPreferences, begin editing mode (Display editing toolbar,
        // note title etc. and hide note title input)
        // Else, create new entry
        if(index == -1) {
            MainActivity.titleArrayList.add("");
            MainActivity.contentArrayList.add("");
            index = MainActivity.titleArrayList.size()-1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }
        else {
            title_input.setText(MainActivity.titleArrayList.get(index));
            note_input.setText(MainActivity.contentArrayList.get(index));
            editing_toolbar.setVisibility(View.VISIBLE);
            editing_category.setVisibility(View.VISIBLE);
            editing_title.setText(MainActivity.titleArrayList.get(index));
            editing_title.setVisibility(View.VISIBLE);

        }

        // Save new note / existing note's changes
        note_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                MainActivity.titleArrayList.set(index, title_input.getText().toString());
                MainActivity.contentArrayList.set(index, note_input.getText().toString());
                MainActivity.arrayAdapter.notifyDataSetChanged();

                sharedPreferences = getApplicationContext().getSharedPreferences(
                        "com.example.blacknoteclone",
                        MODE_PRIVATE
                );

                HashSet<String> hashSetTitle = new HashSet<String>(MainActivity.titleArrayList);
                HashSet<String> hashSetContent = new HashSet<String>(MainActivity.contentArrayList);
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putStringSet("title", hashSetTitle);
                sharedPreferencesEditor.putStringSet("description", hashSetContent);
                sharedPreferencesEditor.apply();

                Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}