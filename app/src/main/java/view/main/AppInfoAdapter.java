package view.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hfj.trafficdog.R;
import com.hfj.trafficdog.core.Appinfo;


import java.util.List;

import view.main.listener.OnPackageClickListener;

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.AppInfoViewHolder>{
    List<Appinfo> appinfoList;
    OnPackageClickListener mListener;


    public AppInfoAdapter( List<Appinfo> appinfoList,OnPackageClickListener mListener) {
        this.appinfoList = appinfoList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public AppInfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_view_list_item,viewGroup,false);
        return new AppInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppInfoViewHolder appInfoViewHolder, int i) {
        Appinfo appinfo = appinfoList.get(i);
        appInfoViewHolder.titleView.setText(appinfo.getName());
        appInfoViewHolder.packageName.setText(appinfo.getPackageName());
        appInfoViewHolder.versionText.setText(appinfo.getVersion() + appinfo.isSystemApp());
        try {
            appInfoViewHolder.imageView.setImageDrawable(appInfoViewHolder.mContext.getPackageManager().getApplicationIcon(appinfo.getPackageName()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return appinfoList.size();
    }



    class AppInfoViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView packageName;
        TextView versionText;
        Context mContext;
        AppCompatImageView imageView;

        public AppInfoViewHolder(View view) {
            super(view);
            this.titleView = view.findViewById(R.id.main_view_title);
            this.packageName = view.findViewById(R.id.main_view_subtitle);
            this.versionText = view.findViewById(R.id.main_view_version);
            this.imageView = view.findViewById(R.id.main_view_list_icon);
            this.mContext = view.getContext();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(appinfoList.get(getAdapterPosition()));
                }
            });
        }
    }
}
