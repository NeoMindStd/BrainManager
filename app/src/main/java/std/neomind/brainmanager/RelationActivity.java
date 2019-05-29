package std.neomind.brainmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

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

public class RelationActivity extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener {

    public static final String EXTRAS_KEYWORD = "keyword";

    public static final String INTENT_KEYWORD_ID = "intentKeywordID";
    private static final String TAG = "RelationActivity";

    private static final int SPAN_COUNT = 3;

    private BrainDBHandler rBrainDBHandler;

    private ArrayList<Keyword> rTargetKeywords;
    private ArrayList<Integer> rTargetIdList;

    private ArrayList<Category> rCategories;
    private ArrayList<Keyword> rKeywords;
    private ArrayList<Keyword> tempKeywords;

    private TextView rKeywordText;
    private int currentrKeyIndex = 0;
    private int rKeywordsSize;
    private Keyword currentKeyword;
    private Test currentTest;

    private Spinner rSpinner;

    private RecyclerView rGridRecyclerView;
    private RecyclerView.Adapter rGridKeywordAdapter;
    private RecyclerView rListRecyclerView;
    private RecyclerView.Adapter rListKeywordAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation);
        Toolbar toolbar = findViewById(R.id.relation_toolbar);
        setSupportActionBar(toolbar);

        rBrainDBHandler = new BrainDBHandler(this);
        rSpinner = findViewById(R.id.relation_spinner);
        rSpinner.setOnItemSelectedListener(spinnerItemListener);

        rGridRecyclerView = findViewById(R.id.relation_grid_recyclerView_keyword);
        rListRecyclerView = findViewById(R.id.relation_list_recyclerView_keyword);

        rKeywordText = findViewById(R.id.relation_textView_keyword);
        rTargetKeywords = new ArrayList<>();
        rTargetIdList = (ArrayList<Integer>) getIntent().getSerializableExtra(EXTRAS_KEYWORD);

        if(rTargetIdList.isEmpty()){
            finish();
            Toast.makeText(getApplicationContext(), getString(R.string.ReviewActivity_noKeyword), Toast.LENGTH_LONG).show();
            return;
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

        ///////////////////

        tempKeywords = new ArrayList<>();
        generate_recycler();
    }
    private void setTargetKeywords(){
        ////TODO 받아오고 DB에서 계속해서 다시 받아와야함. 그래야 relationIds 리스트의 삽입 삭제를 제대로 반영 가능. 문제는 메인화면에서 키워드를 넘겨주는 부분 구현이 좀 더 어려워짐.
        ////TODO 그것도 이것처럼 DB에서 받아와서 갱신할 수 있어야한다.
        ArrayList<Keyword> arrayList;
        BrainDBHandler brainDBHandler = new BrainDBHandler(this);
        try {
            arrayList = brainDBHandler.getAllKeywords();
            brainDBHandler.close();
            rTargetKeywords.clear();
            for(Keyword key : arrayList){
                if(rTargetIdList.contains(key.id)){
                    rTargetKeywords.add(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Collections.sort(rTargetKeywords, (o1, o2) -> {
            String criteria1, criteria2;
            int compareResult;
            criteria1 = o1.name;
            criteria2 = o2.name;
            compareResult = criteria1.compareToIgnoreCase(criteria2);

            return compareResult;
        });
        if(currentrKeyIndex == rTargetKeywords.size()){
            finish();
            return;
        }
        currentKeyword = rTargetKeywords.get(currentrKeyIndex);
        while(currentKeyword.name.equals("")){
            currentKeyword = rTargetKeywords.get(++currentrKeyIndex);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        ///플로팅 액션 버튼
        FloatingActionButton fab = findViewById(R.id.relation_fab);
        //TODO RelationIds 세터 사용됨.
        fab.setOnClickListener(view -> {
            BrainDBHandler dbHandler = new BrainDBHandler(RelationActivity.this);

            int exCount = 0;
            int relationCount = 0;

            for(Keyword keyword : tempKeywords){
                if (keyword.isSelected()) {
                    relationCount++;
//                    try {
//                        dbHandler.addRelation(currentKeyword.id, keyword.id);
//                    }
//                    catch(BrainDBHandler.DataDuplicationException e){
//                        exCount++;
//                        e.printStackTrace();
//                    }
                } else {
//                    dbHandler.removeRelation(currentKeyword.id, keyword.id);
                }
            }
            dbHandler.close();
            if(exCount != 0) {
                Toast.makeText(this, exCount + getString(R.string.RelationActivity_error), Toast.LENGTH_LONG).show();
            }
            Snackbar.make(view, relationCount + getString(R.string.RelationActivity_relationAdd), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.Global_OK), new View.OnClickListener() {
                        @Override
                        public void onClick(View v){
                        }
                    }).show();
            currentrKeyIndex++;
            generate_recycler();
        });
    }

    private void generate_recycler(){
        setTargetKeywords();

        rKeywordText.setText(currentKeyword.name);
        refresh();
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

    @Override
    public void onBackPressed() {
        currentrKeyIndex = 0;
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.relation_app_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.relation_action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(queryTextListener);
        searchView.setOnCloseListener(onCloseListener);

        MenuItem switchItem = menu.findItem(R.id.relation_action_switch);
        Switch switchWiget = switchItem.getActionView().findViewById(R.id.switchForActionBar);
        switchWiget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    rGridRecyclerView.setVisibility(View.GONE);
                    rListRecyclerView.setVisibility(View.VISIBLE);

                }else{
                    rGridRecyclerView.setVisibility(View.VISIBLE);
                    rListRecyclerView.setVisibility(View.GONE);
                }
                setTargetKeywords();
                initRecyclerView();
            }
        });
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick: textView :" + item);
        return false;
    }

    private static Keyword mRequestImageReceiver;

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        try {
//            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
//                ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
//                String[] imagePath = new String[images.size()];
//                for (int i = 0; i < images.size(); i++) imagePath[i] = images.get(i).getPath();
//
//                if (imagePath.length > 0) {
//                    mRequestImageReceiver.imagePath = imagePath[0];
//                    mBrainDBHandler.updateKeyword(mRequestImageReceiver);
//                    refresh();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private void loadPref() {

    }

    private void loadDB() {
        try {
            getCategoriesFromDB();
            initSpinner();

            // loading in spinnerItemListener
            setTargetKeywords();
            getKeywordsFromDB();
            initRecyclerView();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCategoriesFromDB() {
        rCategories = rBrainDBHandler.getAllCategories();
        rBrainDBHandler.close();

        // Sort
        Collections.sort(rCategories, (o1, o2) -> {
            String criteria1, criteria2;
            int compareResult;
            criteria1 = o1.name;
            criteria2 = o2.name;
            compareResult = criteria1.compareToIgnoreCase(criteria2);

            return compareResult;
        });

        rCategories.add(Category.CATEGORY_ALL,
                new Category.Builder().setName(getString(R.string.Category_all)).build());

        for (int i = 0; i < rCategories.size(); i++)
            Log.i(TAG, "loadDB(): mCategories(" + i + ") - " + rCategories.get(i).toStringAbsolutely());
    }

    private void initSpinner() {
        rSpinner.setAdapter(new ArrayAdapter<>(
                this, R.layout.main_category_spinner_item, rCategories));
    }

    private void getKeywordsFromDB() {
        rKeywords = rBrainDBHandler.getAllKeywordsOfTheCategory(rSpinner.getSelectedItemPosition());
        rBrainDBHandler.close();

        // Sort
        Collections.sort(rKeywords, (o1, o2) -> {
            String criteria1, criteria2;
            int compareResult;
            criteria1 = o1.name;
            criteria2 = o2.name;
            compareResult = criteria1.compareToIgnoreCase(criteria2);

            return compareResult;
        });

        for (int i = 0; i < rKeywords.size(); i++)
            Log.i(TAG, "loadDB(): mKeywords(" + i + ") - " + rKeywords.get(i).toStringAbsolutely());
    }

    private void initRecyclerView() {
        tempKeywords.clear();
        tempKeywords.addAll(rKeywords);
        for(Keyword key : tempKeywords){
            if(key.id == currentKeyword.id){
                tempKeywords.remove(key);
                break;
            }
        }
        rGridRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        rGridKeywordAdapter = new ScaleInAnimationAdapter(
                new AlphaInAnimationAdapter(
                        new RelationGridRecyclerAdapter(this, currentKeyword, tempKeywords)));
        rListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rListKeywordAdapter = new ScaleInAnimationAdapter(
                new AlphaInAnimationAdapter(
                        new RelationListRecyclerAdapter(this, currentKeyword, tempKeywords)));

        rGridRecyclerView.setAdapter(rGridKeywordAdapter);
        rListRecyclerView.setAdapter(rListKeywordAdapter);
    }

    private void refresh() {
        loadPref();
        setTargetKeywords();
        loadDB();
    }

    private Spinner.OnItemSelectedListener spinnerItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //loadDB();
            setTargetKeywords();
            getKeywordsFromDB();
            initRecyclerView();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String searchString) {
            setTargetKeywords();
            rKeywords.clear();
            rGridRecyclerView.removeAllViewsInLayout();
            rListRecyclerView.removeAllViewsInLayout();


            // Add filtered items
            String query = "SELECT * FROM " + BrainDBHandler.TABLE_KEYWORDS;
            query += String.format(" WHERE %s LIKE \"%%%s%%\"", BrainDBHandler.FIELD_KEYWORDS_NAME, searchString);
            Log.d("Query", query);
            rKeywords = rBrainDBHandler.getKeywords(query);
            initRecyclerView();

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    private SearchView.OnCloseListener onCloseListener = () -> {
        setTargetKeywords();
        loadDB();
        return false;
    };
}
