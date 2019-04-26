package base;

import android.app.Application;

public class TrafficApp extends Application {
    private static TrafficApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static TrafficApp getInstance(){
        return mInstance;
    }
}
