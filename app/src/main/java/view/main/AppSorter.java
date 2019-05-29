package view.main;

import android.text.TextUtils;

import com.hfj.trafficdog.R;
import com.hfj.trafficdog.core.Appinfo;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sharedstorage.PersistentsMgr;

/**
 * 对应用列表进行排序
 *
 * @author wenjiahui
 */

public class AppSorter {
        /**
         * 排序选项
         */
        public enum SortOptions {
            TITLE(R.string.sort_option_title, sTitleComparator),
            SYSTEMAPP(R.string.sort_option_system_app,sSystemAppComparator),
            NETFLOW(R.string.sort_option_net_flow,sFlowAppComparator);

            public final int msgRes;
            public final Comparator<Appinfo> comparator;

            SortOptions(int msgRes, Comparator<Appinfo> comparator) {
                this.msgRes = msgRes;
                this.comparator = comparator;
            }
        }

        public static final String mSortKey = "app_sort_local";
        private int mSortType = 0;


    public AppSorter() {
        refreshSortType();
    }

    public void sortApps(List<Appinfo> appinfos) {
            if (appinfos == null || appinfos.size() == 0) {
                return;
            }
            for (Appinfo appinfoBean : appinfos) {
                String title = appinfoBean.getName();
                String packageName = appinfoBean.getPackageName();
                if (TextUtils.isEmpty(title)
                        && TextUtils.isEmpty(packageName)
                        ) {
                    appinfoBean.setName("[没有名字]");
                }
            }
            Comparator<Appinfo> comparator = getComparator(mSortType);
            Collections.sort(appinfos, comparator);
        }

        public void refreshSortType() {
            String sortValue = PersistentsMgr.get().getString(mSortKey, "");
            if (TextUtils.isEmpty(sortValue)) {
                return;
            }
            int sortType = Integer.parseInt(sortValue);
            if (sortType != mSortType) {
                mSortType = sortType;
            }
        }

        public boolean isSortTypeChange(int which) {
            boolean same = mSortType == which;
            if (!same) {
                mSortType = which;
                PersistentsMgr.get().putString(mSortKey, String.valueOf(mSortType));
            }
            return same;
        }

        public int getSortType() {
            return mSortType;
        }

        public List<SortOptions> getSortOptions() {
            return Arrays.asList( SortOptions.TITLE,SortOptions.SYSTEMAPP,SortOptions.NETFLOW);
        }

        private Comparator<Appinfo> getComparator(int sortType) {
            SortOptions[] options = SortOptions.values();
            for (SortOptions option : options) {
                if (option.ordinal() == sortType) {
                    return option.comparator;
                }
            }
            return sFlowAppComparator;
        }
        private static final Comparator<Object> CHINA_COMPARE = Collator.getInstance(java.util.Locale.CHINA);

        private static Comparator<Appinfo> sTitleComparator = new Comparator<Appinfo>() {
            @Override
            public int compare(Appinfo lhs, Appinfo rhs) {
                String titleL = lhs.getName();
                String titleR = rhs.getName();

                if (titleL == null) {
                    titleL = "";
                }
                if (titleR == null) {
                    titleR = "";
                }
                return CHINA_COMPARE.compare(titleL,titleR);
            }
        };

        private static Comparator<Appinfo> sSystemAppComparator = new Comparator<Appinfo>() {
            @Override
            public int compare(Appinfo lApp, Appinfo rApp) {
                if (lApp.isSystemApp() && !rApp.isSystemApp()){
                    return 1;
                }

                if (!lApp.isSystemApp() && rApp.isSystemApp()){
                    return -1;
                }
                return 0;
            }
        };

        private static Comparator<Appinfo> sFlowAppComparator = new Comparator<Appinfo>() {
            @Override
            public int compare(Appinfo lApp, Appinfo rApp) {
                long lFlow = lApp.getRxMobileByte() + lApp.getTxMobileByte();
                long rFlow = rApp.getRxMobileByte() + rApp.getTxMobileByte();
                long res = lFlow - rFlow;
                if (res > 0){
                    return -1;
                } else if (res < 0){
                    return 1;
                }
                return 0;
            }
        };

}


