package std.neomind.brainmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mChart = findViewById(R.id.settings_chart);

       /* mChart.setOnChartGestureListener(StatisticsActivity.this);
        mChart.setOnChartValueSelectedListener(StatisticsActivity.this);*/

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setAxisMaximum(60f);
        leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
        leftAxis.setDrawLimitLinesBehindData(true);

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(0, 60f));
        yValues.add(new Entry(1, 50f));
        yValues.add(new Entry(2, 10f));
        yValues.add(new Entry(3, 5f));
        yValues.add(new Entry(4, 25f));
        yValues.add(new Entry(5, 30f));
        LineDataSet set1 = new LineDataSet(yValues, getString(R.string.Statistics_forgettingCurve));

        set1.setFillAlpha(110);

        set1.setColor(Color.RED);
        set1.setLineWidth(3f);
        set1.setValueTextSize(10f);
        set1.setValueTextColor(Color.GREEN);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData data = new LineData(dataSets);

        mChart.setData(data);

        findViewById(R.id.settings_textView_privacyPolicy).setOnClickListener(view -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            String str = getResources().getString(R.string.SettingsActivity_privacyPolicy);
            str = str.substring(0, str.indexOf('\n'));
            intent.putExtra("TITLE", str);
            intent.putExtra("URL", getResources().getString(R.string.SettingsActivity_privacyPolicyURL));
            startActivity(intent);
        });
        findViewById(R.id.settings_textView_license).setOnClickListener(view -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            String str = getResources().getString(R.string.SettingsActivity_openSourceLibraries);
            str = str.substring(0, str.indexOf('\n'));
            intent.putExtra("TITLE", str);
            intent.putExtra("URL", getResources().getString(R.string.SettingsActivity_openSourceLibrariesURL));
            startActivity(intent);
        });
    }
}
