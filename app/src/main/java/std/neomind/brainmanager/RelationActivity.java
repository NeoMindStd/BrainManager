package std.neomind.brainmanager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import std.neomind.brainmanager.data.BrainDBHandler;
import std.neomind.brainmanager.data.Keyword;
import std.neomind.brainmanager.data.RelationGridAdapter;

public class RelationActivity extends AppCompatActivity {

    public static final String INTENT_KEYWORD_ID = "intentKeywordID";

    private TextView keywordText;
    private ArrayList<Keyword> mKeywords;
    private RecyclerView gridView;
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
        BrainDBHandler brainDBHandler = new BrainDBHandler(this);
        try {
            mKeywords = brainDBHandler.getAllKeywords();
            brainDBHandler.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* TODO 변경
        ////////////테스트
        //int item, int cid, String text, String imagePath, int currentLevels, int reviewTimes, String registrationDate
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
        */

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);

        gridView = (RecyclerView) findViewById(R.id.keyword_recyclerview);
        RelationGridAdapter relationGridAdapter = new RelationGridAdapter(this, mKeywords);
        gridView.setLayoutManager(mLayoutManager);
        //키워드가 부족할 경우 회색 박스들 생성
        gridView.setAdapter(relationGridAdapter);
    }

    /**
     * Intent.putExtra 를 통해 정보를 얻으면 사용하면 됨
     * 원래는 사용자가 키워드 하나만 얻어오는줄 알았는데 모든키워드 로드하네
     * 필요없으면 지우삼삼
     * @return 어떤 키워드를 선택했는지 id를 얻어 해당 키워드 얻은 후 리턴
     */
    private Keyword loadKeyword() {
        Bundle intentBundle = getIntent().getExtras();
        assert intentBundle != null;
        int id = intentBundle.getInt(INTENT_KEYWORD_ID, -1);
        BrainDBHandler brainDBHandler = new BrainDBHandler(this);
        Keyword loadedKeyword = null;
        try {
            loadedKeyword = brainDBHandler.findKeyword(BrainDBHandler.FIELD_KEYWORDS_ID, id);
        } catch (BrainDBHandler.NoMatchingDataException e) {
            e.printStackTrace();
            onBackPressed();
        }
        return loadedKeyword;
    }
}
