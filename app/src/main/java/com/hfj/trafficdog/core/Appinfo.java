package com.hfj.trafficdog.core;

import android.os.Parcel;
import android.os.Parcelable;

public class Appinfo implements Parcelable {
    private String name;

    private String version;

    private String packageName;

    private int uid;
    /**
     * 是否为系统APP
     */
    private boolean isSystemApp;

    /**
     * 是否可以联网
     */
    private boolean isNetworkApp;


    private long rxMobileByte;

    private long txMobileByte;

    protected Appinfo(Parcel in) {
        name = in.readString();
        version = in.readString();
        packageName = in.readString();
        uid = in.readInt();
        isSystemApp = in.readByte() != 0;
        isNetworkApp = in.readByte() != 0;
        rxMobileByte = in.readLong();
        txMobileByte = in.readLong();
    }

    public Appinfo() {
    }

    public static final Creator<Appinfo> CREATOR = new Creator<Appinfo>() {
        @Override
        public Appinfo createFromParcel(Parcel in) {
            return new Appinfo(in);
        }

        @Override
        public Appinfo[] newArray(int size) {
            return new Appinfo[size];
        }
    };

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }



    public boolean isNetworkApp() {
        return isNetworkApp;
    }

    public void setNetworkApp(boolean networkApp) {
        isNetworkApp = networkApp;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getRxMobileByte() {
        return rxMobileByte;
    }

    public void setRxMobileByte(long rxMobileByte) {
        this.rxMobileByte = rxMobileByte;
    }

    public long getTxMobileByte() {
        return txMobileByte;
    }

    public void setTxMobileByte(long txMobileByte) {
        this.txMobileByte = txMobileByte;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(version);
        dest.writeString(packageName);
        dest.writeInt(uid);
        dest.writeByte((byte) (isSystemApp ? 1 : 0));
        dest.writeByte((byte) (isNetworkApp ? 1 : 0));
        dest.writeLong(rxMobileByte);
        dest.writeLong(txMobileByte);
    }
}
