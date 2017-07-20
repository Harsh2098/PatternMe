package com.hmproductions.patternme;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import rb.popview.PopField;

public class MainActivity extends AppCompatActivity {

    public int GRID_EDGE;
    RelativeLayout.LayoutParams layoutParams;
    private Button resetButton, undoButton, showGridButton;
    private TextView moves_textView, numberOfMoves_textView;
    private MediaPlayer tadaCelebration;
    private LinearLayout mainLinearLayout;
    private PopField mPopField;
    private boolean[][] originalGrid, userGrid;
    private int undoPositionX = -1, undoPositionY = -1, counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GRID_EDGE = getIntent().getIntExtra(StartActivity.DIFFICULTY_LEVEL, 3);

        originalGrid = new boolean[GRID_EDGE][GRID_EDGE];
        userGrid = new boolean[GRID_EDGE][GRID_EDGE];

        SetupDisplayMetrics();
        BindViews();
        SetupGrids();
        CreateGridLayout();

        ResetButtonClickListener();
        ShowGridButtonClickListener();
        UndoButtonClickListener();

        tadaCelebration = MediaPlayer.create(this, R.raw.tada_celebration);
        mPopField = PopField.attach2Window(this);
    }

    /* Sets up all the display metrics */
    private void SetupDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams = new RelativeLayout.LayoutParams(displayMetrics.widthPixels / GRID_EDGE, displayMetrics.widthPixels / GRID_EDGE);
    }

    private void CreateGridLayout() {

        for (int i = 0; i < GRID_EDGE; ++i) {
            LinearLayout linearLayout = new LinearLayout(this);
            mainLinearLayout.addView(linearLayout);
            for (int j = 0; j < GRID_EDGE; ++j) {
                View myView = LayoutInflater.from(this).inflate(R.layout.grid_item, linearLayout, false);
                myView.setLayoutParams(layoutParams);

                final int finalI = i;
                final int finalJ = j;
                myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onGridCellClick(finalI * GRID_EDGE + finalJ);
                    }
                });
                linearLayout.addView(myView);
            }
        }

    }

    /* Binds all the views from main_activity.xml */
    private void BindViews() {

        mainLinearLayout = (LinearLayout) findViewById(R.id.main_linearLayout);
        resetButton = (Button) findViewById(R.id.resetButton);
        undoButton = (Button) findViewById(R.id.undoButton);
        showGridButton = (Button) findViewById(R.id.showGridButton);
        moves_textView = (TextView) findViewById(R.id.moves_textView);
        numberOfMoves_textView = (TextView) findViewById(R.id.numberOfMoves_textView);

        GradientDrawable resetGradientDrawable = (GradientDrawable) resetButton.getBackground();
        resetGradientDrawable.setColor(Color.parseColor("#cc0000"));

        GradientDrawable undoGradientDrawable = (GradientDrawable) undoButton.getBackground();
        undoGradientDrawable.setColor(Color.parseColor("#2979ff"));

        GradientDrawable showGridGradientDrawable = (GradientDrawable) showGridButton.getBackground();
        showGridGradientDrawable.setColor(Color.parseColor("#669900"));

        moves_textView.setText(String.valueOf(counter));
    }

    /* Creates originalGrid and userGrid */
    private void SetupGrids() {

        // Making Original Grid
        int randomNumber1, randomNumber2;

        // Setting all the elements in both the Grids to false
        for (int i = 0; i < GRID_EDGE; ++i)
            for (int j = 0; j < GRID_EDGE; ++j) {
                userGrid[i][j] = false;
                originalGrid[i][j] = false;
            }

        // Simulating any random 100 clicks to make Original Grid
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {

                // Generating 2 random numbers to click any element in originalGrid [][]
                Random r1 = new Random();
                Random r2 = new Random();

                randomNumber1 = (r1.nextInt(GRID_EDGE));
                randomNumber2 = (r2.nextInt(GRID_EDGE));

                clickGridCell(originalGrid, randomNumber1, randomNumber2);
            }
        }
    }

    private void ResetButtonClickListener() {
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showGridButton.getText().toString().equals(getString(R.string.show_grid))) {

                    if (!firstMove()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder
                                .setTitle("Reset Grid")
                                .setMessage("You will lose all the progress. Do you want to reset player grid ?")
                                .setPositiveButton(getString(R.string.reset), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        resetWholeGrid(userGrid);
                                        swapData(userGrid);

                                        counter = -1;
                                        incrementNumberOfMoves();
                                    }
                                })
                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    } else
                        Toast.makeText(MainActivity.this, "Please begin the game first", Toast.LENGTH_SHORT).show();
                } else if (checkWin())
                    recreate();

                else
                    Toast.makeText(MainActivity.this, "Cannot reset Original Grid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Analogous to swapData() in Recycler view. Takes in @param grid and sets the tile color accordingly */
    private void swapData(boolean[][] grid) {

        for (int i = 0; i < GRID_EDGE; ++i)
            for (int j = 0; j < GRID_EDGE; ++j) {
                View myView = ((LinearLayout) mainLinearLayout.getChildAt(i)).getChildAt(j).findViewById(R.id.view);

                if (grid[i][j])
                    myView.setBackgroundColor(Color.parseColor("#FF9100"));
                else
                    myView.setBackgroundColor(Color.parseColor("#000000"));

            }
    }

    /* Switches between Original Grid and User Grid */
    private void ShowGridButtonClickListener() {

        showGridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showGridButton.getText().toString().equals("SHOW GRID")) {
                    swapData(originalGrid);
                    showGridButton.setText(R.string.my_grid);
                } else {
                    swapData(userGrid);
                    showGridButton.setText(R.string.show_grid);
                }
            }
        });
    }

    /* Handles Undo button click */
    private void UndoButtonClickListener() {

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showGridButton.getText().toString().equals(getString(R.string.my_grid)))
                    Toast.makeText(MainActivity.this, "Cannot undo on Original Grid", Toast.LENGTH_SHORT).show();
                else if (firstMove())
                    Toast.makeText(MainActivity.this, "Please begin the game first", Toast.LENGTH_SHORT).show();

                else {
                    clickGridCell(userGrid, undoPositionX, undoPositionY);
                    swapData(userGrid);
                }
            }
        });
    }

    /* Clicks grid cell in boolean[][]; @param x, y  - position in the grid where cell has to be pressed */
    private void clickGridCell(boolean[][] grid, int x, int y) {

        undoPositionX = x;
        undoPositionY = y;
        if (y != 0)
            grid[x][y - 1] = !grid[x][y - 1];

        if (y != GRID_EDGE - 1)
            grid[x][y + 1] = !grid[x][y + 1];

        if (x != 0)
            grid[x - 1][y] = !grid[x - 1][y];

        if (x != GRID_EDGE - 1)
            grid[x + 1][y] = !grid[x + 1][y];

    }

    /* Checks if originalGrid is equal to userGrid */
    private boolean checkWin() {
        for (int i = 0; i < GRID_EDGE; ++i)
            for (int j = 0; j < GRID_EDGE; ++j)
                if (originalGrid[i][j] != userGrid[i][j])
                    return false;

        return true;
    }

    /* Checks if all the userGrid[][] cells are false */
    private boolean firstMove() {
        for (int i = 0; i < GRID_EDGE; ++i)
            for (int j = 0; j < GRID_EDGE; ++j)
                if (userGrid[i][j])
                    return false;

        return true;
    }

    /* Sets are the elements of grid to false */
    private void resetWholeGrid(boolean[][] grid) {
        for (int i = 0; i < GRID_EDGE; ++i)
            for (int j = 0; j < GRID_EDGE; ++j)
                grid[i][j] = false;
    }

    /* @param gridCellPosition - position where the cell is clicked */
    public void onGridCellClick(int gridCellPosition) {

        if (showGridButton.getText().toString().equals(getString(R.string.show_grid))) {

            clickGridCell(userGrid, gridCellPosition / GRID_EDGE, gridCellPosition % GRID_EDGE);
            incrementNumberOfMoves();
            swapData(userGrid);

            // Checking if user has won the game
            if (checkWin()) {
                Toast.makeText(this, R.string.congratulations, Toast.LENGTH_SHORT).show();
                tadaCelebration.start();

                numberOfMoves_textView.setText(R.string.completed_in_moves);
                numberOfMoves_textView.setTypeface(numberOfMoves_textView.getTypeface(), Typeface.BOLD);
                moves_textView.setTypeface(moves_textView.getTypeface(), Typeface.BOLD | Typeface.ITALIC);
                numberOfMoves_textView.setTextColor(Color.parseColor("#FF0000"));
                moves_textView.setTextColor(Color.parseColor("#FF0000"));

                undoButton.setEnabled(false);
                showGridButton.setEnabled(false);
                mPopField.popView(showGridButton);
                mPopField.popView(undoButton);

                resetButton.setText(R.string.new_game);
                showGridButton.setText(getString(R.string.my_grid));

                GradientDrawable resetGradientDrawable = (GradientDrawable) resetButton.getBackground();
                resetGradientDrawable.setColor(Color.parseColor("#669900"));

            }
        } else
            Toast.makeText(this, "Cannot click on Original Grid", Toast.LENGTH_SHORT).show();
    }

    private void incrementNumberOfMoves() {
        counter++;
        moves_textView.setText(String.valueOf(counter));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.newGame_item) {

            // Checking if user has already won. If yes no alert dialog is displayed.
            if(checkWin())
                recreate();
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setTitle("New Game")
                        .setMessage("You will lose all the progress. Do you want to start new game ?")
                        .setPositiveButton(getString(R.string.new_game), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                recreate();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }

        } else if (item.getItemId() == R.id.changeDifficulty_item) {

            // Checking if user has already won. If yes no alert dialog is displayed.
            if(checkWin())
            {
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setTitle("Change Grid Size")
                        .setMessage("You will lose all the progress. Do you want to start new game with new difficulty level?")
                        .setPositiveButton(getString(R.string.change_grid_size), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(MainActivity.this, StartActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }

        } else if (item.getItemId() == R.id.howToPlay_item) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder
                    .setTitle("How to Play")
                    .setMessage("Objective is to make a pattern matching the original grid. Original grid can be seen by clicking show grid button. Clicking on any one of the Grid tile will change colors of adjacent tiles. Try to complete in least number of moves.\n\nHave fun !")
                    .setPositiveButton("PLAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
}
