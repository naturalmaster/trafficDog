package utilities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import view.stats.StatsActivity;

public class PermissionTool {
    private static final int READ_PHONE_STATE_REQUEST = 37;



    public static void requestPermissions(Context context) {
        if (!hasPermissionToReadNetworkHistory(context)) {
            return;
        }
        if (!hasPermissionToReadPhoneStats(context)) {
            requestPhoneStateStats(context);
        }
    }


    public static boolean hasPermissions(Context context) {
        return hasPermissionToReadNetworkHistory(context) && hasPermissionToReadPhoneStats(context);
    }

    public static boolean hasPermissionToReadNetworkHistory(final Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        final AppOpsManager opsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = opsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,android.os.Process.myUid(),context.getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED){
            return true;
        }
        opsManager.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS, context.getPackageName(), new AppOpsManager.OnOpChangedListener() {
            @Override
            public void onOpChanged(String op, String packageName) {
                int mode = opsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,android.os.Process.myUid(),context.getPackageName());
                if (mode != AppOpsManager.MODE_ALLOWED){
                    return;
                }
                opsManager.stopWatchingMode(this);
                Intent intent = new Intent(context, StatsActivity.class);
                Bundle bundle = ((Activity)context).getIntent().getExtras();
                if (bundle != null){
                    intent.putExtras(bundle);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);

            }
        });
        requestReadNetworkHistoryAccess(context);
        return false;
    }



    public static void requestPhoneStateStats(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST);
    }





    public static boolean hasPermissionToReadPhoneStats(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
            return false;
        } else {
            return true;
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void  requestReadNetworkHistoryAccess(Context context) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        context.startActivity(intent);
    }
}
