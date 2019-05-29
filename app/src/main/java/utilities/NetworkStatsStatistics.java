package utilities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;

import java.util.Calendar;
import java.util.TimeZone;


@TargetApi(Build.VERSION_CODES.M)
public class NetworkStatsStatistics {

    NetworkStatsManager networkStatsManager;
    private Context mContext;
    int packageUid;

    public static final int NETWORK_RX = 0X331;
    public static final int NETWORK_TX = 0X332;
    public static final int NETWORK_TOTAL = 0X333;


    public NetworkStatsStatistics(Context context) {
        this.mContext = context;
       this.networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);

    }

    public NetworkStatsStatistics(Context context, int packageUid) {
        this.mContext = context;
        this.networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
        this.packageUid = packageUid;
    }


    public long getBytes(int packageUid, NetworkType networkType, NetworkPeriod networkPeriod, int type) {
        NetworkStats networkStats = null;
        networkStats = networkStatsManager.queryDetailsForUid(
                networkType.type,
                getSubscriberId(networkType.type),
                TrafficTool.getStartTimeFromPeriod(networkPeriod),
                System.currentTimeMillis(),
                packageUid);
        long rxBytes = 0L;
        long txBytes = 0L;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket);
            rxBytes += bucket.getRxBytes();
            txBytes += bucket.getTxBytes();
        }
        networkStats.close();
        if (type == NETWORK_RX){
            return  rxBytes;
        } else if (type == NETWORK_TOTAL){
            return rxBytes + txBytes;
        }
        return txBytes;
    }

    public long getBytes(NetworkType networkType, NetworkPeriod networkPeriod, int type) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(networkType.type,
                    getSubscriberId(networkType.type),
                    TrafficTool.getStartTimeFromPeriod(networkPeriod),
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        if (type == NETWORK_RX){
            return bucket.getRxBytes();
        } else if (type == NETWORK_TOTAL){
            return bucket.getRxBytes() + bucket.getTxBytes();
        }
        return bucket.getTxBytes();
    }


    public long getBytesRange(int uid,NetworkType networkType,long from,long to) {
        NetworkStats networkStats = null;
        networkStats = networkStatsManager.queryDetailsForUid(
                networkType.type,
                getSubscriberId(networkType.type),
                from,
                to,
                uid);
        long rxBytes = 0L;
        long txBytes = 0L;
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (networkStats.hasNextBucket()) {
            networkStats.getNextBucket(bucket);
            rxBytes += bucket.getRxBytes();
            txBytes += bucket.getTxBytes();
        }
        networkStats.close();
        return rxBytes + txBytes;
    }
//    public long getStartTimeFromPeriod(NetworkPeriod networkPeriod){
//        if (networkPeriod == NetworkPeriod.TOTAL)
//            return 0;
//        final long oneDay = 24 * 60 * 60 * 1000;
//        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
//        Calendar timeCalendar = Calendar.getInstance(curTimeZone);
//        timeCalendar.setTimeInMillis(System.currentTimeMillis());
//        timeCalendar.set(Calendar.HOUR_OF_DAY, 0);
//        timeCalendar.set(Calendar.MINUTE, 0);
//        timeCalendar.set(Calendar.SECOND, 0);
//        timeCalendar.set(Calendar.MILLISECOND, 0);
//        switch (networkPeriod){
//            case THREEDAY:
//                timeCalendar.setTimeInMillis(timeCalendar.getTimeInMillis() - oneDay * 3);
//                break;
//            case WEEK:
//                timeCalendar.set(Calendar.DAY_OF_WEEK,0);
//                break;
//            case MONTH:
//                timeCalendar.set(Calendar.DAY_OF_MONTH,0);
//                break;
//            case YEAR:
//                timeCalendar.set(Calendar.DAY_OF_YEAR,0);
//                break;
//            case TODAY:
//                break;
//        }
//        return timeCalendar.getTimeInMillis();
//    }

    public long getAllTxBytesMobile() {
        return getBytes(NetworkType.mobile,NetworkPeriod.TOTAL, NETWORK_TX);
    }

    public long getAllRxBytesWifi() {
        return getBytes(NetworkType.wifi,NetworkPeriod.TOTAL,NETWORK_RX);

    }

    public long getAllRxBytesMobile() {
        return getBytes(NetworkType.mobile,NetworkPeriod.TOTAL, NETWORK_RX);
    }

    public long getAllTxBytesWifi() {
        return getBytes(NetworkType.wifi,NetworkPeriod.TOTAL,NETWORK_TX);
    }

    public long getPackageRxBytesMobile() {
        return getBytes(packageUid,NetworkType.mobile,NetworkPeriod.TOTAL,NETWORK_RX);
    }

    public long getPackageTxBytesMobile() {        return getBytes(packageUid,NetworkType.mobile,NetworkPeriod.TOTAL,NETWORK_TX);
    }

    public long getPackageRxBytesWifi() {
        return getBytes(packageUid,NetworkType.wifi,NetworkPeriod.TOTAL,NETWORK_RX);

    }

    public long getPackageTxBytesWifi() {
        return getBytes(packageUid,NetworkType.wifi,NetworkPeriod.TOTAL,NETWORK_TX);

    }

    private String getSubscriberId(int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                PermissionTool.hasPermissionToReadNetworkHistory(mContext);
                return tm.getSubscriberId();
            }
            return tm.getSubscriberId();
        }
        return "";
    }

    public static int getPackageUid(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        int uid = -1;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            uid = packageInfo.applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return uid;
    }

    public static boolean isPackage(Context context, CharSequence s) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(s.toString(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }
}
