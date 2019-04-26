package view.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.hfj.trafficdog.R;
import com.hfj.trafficdog.core.Appinfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import base.dialog.AppSortDialog;
import view.BaseActivity;
import view.main.listener.OnPackageClickListener;
import view.stats.StatsActivity;

public class MainActivity extends BaseActivity implements OnPackageClickListener {
    private RecyclerView recyclerView;
    private List<Appinfo> appinfoList;
    private AppSorter mSorter;
    private AppInfoAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        init();
    }

    private void init(){
        mSorter = new AppSorter();
        appinfoList = getPackageList();
        recyclerView = findViewById(R.id.main_activity_app_recyclerview);
        mAdapter = new AppInfoAdapter(appinfoList,this);
        recyclerView.setAdapter(mAdapter);
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
            appinfo.setPackageName(packageInfoItem.packageName);
            appinfo.setVersion(packageInfoItem.versionName);
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
        mSorter.sortApps(resLists);
        return resLists;
    }

    @Override
    public void onClick(Appinfo packageItem) {
        Intent intent =  new Intent(MainActivity.this, StatsActivity.class);
        intent.putExtra(StatsActivity.EXTRA_PACKAGE, packageItem.getPackageName());
        startActivity(intent);
    }

    private void refresh(){
        loadData();
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
}
