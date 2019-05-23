package std.neomind.brainmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import std.neomind.brainmanager.utils.fifteenDaysChart;
import std.neomind.brainmanager.utils.oneMonthDaysChart;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "StatisticsActivity";

    Spinner spinner;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        arrayList= new ArrayList<>();
        arrayList.add("최근한달");
        arrayList.add("최근15일");

        arrayAdapter= new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayList);

        spinner = (Spinner)findViewById(R.id.spinnerSelectPeriod);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(arrayList.get(i)=="최근한달") {
                    Intent intent = new Intent(getApplicationContext(), oneMonthDaysChart.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), fifteenDaysChart.class);
                    startActivity(intent);
                }
                }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

}