package me.herbix.dice;


import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Chaofan on 2017/2/1.
 */

public class DicesFragment extends Fragment {

    public static int GREY = Color.rgb(180, 180, 180);
    public static int RED = Color.rgb(204, 68, 68);
    public static int BLUE = Color.rgb(68, 68, 204);
    public static int YELLOW = Color.rgb(255, 204, 68);
    public static int GREEN = Color.rgb(68, 204, 68);
    public static int PURPLE = Color.rgb(180, 68, 180);
    public static int CYAN = Color.rgb(68, 204, 255);

    public static int GREY_LIGHT = Color.rgb(204, 204, 204);
    public static int RED_LIGHT = Color.rgb(255, 180, 180);
    public static int BLUE_LIGHT = Color.rgb(180, 180, 255);
    public static int YELLOW_LIGHT = Color.rgb(255, 234, 180);
    public static int GREEN_LIGHT = Color.rgb(180, 255, 180);
    public static int PURPLE_LIGHT = Color.rgb(255, 180, 255);
    public static int CYAN_LIGHT = Color.rgb(180, 234, 255);

    public static int[] DICE_TYPE_TO_COLOR = new int[]{
            GREY, RED, BLUE, YELLOW, GREEN, PURPLE, CYAN
    };
    public static int[] DICE_TYPE_TO_COLOR_LIGHT = new int[]{
            GREY_LIGHT, RED_LIGHT, BLUE_LIGHT, YELLOW_LIGHT, GREEN_LIGHT, PURPLE_LIGHT, CYAN_LIGHT
    };

    private DiceView[][] littleDices = new DiceView[SettingsActivity.MAX_DICE_COUNT][6];
    private int[][] diceStatus = new int[SettingsActivity.MAX_DICE_COUNT][7];
    private DiceStatusView[] statusViews = new DiceStatusView[SettingsActivity.MAX_DICE_COUNT];

    public static DicesFragment newInstance(int diceCount, int[] diceTypes) {
        DicesFragment f = new DicesFragment();

        Bundle args = new Bundle();
        args.putInt("DiceCount", diceCount);
        args.putIntArray("DiceTypes", diceTypes.clone());
        f.setArguments(args);

        return f;
    }

    private int getDiceFragment() {
        int diceCount = getArguments().getInt("DiceCount", 1);
        int diceFragment;
        switch (diceCount) {
            case 1:
            default:
                diceFragment = R.layout.one_dice_fragment;
                break;
            case 2:
                diceFragment = R.layout.two_dices_fragment;
                break;
            case 3:
                diceFragment = R.layout.three_dices_fragment;
                break;
            case 4:
                diceFragment = R.layout.four_dices_fragment;
                break;
        }
        return diceFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getDiceFragment(), container, false);

        int[] diceTypes = getArguments().getIntArray("DiceTypes");
        if (diceTypes == null) {
            diceTypes = new int[4];
        }

        setDiceColor(view, R.id.dice1, diceTypes[0]);
        setDiceColor(view, R.id.dice2, diceTypes[1]);
        setDiceColor(view, R.id.dice3, diceTypes[2]);
        setDiceColor(view, R.id.dice4, diceTypes[3]);

        for (int i=0; i<diceTypes.length; i++) {
            if (diceTypes[i] >= DICE_TYPE_TO_COLOR_LIGHT.length) {
                diceTypes[i] = 0;
            }
        }

        addLittleDiceFor((RelativeLayout) view.findViewById(R.id.rollHistory1), 0, DICE_TYPE_TO_COLOR_LIGHT[diceTypes[0]], 0);
        addLittleDiceFor((RelativeLayout) view.findViewById(R.id.rollHistory2), 1, DICE_TYPE_TO_COLOR_LIGHT[diceTypes[1]], 1);

        statusViews[0] = (DiceStatusView) view.findViewById(R.id.status1);
        statusViews[1] = (DiceStatusView) view.findViewById(R.id.status2);

        if (statusViews[0] != null) {
            statusViews[0].setStatus(diceStatus[0]);
            statusViews[0].setColor(DICE_TYPE_TO_COLOR_LIGHT[diceTypes[0]]);
        }
        if (statusViews[1] != null) {
            statusViews[1].setStatus(diceStatus[1]);
            statusViews[1].setColor(DICE_TYPE_TO_COLOR_LIGHT[diceTypes[1]]);
        }

        return view;
    }

    private void setDiceColor(View view, int diceId, int diceType) {
        DiceView dice = (DiceView) view.findViewById(diceId);
        if (dice != null) {
            dice.setColor(DICE_TYPE_TO_COLOR[0]);
            switch (diceType) {
                case 20:
                    dice.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.catan_kac));
                    break;
                case 100:
                    dice.setColor(Color.WHITE);
                    break;
                default:
                    dice.setColor(DICE_TYPE_TO_COLOR[diceType]);
                    break;
            }
        }
    }

    private void addLittleDiceFor(RelativeLayout his1, int diceId, int color, int direction) {
        if (his1 != null) {
            for (int i = 0; i < 6; i++) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dip2px(38), dip2px(38));
                DiceView dice = new DiceView(this.getActivity());
                dice.setId(i + 1);
                dice.setColor(color);
                params.setMargins(dip2px(1), dip2px(1), dip2px(1), dip2px(1));
                if (direction == 0) {
                    if (i == 0) {
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    } else {
                        params.addRule(RelativeLayout.ABOVE, littleDices[diceId][i - 1].getId());
                    }
                } else {
                    if (i == 0) {
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    } else {
                        params.addRule(RelativeLayout.BELOW, littleDices[diceId][i - 1].getId());
                    }
                }
                his1.addView(dice, i, params);
                dice.setVisibility(View.INVISIBLE);
                littleDices[diceId][i] = dice;
            }
        }
    }

    private int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public DiceView[] getLittleDices(int id) {
        return littleDices[id];
    }

    public int[] getDiceStatus(int id) {
        return diceStatus[id];
    }

    public DiceStatusView getStatusViews(int id) {
        return statusViews[id];
    }
}
