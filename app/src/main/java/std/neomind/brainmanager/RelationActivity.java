package std.neomind.brainmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Collections;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;
import std.neomind.brainmanager.data.Category;
import std.neomind.brainmanager.data.Keyword;
import std.neomind.brainmanager.utils.BrainDBHandler;

public class RelationActivity extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener {

    public static final String EXTRAS_KEYWORD = "keyword";

    public static final String INTENT_KEYWORD_ID = "intentKeywordID";
    private static final String TAG = "RelationActivity";

    private static final int SPAN_COUNT = 3;

    private BrainDBHandler mBrainDBHandler;

    private ArrayList<Keyword> mTargetKeywords;
    private ArrayList<Integer> mTargetIdList;

    private ArrayList<Category> mCategories;
    private ArrayList<Keyword> mKeywords;
    private ArrayList<Keyword> mTempKeywords;

    private TextView mKeywordText;
    private int mCurrentRKeyIndex = 0;
    private Keyword mCurrentKeyword;

    private Spinner mSpinner;

    private RecyclerView mGridRecyclerView;
    private RecyclerView.Adapter mGridKeywordAdapter;
    private RecyclerView mListRecyclerView;
    private RecyclerView.Adapter mListKeywordAdapter;
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
            mKeywords.clear();
            mGridRecyclerView.removeAllViewsInLayout();
            mListRecyclerView.removeAllViewsInLayout();


            // Add filtered items
            String query = "SELECT * FROM " + BrainDBHandler.TABLE_KEYWORDS;
            query += String.format(" WHERE %s LIKE \"%%%s%%\"", BrainDBHandler.FIELD_KEYWORDS_NAME, searchString);
            Log.d("Query", query);
            mKeywords = mBrainDBHandler.getKeywords(query);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation);
        Toolbar toolbar = findViewById(R.id.relation_toolbar);
        setSupportActionBar(toolbar);

        mBrainDBHandler = new BrainDBHandler(this);
        mSpinner = findViewById(R.id.relation_spinner);
        mSpinner.setOnItemSelectedListener(spinnerItemListener);

        mGridRecyclerView = findViewById(R.id.relation_grid_recyclerView_keyword);
        mListRecyclerView = findViewById(R.id.relation_list_recyclerView_keyword);

        mKeywordText = findViewById(R.id.relation_textView_keyword);
        mTargetKeywords = new ArrayList<>();
        mTargetIdList = (ArrayList<Integer>) getIntent().getSerializableExtra(EXTRAS_KEYWORD);

        if (mTargetIdList.isEmpty()) {
            finish();
            Toast.makeText(getApplicationContext(), getString(R.string.ReviewActivity_noKeyword), Toast.LENGTH_LONG).show();
            return;
        }

        /* TODO 변경
        ////////////테스트
        //int textView, int cid, String text, String imagePath, int currentLevels, int reviewTimes, String registrationDate
        mKeywordText = findViewById(R.id.keyword_relation_view);
        mKeywordText.setText("Keyword");
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

        ///////////////////

        mTempKeywords = new ArrayList<>();
        generate_recycler();
    }

    private void setTargetKeywords() {
        ////TODO 받아오고 DB에서 계속해서 다시 받아와야함. 그래야 relationIds 리스트의 삽입 삭제를 제대로 반영 가능. 문제는 메인화면에서 키워드를 넘겨주는 부분 구현이 좀 더 어려워짐.
        ////TODO 그것도 이것처럼 DB에서 받아와서 갱신할 수 있어야한다.
        ArrayList<Keyword> arrayList;
        BrainDBHandler brainDBHandler = new BrainDBHandler(this);
        try {
            arrayList = brainDBHandler.getAllKeywords();
            brainDBHandler.close();
            mTargetKeywords.clear();
            for (Keyword key : arrayList) {
                if (mTargetIdList.contains(key.id)) {
                    mTargetKeywords.add(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Collections.sort(mTargetKeywords, (o1, o2) -> {
            String criteria1, criteria2;
            int compareResult;
            criteria1 = o1.name;
            criteria2 = o2.name;
            compareResult = criteria1.compareToIgnoreCase(criteria2);

            return compareResult;
        });
        if (mCurrentRKeyIndex == mTargetKeywords.size()) {
            finish();
            return;
        }
        mCurrentKeyword = mTargetKeywords.get(mCurrentRKeyIndex);
        while (mCurrentKeyword.name.equals("")) {
            if (++mCurrentRKeyIndex == mTargetKeywords.size()) {
                finish();
                return;
            }
            mCurrentKeyword = mTargetKeywords.get(mCurrentRKeyIndex);
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

            for (Keyword keyword : mTempKeywords) {
                if (keyword.isSelected()) {
                    relationCount++;
//                    try {
//                        dbHandler.addRelation(mCurrentKeyword.id, keyword.id);
//                    }
//                    catch(BrainDBHandler.DataDuplicationException e){
//                        exCount++;
//                        e.printStackTrace();
//                    }
                } else {
//                    dbHandler.removeRelation(mCurrentKeyword.id, keyword.id);
                }
            }
            dbHandler.close();
            if (exCount != 0) {
                Toast.makeText(this, exCount + getString(R.string.RelationActivity_error), Toast.LENGTH_LONG).show();
            }
            Snackbar.make(view, relationCount + getString(R.string.RelationActivity_relationAdd), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.Global_OK), v -> {
                    }).show();
            mCurrentRKeyIndex++;
            generate_recycler();
        });
    }

    private void generate_recycler() {
        setTargetKeywords();

        mKeywordText.setText(mCurrentKeyword.name);
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPref();
        loadDB();
    }

    @Override
    public void onBackPressed() {
        mCurrentRKeyIndex = 0;
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
        switchWiget.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mGridRecyclerView.setVisibility(View.GONE);
                mListRecyclerView.setVisibility(View.VISIBLE);

            } else {
                mGridRecyclerView.setVisibility(View.VISIBLE);
                mListRecyclerView.setVisibility(View.GONE);
            }
            setTargetKeywords();
            initRecyclerView();
        });
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick: textView :" + item);
        return false;
    }

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
        mCategories = mBrainDBHandler.getAllCategories();
        mBrainDBHandler.close();

        // Sort
        Collections.sort(mCategories, (o1, o2) -> {
            String criteria1, criteria2;
            int compareResult;
            criteria1 = o1.name;
            criteria2 = o2.name;
            compareResult = criteria1.compareToIgnoreCase(criteria2);

            return compareResult;
        });

        mCategories.add(Category.CATEGORY_ALL,
                new Category.Builder().setName(getString(R.string.Category_all)).build());

        for (int i = 0; i < mCategories.size(); i++)
            Log.i(TAG, "loadDB(): mCategories(" + i + ") - " + mCategories.get(i).toStringAbsolutely());
    }

    private void initSpinner() {
        mSpinner.setAdapter(new ArrayAdapter<>(
                this, R.layout.main_category_spinner_item, mCategories));
    }

    private void getKeywordsFromDB() {
        int cid = mCategories.get(mSpinner.getSelectedItemPosition()).id;
        if (cid == Category.CATEGORY_ALL || cid == Category.NOT_REGISTERED)
            mKeywords = mBrainDBHandler.getAllKeywords();
        else mKeywords = mBrainDBHandler.getAllKeywordsOfTheCategory(cid);
        mBrainDBHandler.close();

        // Sort
        Collections.sort(mKeywords, (o1, o2) -> {
            String criteria1, criteria2;
            int compareResult;
            criteria1 = o1.name;
            criteria2 = o2.name;
            compareResult = criteria1.compareToIgnoreCase(criteria2);

            return compareResult;
        });

        for (int i = 0; i < mKeywords.size(); i++)
            Log.i(TAG, "loadDB(): mKeywords(" + i + ") - " + mKeywords.get(i).toStringAbsolutely());
    }

    private void initRecyclerView() {
        mTempKeywords.clear();
        mTempKeywords.addAll(mKeywords);
        for (Keyword key : mTempKeywords) {
            if (key.id == mCurrentKeyword.id) {
                mTempKeywords.remove(key);
                break;
            }
        }
        mGridRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        mGridKeywordAdapter = new ScaleInAnimationAdapter(
                new AlphaInAnimationAdapter(
                        new RelationGridRecyclerAdapter(this, mCurrentKeyword, mTempKeywords)));
        mListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mListKeywordAdapter = new ScaleInAnimationAdapter(
                new AlphaInAnimationAdapter(
                        new RelationListRecyclerAdapter(this, mCurrentKeyword, mTempKeywords)));

        mGridRecyclerView.setAdapter(mGridKeywordAdapter);
        mListRecyclerView.setAdapter(mListKeywordAdapter);
    }

    private void refresh() {
        loadPref();
        setTargetKeywords();
        loadDB();
    }

    public static class RelationGridRecyclerAdapter extends RecyclerView.Adapter<RelationGridRecyclerAdapter.RelationViewHolder> {
        //rContext 액티비티 context 를 저장함.
        private Context rContext;

        private Keyword rSelectK;

        //mKeywords 전체 키워드들의 배열리스트를 저장함.
        private ArrayList<Keyword> rKeywords;

        public RelationGridRecyclerAdapter(Context context, Keyword targetK, ArrayList<Keyword> keywords) {
            rContext = context;
            rSelectK = targetK;
            rKeywords = keywords;
        }

        //    public RelationGridRecyclerAdapter(Context rContext) {
        //        this.rContext = rContext;
        //        this.mKeywords = null;
        //    }
        @Override
        public RelationViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.review_relation_keyword_list, viewGroup, false);

            RelationViewHolder viewHolder = new RelationViewHolder(view);

            return viewHolder;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //아이템을 생성할 때 발생하는 메소드
        @Override
        public void onBindViewHolder(@NonNull RelationViewHolder viewHolder, int position) {
            Keyword currentItemKey = rKeywords.get(position);
            if (!currentItemKey.name.equals("")) {
                viewHolder.textView.setGravity(Gravity.CENTER);
                viewHolder.textView.setTextAppearance(R.style.textExamTheme);
                viewHolder.textView.setText(rKeywords.get(position).name);
                KeyGridClickListener keyGridClickListener = new KeyGridClickListener(rKeywords.get(position));
                viewHolder.textView.setOnClickListener(keyGridClickListener);
                boolean ck_rel = false;
                //기존 관계성 체크
                for (Integer id : rSelectK.getRelationIds()) {
                    if (currentItemKey.id == id) {
                        ck_rel = true;
                        break;
                    }
                }
                if (ck_rel) {
                    currentItemKey.setSelected(true);
                    viewHolder.textView.setBackground(ContextCompat.
                            getDrawable(rContext, R.drawable.review_relation_blue_edge));
                } else {
                    viewHolder.textView.setBackground(ContextCompat.
                            getDrawable(rContext, R.drawable.review_relation_edge));
                }
            } else {
                viewHolder.textView.setBackground(ContextCompat.
                        getDrawable(rContext, R.drawable.review_relation_gray_edge));
            }
        }

        @Override
        public int getItemCount() {
            return (null != rKeywords) ? rKeywords.size() : 0;
        }

        ///재사용 되어질지도 모르는 코드는 일단 주석처리
        //    public Object getItem(int index) {
        //        return (null != mKeywords) ? mKeywords.get(index) : 0;
        //    }
        //    public long getItemId(int position) {
        //        return position;
        //
        //    }
        //    //////////////////////////
        //
        //
        //    public View getView(int index, View convertView, ViewGroup parent) {
        //        TextView textView = null;
        //        String keytxt =mKeywords.get(index).text;
        //
        //        if (keytxt != "") {
        //            if (null != convertView)
        //                textView = (TextView) convertView;
        //            else {
        //                textView = new TextView(rContext);
        //                textView.setText(keytxt);
        //
        //                // 클릭을 처리하는 KeyGridClickListener 객체를 정의
        //                // 그리고 그것을 textView 클릭 리스너로 설정합니다.
        //                KeyGridClickListener keyGridClickListener
        //                        = new KeyGridClickListener(rContext, mKeywords.get(index), textView);
        //                textView.setOnClickListener(keyGridClickListener);
        //                textView.setBackground(ContextCompat.getDrawable(rContext, R.drawable.review_relation_edge));
        //                textView.setHeight(300);
        //                textView.setGravity(Gravity.CENTER);
        //            }
        //        }
        //        else
        //        {
        //                textView = new TextView(rContext);
        //                // TODO change
        //                //textView.setBackground(ContextCompat.getDrawable(rContext, R.drawable.gray_full_edge));
        //                textView.setHeight(300);
        //        }
        //        return textView;
        //    }

        class RelationViewHolder extends RecyclerView.ViewHolder implements AnimateViewHolder {
            TextView textView;

            RelationViewHolder(View itemView) {
                super(itemView);
                this.textView = itemView.findViewById(R.id.reviewRelation_item_textView);
            }


            @Override
            public void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
                ViewCompat.setTranslationY(itemView, -itemView.getHeight() * 0.3f);
                ViewCompat.setAlpha(itemView, 0f);
            }

            @Override
            public void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {

            }

            @Override
            public void animateAddImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {
                ViewCompat.animate(itemView).
                        translationY(0f)
                        .alpha(1f)
                        .setDuration(300)
                        .setListener(listener)
                        .start();
            }

            @Override
            public void animateRemoveImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {
                ViewCompat.animate(itemView).
                        translationY(-itemView.getHeight() * 0.3f)
                        .alpha(0f)
                        .setDuration(300)
                        .setListener(listener)
                        .start();
            }
        }

        private class KeyGridClickListener implements View.OnClickListener {
            private Keyword key;

            public KeyGridClickListener(Keyword keyword) {
                this.key = keyword;
            }

            public void onClick(View v) {
                //---------------------------------------------------------
                // 클릭된 키워드들을 저장함.
                //
                BrainDBHandler dbHandler = new BrainDBHandler(rContext);
                if (key.isSelected()) {
                    key.setSelected(false);
                    dbHandler.removeRelation(rSelectK.id, key.id);
                    v.setBackground(ContextCompat.getDrawable(rContext, R.drawable.review_relation_edge));
                } else {
                    key.setSelected(true);
                    try {
                        dbHandler.addRelation(rSelectK.id, key.id);
                    } catch (BrainDBHandler.DataDuplicationException e) {
                        Toast.makeText(rContext, "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    v.setBackground(ContextCompat.getDrawable(rContext, R.drawable.review_relation_blue_edge));
                }
                dbHandler.close();
            }
        }
    }

    public static class RelationListRecyclerAdapter extends RecyclerView.Adapter<RelationListRecyclerAdapter.KeywordViewHolder> {

        private static final String TAG = "RelationListRecyclerAdapter";

        private Activity mActivity;
        private Keyword mSelectK;
        private ArrayList<Keyword> mKeywords;

        public RelationListRecyclerAdapter(Activity activity, Keyword targetK, ArrayList<Keyword> keywords) {
            mActivity = activity;
            mSelectK = targetK;
            mKeywords = keywords;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.review_relation_keyword_item, viewGroup, false);
            KeywordViewHolder keywordViewHolder = new KeywordViewHolder(view);
            return keywordViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull KeywordViewHolder keywordViewHolder, int i) {
            keywordViewHolder.build(i);
        }

        @Override
        public int getItemCount() {
            try {
                return mKeywords.size();
            } catch (Exception e) {
                return 0;
            }
        }

        class KeywordViewHolder extends RecyclerView.ViewHolder implements AnimateViewHolder {
            /**
             * @param itemView is convertView
             */

            CircularImageView imageView;
            TextView textView;

            KeywordViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.reviewRelation_item_circularImage_keyword);
                textView = itemView.findViewById(R.id.reviewRelation_item_textView_keyword);
            }

            public void build(int i) {
                Keyword currentItemKey = mKeywords.get(i);
                currentItemKey.setCardView((CardView) itemView);

                if (!currentItemKey.name.equals("")) {
                    boolean ck_rel = false;
                    //기존 관계성 체크
                    for (Integer id : mSelectK.getRelationIds()) {
                        if (currentItemKey.id == id) ck_rel = true;
                    }
                    if (ck_rel) {
                        currentItemKey.setSelected(true);
                        itemView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.review_relation_blue_edge));
                    } else {
                        itemView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.review_relation_edge));
                    }
                    itemView.setOnClickListener(new ItemClickListener());
                } else {
                    itemView.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.review_relation_gray_edge));
                }


                Glide.with(mActivity.getBaseContext())
                        .load(mKeywords.get(i).imagePath)
                        .into(imageView);
                textView.setText(mKeywords.get(i).name);
            }

            @Override
            public void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
                ViewCompat.setTranslationY(itemView, -itemView.getHeight() * 0.3f);
                ViewCompat.setAlpha(itemView, 0f);
            }

            @Override
            public void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {

            }

            @Override
            public void animateAddImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {
                ViewCompat.animate(itemView).
                        translationY(0f)
                        .alpha(1f)
                        .setDuration(300)
                        .setListener(listener)
                        .start();
            }

            @Override
            public void animateRemoveImpl(RecyclerView.ViewHolder holder, ViewPropertyAnimatorListener listener) {
                ViewCompat.animate(itemView).
                        translationY(-itemView.getHeight() * 0.3f)
                        .alpha(0f)
                        .setDuration(300)
                        .setListener(listener)
                        .start();
            }

            private class ItemClickListener implements View.OnClickListener {

                @Override
                public void onClick(View v) {
                    BrainDBHandler dbHandler = new BrainDBHandler(mActivity);
                    if (mKeywords.get(getAdapterPosition()).isSelected()) {
                        mKeywords.get(getAdapterPosition()).setSelected(false);
                        dbHandler.removeRelation(mSelectK.id, mKeywords.get(getAdapterPosition()).id);
                        v.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.review_relation_edge));
                    } else {
                        mKeywords.get(getAdapterPosition()).setSelected(true);
                        try {
                            dbHandler.addRelation(mSelectK.id, mKeywords.get(getAdapterPosition()).id);
                        } catch (BrainDBHandler.DataDuplicationException e) {
                            Toast.makeText(mActivity, mActivity.
                                    getString(R.string.Global_errorOccurred), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        v.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.review_relation_blue_edge));
                    }
                }
            }
        }
    }
}
