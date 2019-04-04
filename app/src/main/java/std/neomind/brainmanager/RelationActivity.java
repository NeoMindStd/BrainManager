package std.neomind.brainmanager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.GridView;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import std.neomind.brainmanager.data.Keyword;

public class RelationActivity extends AppCompatActivity {

    private TextView keywordText;
    private ArrayList<Keyword> mKeywords;
    private GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ///플로팅 액션 버튼
        FloatingActionButton fab = findViewById(R.id.fab_relation);
        fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show());
        ////실구현
//        BrainDBHandler brainDBHandler = new BrainDBHandler(mContext);
//        try {
//            mKeywords = brainDBHandler.getAllKeywords();
//            brainDBHandler.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        ////////////테스트
        //int id, int cid, String text, String imagePath, int currentLevels, int reviewTimes, String registrationDate
        keywordText = findViewById(R.id.keyword_relation_view);
        keywordText.setText("Keyword");
        Keyword.Builder b = new Keyword.Builder();
        b.setText("와!");
        Keyword tempKey = b.build();
        b.setText("으윽!");
        Keyword tempKey2 = b.build();
        mKeywords = new ArrayList<Keyword>();
        mKeywords.add(tempKey);
        mKeywords.add(tempKey2);
        ///////////////

        b.setText("");
        //Keyword temp = b.build();
        int key_size = mKeywords.size();
        for(int i = 12 - key_size; i > 0; i--) {
            mKeywords.add(b.build());
        }

        gridView = (GridView)findViewById(R.id.grid_relation_view);
        RelationGridAdapter relationGridAdapter = new RelationGridAdapter(this, mKeywords);
        //키워드가 부족할 경우 회색 박스들 생성
        gridView.setAdapter(relationGridAdapter);
    }
}
