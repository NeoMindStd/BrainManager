package std.neomind.brainmanager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import std.neomind.brainmanager.data.Category;
import std.neomind.brainmanager.data.Keyword;
import std.neomind.brainmanager.utils.BrainDBHandler;
import std.neomind.brainmanager.utils.CategoryRecyclerAdapter;

public class CategoryActivity extends AppCompatActivity {
    private static final String TAG = "CategoryActivity";

    public static final String EXTRAS_CATEGORY = "category";

    private BrainDBHandler mBrainDBHandler;

    private int categoryID;

    private Category mCategory;
    private ArrayList<Keyword> mKeywords;
    private ArrayList<Keyword> mUpdatedKeywords;

    private SpeedDialView mFab;
    private MaterialEditText mNameEditText;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mKeywordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.category_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.CategoryActivity_title);

        mBrainDBHandler = new BrainDBHandler(this);

        getBundleExtras();

        mCategory = loadCategoryFromDB(categoryID);
        mKeywords = loadKeywordsFromDB(categoryID);
        mUpdatedKeywords = new ArrayList<>();

        mFab = findViewById(R.id.category_fab);
        mFab.setOnActionSelectedListener(fabItemClickListener);
        fabAddAllSubItems();
        mNameEditText = findViewById(R.id.category_editText_name);
        mRecyclerView = findViewById(R.id.category_recyclerView_keyword);

        mNameEditText.setText(mCategory.name);
        initRecyclerView();
    }

    private void getBundleExtras() {
        categoryID = getIntent().getExtras().getInt(EXTRAS_CATEGORY);

        Log.i(TAG, "onCreate: categoryID - " + categoryID);
    }

    private Category loadCategoryFromDB(int id) {
        Category category = null;
        try {
            category = mBrainDBHandler.findCategory(BrainDBHandler.FIELD_CATEGORIES_ID, id);
        } catch (BrainDBHandler.NoMatchingDataException e) { e.printStackTrace(); }
        mBrainDBHandler.close();

        Log.i(TAG, "onCreate: category - " + category);

        return category;
    }

    private ArrayList<Keyword> loadKeywordsFromDB(int id) {
        ArrayList<Keyword> keywords = mBrainDBHandler.getAllKeywordsOfTheCategory(id);
        mBrainDBHandler.close();

        // Sort
        Collections.sort(keywords, (o1, o2) -> {
            String criteria1, criteria2;
            int compareResult;
            criteria1 = o1.name;
            criteria2 = o2.name;
            compareResult = criteria1.compareToIgnoreCase(criteria2);

            return compareResult;
        });

        for (int i = 0; i < keywords.size(); i++)
            Log.i(TAG, "loadKeywordsFromDB(): keywords(" + i + ") - " + keywords.get(i).toStringAbsolutely());

        return keywords;
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mKeywordAdapter = new ScaleInAnimationAdapter(
                new AlphaInAnimationAdapter(
                        new CategoryRecyclerAdapter(this, mKeywords)));
        mRecyclerView.setAdapter(mKeywordAdapter);
    }

    private void fabAddAllSubItems() {
        List<SpeedDialActionItem> speedDialActionItems = new ArrayList<>();
        List<Pair<Integer, Pair<Integer, Integer>>> subItems = new ArrayList<>();
        /*
        subItems.add(new Pair(R.id.category_fab_item_register_Keywords,
                new Pair(R.drawable.ic_main_fab_keyword, R.string.MainActivity_FabItem_addKeywords)));*/
        subItems.add(new Pair(R.id.category_fab_item_uncategorize,
                new Pair(R.drawable.ic_category_fab_uncategory, R.string.CategoryActivity_uncategorize)));

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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.AlertDialog_askReallyCancel))
                .setPositiveButton(getString(R.string.AlertDialog_button_yes),
                        (dialog, which) -> {
                            Toast.makeText(this, getString(R.string.Global_canceled), Toast.LENGTH_SHORT).show();
                            super.onBackPressed();
                        })
                .setNegativeButton(getString(R.string.AlertDialog_button_no), null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.category_app_bar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.category_action_done:
                mCategory.name = mNameEditText.getText().toString();

                mBrainDBHandler.updateCategory(mCategory);
                for(Keyword keyword : mUpdatedKeywords) mBrainDBHandler.updateKeyword(keyword);

                Toast.makeText(this, getString(R.string.Global_updated), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return true;
    }

    private SpeedDialView.OnActionSelectedListener fabItemClickListener = actionItem -> {
        switch (actionItem.getId()) {
            case R.id.category_fab_item_uncategorize:
                for(int i = 0; i < mKeywords.size(); i++) {
                    Keyword keyword = mKeywords.get(i);
                    if(keyword.isSelected()) {
                        keyword.cid = Category.NOT_REGISTERED;
                        mUpdatedKeywords.add(keyword);
                        mKeywords.remove(i);
                        initRecyclerView();
                    }
                }
                break;
                /*
            case R.id.category_fab_item_register_Keywords:
                int i = 0;
                Keyword keyword = mKeywords.get(i);
                keyword.cid = categoryID;
                mKeywords.add(keyword);
                mUpdatedKeywords.add(keyword);
                initRecyclerView();
                break;
        */
        }
        return false;
    };
}