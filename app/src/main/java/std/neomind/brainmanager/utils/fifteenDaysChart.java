package std.neomind.brainmanager.utils;

import androidx.appcompat.app.AppCompatActivity;
import std.neomind.brainmanager.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class fifteenDaysChart extends AppCompatActivity {

    private LineChart mChart;
    private LineChart mChart2;
    Spinner goalSpinner;
    Spinner goalSpinner2;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_fifteen_days_chart);

        mChart = (LineChart) findViewById(R.id.fifteenDaysChart1);
        mChart2 = (LineChart) findViewById(R.id.fifteenDaysChart2);

        Intent intent = getIntent();

      /*mChart.setOnChartGestureListener(StatisticsActivity.this);
        mChart.setOnChartValueSelectedListener(StatisticsActivity.this);*/

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart2.setDragEnabled(true);
        mChart2.setScaleEnabled(false);

        arrayList= new ArrayList<>();
        arrayList.add("30");
        arrayList.add("40");
        arrayList.add("50");
        arrayList.add("60");
        arrayList.add("70");
        arrayList.add("80");
        arrayList.add("90");

        arrayAdapter= new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayList);

        goalSpinner = (Spinner)findViewById(R.id.spinnerSelectGoal);
        goalSpinner.setAdapter(arrayAdapter);
        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(arrayList.get(i)=="30") {
                    mChart.notifyDataSetChanged();
                    mChart.invalidate();
                    LimitLine upper_limit = new LimitLine(30f, "goal");
                    upper_limit.setLineWidth(4f);
                    upper_limit.enableDashedLine(10f, 10f, 0f);
                    upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    upper_limit.setTextSize(15f);

                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(upper_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
                else if(arrayList.get(i)=="40"){
                    mChart.notifyDataSetChanged();
                    mChart.invalidate();
                    LimitLine upper_limit = new LimitLine(40f, "goal");
                    upper_limit.setLineWidth(4f);
                    upper_limit.enableDashedLine(10f, 10f, 0f);
                    upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    upper_limit.setTextSize(15f);

                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(upper_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }

                else if(arrayList.get(i)=="50"){
                    mChart.notifyDataSetChanged();
                    mChart.invalidate();
                    LimitLine upper_limit = new LimitLine(50f, "goal");
                    upper_limit.setLineWidth(4f);
                    upper_limit.enableDashedLine(10f, 10f, 0f);
                    upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    upper_limit.setTextSize(15f);

                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(upper_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
                else if(arrayList.get(i)=="60"){
                    mChart.notifyDataSetChanged();
                    mChart.invalidate();
                    LimitLine upper_limit = new LimitLine(60f, "goal");
                    upper_limit.setLineWidth(4f);
                    upper_limit.enableDashedLine(10f, 10f, 0f);
                    upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    upper_limit.setTextSize(15f);

                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(upper_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
                else if(arrayList.get(i)=="70"){
                    mChart.notifyDataSetChanged();
                    mChart.invalidate();
                    LimitLine upper_limit = new LimitLine(70f, "goal");
                    upper_limit.setLineWidth(4f);
                    upper_limit.enableDashedLine(10f, 10f, 0f);
                    upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    upper_limit.setTextSize(15f);

                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(upper_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
                else if(arrayList.get(i)=="80"){
                    mChart.notifyDataSetChanged();
                    mChart.invalidate();
                    LimitLine upper_limit = new LimitLine(80f, "goal");
                    upper_limit.setLineWidth(4f);
                    upper_limit.enableDashedLine(10f, 10f, 0f);
                    upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    upper_limit.setTextSize(15f);

                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(upper_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
                else if(arrayList.get(i)=="90"){
                    mChart.notifyDataSetChanged();
                    mChart.invalidate();
                    LimitLine upper_limit = new LimitLine(90f, "goal");
                    upper_limit.setLineWidth(4f);
                    upper_limit.enableDashedLine(10f, 10f, 0f);
                    upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    upper_limit.setTextSize(15f);

                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(upper_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        arrayAdapter2= new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayList);

        goalSpinner2 = (Spinner)findViewById(R.id.spinnerSelecttime);
        goalSpinner2.setAdapter(arrayAdapter);
        goalSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(arrayList.get(i)=="30") {
                    mChart2.notifyDataSetChanged();
                    mChart2.invalidate();
                    LimitLine up_limit = new LimitLine(30f, "goal");
                    up_limit.setLineWidth(4f);
                    up_limit.enableDashedLine(10f, 10f, 0f);
                    up_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    up_limit.setTextSize(15f);

                    YAxis leftAxis = mChart2.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(up_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
                else if(arrayList.get(i)=="40"){
                    mChart2.notifyDataSetChanged();
                    mChart2.invalidate();
                    LimitLine up_limit = new LimitLine(40f, "goal");
                    up_limit.setLineWidth(4f);
                    up_limit.enableDashedLine(10f, 10f, 0f);
                    up_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    up_limit.setTextSize(15f);

                    YAxis leftAxis = mChart2.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(up_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }

                else if(arrayList.get(i)=="50"){
                    mChart2.notifyDataSetChanged();
                    mChart2.invalidate();
                    LimitLine up_limit = new LimitLine(50f, "goal");
                    up_limit.setLineWidth(4f);
                    up_limit.enableDashedLine(10f, 10f, 0f);
                    up_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    up_limit.setTextSize(15f);

                    YAxis leftAxis = mChart2.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(up_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
                else if(arrayList.get(i)=="60"){
                    mChart2.notifyDataSetChanged();
                    mChart2.invalidate();
                    LimitLine up_limit = new LimitLine(60f, "goal");
                    up_limit.setLineWidth(4f);
                    up_limit.enableDashedLine(10f, 10f, 0f);
                    up_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    up_limit.setTextSize(15f);

                    YAxis leftAxis = mChart2.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(up_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
                else if(arrayList.get(i)=="70"){
                    mChart2.notifyDataSetChanged();
                    mChart2.invalidate();
                    LimitLine up_limit = new LimitLine(70f, "goal");
                    up_limit.setLineWidth(4f);
                    up_limit.enableDashedLine(10f, 10f, 0f);
                    up_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    up_limit.setTextSize(15f);

                    YAxis leftAxis = mChart2.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(up_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
                else if(arrayList.get(i)=="80"){
                    mChart2.notifyDataSetChanged();
                    mChart2.invalidate();
                    LimitLine up_limit = new LimitLine(80f, "goal");
                    up_limit.setLineWidth(4f);
                    up_limit.enableDashedLine(10f, 10f, 0f);
                    up_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    up_limit.setTextSize(15f);

                    YAxis leftAxis = mChart2.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(up_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
                else if(arrayList.get(i)=="90"){
                    mChart2.notifyDataSetChanged();
                    mChart2.invalidate();
                    LimitLine up_limit = new LimitLine(90f, "goal");
                    up_limit.setLineWidth(4f);
                    up_limit.enableDashedLine(10f, 10f, 0f);
                    up_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    up_limit.setTextSize(15f);

                    YAxis leftAxis = mChart2.getAxisLeft();
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(up_limit);
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.enableAxisLineDashedLine(10f, 10f, 0);
                    leftAxis.setDrawLimitLinesBehindData(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(0, 40f));
        yValues.add(new Entry(1, 50f));
        yValues.add(new Entry(5, 10f));
        yValues.add(new Entry(10, 5f));
        yValues.add(new Entry(12, 25f));
        yValues.add(new Entry(15, 25f));


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
