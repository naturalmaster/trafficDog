package base.dialog;

import android.app.Dialog;
import android.content.Context;

import java.util.ArrayList;
import java.util.Vector;

import utilities.ViewTools;

/**
 * Created by lhz on 2016/11/11.
 */
public class BaseDialog extends Dialog
{
    /**
     * 正在显示的dialog，可通过canCollectDialogForPadPhone标志位不加入处理
     */
    private static Vector<BaseDialog> mShowingDialogs;

    private Context mContext;
    public BaseDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public static void dismissAllShowingDialog()
    {
        // dismiss any dialogs we are managing.
        if (mShowingDialogs != null)
        {
            ArrayList<BaseDialog> showingDialog = new ArrayList<BaseDialog>(mShowingDialogs);
            for (BaseDialog dialog : showingDialog)
            {
                if (dialog != null && dialog.isShowing())
                {
                    if(dialog.getCurrentFocus() != null){
                        ViewTools.hideSoftKeyBoard(dialog.getCurrentFocus());
                    }
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        // avoid:
                        // 1.activity has been finished
                        // 2.activity has been destroyed
                    }
                }
            }
            mShowingDialogs.clear();
        }
    }

    private static void addShowingDialog(BaseDialog dialog)
    {
        if (mShowingDialogs == null)
        {
            mShowingDialogs = new Vector<BaseDialog>();
        }
        if (!mShowingDialogs.contains(dialog))
        {
            mShowingDialogs.add(dialog);
        }
    }

    private static void removeShowingDialog(BaseDialog dialog)
    {
        if (mShowingDialogs.contains(dialog))
            mShowingDialogs.remove(dialog);
    }

    @Override
    public void show() {
        try {
            super.show();
            addShowingDialog(this);
        } catch (Throwable e){};
    }


    @Override
    public void cancel() {
        try {
            removeShowingDialog(this);
            super.cancel();
        }catch (Throwable e){}
    }

    @Override
    public void dismiss() {
        try {
            removeShowingDialog(this);
            super.dismiss();
        } catch (Throwable e){};
    }

    /**
     * 设置允许的最大高度
     */
    public void setMaxHeight(int value) {
        this.mMaxHeight = value;
    }

    /**
     * 允许的最大高度
     */
    private int mMaxHeight;



}
