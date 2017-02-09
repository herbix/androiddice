package me.herbix.dice;

import java.util.Arrays;

/**
 * Created by Chaofan on 2017/2/9.
 */
public class DiceProperty {
    public final int diceCount;
    public final int[] diceTypes;

    public DiceProperty(int diceCount, int[] diceTypes) {
        this.diceCount = diceCount;
        this.diceTypes = diceTypes.clone();
    }

    public DiceProperty(String str) {
        String[] split = str.split(",");
        diceCount = split.length;
        diceTypes = new int[SettingsActivity.MAX_DICE_COUNT];
        int i = 0;
        for (String s : split) {
            diceTypes[i] = Integer.valueOf(split[i]);
            i++;
        }
    }

    public int getNumberDiceCount() {
        int r = 0;
        for (int i=0; i<diceCount; i++) {
            if (DiceTypeUtil.isNumberDice(diceTypes[i])) {
                r++;
            }
        }
        return r;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DiceProperty)) {
            return false;
        }
        DiceProperty dp = (DiceProperty) obj;
        return diceCount == dp.diceCount && Arrays.equals(diceTypes, dp.diceTypes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(diceTypes) ^ diceCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < diceCount; i++) {
            sb.append(diceTypes[i]);
            sb.append(',');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
}
