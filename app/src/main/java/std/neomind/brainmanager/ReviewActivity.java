package std.neomind.brainmanager;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.widget.EditText;

import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import androidx.gridlayout.widget.GridLayout;
import std.neomind.brainmanager.utils.BrainDBHandler;
import std.neomind.brainmanager.data.Keyword;

public class ReviewActivity extends AppCompatActivity {
    private int crrentType;
    private Context mContext;

    private ConstraintLayout keywordLayout;
    private ConstraintLayout descriptionLayout;
    private ArrayList<Keyword> mKeywords;

    private ConstraintLayout examAllLayout;
    private ConstraintLayout[] examLayoutArray;
    private

    int tMargin, lrMargin, bMargin, width, height, keyTextSize, desTextSize;
    Point device_size;

    ScrollView textExamScroll;
    GridLayout textExamLayout;
    EditText examTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mContext = this;
////실구현
        BrainDBHandler brainDBHandler = new BrainDBHandler(mContext);
        try {
            mKeywords = brainDBHandler.getAllKeywords();
            brainDBHandler.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* TODO 변경
        ////////////테스트
        //int item, int cid, String text, String imagePath, int currentLevels, int reviewTimes, String registrationDate
        Keyword.Builder b = new Keyword.Builder();
        b.setText("와!");
        Keyword tempKey = b.build();
        b.setText("으윽!");
        Keyword tempKey2 = b.build();
        mKeywords = new ArrayList<Keyword>();
        mKeywords.add(tempKey);
        mKeywords.add(tempKey2);
        ///////////////
        */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        crrentType = generate_view(mKeywords.get(0));
    }
    //examType은 0,1,2가 있다. 0.오류 1.기본형, 2.객관식문제형, 3.텍스트문제형
    //        this.text = text;
    //        this.imagePath = imagePath;
    public int generate_view(Keyword key){
        Random randomGenerator = new Random();
        int r = randomGenerator.nextInt(3)+1;
        ////테스트


        ////
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        //Log.d("device dpi", "=>" + metrics.densityDpi);
        Display display = getWindowManager().getDefaultDisplay();
        device_size = new Point();
        display.getSize(device_size);

        tMargin = (int)(metrics.densityDpi / 20 * metrics.densityDpi / 160f);
        bMargin = (int)(metrics.densityDpi / 50 * metrics.densityDpi / 160f);

        height = (int)(metrics.densityDpi / 3.2 * metrics.densityDpi / 160f);
        keyTextSize = (int)(metrics.densityDpi / 10);
        desTextSize = (int)(metrics.densityDpi / 20);


        //keywordText.setText("키워드"); //키워드가 들어감
        //ConstraintLayout.LayoutParams param = (ConstraintLayout.LayoutParams) keywordLayout.getLayoutParams();
        //param.setMargins(0, tMargin, 0, bMargin);

//        if(key.name.isEmpty() && ){
//            keywordText.setText("키워드를 만들어주세요");
//            return 0;
//        }
        //아직 db 미구현
//        if(key.key!=Null)
//
        //ConstraintLayout.LayoutParams param = (ConstraintLayout.LayoutParams) keywordLayout.getLayoutParams();
        //param.setMargins(0, tMargin, 0, bMargin);
        keywordLayout = findViewById(R.id.keword_layout);
        ((TextView) keywordLayout.getChildAt(0)).setText("Keyword");

        //keysize = key.name.size()
        //text인지 image인지 확인. db list 구현 아직 안됨
        if(key.name.isEmpty() && key.imagePath.isEmpty())
            return 0;
        while(true) {
            if (r == 1) {
                descriptionLayout = findViewById(R.id.description_layout);
                descriptionLayout.setVisibility(View.VISIBLE);
                descriptionLayout.getChildAt(0).setVisibility(View.VISIBLE);
                descriptionLayout.getChildAt(1).setVisibility(View.VISIBLE);
                ((TextView)descriptionLayout.getChildAt(0)).setText("정답을 생각해보세요");
                if (!key.imagePath.isEmpty()) {
                    ((ImageView) descriptionLayout.getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
                    //descriptionIamge 객체 내용지정(이미지파일)
                }
                return 1;
            } else if (r == 2 && mKeywords.size() > 1) {
                //객관식 문제를 제출하기 위해 설명들의 갯수를 셈. 관련성도 여기에 구현되어야함.
                int count = 0;
                for (Keyword tKey : mKeywords) {
                    //count += tkey.name.size(); //설명의 총 갯수를 구합니다.
                    if (!tKey.name.isEmpty()) count++;
                    if (!tKey.imagePath.isEmpty()) count++;
                    if (count > 3) {
                        count = 4;
                        break;
                    }
                }
                if(count<2) {
                    r = randomGenerator.nextInt(2)+1;
                    if(r==2) r++;
                    continue;
                }
                //객관식 문제 초기화
                examAllLayout = findViewById(R.id.exam_all_layout);
                final int examLayoutCount = examAllLayout.getChildCount();
                examLayoutArray = new ConstraintLayout[examLayoutCount];
                for (int i = 0; i < examLayoutCount; i++) {
                    examLayoutArray[i] = (ConstraintLayout) examAllLayout.getChildAt(i);
                }
                //객관식 문제 제출 총 4개 답안중에 하나 선택해서 정답을 골라야함.
                //case는 문제 갯수를 뜻하는 것이다. case 2,3만을 조건으로 잡고 default를 쓸 수 있겠지만 혹시모른 에러를 잡기위해 case 4 활용
                switch (count) {
                    //
                    case 2:
                        for (int j = 0; j < 3; j += 2) {
                            examLayoutArray[j].setVisibility(View.VISIBLE);
                            for (int i = 0; i < 2; i++) {
                                examLayoutArray[j].getChildAt(i).setVisibility(View.VISIBLE);
                            }
                        }
                        r = randomGenerator.nextInt(2);
                        if (r == 1) r++;
                        if (key.imagePath.isEmpty()) {
                            ((TextView) examLayoutArray[r].getChildAt(0)).setText(key.name);
                        } else
                            ((ImageView) examLayoutArray[r].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
                        break;
                    case 3:
                        for (int j = 0; j < 3; j++) {
                            examLayoutArray[j].setVisibility(View.VISIBLE);
                            for (int i = 0; i < 2; i++) {
                                examLayoutArray[j].getChildAt(i).setVisibility(View.VISIBLE);
                            }
                        }
                        r = randomGenerator.nextInt(3);
                        if (key.imagePath.isEmpty()) {
                            ((TextView) examLayoutArray[r].getChildAt(0)).setText(key.name);
                        } else
                            ((ImageView) examLayoutArray[r].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
                        break;
                    case 4:
                        for (int j = 0; j < 4; j++) {
                            examLayoutArray[j].setVisibility(View.VISIBLE);
                            for (int i = 0; i < 2; i++) {
                                examLayoutArray[j].getChildAt(i).setVisibility(View.VISIBLE);
                            }
                        }
                        r = randomGenerator.nextInt(4);
                        if (key.imagePath.isEmpty()) {
                            ((TextView) examLayoutArray[r].getChildAt(0)).setText(key.name);
                        } else
                            ((ImageView) examLayoutArray[r].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
                        break;
                    default:
                        break;
                }
                return 2;
            }
            //만약 r=2라도 사용자가 작성한 키워드의 갯수가 1개보다 작거나 같으면 r값을 바꿔 while문을 다시 시도한다.
            else if (r == 2 && mKeywords.size() <= 1){
                r = randomGenerator.nextInt(2)+1;
                if(r==2) r++;
                continue;
            }
            else {
                textExamScroll = findViewById(R.id.text_exam_scroll);
                textExamScroll.setVisibility(View.VISIBLE);
                textExamLayout = findViewById(R.id.text_exam);
                textExamLayout.setVisibility(View.VISIBLE);

                examTest = findViewById(R.id.test);
                examTest.setOnKeyListener(on_KeyEvent);
                return 3;
            }
        }

        //param.topMargin = tMargin;
        //keywordText.setLayoutParams(param);


        //ConstraintLayout.LayoutParams param2 = (ConstraintLayout.LayoutParams) descriptionLayout.getLayoutParams();
        //param2.setMargins(0, 0, 0, bMargin);
    }
    //설명을 적어서 맞추는 문제일 때 엔터키의 동작을 변경하는 이벤트 리스너
    private View.OnKeyListener on_KeyEvent = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    //엔터입력시 할일 처리
                    //없으면 입력 하지 않음.
                }
            }
            return false;
        }
    };
}
