package sharedstorage;

import utilities.IPersistent;

public class PersistentsMgr {
    public enum PersistentsType {
        SP, // SharedPreferences
    }

    /*
     * 默认SharedPreferences(文件)持久化存储器
     */
    public static IPersistent get() {
        return get(PersistentsType.SP);
    }

    public static IPersistent get(PersistentsType type) {
        IPersistent persistent = null;
        switch (type) {
            case SP:
                persistent = SPPersistent.getInstance();
                break;

        }
        return persistent;
    }
}
