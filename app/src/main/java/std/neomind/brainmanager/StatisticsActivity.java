package std.neomind.brainmanager;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {
    private static final String TAG="StatisticsActivity";

    private LineChart mChart;
    private LineChart mChart2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        mChart = (LineChart) findViewById(R.id.chart);

       /* mChart.setOnChartGestureListener(StatisticsActivity.this);
        mChart.setOnChartValueSelectedListener(StatisticsActivity.this);*/

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        LimitLine upper_limit = new LimitLine(20f, "goal");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(15f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upper_limit);
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
        LineDataSet set1 = new LineDataSet(yValues, "정답률");

        set1.setFillAlpha(110);

        set1.setColor(Color.RED);
        set1.setLineWidth(3f);
        set1.setValueTextSize(10f);
        set1.setValueTextColor(Color.GREEN);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        mChart.setData(data);


        mChart2 = (LineChart) findViewById(R.id.chart2);

        mChart2.setDragEnabled(true);
        mChart2.setScaleEnabled(false);

        LimitLine up_limit = new LimitLine(30f, "goal");
        up_limit .setLineWidth(4f);
        up_limit .enableDashedLine(10f, 10f, 0f);
        up_limit .setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        up_limit .setTextSize(15f);



        YAxis leftAxis2 = mChart2.getAxisLeft();
        leftAxis2.removeAllLimitLines();
        leftAxis2.addLimitLine(up_limit);
        leftAxis2.setAxisMaximum(100f);
        leftAxis2.enableAxisLineDashedLine(10f, 10f, 0);
        leftAxis2.setDrawLimitLinesBehindData(true);

        ArrayList<Entry> yValue = new ArrayList<>();

        yValue.add(new Entry(0, 60f));
        yValue.add(new Entry(1, 50f));
        yValue.add(new Entry(2, 10f));
        yValue.add(new Entry(3, 5f));
        yValue.add(new Entry(4, 30f));
        LineDataSet set2 = new LineDataSet(yValue, "문재풀이속도");

        set2.setFillAlpha(110);

        set2.setColor(Color.RED);
        set2.setLineWidth(3f);
        set2.setValueTextSize(10f);
        set2.setValueTextColor(Color.GREEN);
        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
        dataSets2.add(set2);

        LineData data2 = new LineData(dataSets2);

        mChart2.setData(data2);

    }




}

