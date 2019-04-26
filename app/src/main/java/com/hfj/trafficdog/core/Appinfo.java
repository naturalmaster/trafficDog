package com.hfj.trafficdog.core;

public class Appinfo {
    private String name;
    private String version;
    private String packageName;
    private boolean isSystemApp;
    private boolean isNetworkApp;

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
}
