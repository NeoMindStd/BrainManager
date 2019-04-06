package std.neomind.brainmanager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
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
import std.neomind.brainmanager.utils.KeywordAdapter;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener {

    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private BrainDBHandler mBrainDBHandler;

    private ArrayList<Category> mCategories;
    private ArrayList<Keyword> mKeywords;

    private Spinner mSpinner;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mKeywordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        FloatingActionButton fab = findViewById(R.id.main_fab);
        fab.setOnClickListener(fabClickListener);
        fab.setOnLongClickListener(fabLongClickListener);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSpinner = findViewById(R.id.spinner);
        mRecyclerView = findViewById(R.id.keyword_recycler_view);

        mBrainDBHandler = new BrainDBHandler(this);
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
            mCategories = mBrainDBHandler.getAllCategories();
            mCategories.add(Category.CATEGORY_ALL,
                    new Category.Builder().setName(getString(R.string.global_all)).build());

            mKeywords = mBrainDBHandler.getAllKeywords();
            for(int i = 0; i < mKeywords.size(); i++)
                mKeywords.get(i).setCardView( findViewById(R.id.keyword_card_view) );

            mBrainDBHandler.close();
            for(int i = 0; i < mCategories.size(); i++)
                Log.i(TAG, "loadDB(): mCategories(" + i + ") - " + mCategories.get(i).toStringAbsolutely());
            for(int i = 0; i < mKeywords.size(); i++)
                Log.i(TAG, "loadDB(): mKeywords(" + i + ") - " + mKeywords.get(i).toStringAbsolutely());

            mSpinner.setAdapter(new ArrayAdapter<>(
                    this, R.layout.spinner_item, mCategories));

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mKeywordAdapter = new ScaleInAnimationAdapter(
                    new AlphaInAnimationAdapter(
                            new KeywordAdapter(this, mKeywords)));
            mRecyclerView.setAdapter(mKeywordAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        loadDB();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        menuInflater.inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
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
        // Handle navigation view item clicks here.
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d(TAG, "onMenuItemClick: item :" + item);
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

    public void pickImage(Keyword requestImageReceiver) {
        mRequestImageReceiver = requestImageReceiver;

        ImagePicker.with(this)                                                                  // Initialize ImagePicker with activity or fragment context
                .setToolbarColor(getColorHexStringFromColors(R.color.colorPrimary))                     // Toolbar color
                .setStatusBarColor(getColorHexStringFromColors(R.color.colorPrimary))                   // StatusBar color (works with SDK >= 21  )
                .setToolbarTextColor(getColorHexStringFromColors(R.color.WHITE))                        // Toolbar text color (Title and Done button)
                .setToolbarIconColor(getColorHexStringFromColors(R.color.WHITE))                        // Toolbar icon color (Back and Camera button)
                //.setProgressBarColor(getColorHexStringFromColors(R.color.imagePickerProgressBarColor))  // ProgressBar color
                //.setBackgroundColor(getColorHexStringFromColors(R.color.imagePickerBackground))         // Background color
                .setCameraOnly(false)                                                                   // Camera mode
                .setMultipleMode(true)                                                                  // Select multiple images or single image
                .setFolderMode(true)                                                                    // Folder mode
                .setShowCamera(true)                                                                    // Show camera button
                //.setFolderTitle(getString(R.string.MainActivity_Albums))                                                                  // Folder title (works with FolderMode = true)
                //.setImageTitle(getString(R.string.MainActivity_Gallery))                                                                 // Image title (works with FolderMode = false)
                //.setDoneTitle(getString(R.string.MainActivity_Done))                                                                    // Done button title
                //.setLimitMessage(getString(R.string.MainActivity_MaximumSelection))                                          // Selection limit message
                .setMaxSize(1)                                                                         // Max images can be selected
                //.setSavePath(getString(R.string.APP_NAME))                                                         // Image capture folder name
                //.setSelectedImages(images)                                                              // Selected images
                .setAlwaysShowDoneButton(true)                                                          // Set always show done button fadeIn multiple mode
                .setRequestCode(PICK_IMAGE_REQUEST)                                                  // Set request code, default Config.RC_PICK_IMAGES
                .setKeepScreenOn(true)                                                                  // Keep screen on when selecting images
                .start();
    }

    private String getColorHexStringFromColors(int color) {
        return "#" + Integer.toHexString(getResources().getColor(color));
    }

    private View.OnClickListener fabClickListener = view -> {
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.MainActivity_addKeyword))
                .setView(editText)
                .setPositiveButton(getString(R.string.global_confirm),
                        (dialog, which) -> {
                            BrainDBHandler dbHandler = new BrainDBHandler(MainActivity.this);
                            Keyword resultKeyword = new Keyword.Builder()
                                    .setName(editText.getText().toString())
                                    .build();
                            dbHandler.addKeyword(resultKeyword);
                            mKeywords.add(resultKeyword);
                        })
                .setNeutralButton(getString(R.string.global_neutral), null)
                .show();
    };

    private View.OnLongClickListener fabLongClickListener = view -> {
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.MainActivity_addCategory))
                .setView(editText)
                .setPositiveButton(getString(R.string.global_confirm),
                        (dialog, which) -> {
                            BrainDBHandler dbHandler = new BrainDBHandler(MainActivity.this);
                            Category resultCategory = new Category.Builder().
                                    setName(editText.getText().toString())
                                    .build();
                            dbHandler.addCategory(resultCategory);
                            mCategories.add(resultCategory);
                        })
                .setNeutralButton(getString(R.string.global_neutral), null)
                .show();
        return true;
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
