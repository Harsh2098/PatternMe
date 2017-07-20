package com.hmproductions.patternme;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class StartActivity extends AppCompatActivity {

    public static final String DIFFICULTY_LEVEL = "difficulty-level";
    Spinner mDifficultySpinner;
    Button startButton;

    String difficulty;
    String[] difficulty_options = new String[]{"Easy","Medium","Hard"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mDifficultySpinner = (Spinner)findViewById(R.id.difficulty_spinner);
        startButton = (Button)findViewById(R.id.start_button);

        GradientDrawable startGradientDrawable = (GradientDrawable)startButton.getBackground();
        startGradientDrawable.setColor(Color.parseColor("#669900"));

        setupSpinner();
        StartButtonClickListener();
    }

    private void StartButtonClickListener() {

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra(DIFFICULTY_LEVEL, difficulty);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.spinner_item,difficulty_options);

        mDifficultySpinner.setAdapter(adapter);

        mDifficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0 :
                        difficulty = "easy";
                        break;

                    case 1 :
                        difficulty = "medium";
                        break;

                    case 2 :
                        difficulty = "hard";
                        break;

                    default:
                        difficulty = "easy";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                difficulty = "easy";
            }
        });
    }
}
