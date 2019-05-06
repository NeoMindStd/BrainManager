package std.neomind.brainmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import std.neomind.brainmanager.data.Category;
import std.neomind.brainmanager.data.Keyword;
import std.neomind.brainmanager.utils.BrainDBHandler;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Collections;

public class KeywordActivity extends AppCompatActivity {
    private static final String TAG = "KeywordActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private BrainDBHandler mBrainDBHandler;

    private ArrayList<Category> mCategories;
    private Keyword mKeyword;

    private PhotoView mPhotoView;
    private Button mAddPhotoButton;
    private MaterialEditText mNameEditText;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBrainDBHandler = new BrainDBHandler(this);

        mKeyword = new Keyword.Builder().build();
        mPhotoView = findViewById(R.id.keyword_photoView);

        mAddPhotoButton = findViewById(R.id.keyword_button_addPhoto);
        mAddPhotoButton.setOnClickListener(view -> {
            pickImage();
        });

        loadCategoriesFromDB();

        mNameEditText = findViewById(R.id.keyword_editText_name);

        mSpinner = findViewById(R.id.keyword_spinner_categories);
        mSpinner.setAdapter(new ArrayAdapter<>(
                this, R.layout.keyword_category_spinner_item, mCategories));
    }

    private void loadCategoriesFromDB() {
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
                new Category.Builder().setName(getString(R.string.KeywordActivity_none)).build());

        for (int i = 0; i < mCategories.size(); i++)
            Log.i(TAG, "loadCategoriesFromDB(): mCategories(" + i + ") - " + mCategories.get(i).toStringAbsolutely());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.keyword_app_bar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.keyword_action_cancel:
                Toast.makeText(this, getString(R.string.KeywordActivity_canceled), Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;

            case R.id.keyword_action_done:
                mKeyword.name = mNameEditText.getText().toString();
                mKeyword.cid = mCategories.get(mSpinner.getSelectedItemPosition()).id;
                mBrainDBHandler.addKeyword(mKeyword);

                Toast.makeText(this, getString(R.string.KeywordActivity_confirmed), Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
                ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
                String[] imagePath = new String[images.size()];
                for (int i = 0; i < images.size(); i++) imagePath[i] = images.get(i).getPath();

                if (imagePath.length > 0) {
                    mKeyword.imagePath = imagePath[0];
                    Glide.with(this)
                            .load(mKeyword.imagePath)
                            .into(mPhotoView);
                    mAddPhotoButton.setText("사진 변경");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void pickImage() {
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
}
