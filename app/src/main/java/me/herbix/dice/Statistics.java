package me.herbix.dice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Chaofan on 2017/2/8.
 */

public class Statistics extends SQLiteOpenHelper {

    public static final String TABLE = "DiceStatistics";
    public static final String ROW_DICE_PROPERTY = "DiceProperty";
    public static final String ROW_RECORD_TYPE = "RecordType";
    public static final String ROW_KEY = "Key";
    public static final String ROW_VALUE = "Value";
    public static final String RECORD_TYPE_SUM = "Sum";
    public static final String RECORD_TYPE_DICE = "Dice";
    public static final String RECORD_TYPE_DICE_FORMAT = "Dice%d";

    private final Map<Integer, Integer> diceResults = new HashMap<>();
    private final Map<Integer, Integer> sumResults = new HashMap<>();
    private final Map<Integer, Integer>[] dicesResults = new Map[SettingsActivity.MAX_DICE_COUNT];
    private DiceProperty currentProperty = null;

    public Statistics(Context context) {
        super(context, "Dice", null, 1);
        for (int i=0; i<dicesResults.length; i++) {
            dicesResults[i] = new HashMap<>();
        }
    }

    public List<DiceProperty> getStoredDiceProperties() {
        List<DiceProperty> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(true, TABLE, new String[]{ROW_DICE_PROPERTY}, null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String recordType = c.getString(c.getColumnIndex(ROW_DICE_PROPERTY));
            list.add(new DiceProperty(recordType));
            c.moveToNext();
        }
        c.close();
        db.close();
        return list;
    }

    public void setDiceProperty(int diceCount, int[] diceTypes) {
        flush();
        clear();

        SQLiteDatabase db = getReadableDatabase();
        currentProperty = new DiceProperty(diceCount, diceTypes);
        Cursor c = db.query(TABLE, null, ROW_DICE_PROPERTY + " = '" + currentProperty + "'", null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String recordType = c.getString(c.getColumnIndex(ROW_RECORD_TYPE));
            int key = c.getInt(c.getColumnIndex(ROW_KEY));
            int value = c.getInt(c.getColumnIndex(ROW_VALUE));
            switch (recordType) {
                case RECORD_TYPE_SUM:
                    sumResults.put(key, value);
                    break;
                case RECORD_TYPE_DICE:
                    diceResults.put(key, value);
                    break;
                default:
                    int id = recordType.charAt(recordType.length() - 1) - '0';
                    dicesResults[id].put(key, value);
                    break;
            }
            c.moveToNext();
        }
        c.close();
        db.close();
    }

    public void addSumResult(int value) {
        synchronized (sumResults) {
            Integer v = sumResults.get(value);
            if (v == null) {
                sumResults.put(value, 1);
            } else {
                sumResults.put(value, v + 1);
            }
        }
    }

    public void addDiceResult(int id, int value) {
        Integer v;
        synchronized (dicesResults[id]) {
            v = dicesResults[id].get(value);
            if (v == null) {
                dicesResults[id].put(value, 1);
            } else {
                dicesResults[id].put(value, v + 1);
            }
        }
        if (currentProperty != null && DiceTypeUtil.isNumberDice(currentProperty.diceTypes[id])) {
            synchronized (diceResults) {
                v = diceResults.get(value);
                if (v == null) {
                    diceResults.put(value, 1);
                } else {
                    diceResults.put(value, v + 1);
                }
            }
        }
    }

    public Map<Integer, Integer> getDiceResults() {
        return diceResults;
    }

    public Map<Integer, Integer> getSumResults() {
        return sumResults;
    }

    public Map<Integer, Integer> getDiceResults(int id) { return dicesResults[id]; }

    public void reset() {
        clear();

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, ROW_DICE_PROPERTY + " = '" + currentProperty + "'", null);
        db.close();
    }

    private void clear() {
        diceResults.clear();
        sumResults.clear();
        for (int i=0; i<dicesResults.length; i++) {
            dicesResults[i].clear();
        }
    }

    public void flush() {
        if (currentProperty == null) {
            return;
        }

        SQLiteDatabase db = getWritableDatabase();
        for (Map.Entry<Integer, Integer> e : diceResults.entrySet()) {
            insertOrUpdate(db, RECORD_TYPE_DICE, e.getKey(), e.getValue());
        }
        for (Map.Entry<Integer, Integer> e : sumResults.entrySet()) {
            insertOrUpdate(db, RECORD_TYPE_SUM, e.getKey(), e.getValue());
        }
        for (int i=0; i<dicesResults.length; i++) {
            for (Map.Entry<Integer, Integer> e : dicesResults[i].entrySet()) {
                insertOrUpdate(db, String.format(Locale.ENGLISH, RECORD_TYPE_DICE_FORMAT, i), e.getKey(), e.getValue());
            }
        }
        db.close();
    }

    private void insertOrUpdate(SQLiteDatabase db, String recordType, Integer key, Integer value) {
        ContentValues cv = new ContentValues();
        cv.put(ROW_DICE_PROPERTY, currentProperty.toString());
        cv.put(ROW_RECORD_TYPE, recordType);
        cv.put(ROW_KEY, key);
        cv.put(ROW_VALUE, value);
        long result = db.insert(TABLE, null, cv);
        if (result == -1) {
            db.update(TABLE, cv, ROW_DICE_PROPERTY + " = '" + currentProperty + "' and " +
                    ROW_RECORD_TYPE + " = '" + recordType + "'", null);
        }
    }

    @Override
    public synchronized void close() {
        flush();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE + " (" +
                ROW_DICE_PROPERTY + " varchar(255)," +
                ROW_RECORD_TYPE + " varchar(15)," +
                ROW_KEY + " integer," +
                ROW_VALUE + " integer" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
