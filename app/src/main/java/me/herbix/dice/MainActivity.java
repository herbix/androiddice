package me.herbix.dice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Random rand = new Random();

    private int diceCount = 2;
    private int[] diceTypes = new int[4];

    private boolean inSettings = false;

    private DicesFragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        loadSettings();
        loadUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (inSettings) {
            loadSettings();
            loadUI();
            inSettings = false;
        }
    }

    private void loadSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        diceCount = Integer.valueOf(preferences.getString(SettingsActivity.DICE_COUNT, "2"));
        diceTypes[0] = Integer.valueOf(preferences.getString(SettingsActivity.DICE1_TYPE, "0"));
        diceTypes[1] = Integer.valueOf(preferences.getString(SettingsActivity.DICE2_TYPE, "0"));
        diceTypes[2] = Integer.valueOf(preferences.getString(SettingsActivity.DICE3_TYPE, "0"));
        diceTypes[3] = Integer.valueOf(preferences.getString(SettingsActivity.DICE4_TYPE, "0"));
    }

    private void loadUI() {
        currentFragment = DicesFragment.newInstance(diceCount, diceTypes);
        getFragmentManager().beginTransaction()
                .replace(R.id.main_activity_inner, currentFragment)
                .commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_option:
                inSettings = true;
                startActivity(new Intent("me.herbix.dice.SettingsActivity"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void rollDice(View view) {
        if (view == null || !(view instanceof DiceView)) {
            return;
        }

        final DiceView dice = (DiceView) view;

        new Thread() {
            @Override
            public void run() {
                for (int i=1; i<=10; i++) {
                    dice.setNumber(rand.nextInt(6) + 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dice.invalidate();
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int id = getDiceIdByViewId(dice.getId());
                        DiceView[] dices = currentFragment.getLittleDices(id);
                        int[] status = currentFragment.getDiceStatus(id);
                        DiceStatusView statusView = currentFragment.getStatusViews(id);

                        if (statusView != null) {
                            for (int i = dices.length - 1; i >= 1; i--) {
                                dices[i].setVisibility(dices[i - 1].getVisibility());
                                dices[i].setNumber(dices[i - 1].getNumber());
                                dices[i].invalidate();
                            }

                            int number = dice.getNumber();

                            dices[0].setVisibility(View.VISIBLE);
                            dices[0].setNumber(number);
                            dices[0].invalidate();

                            status[0]++;
                            status[number]++;
                            statusView.invalidate();
                        }
                    }
                });
            }
        }.start();

    }

    private int getDiceIdByViewId(int id) {
        switch (id) {
            case R.id.dice1:
                return 0;
            case R.id.dice2:
                return 1;
            case R.id.dice3:
                return 2;
            case R.id.dice4:
                return 3;
        }
        return 0;
    }

    public void rollAll(View view) {
        rollDice(findViewById(R.id.dice1));
        rollDice(findViewById(R.id.dice2));
        rollDice(findViewById(R.id.dice3));
        rollDice(findViewById(R.id.dice4));
    }
}
