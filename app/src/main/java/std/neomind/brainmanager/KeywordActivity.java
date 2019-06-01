package std.neomind.brainmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import std.neomind.brainmanager.data.Category;
import std.neomind.brainmanager.data.Description;
import std.neomind.brainmanager.data.Keyword;
import std.neomind.brainmanager.utils.BrainDBHandler;
import std.neomind.brainmanager.utils.FileManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class KeywordActivity extends AppCompatActivity {
    private static final String TAG = "KeywordActivity";


    public static final int INTENT_MODE_REGISTER = 0;
    public static final int INTENT_MODE_VIEW = 1;
    public static final int INTENT_MODE_UPDATE = 2;
    public static final String EXTRAS_INTENT_MODE = "intentMode";
    public static final String EXTRAS_KEYWORD = "keyword";

    private static final int PICK_IMAGE_REQUEST = 1;

    private BrainDBHandler mBrainDBHandler;

    private int currentMode;
    private int keywordID;

    private ArrayList<Category> mCategories;
    private Keyword mKeyword;
    private String beforeImagePath;

    private PhotoView mPhotoView;
    private View mDivider;
    private Button mAddImageButton;
    private MaterialEditText mNameEditText;
    private Spinner mCategorySpinner;
    private Spinner mDescriptionSpinner;
    private Button mDescriptionAddButton;
    private Button mDescriptionUpdateButton;
    private Button mDescriptionDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword);
        Toolbar toolbar = findViewById(R.id.keyword_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBrainDBHandler = new BrainDBHandler(this);

        getBundleExtras();

        mPhotoView = findViewById(R.id.keyword_photoView);
        mDivider = findViewById(R.id.keyword_divider_photoView_editText);
        mAddImageButton = findViewById(R.id.keyword_button_addImage);
        mAddImageButton.setOnClickListener(view -> pickImage());

        mKeyword = loadKeywordFromDB(keywordID);
        loadCategoriesFromDB();

        mNameEditText = findViewById(R.id.keyword_editText_name);

        mCategorySpinner = findViewById(R.id.keyword_spinner_categories);
        mCategorySpinner.setAdapter(new ArrayAdapter<>(
                this, R.layout.global_spinner_item, mCategories));

        mDescriptionSpinner = findViewById(R.id.keyword_spinner_descriptions);
        initDescriptionSpinner();

        mDescriptionAddButton = findViewById(R.id.keyword_button_addDescription);
        mDescriptionAddButton.setOnClickListener(mCategoryButtonClickListener);

        mDescriptionUpdateButton = findViewById(R.id.keyword_button_updateDescription);
        mDescriptionUpdateButton.setOnClickListener(mCategoryButtonClickListener);

        mDescriptionDeleteButton = findViewById(R.id.keyword_button_deleteDescription);
        mDescriptionDeleteButton.setOnClickListener(mCategoryButtonClickListener);

        switch(currentMode) {
            case INTENT_MODE_REGISTER :
                getSupportActionBar().setTitle(R.string.KeywordActivity_titleRegister);

                mKeyword = new Keyword.Builder().build();
                break;

            case INTENT_MODE_UPDATE:
                getSupportActionBar().setTitle(R.string.KeywordActivity_titleUpdate);

                Log.i(TAG, "onCreate: mKeyword - " + mKeyword);

                if(!mKeyword.imagePath.isEmpty()) {
                    Glide.with(this)
                            .load(mKeyword.imagePath)
                            .into(mPhotoView);
                    mAddImageButton.setText(getString(R.string.KeywordActivity_updateImage));
                }

                mNameEditText.setText(mKeyword.name);

                for(int i = 0; i < mCategories.size(); i++) {
                    if(mCategories.get(i).id == mKeyword.cid) {
                        mCategorySpinner.setSelection(i);
                    }
                }
                break;

            case INTENT_MODE_VIEW :
                getSupportActionBar().setTitle(R.string.KeywordActivity_titleView);

                Log.i(TAG, "onCreate: mKeyword - " + mKeyword);

                mAddImageButton.setVisibility(View.GONE);
                if(!mKeyword.imagePath.isEmpty()) {
                    Glide.with(this)
                            .load(mKeyword.imagePath)
                            .into(mPhotoView);
                } else {
                    mPhotoView.setVisibility(View.GONE);
                    mDivider.setVisibility(View.GONE);
                }
                mDescriptionAddButton.setVisibility(View.GONE);
                mDescriptionUpdateButton.setVisibility(View.GONE);
                mDescriptionDeleteButton.setVisibility(View.GONE);

                findViewById(R.id.layoutKeywordContentsFields).
                        setFocusableInTouchMode(false); // mNameEditText's label 강조하기 위함
                mNameEditText.setText(mKeyword.name);
                mNameEditText.setInputType(0x00000000); // 0x00000000 = none
                mNameEditText.setShowClearButton(false);
                mNameEditText.setEnabled(true);
                mNameEditText.setHideUnderline(true);

                for(int i = 0; i < mCategories.size(); i++) {
                    if(mCategories.get(i).id == mKeyword.cid) {
                        mCategorySpinner.setSelection(i);
                    }
                }
                mCategorySpinner.setEnabled(false);
                mCategorySpinner.setBackground(null);
                break;
        }
    }

    private void getBundleExtras() {
        currentMode = getIntent().getExtras().getInt(EXTRAS_INTENT_MODE);
        keywordID = getIntent().getExtras().getInt(EXTRAS_KEYWORD, Keyword.NOT_REGISTERED);

        Log.i(TAG, "onCreate: currentMode - " + currentMode);
        Log.i(TAG, "onCreate: keywordID - " + keywordID);
    }

    private Keyword loadKeywordFromDB(int id) {
        Keyword keyword = null;
        try {
            keyword = mBrainDBHandler.findKeyword(BrainDBHandler.FIELD_KEYWORDS_ID, id);
            beforeImagePath = keyword.imagePath;
            Log.i(TAG, "loadKeywordFromDB: loaded keyword - " + keyword.toStringAbsolutely());
        } catch (BrainDBHandler.NoMatchingDataException e) { e.printStackTrace(); }
        mBrainDBHandler.close();

        return keyword;
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

    private void initDescriptionSpinner() {
        if(mKeyword != null) mDescriptionSpinner.setAdapter(new ArrayAdapter<>(
                this, R.layout.global_spinner_item, mKeyword.getDescriptions()));
    }

    private View.OnClickListener mCategoryButtonClickListener = view -> {
        final EditText editText = new EditText(this);
        switch (view.getId()) {
            case R.id.keyword_button_addDescription:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.KeywordActivity_addDescription))
                        .setView(editText)
                        .setPositiveButton(getString(R.string.Global_confirm),
                                (dialog, which) -> {
                                    Description description = new Description();
                                    description.description = editText.getText().toString();
                                    if (keywordID != Keyword.NOT_REGISTERED) {
                                        mBrainDBHandler.addDescription(description, keywordID);
                                        mKeyword.setDescriptions(
                                                mBrainDBHandler.getAllDescriptionsOfTheKeyword(keywordID));
                                    } else {
                                        mKeyword.getDescriptions().add(description);
                                    }
                                    initDescriptionSpinner();
                                })
                        .setNeutralButton(getString(R.string.Global_negative), null)
                        .show();
                break;
            case R.id.keyword_button_updateDescription:
                if (!mKeyword.getDescriptions().isEmpty()) {
                    Description description = mKeyword.getDescriptions().
                            get(mDescriptionSpinner.getSelectedItemPosition());
                    editText.setText(description.toString());
                    editText.setSelection(description.toString().length());
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.KeywordActivity_updateDescription))
                            .setView(editText)
                            .setPositiveButton(getString(R.string.Global_confirm),
                                    (dialog, which) -> {
                                        description.description = editText.getText().toString();
                                        if (keywordID != Keyword.NOT_REGISTERED) {
                                            mBrainDBHandler.updateDescription(description, keywordID);
                                            mKeyword.setDescriptions(
                                                    mBrainDBHandler.getAllDescriptionsOfTheKeyword(keywordID));
                                        }
                                        initDescriptionSpinner();
                                    })
                            .setNeutralButton(getString(R.string.Global_negative), null)
                            .show();
                }
                break;
            case R.id.keyword_button_deleteDescription:
                if (!mKeyword.getDescriptions().isEmpty()) {
                    if (keywordID != Keyword.NOT_REGISTERED) {
                        mBrainDBHandler.removeDescription(
                                mKeyword.getDescriptions().get(
                                        mDescriptionSpinner.getSelectedItemPosition()).id);
                        mKeyword.setDescriptions(
                                mBrainDBHandler.getAllDescriptionsOfTheKeyword(keywordID));
                    } else {
                        mKeyword.getDescriptions().remove(
                                mDescriptionSpinner.getSelectedItemPosition());
                    }
                    initDescriptionSpinner();
                }
                break;
        }
    };

    @Override
    public void onBackPressed() {
        if(currentMode == INTENT_MODE_VIEW)  super.onBackPressed();
        else {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.keyword_app_bar, menu);

        if(currentMode == INTENT_MODE_VIEW) menu.findItem(R.id.keyword_action_done).setVisible(false);
        else menu.findItem(R.id.keyword_action_done).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.keyword_action_done:
                mKeyword.name = mNameEditText.getText().toString();
                mKeyword.cid = mCategories.get(mCategorySpinner.getSelectedItemPosition()).id;

                File origin = new File(mKeyword.imagePath);
                File target = new File(getFilesDir(), System.currentTimeMillis() + "." + FileManager.getExtension(origin));

                boolean deleteFlag = getSharedPreferences(getString(R.string.
                        SharedPreferencesName), MODE_PRIVATE).getBoolean(getString(
                        R.string.SharedPreferences_deleteOriginalImage), false);

                Log.i(TAG, "onOptionsItemSelected: deleteFlag - " + deleteFlag);
                // 기본적으로 내부 저장소로 사진을 복사하되, 원본 이미지 삭제가 체크되면 이미지를 삭제한다.
                if (FileManager.moveFile(origin, target, deleteFlag)) {
                    mKeyword.imagePath = target.toString();
                    FileManager.refreshGallery(this, origin.toString());
                    Log.i(TAG, "onOptionsItemSelected: original image path - " + origin.toString());
                    Log.i(TAG, "onOptionsItemSelected: moved image path - " + mKeyword.imagePath);
                }

                // 만약 변경 전 사진이 내부 저장소에 위치해 있었다면 해당 사진 삭제
                if(FileManager.isInternalStorageFile(this, beforeImagePath))
                    Log.i(TAG, "onOptionsItemSelected: delete before image result - " +
                            FileManager.deleteFile(new File(beforeImagePath)));

                String infoHead = "";
                switch (currentMode) {
                    case INTENT_MODE_REGISTER :
                        mBrainDBHandler.addKeyword(mKeyword);
                        infoHead = getString(R.string.Global_added);
                        break;

                    case INTENT_MODE_UPDATE:
                        mBrainDBHandler.updateKeyword(mKeyword);
                        infoHead = getString(R.string.Global_updated);
                        break;
                }
                Log.d(TAG, "onOptionsItemSelected: " + infoHead + "- " + mKeyword.toStringAbsolutely());
                Toast.makeText(this, infoHead, Toast.LENGTH_SHORT).show();

                finish();
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
                    mAddImageButton.setText(getString(R.string.KeywordActivity_updateImage));
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
                //.setFolderTitle(getString(R.string.MainActivity_Albums))                              // Folder EXTRAS_TITLE (works with FolderMode = true)
                //.setImageTitle(getString(R.string.MainActivity_Gallery))                              // Image EXTRAS_TITLE (works with FolderMode = false)
                //.setDoneTitle(getString(R.string.MainActivity_Done))                                  // Done button EXTRAS_TITLE
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
