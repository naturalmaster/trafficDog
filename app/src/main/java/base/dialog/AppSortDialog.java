package base.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hfj.trafficdog.R;

import utilities.ViewTools;

public class AppSortDialog extends BottomSheetDialog implements View.OnClickListener {

    private CharSequence[] mItems;
    private int mCheckedItem;
    private OnClickListener mOnClickListener;

    private View mRootView;
    private ViewGroup mSortGroupView;

    public AppSortDialog(Context context) {
        super(context);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        int margin = context.getResources().getDimensionPixelOffset(R.dimen.dialog_item_margin);
        attributes.width = ViewTools.getDisplayWidth(context) - 2 * margin;
        window.setAttributes(attributes);
    }

    @Override
    protected View createContentView() {
        if (mRootView == null) {
            mRootView = LayoutInflater.from(getContext()).inflate(R.layout.note_sort_dailog, null);
            mSortGroupView = (ViewGroup) mRootView.findViewById(R.id.note_sort_options_content);
        }
        return mRootView;
    }

    public AppSortDialog setSingleChoiceItems(CharSequence[] items, int checkedItem, final OnClickListener listener) {
        mItems = items;
        mCheckedItem = checkedItem;
        mOnClickListener = listener;

        return this;
    }

    @Override
    public void show() {
        renderView();
        super.show();
    }

    private void renderView() {
        mSortGroupView.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < mItems.length; i++) {
            CharSequence option = mItems[i];
            boolean checked = i == mCheckedItem;

            View itemView = inflater.inflate(R.layout.note_sort_dailog_item, mSortGroupView, false);
            View icon = itemView.findViewById(R.id.ico_note_sort_order);
            if (checked) {
                icon.setVisibility(View.VISIBLE);
            }
            TextView tvSortName = (TextView) itemView.findViewById(R.id.tv_note_sort_item);
            tvSortName.setSelected(checked);
            tvSortName.setText(option);
            //if (i == mItems.length - 1) {
            //    itemView.findViewById(R.id.divider).setVisibility(View.GONE);
            //}
            itemView.setTag(i);
            itemView.setOnClickListener(this);

            mSortGroupView.addView(itemView);
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof Integer) {
            int choice = (int) tag;
            if (mOnClickListener != null) {
                mOnClickListener.onClick(this, choice);
            }
        }
    }
}