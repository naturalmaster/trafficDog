package base.dialog;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hfj.trafficdog.R;

public class BottomSheetDialog extends BaseDialog
{
    /**
     * 默认的动画 时间
     */
    private final static int ANIMATION_DURATION = 256;

    private View.OnClickListener mOkClickListener;
    private View.OnClickListener mCancelClickListener;

    protected CardView mOkLayout;
    protected TextView mOkText;
    protected CardView mCancelLayout;
    protected TextView mCancelText;

    private View mRootView;

    private boolean mHasShowAnimationDone = false;

    public BottomSheetDialog(Context context)
    {
        super(context);

        Window window = this.getWindow();

        //去掉dialog自己的title
        window.requestFeature(Window.FEATURE_NO_TITLE);

        //去掉dialog默认的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.getDecorView().setBackgroundColor(0x00000000);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //位置设置到底部
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);

        mRootView = createContentView();
        setContentView(mRootView);

        setCanceledOnTouchOutside(true);
    }

    public void setWarnStyle() {
        mOkText.setTextColor(Color.RED);
        mOkLayout.setCardBackgroundColor(Color.WHITE);
        TextView dt = (TextView)findViewById(R.id.dialog_description_text);
        dt.setBackgroundColor(getContext().getResources().getColor(R.color.dialog_item_description_background_color));
        dt.setTextColor(getContext().getResources().getColor(R.color.dialog_item_description_text_color));
        View divide = findViewById(R.id.dialog_description_divide);
        divide.setBackgroundColor(getContext().getResources().getColor(R.color.dialog_item_description_divide_color));
    }

    public void show() {
        super.show();
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            if (mHasShowAnimationDone)
                return;

            mHasShowAnimationDone = true;
            mRootView.animate().setDuration(ANIMATION_DURATION);
            mRootView.animate().setListener(null);
            mRootView.setTranslationY(mRootView.getMeasuredHeight());
            mRootView.animate().translationY(0);
        }
    }

    protected View createContentView()
    {
        View root = LayoutInflater.from(getContext()).inflate(getLayoutId(), null);

        mOkLayout = (CardView) root.findViewById(R.id.ok_layout);
        mOkText =  (TextView) root.findViewById(R.id.dialog_ok_text);
        mOkText.setOnClickListener(mOnclickListener);

        mCancelLayout = (CardView) root.findViewById(R.id.cancel_layout);
        mCancelText = (TextView) root.findViewById(R.id.dialog_cancel_text);
        mCancelText.setOnClickListener(mOnclickListener);

        //取消的默认实现
        mCancelClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        };

        return root;
    }

    protected int getLayoutId() {
        return R.layout.public_dialog_ok_cancel_layout;
    }

    public void setOkListener(View.OnClickListener l)
    {
        this.mOkClickListener = l;
    }

    public void setCancelListener(View.OnClickListener l)
    {
        this.mCancelClickListener = l;
    }

    public void setOkText(String text)
    {
        mOkText.setText(text);
    }

    public void setCancelText(String text)
    {
        mCancelText.setText(text);
    }

    public void setDescriptionText(String text)
    {
        TextView tv = (TextView)findViewById(R.id.dialog_description_text);
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);

        View divide = findViewById(R.id.dialog_description_divide);
        divide.setVisibility(View.VISIBLE);
    }

    public boolean isDescriptionTextVisible()
    {
        return findViewById(R.id.dialog_description_text).getVisibility() == View.VISIBLE;
    }

    @Override
    public void dismiss() {
        mRootView.animate().setDuration(ANIMATION_DURATION);
        mRootView.animate().translationY(mRootView.getHeight());
        mRootView.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                BottomSheetDialog.super.dismiss();
                mHasShowAnimationDone = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                BottomSheetDialog.super.dismiss();
                mHasShowAnimationDone = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mOkText) {
                if (mOkClickListener != null) {
                    mOkClickListener.onClick(v);
                }
            } else if (v == mCancelText) {
                if (mCancelClickListener != null)
                    mCancelClickListener.onClick(v);
            }
        }
    };
}
