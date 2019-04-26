package utilities;

import android.content.Context;
import android.os.ResultReceiver;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class ViewTools {
    private static DisplayMetrics metrics = new DisplayMetrics();
    public static boolean hideSoftKeyBoard(View targetView, ResultReceiver resultReceiver)
    {
        if (null != targetView)
        {
            InputMethodManager imm = (InputMethodManager) targetView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (null != imm)
            {
                boolean result = imm.hideSoftInputFromWindow(targetView.getWindowToken(), 0, resultReceiver);
                return result;
            }
        }
        return false;
    }
    public static void hideSoftKeyBoard(View targetView)
    {
        hideSoftKeyBoard(targetView, null);
    }


    public static int getDisplayWidth(Context context)
    {
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}
