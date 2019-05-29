package view.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.hfj.trafficdog.R;
import com.hfj.trafficdog.core.Appinfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import base.RequestFlowThread;
import base.dialog.AppSortDialog;
import sharedstorage.IPersistentKey;
import sharedstorage.PersistentsMgr;
import utilities.NetworkPeriod;
import utilities.NetworkStatsStatistics;
import utilities.NetworkType;
import utilities.TrafficTool;
import view.BaseActivity;
import view.main.listener.OnFlowRefreshListener;
import view.main.listener.OnPackageClickListener;
import view.stats.StatsActivity;

public class MainActivity extends BaseActivity implements OnPackageClickListener, OnChartValueSelectedListener, IPersistentKey {
    private RecyclerView recyclerView;
    private List<Appinfo> appinfoList;
    private AppSorter mSorter;
    private AppInfoAdapter mAdapter;
    private TextView tx_flow_digit;
    private TextView tx_flow_unit;

    private SwipeRefreshLayout swipeRefreshLayout;

    private PieChart pieChart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        init();
    }

    private void init(){
        initView();
        mSorter = new AppSorter();
        appinfoList = getPackageList();
        recyclerView = findViewById(R.id.main_activity_app_recyclerview);
        mAdapter = new AppInfoAdapter(appinfoList,this);
        recyclerView.setAdapter(mAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("mainActivity:67","我又在刷新啊");
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setCenterTextTypeface(tfLight);
        pieChart.setCenterText(generateCenterSpannableText());

        pieChart.setDrawHoleEnabled(false);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        pieChart.setOnChartValueSelectedListener(this);
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTypeface(tfRegular);
        pieChart.setEntryLabelTextSize(12f);

        setData();
    }

    private void initView(){
        pieChart = findViewById(R.id.main_view_bar_char);
        tx_flow_digit = findViewById(R.id.tx_network_flow_digit);
        tx_flow_unit = findViewById(R.id.tx_network_unit);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_main_activity);
    }



    private void setData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        if (!PersistentsMgr.get().getBoolean(CONST_KEY_IF_RANK_EXIST,false)){
            for (int i = 0; i < 1 ; i++) {
                entries.add(new PieEntry(5f,
                        "未载入",
                        getResources().getDrawable(R.drawable.star)));
            }
        }else {
            Long total = PersistentsMgr.get().getLong(CONST_KEY_TOTAL_FLOW_TODAY,0L);
           String[] appName = PersistentsMgr.get().getString(CONST_KEY_APP_NAME_STR,"").split(",");
           String[] flowVal = PersistentsMgr.get().getString(CONST_KEY_APP_VAL_STR,"").split(",");
            for (int i = 0; i < appName.length ; i++) {
                Long tmpVal = Long.valueOf(flowVal[i]);
                total -= tmpVal;
                entries.add(new PieEntry(tmpVal,
                        appName[i],
                        getResources().getDrawable(R.drawable.star)));
            }

            entries.add(new PieEntry(total > 0 ? total:0
                    ,"其他",getResources().getDrawable(R.drawable.star)));
        }


        PieDataSet dataSet = new PieDataSet(entries, "应用名称");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(tfLight);
        pieChart.setData(data);
        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }
    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("流量消耗\n");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 4, 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_list_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                Toast.makeText(this,"click search",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_sort:
                showSortOptions();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Appinfo> getPackageList(){
        List<Appinfo> resLists;
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        resLists = new ArrayList<>(packageInfos.size());
        for (PackageInfo packageInfoItem : packageInfos){
            if (packageManager.checkPermission(Manifest.permission.INTERNET,packageInfoItem.packageName)
             ==  PackageManager.PERMISSION_DENIED){
                //该应用没有网络权限
                continue;
            }
            Appinfo appinfo = new Appinfo();
            appinfo.setNetworkApp(true);
            appinfo.setPackageName(packageInfoItem.packageName);
            appinfo.setVersion(packageInfoItem.versionName);
            int uid = NetworkStatsStatistics.getPackageUid(this, packageInfoItem.packageName);
            appinfo.setUid(uid);
            resLists.add(appinfo);
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo =packageManager.getApplicationInfo(packageInfoItem.packageName,PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (applicationInfo != null){
                CharSequence name = packageManager.getApplicationLabel(applicationInfo);
                Class reflectApplicationInfo = applicationInfo.getClass();
                try {
                    Method isSystemApp = reflectApplicationInfo.getDeclaredMethod("isSystemApp");
                    isSystemApp.setAccessible(true);
                    boolean res = (Boolean) isSystemApp.invoke(applicationInfo);
                    appinfo.setSystemApp(res);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (name != null)
                    appinfo.setName(name.toString());
            }
        }
        requestFlow();
        return resLists;
    }


    /**
     * 将流量消耗较高的前4个存入sharedPreferences
     * @param appinfos
     */
    private void restoreFirstApp(List<Appinfo> appinfos){

        if (appinfos == null || appinfos.size() == 0){
            return;
        }
        PersistentsMgr.get().putBoolean(IPersistentKey.CONST_KEY_IF_RANK_EXIST,true);
        AppSorter sorter = new AppSorter();
        sorter.isSortTypeChange(AppSorter.SortOptions.NETFLOW.ordinal());

        sorter.sortApps(appinfos);

        int size = appinfos.size() > 4 ? 4 : appinfos.size();
        StringBuilder appNameStr = new StringBuilder();
        StringBuilder appFlowVal = new StringBuilder();
        for (int i =0;i<size;i++){
            Appinfo appinfo = appinfos.get(i);
            appNameStr.append(appinfo.getName() + ",");
            appFlowVal.append(appinfo.getTxMobileByte() + appinfo.getRxMobileByte() + ",");
        }
        PersistentsMgr.get().putString(CONST_KEY_APP_NAME_STR,appNameStr.toString().substring(0,appNameStr.length()-1));

        PersistentsMgr.get().putString(CONST_KEY_APP_VAL_STR,appFlowVal.toString().substring(0,appFlowVal.length()-1));
    }

    private void requestFlow(){
        swipeRefreshLayout.setRefreshing(true);
        new Thread(new RequestFlowThread(getApplicationContext(),appinfoList, new OnFlowRefreshListener() {
            @Override
            public void onRefreshItem(long todayflow, int index) {

            }
            @Override
            public void onRefreshFinish() {
                restoreFirstApp(appinfoList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        setData();
                    }
                });

            }

            @Override
            public void onRefreshFail() {

            }
        })).start();
    }
    @Override
    protected void onStart() {
        super.onStart();

        refresh();
    }

    @Override
    protected void onFlowChanged() {
        requestFlow();
    }

    @Override
    public void onClick(Appinfo packageItem) {
        Intent intent =  new Intent(MainActivity.this, StatsActivity.class);
        intent.putExtra("appinfo",packageItem);
        intent.putExtra(StatsActivity.EXTRA_PACKAGE, packageItem.getPackageName());
        startActivity(intent);
    }

    private void refresh(){
        NetworkStatsStatistics networkStatsStatistics = new NetworkStatsStatistics(getApplicationContext());
        long totalToday = networkStatsStatistics.getBytes(NetworkType.mobile, NetworkPeriod.TODAY,NetworkStatsStatistics.NETWORK_RX)
                + networkStatsStatistics.getBytes(NetworkType.mobile, NetworkPeriod.TODAY,NetworkStatsStatistics.NETWORK_TX);
        PersistentsMgr.get().putLong(CONST_KEY_TOTAL_FLOW_TODAY,totalToday);
        tx_flow_digit.setText(TrafficTool.getDigitString(totalToday));
        tx_flow_unit.setText(TrafficTool.getUnit(totalToday).name());
        loadData();
        requestFlow();
    }

    private void loadData(){
        mSorter.sortApps(appinfoList);
        mAdapter.notifyDataSetChanged();
    }

    private void showSortOptions() {
        List<AppSorter.SortOptions> sortOptions = mSorter.getSortOptions();
        final String[] sortItems = new String[sortOptions.size()];
        for (int i = 0; i < sortOptions.size(); i++) {
            sortItems[i] = getString(sortOptions.get(i).msgRes);
        }
        new AppSortDialog(this)
                .setSingleChoiceItems(sortItems, mSorter.getSortType(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!mSorter.isSortTypeChange(which)) {
                            refresh();
                        }
                    }
                }).show();
    }

    @Override
    protected void saveToGallery() {
        saveToGallery(pieChart, "MainActivityBar");
    }
}
