package view.stats;

import android.annotation.TargetApi;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hfj.trafficdog.R;

import utilities.NetworkStatsHelper;
import utilities.PermissionTool;
import utilities.TrafficTool;
import view.BaseActivity;
import view.main.MainActivity;

public class StatsActivity extends BaseActivity {
    public static final String EXTRA_PACKAGE = "ExtraPackage";

    private TextView totalRxTV;
    private TextView totalTxTV;
    private TextView packageRxTV;
    private TextView packageTxTV;

    AppCompatImageView ivIcon;
    Toolbar toolbar;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivIcon = findViewById(R.id.avatar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PermissionTool.requestPermissions(this);
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
            toolbar.setSubtitle(packageName + ":" + NetworkStatsHelper.getPackageUid(this, packageName));
            setSupportActionBar(toolbar);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!NetworkStatsHelper.isPackage(StatsActivity.this, packageName)) {
            return;
        }
        fillData(packageName);
    }

    private void init(){
        totalRxTV = findViewById(R.id.network_stats_manager_all_Rx_value);
        totalTxTV = findViewById(R.id.network_stats_manager_all_Tx_value);
        packageRxTV = findViewById(R.id.network_stats_package_rx_value);
        packageTxTV = findViewById(R.id.network_stats_package_tx_value);
    }

    private void fillData(String packageName) {
        int uid = NetworkStatsHelper.getPackageUid(this, packageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
            NetworkStatsHelper networkStatsHelper = new NetworkStatsHelper(networkStatsManager, uid);
            fillNetworkStatsAll(networkStatsHelper);
            fillNetworkStatsPackage(uid, networkStatsHelper);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void fillNetworkStatsAll(NetworkStatsHelper networkStatsHelper) {
        long mobileWifiRx = networkStatsHelper.getAllRxBytesMobile(this) + networkStatsHelper.getAllRxBytesWifi();
        totalRxTV.setText(TrafficTool.BparseToRacialDigit(mobileWifiRx));
        long mobileWifiTx = networkStatsHelper.getAllTxBytesMobile(this) + networkStatsHelper.getAllTxBytesWifi();
        totalTxTV.setText(TrafficTool.BparseToRacialDigit(mobileWifiTx));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void fillNetworkStatsPackage(int uid, NetworkStatsHelper networkStatsHelper) {
        long mobileWifiRx = networkStatsHelper.getPackageRxBytesMobile(this) + networkStatsHelper.getPackageRxBytesWifi();
        packageRxTV.setText(TrafficTool.BparseToRacialDigit(mobileWifiRx));
        long mobileWifiTx = networkStatsHelper.getPackageTxBytesMobile(this) + networkStatsHelper.getPackageTxBytesWifi();
        packageTxTV.setText(TrafficTool.BparseToRacialDigit(mobileWifiTx));
    }
}
