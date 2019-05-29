package base;

import android.content.Context;

import com.hfj.trafficdog.core.Appinfo;

import java.util.List;

import utilities.NetworkPeriod;
import utilities.NetworkStatsStatistics;
import utilities.NetworkType;
import view.main.AppSorter;
import view.main.listener.OnFlowRefreshListener;

public class RequestFlowThread implements Runnable{
    private List<Appinfo> appinfoList;
    private OnFlowRefreshListener listener;
    private Context mContext;

    public RequestFlowThread(Context context,List<Appinfo> appinfoList, OnFlowRefreshListener listener) {
        this.appinfoList = appinfoList;
        this.listener = listener;
        this.mContext = context;
    }

    @Override
    public void run() {
        NetworkStatsStatistics statsStatistics = new NetworkStatsStatistics(mContext);
        if (appinfoList == null || appinfoList.size() == 0){
            listener.onRefreshFail();
            return;
        }
        for (int i=0;i<appinfoList.size();i++) {
            Appinfo appinfo = appinfoList.get(i);
            long rxByte = statsStatistics.getBytes(appinfo.getUid(), NetworkType.mobile, NetworkPeriod.TODAY,NetworkStatsStatistics.NETWORK_RX);
            long txByte = statsStatistics.getBytes(appinfo.getUid(),NetworkType.mobile,NetworkPeriod.TODAY,NetworkStatsStatistics.NETWORK_TX);
            appinfo.setRxMobileByte(rxByte);
            appinfo.setTxMobileByte(txByte);
            listener.onRefreshItem(rxByte + txByte,i);
        }
        new AppSorter().sortApps(appinfoList);
        listener.onRefreshFinish();
    }
}