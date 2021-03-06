package me.herbix.dice;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;

/**
 * Created by Chaofan on 2017/2/8.
 */
public class DiceTypeUtil {

    public static final int DICE_TYPE_STANDARD = 100;
    public static final int DICE_TYPE_CATAN_KAC = 20;

    public static final int GREY = Color.rgb(180, 180, 180);
    public static final int RED = Color.rgb(204, 68, 68);
    public static final int BLUE = Color.rgb(68, 68, 204);
    public static final int YELLOW = Color.rgb(255, 204, 68);
    public static final int GREEN = Color.rgb(68, 204, 68);
    public static final int PURPLE = Color.rgb(180, 68, 180);
    public static final int CYAN = Color.rgb(68, 204, 255);

    public static final int GREY_LIGHT = Color.rgb(204, 204, 204);
    public static final int RED_LIGHT = Color.rgb(255, 180, 180);
    public static final int BLUE_LIGHT = Color.rgb(180, 180, 255);
    public static final int YELLOW_LIGHT = Color.rgb(255, 234, 180);
    public static final int GREEN_LIGHT = Color.rgb(180, 255, 180);
    public static final int PURPLE_LIGHT = Color.rgb(255, 180, 255);
    public static final int CYAN_LIGHT = Color.rgb(180, 234, 255);

    public static final int[] DICE_TYPE_TO_COLOR = new int[]{
            GREY, RED, BLUE, YELLOW, GREEN, PURPLE, CYAN
    };
    public static final int[] DICE_TYPE_TO_COLOR_LIGHT = new int[]{
            GREY_LIGHT, RED_LIGHT, BLUE_LIGHT, YELLOW_LIGHT, GREEN_LIGHT, PURPLE_LIGHT, CYAN_LIGHT
    };

    public static void setDiceColor(Resources resources, View view, int diceId, int diceType) {
        DiceView dice = (DiceView) view.findViewById(diceId);
        if (dice != null) {
            dice.setColor(DICE_TYPE_TO_COLOR[0]);
            switch (diceType) {
                case DICE_TYPE_STANDARD:
                    dice.setColor(Color.WHITE);
                    break;
                default:
                    if (isNumberDice(diceType)) {
                        dice.setColor(DICE_TYPE_TO_COLOR[diceType]);
                    } else {
                        dice.setBitmap(BitmapFactory.decodeResource(resources, getDiceResourceId(diceType)));
                    }
                    break;
            }
        }
    }

    public static int getDiceResourceId(int diceType) {
        switch (diceType) {
            case DICE_TYPE_CATAN_KAC:
                return R.drawable.catan_kac;
            default:
                return 0;
        }
    }

    public static int getDiceColorLight(int diceType) {
        if (diceType >= 0 && diceType < DICE_TYPE_TO_COLOR_LIGHT.length) {
            return DICE_TYPE_TO_COLOR_LIGHT[diceType];
        }
        return GREY_LIGHT;
    }

    public static boolean isNumberDice(int diceType) {
        return (diceType >= 0 && diceType < 20) || diceType == 100;
    }

    public static int getStatisticsLabel(int diceType, int value) {
        if (isNumberDice(diceType)) {
            return value;
        }
        switch (diceType) {
            case DICE_TYPE_CATAN_KAC:
                if (value % 2 == 0) {
                    return 1;
                } else {
                    return (value / 2) + 2;
                }
            default:
                return value;
        }
    }

    public static int getValueFromStatisticsLabel(int diceType, int label) {
        if (isNumberDice(diceType)) {
            return label;
        }
        switch (diceType) {
            case DICE_TYPE_CATAN_KAC:
                if (label == 1) {
                    return 6;
                } else {
                    return (label - 2) * 2 + 1;
                }
            default:
                return label;
        }
    }

    public static int getStatisticsLabelCount(int diceType) {
        if (isNumberDice(diceType)) {
            return 6;
        }
        switch (diceType) {
            case DICE_TYPE_CATAN_KAC:
                return 4;
            default:
                return 6;
        }
    }
}
