package me.herbix.dice;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by Chaofan on 2017/2/1.
 */

public class SettingsActivity extends AppCompatActivity {
    public static final String DICE_COUNT = "dice_count";
    public static final String DICE1_TYPE = "dice1_type";
    public static final String DICE2_TYPE = "dice2_type";
    public static final String DICE3_TYPE = "dice3_type";
    public static final String DICE4_TYPE = "dice4_type";

    public static final int MAX_DICE_COUNT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
