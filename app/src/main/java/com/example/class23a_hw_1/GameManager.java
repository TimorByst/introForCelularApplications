package com.example.class23a_hw_1;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager {

    private int crashes = 0;
    private int life;
    private int score;
    private int cols;
    private int rows;
    private int carRow;
    private int coinValue;
    private int currentCoinRow = 0;
    private int randomCoinStartingColumn;
    private final int[] crashSounds = {R.raw.uh_sound, R.raw.bruh_sound};
    private int randomObstacleStartingPosition;
    private boolean isCoinInPlay = false;
    private final int OBJECTS_STARTING_ROW = 0;
    private MediaPlayer mediaPlayer;
    private Context context;

    /*Default constructor*/
    public GameManager() {
    }

    public GameManager setContext(Context context){
        this.context = context;
        return this;
    }
    public GameManager setLives(int lives) {
        this.life = lives;
        return this;
    }

    public GameManager setCols(int cols) {
        this.cols = cols;
        return this;
    }

    public GameManager setRows(int rows) {
        this.rows = rows;
        return this;
    }

    public GameManager setCarRow(int carRow) {
        this.carRow = carRow;
        return this;
    }

    public GameManager setCoinValue(int coinValue) {
        this.coinValue = coinValue;
        return this;
    }

    public void init(){
        mediaPlayer = new MediaPlayer();
    }

    public int getCrashes() {
        return crashes;
    }

    public int getScore() {
        return score;
    }

    public void restart() {
        crashes = 0;
    }

    public boolean isLose() {
        return life == crashes;
    }

    public void incrementScore(int value) {
        score += value;
    }

    public void crashed() {
        crashes++;
    }

    public void playRandomCrashSound(Context context){
        int soundIndex = (int)(Math.random() * crashSounds.length);
        int soundId = crashSounds[soundIndex];

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(context,
                    Uri.parse("android.resource://" + context.getPackageName() + "/" + soundId));
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch (IOException e){
            Log.d("Error", "GameManager: MediaPlayer error: " + e.getMessage());
        }
    }
    /**
     *
     * @param currentCarPos the current car position within the array of valid car positions
     * @param value the button_id in case of playing with buttons,
     *             or the move_to if playing with sensors
     * @param carSpot the array of valid car positions
     * @param moveByButtons flag that indicates the method of play
     */
    public void moveCar(int currentCarPos, int value, AppCompatImageView[] carSpot, boolean moveByButtons){
        if(moveByButtons){
            moveCarByButtons(currentCarPos, value, carSpot);
        }else{
            moveCarBySensors(currentCarPos, value, carSpot);
        }
    }
    /**
     * Moves the car horizontally by press of buttons,
     * check if the car can move to the desired location
     * and changes the visibility of the image view accordingly.
     *
     * @param currentCarPos the current car position,
     *                     value is changed within the function accordingly
     * @param buttonId the button id
     * @param carSpot the array of valid car positions
     */
    private void moveCarByButtons(int currentCarPos, int buttonId, AppCompatImageView[] carSpot) {
        if (buttonId == R.id.left_ICN_arrow) {
            if (currentCarPos > 0) {
                carSpot[currentCarPos].setVisibility(View.INVISIBLE);
                carSpot[currentCarPos-1].setVisibility(View.VISIBLE);
            }
        } else {
            if (currentCarPos < cols - 1) {
                carSpot[currentCarPos].setVisibility(View.INVISIBLE);
                carSpot[currentCarPos+1].setVisibility(View.VISIBLE);
            }
        }
    }
    /**
     * Moves the car horizontally by tilting the phone,
     * check if the car can move to the desired location
     * and changes the visibility of the image view accordingly.
     *
     * @param currentCarPos the current car position
     * @param move_to the registered tilt value by the sensor
     * @param carSpot the array of valid car positions
     */
    private void moveCarBySensors(int currentCarPos, int move_to, AppCompatImageView[] carSpot){
        carSpot[currentCarPos].setVisibility(View.INVISIBLE);
        carSpot[move_to].setVisibility(View.VISIBLE);
    }

    public void moveCoins(AppCompatImageView[][] coins) {
        if (currentCoinRow < rows - 1 && isCoinInPlay) {
            coins[currentCoinRow++][randomCoinStartingColumn].setVisibility(View.INVISIBLE);
            coins[currentCoinRow][randomCoinStartingColumn].setVisibility(View.VISIBLE);
        } else {
                coins[currentCoinRow][randomCoinStartingColumn].setVisibility(View.INVISIBLE);
            do {
                randomCoinStartingColumn = ThreadLocalRandom.current().nextInt(cols);
            } while (randomCoinStartingColumn == randomObstacleStartingPosition);
            coins[OBJECTS_STARTING_ROW][randomCoinStartingColumn].setVisibility(View.VISIBLE);
            currentCoinRow = OBJECTS_STARTING_ROW;
            isCoinInPlay = true;
        }
    }

    public void moveObstacles(AppCompatImageView[][] obstacles) {
        moveMatrixObjects(obstacles);
        randomObstacleStartingPosition = ThreadLocalRandom.current().nextInt(cols);
        obstacles[OBJECTS_STARTING_ROW][randomObstacleStartingPosition].setVisibility(View.VISIBLE);
    }

    /**
     * The objects move vertically downwards,
     * starting from ROW-2 (one before the last) and places the current
     * obstacle in the i+1 position.
     * the objects on the ROW-1 (last) position is removed (set invisible),
     * the objects on the first rows gets a random place horizontally.
     *
     * @param objects a matrix of to the objects that are visible on the map
     */
    private void moveMatrixObjects(AppCompatImageView[][] objects) {
        for (int i = rows - 1; i >= 0; i--) {
            for (int j = cols - 1; j >= 0; j--) {
                if (objects[i][j].getVisibility() == View.VISIBLE) {
                    if (i != rows - 1) {
                        objects[i + 1][j].setVisibility(View.VISIBLE);
                    }
                }
                objects[i][j].setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Checks if a collision happened between the car and a coin/stone,
     * if a car and a coin are visible at the same location at the same time increment the score
     * and return false,
     * if a car and a stone are visible at the same location at the same time change the icon to
     * crash animation and return true
     */
    public boolean checkCollisions(
            int currentCarPos,
            AppCompatImageView[] carSpot,
            AppCompatImageView[] crashSpot,
            AppCompatImageView[][] obstacles,
            AppCompatImageView[][] coins,
            AppCompatTextView main_LBL_score) {

        if (carSpot[currentCarPos].getVisibility() == View.VISIBLE
                && obstacles[carRow - 1][currentCarPos].getVisibility() == View.VISIBLE) {
            obstacleCollisionResponse(currentCarPos, carRow - 1, carSpot, crashSpot, obstacles);
            return true;
        } else if (carSpot[currentCarPos].getVisibility() == View.VISIBLE
                && obstacles[carRow][currentCarPos].getVisibility() == View.VISIBLE) {
            obstacleCollisionResponse(currentCarPos, carRow, carSpot, crashSpot, obstacles);
            return true;
        }
        checkCoinPickUp(currentCarPos, carSpot, coins, main_LBL_score);
        return false;
    }

    public void checkCoinPickUp(int currentCarPos,
                                   AppCompatImageView[] carSpot,
                                   AppCompatImageView[][] coins,
                                   AppCompatTextView main_LBL_score){
        if (carSpot[currentCarPos].getVisibility() == View.VISIBLE &&
                coins[carRow - 1][currentCarPos].getVisibility() == View.VISIBLE)
            coinCollisionResponse(currentCarPos, carRow - 1, coins, main_LBL_score);
        else if (carSpot[currentCarPos].getVisibility() == View.VISIBLE &&
                coins[carRow][currentCarPos].getVisibility() == View.VISIBLE)
            coinCollisionResponse(currentCarPos, carRow, coins, main_LBL_score);
    }


    /**
     * Animates the car crash
     *
     * @param row the row in which the collision happened
     */
    private void obstacleCollisionResponse(int currentCarPos,
                                           int row,
                                           AppCompatImageView[] carSpot,
                                           AppCompatImageView[] crashSpot,
                                           AppCompatImageView[][] obstacles) {
        carSpot[currentCarPos].setVisibility(View.INVISIBLE);
        obstacles[row][currentCarPos].setVisibility(View.INVISIBLE);
        crashSpot[currentCarPos].setVisibility(View.VISIBLE);
        playRandomCrashSound(context);
        MySignal.getInstance().frenchToast("Merde !");
        MySignal.getInstance().vibrate();
        crashed();
    }

    /**
     * Updates the score on coin hit and throws a french toast message
     *
     * @param row the row in which the collision happened
     */
    private void coinCollisionResponse(int currentCarPos,
                                       int row,
                                       AppCompatImageView[][] coins,
                                       AppCompatTextView main_LBL_score) {
        coins[row][currentCarPos].setVisibility(View.INVISIBLE);
        isCoinInPlay = false;
        MySignal.getInstance().frenchToast("Oh là là");
        incrementScore(coinValue);
        main_LBL_score.setText(""+getScore());
    }

    /**
     * Resets car visibility to the last car position.
     * This function is called after the crash animation
     * to show back the car and hide the crash animation.
     */
    public void resetCarVisibility(int currentCarPos,
                                   AppCompatImageView[] carSpot,
                                   AppCompatImageView[] crashSpot) {
        for (int i = 0; i < cols; i++) {
            if (i != currentCarPos) {
                carSpot[i].setVisibility(View.INVISIBLE);
            } else {
                carSpot[i].setVisibility(View.VISIBLE);
            }
            crashSpot[i].setVisibility(View.INVISIBLE);
        }
    }

    public void destroy(){
        mediaPlayer.release();
    }

}
