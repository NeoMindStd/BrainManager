package std.neomind.brainmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import std.neomind.brainmanager.utils.BrainDBHandler;
import std.neomind.brainmanager.data.Category;
import std.neomind.brainmanager.data.Keyword;
import std.neomind.brainmanager.utils.KeywordRecyclerAdapter;
import std.neomind.brainmanager.utils.PermissionManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener {

    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

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
        if(!mPermissionManager.checkGranted()) mPermissionManager.request();
        initActivity();
    }

    private void initActivity() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mFab = findViewById(R.id.main_fab);
        mFab.setOnActionSelectedListener(faItemClickListener);
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

        loadPref();
        loadDB();
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

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar textView clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view textView clicks here.
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.nav_review :
                intent = new Intent(this, ReviewActivity.class);
                break;
            case R.id.nav_statistics :
                intent = new Intent(this, StatisticsActivity.class);
                break;
            case R.id.nav_relation :
                intent = new Intent(this, RelationActivity.class);
                break;
            case R.id.nav_settings :
                intent = new Intent(this, SettingsActivity.class);
                break;
        }
        try {
            startActivity(intent);
        } catch (Exception e) { e.printStackTrace(); }

        DrawerLayout drawer = findViewById(R.id.main_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick: textView :" + item);
        return false;
    }

    private static Keyword mRequestImageReceiver;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
                ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
                String[] imagePath = new String[images.size()];
                for (int i = 0; i < images.size(); i++) imagePath[i] = images.get(i).getPath();

                if (imagePath.length > 0) {
                    mRequestImageReceiver.imagePath = imagePath[0];
                    mBrainDBHandler.updateKeyword(mRequestImageReceiver);
                    refresh();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadPref() {

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

        for(int i = 0; i < mCategories.size(); i++)
            Log.i(TAG, "loadDB(): mCategories(" + i + ") - " + mCategories.get(i).toStringAbsolutely());
    }

    private void initSpinner() {
        mSpinner.setAdapter(new ArrayAdapter<>(
                this, R.layout.category_spinner_item, mCategories));
    }

    private void getKeywordsFromDB() {
        mKeywords = mBrainDBHandler.getAllKeywordsOfTheCategory(mSpinner.getSelectedItemPosition());
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

        for(int i = 0; i < mKeywords.size(); i++)
            mKeywords.get(i).setCardView( findViewById(R.id.keyword_card_view) );


        for(int i = 0; i < mKeywords.size(); i++)
            Log.i(TAG, "loadDB(): mKeywords(" + i + ") - " + mKeywords.get(i).toStringAbsolutely());
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mKeywordAdapter = new ScaleInAnimationAdapter(
                new AlphaInAnimationAdapter(
                        new KeywordRecyclerAdapter(this, mKeywords)));
        mRecyclerView.setAdapter(mKeywordAdapter);
    }

    private void refresh() {
        loadPref();
        loadDB();
    }

    public void pickImage(Keyword requestImageReceiver) {
        mRequestImageReceiver = requestImageReceiver;

        ImagePicker.with(this)                                                                  // Initialize ImagePicker with activity or fragment context
                .setToolbarColor(getColorHexStringFromColors(R.color.colorPrimary))                     // Toolbar color
                .setStatusBarColor(getColorHexStringFromColors(R.color.colorPrimary))                   // StatusBar color (works with SDK >= 21  )
                .setToolbarTextColor(getColorHexStringFromColors(R.color.WHITE))                        // Toolbar text color (Title and Done button)
                .setToolbarIconColor(getColorHexStringFromColors(R.color.WHITE))                        // Toolbar icon color (Back and Camera button)
                //.setProgressBarColor(getColorHexStringFromColors(R.color.imagePickerProgressBarColor))// ProgressBar color
                //.setBackgroundColor(getColorHexStringFromColors(R.color.imagePickerBackground))       // Background color
                .setCameraOnly(false)                                                                   // Camera mode
                .setMultipleMode(false)                                                                 // Select multiple images or single image
                .setMaxSize(1)                                                                          // Max images can be selected
                .setFolderMode(true)                                                                    // Folder mode
                .setShowCamera(true)                                                                    // Show camera button
                //.setFolderTitle(getString(R.string.MainActivity_Albums))                              // Folder title (works with FolderMode = true)
                //.setImageTitle(getString(R.string.MainActivity_Gallery))                              // Image title (works with FolderMode = false)
                //.setDoneTitle(getString(R.string.MainActivity_Done))                                  // Done button title
                //.setLimitMessage(getString(R.string.MainActivity_MaximumSelection))                   // Selection limit message
                //.setSavePath(getString(R.string.APP_NAME))                                            // Image capture folder name
                //.setSelectedImages(images)                                                            // Selected images
                .setAlwaysShowDoneButton(false)                                                         // Set always show done button fadeIn multiple mode
                .setRequestCode(PICK_IMAGE_REQUEST)                                                     // Set request code, default Config.RC_PICK_IMAGES
                .setKeepScreenOn(true)                                                                  // Keep screen on when selecting images
                .start();
    }

    private String getColorHexStringFromColors(int color) {
        return "#" + Integer.toHexString(getResources().getColor(color));
    }

    private void fabRemoveAllSubItems() {
        final int size = mFab.getActionItems().size();
        for (int i = size - 1; i >= 0; i--) mFab.removeActionItem(i);
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

    private SpeedDialView.OnActionSelectedListener faItemClickListener = actionItem -> {
        final EditText editText = new EditText(this);
        switch (actionItem.getId()) {
            case R.id.main_fab_item_register_Categories:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.MainActivity_addCategory))
                        .setView(editText)
                        .setPositiveButton(getString(R.string.AlertDialog_confirm),
                                (dialog, which) -> {
                                    Category resultCategory = new Category.Builder().
                                            setName(editText.getText().toString())
                                            .build();
                                    mBrainDBHandler.addCategory(resultCategory);
                                    try {
                                        resultCategory = mBrainDBHandler.findLastCategory();
                                        for(int i = 1; i < mCategories.size(); i++) {
                                            if(resultCategory.name.
                                                    compareToIgnoreCase(mCategories.get(i).name) < 0) {
                                                mCategories.add(i, resultCategory);
                                                initSpinner();
                                                break;
                                            }
                                        }
                                    } catch (BrainDBHandler.NoMatchingDataException e) { e.printStackTrace(); }
                                })
                        .setNeutralButton(getString(R.string.AlertDialog_neutral), null)
                        .show();
                break;
            case R.id.main_fab_item_register_Keywords:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.MainActivity_addKeyword))
                        .setView(editText)
                        .setPositiveButton(getString(R.string.AlertDialog_confirm),
                                (dialog, which) -> {
                                    Keyword resultKeyword = new Keyword.Builder()
                                            .setName(editText.getText().toString())
                                            .build();
                                    mBrainDBHandler.addKeyword(resultKeyword);
                                    try {
                                        resultKeyword = mBrainDBHandler.findLastKeyword();
                                        for(int i = 0; i < mKeywords.size(); i++) {
                                            if(resultKeyword.name.
                                                    compareToIgnoreCase(mKeywords.get(i).name) < 0) {
                                                mKeywords.add(i, resultKeyword);
                                                initRecyclerView();
                                                break;
                                            }
                                        }
                                    } catch (BrainDBHandler.NoMatchingDataException e) { e.printStackTrace(); }
                                })
                        .setNeutralButton(getString(R.string.AlertDialog_neutral), null)
                        .show();
                break;
        }
        return false;
    };

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
