package view.stats;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hfj.trafficdog.R;
import com.hfj.trafficdog.core.Appinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import utilities.NetworkPeriod;
import utilities.NetworkStatsStatistics;
import utilities.NetworkType;
import utilities.PermissionTool;
import utilities.TrafficTool;
import view.BaseActivity;
import view.main.MainActivity;

public class StatsActivity extends BaseActivity {
    public static final String EXTRA_PACKAGE = "ExtraPackage";

    private Appinfo appinfo;
    private TextView tv_today_flow;
    private TextView tv_weekly_flow;

    private BarChart barChart;

    AppCompatImageView ivIcon;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivIcon = findViewById(R.id.avatar);
        barChart = findViewById(R.id.network_stats_bar_chart);
        appinfo = getIntent().getParcelableExtra("appinfo");
        initBarChart();

    }


    private void initBarChart(){
        ValueFormatter xAxisFormatter = new AppNameXAxisDataFormatter();

//        barChart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-ax is separately
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        // add a nice and smooth animation
        barChart.animateY(1500);
        barChart.getLegend().setEnabled(false);

    }
    private void refreshBarchart(){
        ArrayList<BarEntry> values = new ArrayList<>();

        List<Long> byteList = TrafficTool.getBytesWithTime(new NetworkStatsStatistics(getApplicationContext())
                ,appinfo.getUid());

        Long max = Collections.max(byteList);
        TrafficTool.FlowUnit flowUnit = TrafficTool.getUnit(max);

        int index = 0;
        for (int i = 0; i < byteList.size() ; i+=2) {
            long val = byteList.get(i) ;
            if (i != (byteList.size() -1) ){
                val += byteList.get(i+1);
            }

            values.add(new BarEntry(index++, (float) TrafficTool.BparseToSpecialDigit(flowUnit,val)));
        }

        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "dataSet");
            Description des = new Description();
            des.setText("各时段流量数据（单位:" + flowUnit.name() + "）");
            barChart.setDescription(des);
            barChart.getDescription().setEnabled(true);
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
            barChart.setFitBars(true);
        }

        barChart.invalidate();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onFlowChanged() {
        refreshBarchart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!PermissionTool.hasPermissions(this)) {
            return;
        }
        init();
        checkIntent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
             if (isTaskRoot()){
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        String packageName = extras.getString(EXTRA_PACKAGE);
        if (packageName == null) {
            return;
        }
        try {
            ivIcon.setImageDrawable(getPackageManager().getApplicationIcon(packageName));
            toolbar.setTitle(getPackageManager().getApplicationLabel(
                    getPackageManager().getApplicationInfo(
                            packageName, PackageManager.GET_META_DATA)));
            toolbar.setSubtitle(packageName + ":" + NetworkStatsStatistics.getPackageUid(this, packageName));
            setSupportActionBar(toolbar);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!NetworkStatsStatistics.isPackage(StatsActivity.this, packageName)) {
            return;
        }
        fillData(packageName);
    }

    private void init(){
        tv_today_flow = findViewById(R.id.network_stats_today_val);
        tv_weekly_flow = findViewById(R.id.network_stats_weekly_val);
        refreshBarchart();
    }

    private void fillData(String packageName) {
        int uid = NetworkStatsStatistics.getPackageUid(this, packageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsStatistics networkStatsStatistics = new NetworkStatsStatistics(getApplicationContext(), uid);

            long today = networkStatsStatistics.getBytes(uid, NetworkType.mobile, NetworkPeriod.TODAY,NetworkStatsStatistics.NETWORK_TOTAL);
            long week = networkStatsStatistics.getBytes(uid, NetworkType.mobile, NetworkPeriod.WEEK,NetworkStatsStatistics.NETWORK_TOTAL);
            tv_today_flow.setText(TrafficTool.BparseToRacialDigit(today));
            tv_weekly_flow.setText(TrafficTool.BparseToRacialDigit(week));


        }
    }

    @Override
    protected void saveToGallery() {
    }
}
