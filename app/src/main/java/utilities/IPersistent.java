package utilities;

import java.util.ArrayList;
import java.util.HashMap;

import sharedstorage.IPersistentPublicKeys;

/**
 * 持久化存储接口
 */
public interface IPersistent {
    // ============public存储 =============

    boolean putBoolean(IPersistentPublicKeys key, boolean value);

    boolean putFloat(IPersistentPublicKeys key, float value);

    boolean putInt(IPersistentPublicKeys key, int value);

    boolean putLong(IPersistentPublicKeys key, long value);

    boolean putLong(String key, long value);

    boolean putString(IPersistentPublicKeys key, String value);

    boolean putString(String key, String value);

    boolean remove(IPersistentPublicKeys key);

    boolean remove(String key);

    boolean contains(IPersistentPublicKeys key);

    boolean getBoolean(IPersistentPublicKeys key, boolean defValue);

    float getFloat(IPersistentPublicKeys key, float defValue);

    int getInt(IPersistentPublicKeys key, int defValue);

    long getLong(IPersistentPublicKeys key, long defValue);

    long getLong(String key, long defValue);

    String getString(IPersistentPublicKeys key, String defValue);

    String getString(String key, String defValue);

    // ============public存储end =============


}
