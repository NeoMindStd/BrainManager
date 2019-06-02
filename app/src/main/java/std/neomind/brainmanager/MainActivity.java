package std.neomind.brainmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;
import std.neomind.brainmanager.utils.BrainDBHandler;
import std.neomind.brainmanager.data.Category;
import std.neomind.brainmanager.data.Keyword;
import std.neomind.brainmanager.utils.BrainSerialDataIO;
import std.neomind.brainmanager.utils.FileManager;
import std.neomind.brainmanager.utils.PermissionManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener {

    private static final String TAG = "MainActivity";

    private PermissionManager mPermissionManager;
    private BrainDBHandler mBrainDBHandler;

    private ArrayList<Category> mCategories;
    private ArrayList<Keyword> mKeywords;

    private SpeedDialView mFab;
    private Spinner mSpinner;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mKeywordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPermissionManager = new PermissionManager(this);
        if (!mPermissionManager.checkGranted()) mPermissionManager.request();
        initActivity();
    }

    private void initActivity() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mFab = findViewById(R.id.main_fab);
        mFab.setOnActionSelectedListener(fabItemClickListener);
        fabAddAllSubItems();

        DrawerLayout drawerLayout = findViewById(R.id.main_drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.main_navView);
        navigationView.setNavigationItemSelectedListener(this);

        mSpinner = findViewById(R.id.main_spinner);
        mSpinner.setOnItemSelectedListener(spinnerItemListener);
        mRecyclerView = findViewById(R.id.main_recyclerView_keyword);

        mBrainDBHandler = new BrainDBHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDB();

        ArrayList<Integer> reviewList = new ArrayList<>();
        ArrayList<Long> reviewDateList = new ArrayList<>();
        try {
            BrainSerialDataIO.getNextReviewTimeInfo(this, reviewList, reviewDateList);
        } catch (BrainSerialDataIO.LoadFailException e) {   // 오류가 생기면 초기화 한다.
            reviewList = new ArrayList<>();
            reviewDateList = new ArrayList<>();
            e.printStackTrace();
        }
        boolean tempFlag = false;
        if(reviewDateList.size() > 0 && reviewDateList.size() == reviewList.size()) {
            ArrayList<Keyword> allKeywords;
            try {
                allKeywords = mBrainDBHandler.getAllKeywords();
                mBrainDBHandler.close();
            } catch (Exception e) {
                allKeywords = new ArrayList<>();
                e.printStackTrace();
            }
            //전체 키워드들 중에서 복습간격이 만기된 알림이 있으면 플래그를 true로 만듬
            for (Keyword key : allKeywords) {
                if (reviewList.contains(key.id)) {
                    if (reviewDateList.get(reviewList.indexOf(key.id)) < System.currentTimeMillis()) {
                        tempFlag = true;
                        break;
                    }
                }
            }
        }
        NavigationView navigationView = findViewById(R.id.main_navView);
        Menu menu = navigationView.getMenu();
        MenuItem expired_item = menu.findItem(R.id.nav_expired_review);
        expired_item.setVisible(tempFlag);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_app_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.main_action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(queryTextListener);
        searchView.setOnCloseListener(onCloseListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar textView clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.main_action_deleteCategories :
                if (mSpinner.getSelectedItemPosition() != Category.CATEGORY_ALL) {
                    mBrainDBHandler.removeCategory(mCategories.get(mSpinner.getSelectedItemPosition()).id);
                    onResume();
                } else Toast.makeText(this, R.string.MainActivity_unableToDelete, Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_action_editCategories :
                if (mSpinner.getSelectedItemPosition() != Category.CATEGORY_ALL) {
                    Intent intent = new Intent(this, CategoryActivity.class);
                    intent.putExtra(CategoryActivity.EXTRAS_CATEGORY, mCategories.get(mSpinner.getSelectedItemPosition()).id);
                    startActivity(intent);
                } else Toast.makeText(this, R.string.MainActivity_unableToEdit, Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view textView clicks here.
        Intent intent = null;
        ArrayList<Integer> list = new ArrayList<>();
        for(Keyword k : mKeywords){
            list.add(k.id);
        }

        switch (item.getItemId()) {
            case R.id.nav_review:
                intent = new Intent(this, ReviewActivity.class);
                intent.putExtra(ReviewActivity.EXTRAS_KEYWORD, list);
                break;
            case R.id.nav_expired_review:
                intent = new Intent(this, ReviewActivity.class);
                intent.putExtra(ReviewActivity.EXTRAS_MODE, ReviewActivity.EXPIRED_MODE);
                break;
            case R.id.nav_statistics:
                intent = new Intent(this, StatisticsActivity.class);
                break;
            case R.id.nav_relation:
                intent = new Intent(this, RelationActivity.class);
                intent.putExtra(RelationActivity.EXTRAS_KEYWORD, list);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = findViewById(R.id.main_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick: textView :" + item);
        return false;
    }

    private void loadDB() {
        try {
            getCategoriesFromDB();
            initSpinner();

            // loading in spinnerItemListener
            //getKeywordsFromDB();
            //initRecyclerView();

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
        if(cid == Category.CATEGORY_ALL || cid == Category.NOT_REGISTERED) mKeywords = mBrainDBHandler.getAllKeywords();
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mKeywordAdapter = new ScaleInAnimationAdapter(
                new AlphaInAnimationAdapter(
                        new MainRecyclerAdapter(this, mKeywords)));
        mRecyclerView.setAdapter(mKeywordAdapter);
    }

    private void fabAddAllSubItems() {
        List<SpeedDialActionItem> speedDialActionItems = new ArrayList<>();
        List<Pair<Integer, Pair<Integer, Integer>>> subItems = new ArrayList<>();
        subItems.add(new Pair(R.id.main_fab_item_register_Keywords,
                new Pair(R.drawable.ic_main_fab_keyword, R.string.MainActivity_FabItem_addKeywords)));
        subItems.add(new Pair(R.id.main_fab_item_register_Categories,
                new Pair(R.drawable.ic_main_fab_category, R.string.MainActivity_FabItem_addCategories)));

        for (Pair<Integer, Pair<Integer, Integer>> item : subItems) {
            speedDialActionItems.add(
                    new SpeedDialActionItem.Builder(item.first, item.second.first)
                            .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme()))
                            .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.WHITE, getTheme()))
                            .setLabel(item.second.second)
                            .setLabelColor(Color.WHITE)
                            .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme()))
                            .setLabelClickable(true)
                            .create());
        }
        mFab.addAllActionItems(speedDialActionItems);
    }


    /***********************************************************
     *                      Event Listener
     ***********************************************************/

    private Spinner.OnItemSelectedListener spinnerItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //loadDB();
            getKeywordsFromDB();
            initRecyclerView();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private SpeedDialView.OnActionSelectedListener fabItemClickListener = actionItem -> {
        final EditText editText = new EditText(this);
        switch (actionItem.getId()) {
            case R.id.main_fab_item_register_Categories:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.MainActivity_addCategory))
                        .setView(editText)
                        .setPositiveButton(getString(R.string.Global_confirm),
                                (dialog, which) -> {
                                    Category resultCategory = new Category.Builder().
                                            setName(editText.getText().toString())
                                            .build();
                                    mBrainDBHandler.addCategory(resultCategory);
                                    try {
                                        resultCategory = mBrainDBHandler.findLastCategory();
                                        for(int i = 1; i < mCategories.size(); i++) {
                                            if(i ==  mCategories.size()-1) {
                                                mCategories.add(resultCategory);
                                                break;
                                            } else if(resultCategory.name.
                                                    compareToIgnoreCase(mCategories.get(i).name) < 0) {
                                                mCategories.add(i, resultCategory);
                                                break;
                                            }
                                        }
                                        initSpinner();
                                    } catch (BrainDBHandler.NoMatchingDataException e) { e.printStackTrace(); }
                                })
                        .setNeutralButton(getString(R.string.Global_negative), null)
                        .show();
                break;
            case R.id.main_fab_item_register_Keywords:
                Intent intent = new Intent(this, KeywordActivity.class);
                intent.putExtra(KeywordActivity.EXTRAS_INTENT_MODE, KeywordActivity.INTENT_MODE_REGISTER);
                intent.putExtra(KeywordActivity.EXTRAS_KEYWORD, Keyword.NOT_REGISTERED);
                startActivity(intent);
                break;
        }
        return false;
    };

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String searchString) {
            mKeywords.clear();
            mRecyclerView.removeAllViewsInLayout();

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
        loadDB();
        initRecyclerView();
        return false;
    };

    public static class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.KeywordViewHolder> {
        private MainActivity mActivity;
        private ArrayList<Keyword> mKeywords;

        public MainRecyclerAdapter(MainActivity activity, ArrayList<Keyword> keywords) {
            mActivity = activity;
            mKeywords = keywords;
        }

        @NonNull
        @Override
        public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.main_keyword_item, viewGroup, false);
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
                imageView = itemView.findViewById(R.id.main_item_circularImage_keyword);
                textView = itemView.findViewById(R.id.main_item_textView_keyword);
            }

            public void build(int i) {
                mKeywords.get(i).setCardView((CardView)itemView);

                itemView.setOnClickListener(new ItemClickListener());
                itemView.setOnLongClickListener(new ItemLongClickListener(this));

                Glide.with(mActivity.getBaseContext())
                        .load(mKeywords.get(i).imagePath)
                        .into(imageView);
                textView.setText(mKeywords.get(i).name);
            }

            public void removeItem() {
                mKeywords.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            }

            public void removeItem(int i) {
                mKeywords.remove(i);
                notifyItemRemoved(i);
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
                    Intent intent = new Intent(mActivity, KeywordActivity.class);
                    intent.putExtra(KeywordActivity.EXTRAS_INTENT_MODE, KeywordActivity.INTENT_MODE_VIEW);
                    intent.putExtra(KeywordActivity.EXTRAS_KEYWORD, mKeywords.get(getAdapterPosition()).id);
                    mActivity.startActivity(intent);
                }
            }

            private class ItemLongClickListener implements View.OnLongClickListener {
                private KeywordViewHolder mKeywordViewHolder;

                private ItemLongClickListener(KeywordViewHolder keywordViewHolder) {
                    mKeywordViewHolder = keywordViewHolder;
                }

                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mActivity, v);
                    MenuInflater inflater = popupMenu.getMenuInflater();
                    Menu menu = popupMenu.getMenu();
                    inflater.inflate(R.menu.main_context_keyword, menu);
                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {

                            case R.id.item_edit:
                                Intent intent = new Intent(mActivity, KeywordActivity.class);
                                intent.putExtra(KeywordActivity.EXTRAS_INTENT_MODE, KeywordActivity.INTENT_MODE_UPDATE);
                                intent.putExtra(KeywordActivity.EXTRAS_KEYWORD, mKeywords.get(getAdapterPosition()).id);
                                mActivity.startActivity(intent);
                                break;

                            case R.id.item_remove:
                                try {
                                    BrainDBHandler dbHandler = new BrainDBHandler(mActivity);

                                    dbHandler.removeKeyword(mKeywords.get(getAdapterPosition()).id);       // db 삭제

                                    // 만약 변경 전 사진이 내부 저장소에 위치해 있었다면 해당 사진 삭제
                                    if(FileManager.isInternalStorageFile(mActivity, mKeywords.get(getAdapterPosition()).imagePath))
                                        Log.i(TAG, "onLongClick: delete before image result - " +
                                                FileManager.deleteFile(new File(mKeywords.get(getAdapterPosition()).imagePath)));

                                    mKeywordViewHolder.removeItem();                   // 기프티콘 배열에서 삭제 및 뷰홀더 리로드

                                    //시리얼라이즈 저장된 곳에서 id에 해당하는 것 삭제.
                                    try{
                                        BrainSerialDataIO.deleteOneNextReivewTimeInfo(mActivity, mKeywords.get(getAdapterPosition()).id);
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                        }
                        return true;
                    });
                    popupMenu.show();
                    return true;
                }
            }
        }
    }
}
