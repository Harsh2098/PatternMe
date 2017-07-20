package com.hmproductions.patternme;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartActivity extends AppCompatActivity {

    public static final String DIFFICULTY_LEVEL = "difficulty-level";
    Button startButton;
    EditText num_editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startButton = (Button)findViewById(R.id.start_button);

        num_editText = (EditText)findViewById(R.id.difficulty_editText);

        GradientDrawable startGradientDrawable = (GradientDrawable)startButton.getBackground();
        startGradientDrawable.setColor(Color.parseColor("#669900"));

        StartButtonClickListener();
    }

    private void StartButtonClickListener() {

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);

                if (num_editText.getText().toString().isEmpty() || num_editText.getText().toString().equals(""))
                    intent.putExtra(DIFFICULTY_LEVEL, 3);
                else
                    intent.putExtra(DIFFICULTY_LEVEL, Integer.parseInt(num_editText.getText().toString()));
                startActivity(intent);
                finish();
            }
        });
    }
}
