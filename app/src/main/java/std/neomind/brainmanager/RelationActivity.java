package std.neomind.brainmanager;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import std.neomind.brainmanager.data.Category;
import std.neomind.brainmanager.data.Test;
import std.neomind.brainmanager.utils.BrainDBHandler;
import std.neomind.brainmanager.data.Keyword;
import std.neomind.brainmanager.utils.RelationGridRecyclerAdapter;
import std.neomind.brainmanager.utils.RelationListRecyclerAdapter;

public class RelationActivity extends AppCompatActivity {

    public static final String INTENT_KEYWORD_ID = "intentKeywordID";
    private static final String TAG = "RelationActivity";

    private static final int SPAN_COUNT = 3;


    private BrainDBHandler rBrainDBHandler;

    private ArrayList<Category> rCategories;
    private ArrayList<Keyword> rKeywords;

    private TextView rKeywordText;
    private static int currentrKeyIndex = 0;
    private int rKeywordsSize;
    private Keyword currentKeyword;
    private Test currentTest;

    private RecyclerView rKeywordRecyclerView;
    private Spinner rSpinner;
    private RecyclerView rListRecyclerView;
    private RecyclerView.Adapter rKeywordAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation);
        Toolbar toolbar = findViewById(R.id.relation_toolbar);
        setSupportActionBar(toolbar);

        rBrainDBHandler = new BrainDBHandler(this);
        rSpinner = findViewById(R.id.relation_spinner);
        rListRecyclerView = findViewById(R.id.relation_list_recyclerView_keyword);

        DrawerLayout drawerLayout = findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ///플로팅 액션 버튼
        FloatingActionButton fab = findViewById(R.id.relation_fab);
        //TODO RelationIds 세터 사용됨. SQLite좀 이상함 kid1이랑 kid2가 뭐임.
        fab.setOnClickListener(view -> {
            BrainDBHandler dbHandler = new BrainDBHandler(RelationActivity.this);
            currentKeyword.setRelationIds(((RelationGridRecyclerAdapter)rKeywordRecyclerView.getAdapter()).getSelectedList());
            Snackbar.make(view, "관계성이 추가되었습니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            generate_recycler();
            });
        ////실구현
        BrainDBHandler brainDBHandler = new BrainDBHandler(this);
        try {
            rKeywords = brainDBHandler.getAllKeywords();
            brainDBHandler.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* TODO 변경
        ////////////테스트
        //int textView, int cid, String text, String imagePath, int currentLevels, int reviewTimes, String registrationDate
        rKeywordText = findViewById(R.id.keyword_relation_view);
        rKeywordText.setText("Keyword");
        Keyword.Builder b = new Keyword.Builder();
        b.setText("와!");
        Keyword tempKey = b.build();
        b.setText("으윽!");
        Keyword tempKey2 = b.build();
        rKeywords = new ArrayList<Keyword>();
        rKeywords.add(tempKey);
        rKeywords.add(tempKey2);
        ///////////////

        b.setText("");
        //Keyword temp = b.build();
        int key_size = rKeywords.size();
        for(int i = 12 - key_size; i > 0; i--) {
            rKeywords.add(b.build());
        }
        */
        rKeywordText = findViewById(R.id.relation_textView_keyword);

        generate_recycler();
    }
    private void generate_recycler(){
        if(currentrKeyIndex == rKeywords.size()){
            finish();
            return;
        };
        ArrayList<Keyword> tKeywords = new ArrayList<>();
        tKeywords.addAll(rKeywords);
        currentKeyword = rKeywords.get(currentrKeyIndex++);
        tKeywords.remove(currentKeyword);
        rKeywordText.setText(currentKeyword.name);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        rKeywordRecyclerView = findViewById(R.id.relation_grid_recyclerView_keyword);
        RelationGridRecyclerAdapter rGridRecyclerAdapter = new RelationGridRecyclerAdapter(this, tKeywords);
        rKeywordRecyclerView.setLayoutManager(mLayoutManager);
        //키워드가 부족할 경우 회색 박스들 생성
        rKeywordRecyclerView.setAdapter(rGridRecyclerAdapter);
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
        int id = intentBundle.getInt(INTENT_KEYWORD_ID, Keyword.NOT_REGISTERED);
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


    @Override
    protected void onResume() {
        super.onResume();

        loadPref();
        loadDB();
    }
    private void loadPref() {

    }
    private void loadDB() {
        try {
            rCategories = rBrainDBHandler.getAllCategories();
            rCategories.add(Category.CATEGORY_ALL,
                    new Category.Builder().setName(getString(R.string.Category_all)).build());

            rKeywords = rBrainDBHandler.getAllKeywords();
            for(int i = 0; i < rKeywords.size(); i++)
                rKeywords.get(i).setCardView( findViewById(R.id.keyword_card_view) );

            rBrainDBHandler.close();
            for(int i = 0; i < rCategories.size(); i++)
                Log.i(TAG, "loadDB(): rCategories(" + i + ") - " + rCategories.get(i).toStringAbsolutely());
            for(int i = 0; i < rKeywords.size(); i++)
                Log.i(TAG, "loadDB(): rKeywords(" + i + ") - " + rKeywords.get(i).toStringAbsolutely());

            rSpinner.setAdapter(new ArrayAdapter<>(
                    this, R.layout.category_spinner_item, rCategories));

            rListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            rKeywordAdapter = new ScaleInAnimationAdapter(
                    new AlphaInAnimationAdapter(
                            new RelationListRecyclerAdapter(this, rKeywords)));
            rListRecyclerView.setAdapter(rKeywordAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.relation_app_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }
    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };
}
