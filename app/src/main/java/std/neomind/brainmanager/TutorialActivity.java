package std.neomind.brainmanager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.matthewtamlin.android_utilities_library.helpers.BitmapEfficiencyHelper;
import com.matthewtamlin.android_utilities_library.helpers.ScreenSizeHelper;
import com.matthewtamlin.sliding_intro_screen_library.background.BackgroundManager;
import com.matthewtamlin.sliding_intro_screen_library.background.ColorBlender;
import com.matthewtamlin.sliding_intro_screen_library.buttons.IntroButton;
import com.matthewtamlin.sliding_intro_screen_library.buttons.IntroButtonAccessor;
import com.matthewtamlin.sliding_intro_screen_library.core.IntroActivity;
import com.matthewtamlin.sliding_intro_screen_library.pages.ParallaxPage;
import com.matthewtamlin.sliding_intro_screen_library.transformers.MultiViewParallaxTransformer;

import java.util.ArrayList;
import java.util.Collection;

public class TutorialActivity extends IntroActivity {
    private static final int[] BACKGROUND_COLORS = {0xff33B7A9, 0xff026D67, 0xff025E73, 0xff468535};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureTransformer();
        configureBackground();

        final IntroButtonAccessor leftButtonAccessor = getLeftButtonAccessor();
        leftButtonAccessor.setText(getString(R.string.TutorialActivity_skip), null);
        final IntroButtonAccessor finalButtonAccessor = getFinalButtonAccessor();
        finalButtonAccessor.setText(getString(R.string.TutorialActivity_done), null);
    }


    @Override
    protected Collection<? extends Fragment> generatePages(Bundle savedInstanceState) {
        final ArrayList<Fragment> pages = new ArrayList<>();

        // Get the screen dimensions so that Bitmaps can be loaded efficiently
        final int screenWidth = ScreenSizeHelper.getScreenWidthPx(this);
        final int screenHeight = ScreenSizeHelper.getScreenHeightPx(this);

        // Load the Bitmap resources into memory
        final Bitmap[] frontBitmapArray = new Bitmap[4];
        final Bitmap[] backBitmapArray = new Bitmap[4];

        frontBitmapArray[0] = BitmapEfficiencyHelper.decodeResource(this, R.raw.front_start_tuto,
                screenWidth, screenHeight);
        frontBitmapArray[1] = BitmapEfficiencyHelper.decodeResource(this, R.raw.front_main_tuto,
                screenWidth, screenHeight);
        frontBitmapArray[2] = BitmapEfficiencyHelper.decodeResource(this, R.raw.front_review_tuto,
                screenWidth, screenHeight);
        frontBitmapArray[3] = BitmapEfficiencyHelper.decodeResource(this, R.raw.front_relation_tuto,
                screenWidth, screenHeight);

        backBitmapArray[0] = BitmapEfficiencyHelper.decodeResource(this, R.raw.back_start_tuto,
                screenWidth, screenHeight);
        backBitmapArray[1] = BitmapEfficiencyHelper.decodeResource(this, R.raw.back_main_tuto,
                screenWidth, screenHeight);
        backBitmapArray[2] = BitmapEfficiencyHelper.decodeResource(this, R.raw.back_review_tuto,
                screenWidth, screenHeight);
        backBitmapArray[3] = BitmapEfficiencyHelper.decodeResource(this, R.raw.back_relation_tuto,
                screenWidth, screenHeight);

        // Create as many pages as there are background colors
        for (int i = 0; i < BACKGROUND_COLORS.length; i++) {
            final ParallaxPage newPage = ParallaxPage.newInstance();
            newPage.setFrontImage(frontBitmapArray[i]);
            newPage.setBackImage(backBitmapArray[i]);
            pages.add(newPage);
        }

        return pages;
    }

    //
//    private boolean introductionCompletedPreviously() {
//        final SharedPreferences sp = getSharedPreferences(getString(R.string.SharedPreferencesName), MODE_PRIVATE);
//        return sp.getBoolean(getString(R.string.SharedPreferences_firstStart), false);
//    }
    @Override
    protected IntroButton.Behaviour generateFinalButtonBehaviour() {
        /* 완료버튼을 누르면 쉐어프리퍼런스를 갱신함. 그리고 현재 액티비티를 종료.        */
        final SharedPreferences sp = getSharedPreferences(getString(R.string.
                SharedPreferencesName), MODE_PRIVATE);
        final SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(getString(R.string.
                SharedPreferences_firstStart), false);
        return new DoFinish(pendingEdits);
    }

    @Override
    public void onBackPressed() {
        //Do nothing.
    }

    private void configureTransformer() {
        final MultiViewParallaxTransformer transformer = new MultiViewParallaxTransformer();
        transformer.withParallaxView(R.id.page_fragment_imageHolderFront, 1.2f);
        setPageTransformer(false, transformer);
    }

    private void configureBackground() {
        final BackgroundManager backgroundManager = new ColorBlender(BACKGROUND_COLORS);
        setBackgroundManager(backgroundManager);
    }

    public static final class DoFinish extends IntroButton.BehaviourAdapter {
        private final SharedPreferences.Editor editsToMake;

        public DoFinish(final SharedPreferences.Editor editsToMake) {
            this.editsToMake = editsToMake;
        }

        @Override
        public final void run() {
            if (getActivity() != null) {
                if (editsToMake != null) {
                    editsToMake.apply();
                }
                getActivity().finish();
            }
        }
    }
}
