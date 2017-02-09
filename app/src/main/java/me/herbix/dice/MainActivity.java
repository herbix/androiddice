package me.herbix.dice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private Random rand = new Random();

    private int diceCount = 2;
    private int[] diceTypes = new int[4];
    private boolean showSum = false;

    private boolean inSettings = false;

    private DicesFragment currentFragment = null;

    private TextView sumView = null;
    private AtomicInteger diceSumAtom = new AtomicInteger(0);

    private Statistics statistics = null;

    private volatile boolean isRollAll = false;
    private AtomicInteger rollAllCount = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        statistics = new Statistics(this);

        loadSettings();
        loadUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (statistics == null) {
            statistics = new Statistics(this);
            statistics.setDiceProperty(diceCount, diceTypes);
        }
        if (inSettings) {
            loadSettings();
            loadUI();
            inSettings = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (statistics != null) {
            statistics.flush();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (statistics != null) {
            statistics.close();
            statistics = null;
        }
    }

    private void loadSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        diceCount = Integer.valueOf(preferences.getString(SettingsActivity.DICE_COUNT, "2"));
        diceTypes[0] = Integer.valueOf(preferences.getString(SettingsActivity.DICE1_TYPE, "0"));
        diceTypes[1] = Integer.valueOf(preferences.getString(SettingsActivity.DICE2_TYPE, "0"));
        diceTypes[2] = Integer.valueOf(preferences.getString(SettingsActivity.DICE3_TYPE, "0"));
        diceTypes[3] = Integer.valueOf(preferences.getString(SettingsActivity.DICE4_TYPE, "0"));
        showSum = preferences.getBoolean(SettingsActivity.SHOW_SUM, true);
        statistics.setDiceProperty(diceCount, diceTypes);
    }

    private void loadUI() {
        currentFragment = DicesFragment.newInstance(diceCount, diceTypes);
        getFragmentManager().beginTransaction()
                .replace(R.id.main_activity_inner, currentFragment)
                .commit();
        sumView = (TextView) findViewById(R.id.sum_text);
        diceSumAtom.set(0);
        for (int i=0; i<diceCount; i++) {
            if (DiceTypeUtil.isNumberDice(diceTypes[i])) {
                diceSumAtom.addAndGet(6);
            }
        }
        setSumView();
    }

    private void setSumView() {
        if (sumView != null) {
            if (showSum) {
                sumView.setText(String.format(getString(R.string.sum), diceSumAtom.get()));
                sumView.setVisibility(View.VISIBLE);
            } else {
                sumView.setVisibility(View.INVISIBLE);
            }
        }
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
            case R.id.action_statistics:
                if (statistics != null) {
                    statistics.close();
                    statistics = null;
                }
                startActivity(new Intent("me.herbix.dice.StatisticsActivity"));
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
                final int id = getDiceIdByViewId(dice.getId());
                int newNumber = 0;
                for (int i=1; i<=10; i++) {
                    int oldNumber = dice.getNumber();
                    newNumber = rand.nextInt(6) + 1;
                    if (DiceTypeUtil.isNumberDice(diceTypes[id])) {
                        diceSumAtom.addAndGet(newNumber - oldNumber);
                    }
                    dice.setNumber(newNumber);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSumView();
                            dice.invalidate();
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                final int number = newNumber;
                if (statistics != null) {
                    statistics.addDiceResult(id, number);
                }
                if (isRollAll) {
                    int count = rollAllCount.incrementAndGet();
                    if (count == diceCount) {
                        int sumValue = diceSumAtom.get();
                        if (statistics != null && sumValue > 0) {
                            statistics.addSumResult(sumValue);
                        }
                        isRollAll = false;
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DiceView[] dices = currentFragment.getLittleDices(id);
                        int[] status = currentFragment.getDiceStatus(id);
                        DiceStatusView statusView = currentFragment.getStatusViews(id);

                        if (statusView != null) {
                            for (int i = dices.length - 1; i >= 1; i--) {
                                dices[i].setVisibility(dices[i - 1].getVisibility());
                                dices[i].setNumber(dices[i - 1].getNumber());
                                dices[i].invalidate();
                            }

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
        isRollAll = true;
        rollAllCount.set(0);
        rollDice(findViewById(R.id.dice1));
        rollDice(findViewById(R.id.dice2));
        rollDice(findViewById(R.id.dice3));
        rollDice(findViewById(R.id.dice4));
    }
}
