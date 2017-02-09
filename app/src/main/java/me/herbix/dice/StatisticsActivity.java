package me.herbix.dice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Chaofan on 2017/2/8.
 */
public class StatisticsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Statistics statistics = null;
    private DiceAdapter diceAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistics);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        statistics = new Statistics(this);
        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        ListView listView = (ListView) findViewById(R.id.statistics_list);
        diceAdapter = new DiceAdapter(this, statistics.getStoredDiceProperties());
        listView.setAdapter(diceAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (statistics != null) {
            statistics.close();
            statistics = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setTitle(R.string.delete_statistics)
                        .setMessage(R.string.delete_all_statistics_desc)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (DiceProperty dp : diceAdapter.propertyList) {
                                    statistics.setDiceProperty(dp.diceCount, dp.diceTypes);
                                    statistics.reset();
                                }
                                refreshList();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (diceAdapter.propertyList.isEmpty()) {
            return;
        }
        DiceProperty dp = (DiceProperty) diceAdapter.getItem(position);
        Intent intent = new Intent("me.herbix.dice.StatisticsItemActivity");
        intent.putExtra("DiceCount", dp.diceCount);
        intent.putExtra("DiceTypes", dp.diceTypes);
        startActivity(intent);
    }

    class DiceAdapter extends BaseAdapter {

        private final LayoutInflater inflater;
        private final List<DiceProperty> propertyList;

        DiceAdapter(Context context, List<DiceProperty> propertyList) {
            this.propertyList = propertyList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return Math.max(1, propertyList.size());
        }

        @Override
        public Object getItem(int position) {
            return propertyList.isEmpty() ? null : propertyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (propertyList.isEmpty()) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
                TextView view = (TextView) convertView;
                view.setText(R.string.no_statistics);
                return convertView;
            }

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.statistics_list_item, null);
            }

            DiceProperty property = propertyList.get(position);
            int[] diceTypes = property.diceTypes;
            int diceCount = property.diceCount;

            DiceTypeUtil.setDiceColor(convertView.getResources(), convertView, R.id.dice1, diceTypes[0]);
            DiceTypeUtil.setDiceColor(convertView.getResources(), convertView, R.id.dice2, diceTypes[1]);
            DiceTypeUtil.setDiceColor(convertView.getResources(), convertView, R.id.dice3, diceTypes[2]);
            DiceTypeUtil.setDiceColor(convertView.getResources(), convertView, R.id.dice4, diceTypes[3]);

            convertView.findViewById(R.id.dice2).setVisibility(diceCount > 1 ? View.VISIBLE : View.INVISIBLE);
            convertView.findViewById(R.id.dice3).setVisibility(diceCount > 2 ? View.VISIBLE : View.INVISIBLE);
            convertView.findViewById(R.id.dice4).setVisibility(diceCount > 3 ? View.VISIBLE : View.INVISIBLE);

            return convertView;
        }
    }
}
