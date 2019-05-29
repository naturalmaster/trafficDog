package view.stats;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class AppNameXAxisDataFormatter extends ValueFormatter {
    private final String[] mTimes = new String[]{
            "0点", "2点", "4点", "6点", "8点", "10", "12", "14", "16", "18", "20", "22", "24"
    };
    @Override
    public String getFormattedValue(float value) {

        return mTimes[(int) value];

    }
}
