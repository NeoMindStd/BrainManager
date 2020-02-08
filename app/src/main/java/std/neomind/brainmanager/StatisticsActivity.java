package std.neomind.brainmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {
    private Spinner spinner;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        arrayList = new ArrayList<>();
        arrayList.add(getString(R.string.StatisticsActivity_chooseOne));
        arrayList.add(getString(R.string.StatisticsActivity_recent30));
        arrayList.add(getString(R.string.StatisticsActivity_recent15));

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayList);

        spinner = findViewById(R.id.spinnerSelectPeriod);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (arrayList.get(i) == getString(R.string.StatisticsActivity_recent30)) {
                    Intent intent = new Intent(getApplicationContext(), OneMonthDaysChartActivity.class);
                    startActivity(intent);
                } else if (arrayList.get(i) == getString(R.string.StatisticsActivity_recent15)) {
                    Intent intent = new Intent(getApplicationContext(), FifteenDaysChartActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}