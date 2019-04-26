package sharedstorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import base.TrafficApp;
import utilities.IPersistent;

/**
 * 持久化数据存储
 * 封装SharedPreferences进行文件存储
 * (请不要直接调用该单例，通过PersistentsMgr进行使用)
 */
class SPPersistent implements IPersistent {
    private static final String TAG = "SPPersistent";
    private static SPPersistent _instance;
    private int MODE_MULTI_PROCESS = 4;  //Context.MODE_MULTI_PROCESS
    private int preferencesModel = (Build.VERSION.SDK_INT > 10) ? MODE_MULTI_PROCESS: Context.MODE_PRIVATE;

    private static final String DEFAULT_SAVE_FILE = "public_default"; // public存储位置, 存储简单数据, 共享一个文件
    private SharedPreferences publicSp; // 支持多进程的 SharedPreferences

    public static SPPersistent getInstance() {
        if (_instance == null) {
            _instance = new SPPersistent();
        }
        return _instance;
    }

    private void getPublicSp() {

        publicSp = TrafficApp.getInstance().getSharedPreferences(DEFAULT_SAVE_FILE, preferencesModel);
    }

    /* 如果有需求再扩展数据监听
     * public interface OnPersistentChangeListener {
        void onPersistentChanged(String fileName, String key);
    }*/

    // ============public存储 =============
    @Override
    public boolean putBoolean(IPersistentPublicKeys key, boolean value) {
        getPublicSp();
        SharedPreferences.Editor editor = publicSp.edit();
        editor.putBoolean(key.getString(), value);
        boolean rst = editor.commit();
        return rst;
    }

    @Override
    public boolean putFloat(IPersistentPublicKeys key, float value) {
        getPublicSp();
        SharedPreferences.Editor editor = publicSp.edit();
        editor.putFloat(key.getString(), value);
        boolean rst = editor.commit();
        return rst;
    }

    @Override
    public boolean putInt(IPersistentPublicKeys key, int value) {
        getPublicSp();
        SharedPreferences.Editor editor = publicSp.edit();
        editor.putInt(key.getString(), value);
        boolean rst = editor.commit();
        return rst;
    }

    @Override
    public boolean putLong(IPersistentPublicKeys key, long value) {
        return putLong(key.getString(), value);
    }

    @Override
    public boolean putLong(String key, long value) {
        getPublicSp();
        SharedPreferences.Editor editor = publicSp.edit();
        editor.putLong(key, value);
        boolean rst = editor.commit();
        return rst;
    }

    @Override
    public boolean putString(IPersistentPublicKeys key, String value) {
        return putString(key.getString(), value);
    }

    @Override
    public boolean putString(String key, String value) {
        getPublicSp();
        SharedPreferences.Editor editor = publicSp.edit();
        editor.putString(key, value);
        boolean rst = editor.commit();
        return rst;
    }

    @Override
    public boolean remove(IPersistentPublicKeys key) {
        return remove(key.getString());
    }

    @Override
    public boolean remove(String key) {
        getPublicSp();
        SharedPreferences.Editor editor = publicSp.edit();
        editor.remove(key);
        boolean rst = editor.commit();
        return rst;
    }

    // =====get======
    @Override
    public boolean contains(IPersistentPublicKeys key) {
        getPublicSp();
        boolean rst = publicSp.contains(key.getString());
        return rst;
    }

    @Override
    public boolean getBoolean(IPersistentPublicKeys key, boolean defValue) {
        getPublicSp();
        boolean rst =defValue;
        try {
            rst = publicSp.getBoolean(key.getString(), defValue);
        } catch (ClassCastException ex) {  //几率很小，如被篡改类型
            remove(key);
            ex.printStackTrace();
        }
        return rst;
    }

    @Override
    public float getFloat(IPersistentPublicKeys key, float defValue) {
        getPublicSp();
        float rst = defValue;
        try {
            rst = publicSp.getFloat(key.getString(), defValue);
        } catch (ClassCastException ex) {
            remove(key);
            ex.printStackTrace();
        }
        return rst;
    }

    @Override
    public int getInt(IPersistentPublicKeys key, int defValue) {
        getPublicSp();
        int rst = defValue;
        try {
            rst = publicSp.getInt(key.getString(), defValue);
        } catch (ClassCastException ex) {
            remove(key);
            ex.printStackTrace();
        }
        return rst;
    }

    @Override
    public long getLong(IPersistentPublicKeys key, long defValue) {
        return getLong(key.getString(), defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        getPublicSp();
        long rst = defValue;
        try {
            rst = publicSp.getLong(key, defValue);
        } catch (ClassCastException ex) {
            remove(key);
            ex.printStackTrace();
        }
        return rst;
    }

    @Override
    public String getString(IPersistentPublicKeys key, String defValue) {
        return getString(key.getString(), defValue);
    }

    @Override
    public String getString(String key, String defValue) {
        getPublicSp();
        String rst = defValue;
        try {
            rst = publicSp.getString(key, defValue);
        } catch (ClassCastException ex) {
            remove(key);
            ex.printStackTrace();
        }
        return rst;
    }

    // ============public存储end =============

}
