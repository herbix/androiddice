package me.herbix.dice;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Chaofan on 2017/2/9.
 */

public class StatisticsItemActivity extends AppCompatActivity {

    private DiceProperty diceProperty = null;
    private Statistics statistics = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistics_item);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        statistics = new Statistics(this);

        Intent intent = getIntent();
        int diceCount = intent.getIntExtra("DiceCount", 1);
        int[] diceTypes = intent.getIntArrayExtra("DiceTypes");
        if (diceTypes == null) {
            diceTypes = new int[SettingsActivity.MAX_DICE_COUNT];
        }
        diceProperty = new DiceProperty(diceCount, diceTypes);
        statistics.setDiceProperty(diceCount, diceTypes);

        ListView listView = (ListView) findViewById(R.id.statistics_list);
        listView.setAdapter(new ChartAdapter());
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
                    .setMessage(R.string.delete_statistics_desc)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            statistics.reset();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    class ChartAdapter extends BaseAdapter implements IValueFormatter {
        private LayoutInflater inflater;
        private View[] views = new View[2 + SettingsActivity.MAX_DICE_COUNT];

        ChartAdapter() {
            this.inflater = LayoutInflater.from(StatisticsItemActivity.this);
        }

        @Override
        public int getCount() {
            return 2 + diceProperty.diceCount;
        }

        @Override
        public Object getItem(int position) {
            switch (position) {
                case 0:
                    return statistics.getSumResults();
                case 1:
                    return statistics.getDiceResults();
                default:
                    return statistics.getDiceResults(position - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (views[position] == null) {
                views[position] = inflater.inflate(R.layout.statistics_item_list_item, null);
            }

            convertView = views[position];

            TextView title = (TextView) convertView.findViewById(R.id.title);
            BarChart chart = (BarChart) convertView.findViewById(R.id.chart);
            DiceView dice = (DiceView) convertView.findViewById(R.id.dice);

            chart.setDescription(null);
            chart.getLegend().setEnabled(false);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawAxisLine(true);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1);

            YAxis yAxis;
            yAxis = chart.getAxisLeft();
            yAxis.setAxisMinimum(0);
            yAxis.setGranularity(1);

            yAxis = chart.getAxisRight();
            yAxis.setAxisMinimum(0);
            yAxis.setGranularity(1);

            Map<Integer, Integer> dataSource = null;
            int lowRange = 0;
            int highRange = 0;

            switch (position) {
                case 0:
                    dataSource = statistics.getSumResults();
                    title.setText(getString(R.string.sum_distribution));
                    int numberDiceCount = diceProperty.getNumberDiceCount();
                    lowRange = numberDiceCount;
                    highRange = 6 * numberDiceCount;
                    dice.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    dataSource = statistics.getDiceResults();
                    title.setText(getString(R.string.dice_distribution));
                    lowRange = 1;
                    highRange = 6;
                    dice.setVisibility(View.INVISIBLE);
                    break;
                default:
                    int diceId = position - 2;
                    dataSource = statistics.getDiceResults(diceId);
                    title.setText(getDiceDistributionString(diceId));
                    lowRange = 1;
                    highRange = 6;
                    dice.setVisibility(View.VISIBLE);
                    DiceTypeUtil.setDiceColor(getResources(), convertView, R.id.dice, diceProperty.diceTypes[diceId]);
                    break;
            }

            xAxis.setAxisMinimum(lowRange - 0.5f);
            xAxis.setAxisMaximum(highRange + 0.5f);

            if (dataSource != null && !dataSource.isEmpty()) {
                List<BarEntry> entries = new ArrayList<>();
                for (int i=lowRange; i<=highRange; i++) {
                    Integer v = dataSource.get(i);
                    entries.add(new BarEntry(i, v == null ? 0 : v));
                }
                BarDataSet dataSet = new BarDataSet(entries, title.getText().toString());
                dataSet.setColor(Color.GRAY);
                dataSet.setValueFormatter(this);
                BarData data = new BarData(dataSet);
                chart.setData(data);
            } else {
                chart.setData(null);
            }

            return convertView;
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf((int) value);
        }
    }

    @NonNull
    private String getDiceDistributionString(int id) {
        switch (id) {
            case 0:
                return getString(R.string.dice1_distribution);
            case 1:
                return getString(R.string.dice2_distribution);
            case 2:
                return getString(R.string.dice3_distribution);
            case 3:
                return getString(R.string.dice4_distribution);
            default:
                return null;
        }
    }
}
