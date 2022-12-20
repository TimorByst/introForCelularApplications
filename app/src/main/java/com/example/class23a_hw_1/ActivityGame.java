package com.example.class23a_hw_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

public class ActivityGame extends AppCompatActivity {

    public interface CallbackTimer {
        void tick();
    }

    private MyAccelerometerDetector myAccelerometerDetector;
    private AppCompatImageView main_IMG_lanes;
    private AppCompatTextView main_LBL_score;
    private AppCompatTextView main_LBL_distance;

    private ExtendedFloatingActionButton left_ICN_arrow;
    private ExtendedFloatingActionButton right_ICN_arrow;

    private AppCompatImageView[][] obstacles;
    private AppCompatImageView[][] coins;
    private AppCompatImageView[] carSpot;
    private AppCompatImageView[] crashSpot;
    private ShapeableImageView[] lives;

    /* The number of rows on screen */
    private final int ROWS = 8;
    /* The number of elements in a row */
    private final int COLS = 5;
    /* only relevant when car is allowed to move 1D (horizontally)*/
    private final int CAR_ROW = 6;
    /* The row from which the obstacle start to appear*/
    private final int OBJECTS_STARTING_ROW = 0;
    /* the coin score value */
    private final int COIN = 10;
    /* The time intervals which the games updates*/
    private final int[] gameSpeed = {1000, 750, 500};
    /* An index to indicate in which speed the game is currently running */
    private int currentGameSpeed = 0;
    /* Determine how significant the tilt on the X-axis should be
     in order to register as a shift to that lane */
    private final float CENTER_LANE_SENSITIVITY = 0.5f;
    private final float LEFT_LANE_SENSITIVITY = 2;
    private final float LEFTEST_LANE_SENSITIVITY = 4;
    private final float RIGHT_LANE_SENSITIVITY = -2;
    private final float RIGHTEST_LANE_SENSITIVITY = -4;
    private final int OFFSET = 1;
    /* For convenience represents each lane as a number in the car row */
    private final int LEFTEST = 0;
    private final int LEFT = 1;
    private final int CENTER = 2;
    private final int RIGHT = 3;
    private final int RIGHTEST = 4;
    /* Index that indicates the current car position */
    private int currentCarPos;
    /* Flag to indicate when to stop and start timer onResume and onPause methods */
    private boolean firstStart = true;
    /* Flag to indicate the moveCar logic */
    private boolean moveCarByButtons;
    /* The game speed the player chose to play Fast/Slow*/
    private boolean moveCarByButtonsFast;
    private MyTicker myTicker;
    private GameManager gameManager;
    CallbackTimer callbackTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent previous = getIntent();
        moveCarByButtons = previous.getBooleanExtra("PlayMode", moveCarByButtons);
        moveCarByButtonsFast = previous.getBooleanExtra("GameSpeed", moveCarByButtonsFast);

        initGame();
        runTimer();
    }

    private MyAccelerometerDetector.Callback_moveCar
            callback_moveCar = new MyAccelerometerDetector.Callback_moveCar() {
        @Override
        public void moveCar(float pivot, float currentPos, float lastPos) {
            int move_to = currentCarPos;
            boolean skip = false;
            if (currentPos < RIGHTEST_LANE_SENSITIVITY
                    && currentCarPos != RIGHTEST) {
                move_to = RIGHTEST;
            } else if (RIGHTEST_LANE_SENSITIVITY < currentPos
                    && currentPos < RIGHT_LANE_SENSITIVITY
                    && currentCarPos != RIGHT) {
                move_to = RIGHT;
            } else if (Math.abs(currentPos) < Math.abs(CENTER_LANE_SENSITIVITY)
                    && currentCarPos != CENTER) {
                move_to = CENTER;
            } else if (LEFT_LANE_SENSITIVITY < currentPos
                    && currentPos < LEFTEST_LANE_SENSITIVITY
                    && currentCarPos != LEFT) {
                move_to = LEFT;
            } else if (LEFTEST_LANE_SENSITIVITY < currentPos
                    && currentCarPos != LEFTEST) {
                move_to = LEFTEST;
            } else {
                //Don't move the car
                skip = true;
            }
            if (!skip) {
                gameManager.moveCar(currentCarPos, move_to, carSpot, moveCarByButtons);
                currentCarPos = move_to;
            }
        }

        @Override
        public void changeSpeed(float startingValue, float currentValue) {
            /* Not implemented */
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!moveCarByButtons) {
            myAccelerometerDetector.start();
        }
        if (!myTicker.isRunning() && !firstStart) {
            myTicker.start(gameSpeed[currentGameSpeed]);
        }

    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!moveCarByButtons) {
            myAccelerometerDetector.stop();
        }
        if (myTicker.isRunning()) {
            myTicker.stop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameManager.destroy();
    }

    private void runTimer() {
        callbackTimer = new CallbackTimer() {
            @Override
            public void tick() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ticker();
                    }
                });
            }
        };
        myTicker = new MyTicker(callbackTimer);
        if (!myTicker.isRunning() && firstStart) {
            myTicker.start(gameSpeed[currentGameSpeed]);
            firstStart = false;
        }
    }

    private void ticker() {
        refreshUI();
        main_LBL_distance.setText(Integer.parseInt(main_LBL_distance.getText().toString()) + 1 + "");
    }

    // ------------------------------------ Game Logic Section -------------------------------------
    /*Resets the screen to display all the icons in the current positions*/
    private void refreshUI() {
        if(!gameManager.checkCollisions(currentCarPos, carSpot, crashSpot, obstacles, coins, main_LBL_score)) {
            if (gameManager.isLose()) {
                MySignal.getInstance().frenchToast("Game Over!");
                loadRecordsPage();
                gameManager.restart();
            } else {
                gameManager.resetCarVisibility(currentCarPos, carSpot, crashSpot);
                gameManager.moveObstacles(obstacles);
                gameManager.moveCoins(coins);
                showLives();
            }
        }
    }

    private void loadRecordsPage() {
        Intent intent = new Intent(this, ActivityRecords.class);
        startActivity(intent);
    }

    /**
     * Shows the current state of lives
     */
    private void showLives() {
        for (int i = 0; i < gameManager.getCrashes(); i++)
            lives[i].setVisibility(View.INVISIBLE);
    }

    //------------------------------------- Game Setup Section -------------------------------------

    /**
     * Initializes the game, finds all views, buttons, loads images using Glide,
     * and sets the initial position of the car and obstacles
     */
    private void initGame() {
        findViews();
        main_LBL_score.setText("0");
        main_LBL_distance.setText("0");

        loadImages();

        for (int i = 0; i < ROWS; i++) {
            for (AppCompatImageView obstacle : obstacles[i])
                obstacle.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < ROWS; i++) {
            for (AppCompatImageView coins : coins[i])
                coins.setVisibility(View.INVISIBLE);
        }


        for (AppCompatImageView car : carSpot)
            car.setVisibility(View.INVISIBLE);

        currentCarPos = (int) Math.floor(COLS / 2);
        carSpot[currentCarPos].setVisibility(View.VISIBLE);
        obstacles[OBJECTS_STARTING_ROW][currentCarPos].setVisibility(View.VISIBLE);

        for (AppCompatImageView crash : crashSpot)
            crash.setVisibility(View.INVISIBLE);

        gameManager = new GameManager();
        gameManager.init();
        gameManager.setContext(this)
                .setLives(lives.length)
                .setRows(ROWS)
                .setCols(COLS)
                .setCarRow(CAR_ROW)
                .setCoinValue(COIN);

        if (moveCarByButtons) {
            initButton(left_ICN_arrow);
            initButton(right_ICN_arrow);
            if(moveCarByButtonsFast){
                currentGameSpeed = gameSpeed.length - 1;
            }
        } else {
            myAccelerometerDetector = new MyAccelerometerDetector(this, callback_moveCar);
            myAccelerometerDetector.start();
        }

    }

    private void initButton(ExtendedFloatingActionButton button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameManager.moveCar(currentCarPos, button.getId(), carSpot, moveCarByButtons);
                if (button.getId() == left_ICN_arrow.getId() && currentCarPos > 0) {
                    currentCarPos--;
                } else if (currentCarPos < COLS - 1) {
                    currentCarPos++;
                }
            }
        });
    }

    private void findViews() {
        left_ICN_arrow = findViewById(R.id.left_ICN_arrow);
        right_ICN_arrow = findViewById(R.id.right_ICN_arrow);

        main_IMG_lanes = findViewById(R.id.main_IMG_lanes);
        main_LBL_score = findViewById(R.id.main_LBL_score);
        main_LBL_distance = findViewById(R.id.main_LBL_distance);

        findObstacles();
        findCoins();
        findCars();
        findCrashes();
        findHearts();
    }

    private void findObstacles() {
        obstacles = new AppCompatImageView[][]{
                {
                        findViewById(R.id.obstacle_IMG_pos_00),
                        findViewById(R.id.obstacle_IMG_pos_01),
                        findViewById(R.id.obstacle_IMG_pos_02),
                        findViewById(R.id.obstacle_IMG_pos_03),
                        findViewById(R.id.obstacle_IMG_pos_04),
                },

                {
                        findViewById(R.id.obstacle_IMG_pos_10),
                        findViewById(R.id.obstacle_IMG_pos_11),
                        findViewById(R.id.obstacle_IMG_pos_12),
                        findViewById(R.id.obstacle_IMG_pos_13),
                        findViewById(R.id.obstacle_IMG_pos_14),

                },

                {
                        findViewById(R.id.obstacle_IMG_pos_20),
                        findViewById(R.id.obstacle_IMG_pos_21),
                        findViewById(R.id.obstacle_IMG_pos_22),
                        findViewById(R.id.obstacle_IMG_pos_23),
                        findViewById(R.id.obstacle_IMG_pos_24),
                },

                {
                        findViewById(R.id.obstacle_IMG_pos_30),
                        findViewById(R.id.obstacle_IMG_pos_31),
                        findViewById(R.id.obstacle_IMG_pos_32),
                        findViewById(R.id.obstacle_IMG_pos_33),
                        findViewById(R.id.obstacle_IMG_pos_34),
                },

                {
                        findViewById(R.id.obstacle_IMG_pos_40),
                        findViewById(R.id.obstacle_IMG_pos_41),
                        findViewById(R.id.obstacle_IMG_pos_42),
                        findViewById(R.id.obstacle_IMG_pos_43),
                        findViewById(R.id.obstacle_IMG_pos_44),
                },

                {
                        findViewById(R.id.obstacle_IMG_pos_50),
                        findViewById(R.id.obstacle_IMG_pos_51),
                        findViewById(R.id.obstacle_IMG_pos_52),
                        findViewById(R.id.obstacle_IMG_pos_53),
                        findViewById(R.id.obstacle_IMG_pos_54),
                },

                {
                        findViewById(R.id.obstacle_IMG_pos_60),
                        findViewById(R.id.obstacle_IMG_pos_61),
                        findViewById(R.id.obstacle_IMG_pos_62),
                        findViewById(R.id.obstacle_IMG_pos_63),
                        findViewById(R.id.obstacle_IMG_pos_64),
                },

                {
                        findViewById(R.id.obstacle_IMG_pos_70),
                        findViewById(R.id.obstacle_IMG_pos_71),
                        findViewById(R.id.obstacle_IMG_pos_72),
                        findViewById(R.id.obstacle_IMG_pos_73),
                        findViewById(R.id.obstacle_IMG_pos_74),
                },
        };
    }

    private void findCoins() {
        coins = new AppCompatImageView[][]{
                {
                        findViewById(R.id.coin_IMG_pos_00),
                        findViewById(R.id.coin_IMG_pos_01),
                        findViewById(R.id.coin_IMG_pos_02),
                        findViewById(R.id.coin_IMG_pos_03),
                        findViewById(R.id.coin_IMG_pos_04),
                },

                {
                        findViewById(R.id.coin_IMG_pos_10),
                        findViewById(R.id.coin_IMG_pos_11),
                        findViewById(R.id.coin_IMG_pos_12),
                        findViewById(R.id.coin_IMG_pos_13),
                        findViewById(R.id.coin_IMG_pos_14),

                },

                {
                        findViewById(R.id.coin_IMG_pos_20),
                        findViewById(R.id.coin_IMG_pos_21),
                        findViewById(R.id.coin_IMG_pos_22),
                        findViewById(R.id.coin_IMG_pos_23),
                        findViewById(R.id.coin_IMG_pos_24),
                },

                {
                        findViewById(R.id.coin_IMG_pos_30),
                        findViewById(R.id.coin_IMG_pos_31),
                        findViewById(R.id.coin_IMG_pos_32),
                        findViewById(R.id.coin_IMG_pos_33),
                        findViewById(R.id.coin_IMG_pos_34),
                },

                {
                        findViewById(R.id.coin_IMG_pos_40),
                        findViewById(R.id.coin_IMG_pos_41),
                        findViewById(R.id.coin_IMG_pos_42),
                        findViewById(R.id.coin_IMG_pos_43),
                        findViewById(R.id.coin_IMG_pos_44),
                },

                {
                        findViewById(R.id.coin_IMG_pos_50),
                        findViewById(R.id.coin_IMG_pos_51),
                        findViewById(R.id.coin_IMG_pos_52),
                        findViewById(R.id.coin_IMG_pos_53),
                        findViewById(R.id.coin_IMG_pos_54),
                },

                {
                        findViewById(R.id.coin_IMG_pos_60),
                        findViewById(R.id.coin_IMG_pos_61),
                        findViewById(R.id.coin_IMG_pos_62),
                        findViewById(R.id.coin_IMG_pos_63),
                        findViewById(R.id.coin_IMG_pos_64),
                },

                {
                        findViewById(R.id.coin_IMG_pos_70),
                        findViewById(R.id.coin_IMG_pos_71),
                        findViewById(R.id.coin_IMG_pos_72),
                        findViewById(R.id.coin_IMG_pos_73),
                        findViewById(R.id.coin_IMG_pos_74),
                },
        };
    }

    private void findCars() {
        carSpot = new AppCompatImageView[]{
                findViewById(R.id.car_IMG_pos_0),
                findViewById(R.id.car_IMG_pos_1),
                findViewById(R.id.car_IMG_pos_2),
                findViewById(R.id.car_IMG_pos_3),
                findViewById(R.id.car_IMG_pos_4),
        };
    }

    private void findCrashes() {
        crashSpot = new AppCompatImageView[]{
                findViewById(R.id.crash_IMG_pos_0),
                findViewById(R.id.crash_IMG_pos_1),
                findViewById(R.id.crash_IMG_pos_2),
                findViewById(R.id.crash_IMG_pos_3),
                findViewById(R.id.crash_IMG_pos_4),
        };
    }

    private void findHearts() {
        lives = new ShapeableImageView[]{
                findViewById(R.id.main_heart_IMG_one),
                findViewById(R.id.main_heart_IMG_two),
                findViewById(R.id.main_heart_IMG_three),
        };
    }

    private void loadImages() {
        loadLandscape();
        loadCarImg();
        loadCrashImg();
        loadObstaclesImg();
        loadCoinsImg();
    }

    private void loadLandscape() {
        Glide
                .with(this)
                .load(R.drawable.three_lane_highway)
                .into(main_IMG_lanes);
    }

    /**
     * Binds images and views in a row
     * note that this function deals with AppCompactImageView
     *
     * @param imageResource the image resource to bind.
     * @param view          the view to bind the image to.
     */
    private void loadRowOfImages(int imageResource, AppCompatImageView[] view) {
        for (int i = 0; i < COLS; i++) {
            Glide
                    .with(this)
                    .load(imageResource)
                    .into(view[i]);
        }
    }

    private void loadCrashImg() {
        loadRowOfImages(R.drawable.explosion, crashSpot);
    }

    private void loadCarImg() {
        loadRowOfImages(R.drawable.car_blue, carSpot);
    }

    private void loadObstaclesImg() {
        for (int i = 0; i < ROWS; i++)
            loadRowOfImages(R.drawable.granite_img, obstacles[i]);
    }

    private void loadCoinsImg() {
        for (int i = 0; i < ROWS; i++)
            loadRowOfImages(R.drawable.bit_coin, coins[i]);
    }
}