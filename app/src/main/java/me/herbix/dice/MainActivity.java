package me.herbix.dice;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Random rand = new Random();

    private DiceView[] blueDices = new DiceView[6];
    private DiceView[] redDices = new DiceView[6];

    private int[] blueStatus = new int[7];
    private int[] redStatus = new int[7];

    private DiceStatusView blueStatusView;
    private DiceStatusView redStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        RelativeLayout his1 = (RelativeLayout) findViewById(R.id.rollHisBlue);

        for (int i=0; i<6; i++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dip2px(38), dip2px(38));
            DiceView dice = new DiceView(this);
            dice.setId(i + 1);
            dice.setColor(Color.rgb(180, 180, 255));
            params.setMargins(dip2px(1), dip2px(1), dip2px(1), dip2px(1));
            if (i == 0) {
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            } else {
                params.addRule(RelativeLayout.ABOVE, blueDices[i - 1].getId());
            }
            assert his1 != null;
            his1.addView(dice, i, params);
            dice.setVisibility(View.INVISIBLE);
            blueDices[i] = dice;
        }

        RelativeLayout his2 = (RelativeLayout) findViewById(R.id.rollHisRed);

        for (int i=0; i<6; i++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dip2px(38), dip2px(38));
            DiceView dice = new DiceView(this);
            dice.setId(i + 1);
            dice.setColor(Color.rgb(255, 180, 180));
            params.setMargins(dip2px(1), dip2px(1), dip2px(1), dip2px(1));
            if (i == 0) {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            } else {
                params.addRule(RelativeLayout.BELOW, redDices[i - 1].getId());
            }
            assert his2 != null;
            his2.addView(dice, i, params);
            dice.setVisibility(View.INVISIBLE);
            redDices[i] = dice;
        }

        blueStatusView = (DiceStatusView) findViewById(R.id.blueStatus);
        redStatusView = (DiceStatusView) findViewById(R.id.redStatus);

        blueStatusView.setStatus(blueStatus);
        redStatusView.setStatus(redStatus);
    }

    public int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void rollDice(View view) {
        if (!(view instanceof DiceView)) {
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
                        boolean isBlue = dice.getId() == R.id.blueDice;
                        DiceView[] dices = isBlue ? blueDices : redDices;
                        int[] status = isBlue ? blueStatus : redStatus;
                        DiceStatusView statusView = isBlue ? blueStatusView : redStatusView;

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
                });
            }
        }.start();

    }

    public void rollBoth(View view) {
        rollDice(findViewById(R.id.blueDice));
        rollDice(findViewById(R.id.redDice));
    }
}
