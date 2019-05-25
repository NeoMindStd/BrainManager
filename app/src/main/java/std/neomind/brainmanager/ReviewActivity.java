package std.neomind.brainmanager;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.KeyEvent;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.snackbar.Snackbar;

import std.neomind.brainmanager.data.Description;
import std.neomind.brainmanager.data.Test;
import std.neomind.brainmanager.utils.BrainDBHandler;
import std.neomind.brainmanager.data.Keyword;

public class ReviewActivity extends AppCompatActivity{
    static final int EXAM_LAYOUT_COUNT = 4;
    public static final String EXTRAS_KEYWORD = "keyword";

    private int currentExamType;    //0이면 오류 1이면 기본문제. 2면 객관식문제 3이면 설명빈칸입력문제
    private Context mContext;

    private ArrayList<Keyword> mTargetKeywords;
    private int currentmKeyIndex;
    private int mKeywordsSize;

    private ArrayList<Keyword> allKeywords;
    private int allKeySize;

    private ConstraintLayout examAllLayout;
    private ConstraintLayout[] examLayoutArray;
    private int tMargin, lrMargin, bMargin, width, height, keyTextSize, desTextSize;
    Point device_size;

    private int selectedExam;   //-1일 때는 선택x상태
    private int answer;
    private String answerString;
    private long examStartTime;
    private long examEndTime;

    private ScrollView textExamScroll;
    private GridLayout textExamLayout;
    private EditText examText;

    private ConstraintLayout keywordLayout;
    private ConstraintLayout descriptionLayout;

    private Button nextButton;
    private Button AnswerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mContext = this;
        examAllLayout = findViewById(R.id.review_layout_examAll);
        descriptionLayout = findViewById(R.id.review_layout_description);
        textExamScroll = findViewById(R.id.review_scroll_exam);
////
        ArrayList<Integer> listID = new ArrayList<>();
        listID = (ArrayList<Integer>) getIntent().getSerializableExtra(EXTRAS_KEYWORD);



        if(listID.isEmpty()){
            finish();
            Toast.makeText(getApplicationContext(), getString(R.string.ReviewActivity_noKeyword), Toast.LENGTH_LONG).show();
        }
        else {
            BrainDBHandler brainDBHandler = new BrainDBHandler(mContext);
            try {
                allKeywords = brainDBHandler.getAllKeywords();
                brainDBHandler.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mTargetKeywords = new ArrayList<Keyword>();
            for(Keyword key : allKeywords){
                if(listID.contains(key.id))
                    mTargetKeywords.add(key);
            }

            allKeySize = allKeywords.size();
            mKeywordsSize = mTargetKeywords.size();

            Collections.sort(mTargetKeywords, (o1, o2) -> {
                String criteria1, criteria2;
                int compareResult;
                criteria1 = o1.name;
                criteria2 = o2.name;
                compareResult = criteria1.compareToIgnoreCase(criteria2);

                return compareResult;
            });

            ArrayList<Integer> ckList = new ArrayList<>();
            ArrayList<Integer> relationList;
            int pos = 0;
            for(Keyword key : mTargetKeywords){
                if(pos == mTargetKeywords.size()) break;
                ckList.add(key.id);
                relationList = key.getRelationIds();
                for(int rid : relationList){
                    if(!ckList.contains(rid)) {
                        for(Keyword tempKey : mTargetKeywords){
                            if(rid == tempKey.id) {
                                Collections.swap(mTargetKeywords, pos + 1, mTargetKeywords.indexOf(tempKey));
                                break;
                            }
                        }
                        break;
                    }
                }
                pos++;
            }
            ckList.clear();

            nextButton = findViewById(R.id.review_button_nextExam);
            AnswerButton = findViewById(R.id.review_button_showAnswer);
            AnswerButton.setOnClickListener(onAnswerViewClickEvent);

            /* ENDDO 변경
            ////////////테스트
            //int textView, int cid, String text, String imagePath, int currentLevels, int reviewTimes, String registrationDate
            Keyword.Builder b = new Keyword.Builder();
            b.setText("와!");
            Keyword tempKey = b.build();
            b.setText("으윽!");
            Keyword tempKey2 = b.build();
            mTargetKeywords = new ArrayList<Keyword>();
            mTargetKeywords.add(tempKey);
            mTargetKeywords.add(tempKey2);
            ///////////////
            */

            Toolbar toolbar = findViewById(R.id.main_toolbar);
            setSupportActionBar(toolbar);
            currentmKeyIndex = 0;
            currentExamType = generate_view(mTargetKeywords.get(currentmKeyIndex));   //TODO 0가 리턴될 경우 오류처리 표시 해야됨.
        }
    }
    //examType은 0,1,2가 있다. 0.오류 1.기본형, 2.객관식문제형, 3.텍스트문제형
    //        this.text = text;
    //        this.imagePath = imagePath;
    private int generate_view(Keyword key){
        selectedExam = -1;
        Random randomGenerator = new Random();
        int r = randomGenerator.nextInt(3)+1;
        ////테스트
        int keyIndex = allKeywords.indexOf(key);

        ////
//        DisplayMetrics metrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//        windowManager.getDefaultDisplay().getMetrics(metrics);
//        //Log.d("device dpi", "=>" + metrics.densityDpi);
//        Display display = getWindowManager().getDefaultDisplay();
//        device_size = new Point();
//        display.getSize(device_size);
//
//        tMargin = (int)(metrics.densityDpi / 20 * metrics.densityDpi / 160f);
//        bMargin = (int)(metrics.densityDpi / 50 * metrics.densityDpi / 160f);
//
//        height = (int)(metrics.densityDpi / 3.2 * metrics.densityDpi / 160f);
//        keyTextSize = (int)(metrics.densityDpi / 10);
//        desTextSize = (int)(metrics.densityDpi / 20);


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

        //레이아웃 참조를 가져올 지역변수.


        keywordLayout = findViewById(R.id.review_layout_keyword);
        for(int i=0; i<keywordLayout.getChildCount(); i++)
            keywordLayout.getChildAt(i).setVisibility(View.GONE);

        //VISIBLE 초기화

        examAllLayout.setVisibility(View.GONE);
        descriptionLayout.setVisibility(View.GONE);
        textExamScroll.setVisibility(View.GONE);
        ((EditText) descriptionLayout.getChildAt(0)).setTextAppearance(R.style.keywordTheme);
        descriptionLayout.getChildAt(0).setEnabled(true);
        descriptionLayout.getChildAt(0).setClickable(true);
        descriptionLayout.getChildAt(0).setFocusable(true);
        descriptionLayout.getChildAt(0).setFocusableInTouchMode(true);
        ((EditText) descriptionLayout.getChildAt(0)).setCursorVisible(true);
        descriptionLayout.getChildAt(0).requestFocus();
        descriptionLayout.getChildAt(0).invalidate();
        ((EditText) descriptionLayout.getChildAt(0)).setText("");


        //keysize = key.name.size()
        if(key.name.isEmpty() || (key.imagePath.isEmpty() && key.getDescriptions().isEmpty())) {
            //TODO 코드 중복 존재함.
            if(++currentmKeyIndex < mKeywordsSize) currentExamType = generate_view(mTargetKeywords.get(currentmKeyIndex));
            else{
                finish();
                Toast.makeText(getApplicationContext(), getString(R.string.ReviewActivity_lastKeyword), Toast.LENGTH_LONG).show();
            }
            return currentExamType;
        }
        while(true) {
            if (r == 1) {
               answerString = key.name;

                descriptionLayout.setVisibility(View.VISIBLE);
                if (key.imagePath.isEmpty()) {
                    String descriptionString ="";
                    for(Description s : key.getDescriptions()){
                        descriptionString += s.description + "\n";
                    }
                    descriptionString = descriptionString.substring(0, descriptionString.length()-1);

                    keywordLayout.getChildAt(0).setVisibility(View.VISIBLE);
                    keywordLayout.getChildAt(1).setVisibility(View.GONE);
                    ((TextView)keywordLayout.getChildAt(0)).setText(descriptionString);
                    //keywordLayout.setOnLongClickListener();
                    keywordLayout.getChildAt(0).setOnLongClickListener(longClickListener);
                }else{
                    keywordLayout.getChildAt(0).setVisibility(View.GONE);
                    keywordLayout.getChildAt(1).setVisibility(View.VISIBLE);
                    ((ImageView) keywordLayout.getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
                    //descriptionImage 객체 내용지정(이미지파일)
                    keywordLayout.getChildAt(1).setOnLongClickListener(longClickListener);
                }
                descriptionLayout.getChildAt(0).setVisibility(View.VISIBLE);
                descriptionLayout.getChildAt(0).setOnKeyListener(onKeyEvent);

                examStartTime = System.currentTimeMillis(); //문제의 시작시간을 저장
                return 1;
            } else if (r == 2 && mKeywordsSize > 1) {
                keywordLayout.getChildAt(0).setVisibility(View.VISIBLE);
                ((TextView) keywordLayout.getChildAt(0)).setText(key.name);
                examAllLayout.setVisibility(View.VISIBLE);
                //객관식 문제를 제출하기 위해 설명들의 갯수를 셈. 관련성도 여기에 구현되어야함.
                int count = 0; //count가 될 수 있는 수는 2 3 4
                for (Keyword tKey : allKeywords) {
                    //count += tkey.name.size(); //설명의 총 갯수를 구합니다.
                    if (!tKey.getDescriptions().isEmpty()) count++;
                    if (!tKey.imagePath.isEmpty()) count++;
                    if (count > 4) {
                        count = 4;
                        break;
                    }
                }
                if(count<2) {   //설명의 갯수가 총 2개가 안된다면 다시 r=2가 않게 while문을 돌림
                    r = randomGenerator.nextInt(2)+1;
                    if(r==2) r++;
                    continue;
                }
                //객관식 문제 초기화. TODO examAllLayout이 한번밖에 안쓰인다면 이를 짧게 수정해야됨. 혹은 확장성을 위해 그대로 두거나

                examLayoutArray = new ConstraintLayout[EXAM_LAYOUT_COUNT];
//                for (int i = 0; i < EXAM_LAYOUT_COUNT; i++) {
//                    examLayoutArray[i] = (ConstraintLayout) examAllLayout.getChildAt(i);
//                    examLayoutArray[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_rounded));
//                }
                //객관식 문제 제출 총 4개 답안중에 하나 선택해서 정답을 골라야함.
                //case는 문제 갯수를 뜻하는 것이다. case 2,3만을 조건으로 잡고 default를 쓸 수 있겠지만 혹시모른 에러를 잡기위해 case 4 활용
                r = randomGenerator.nextInt(count);
                answer = r; // 문제 정답 번호와 answer을 일치시킴

                //랜덤한 count개의 정답이 아닌 보기 //TODO 관련성 기반으로 보기가 주어지는 경우도 정의해야함.
                int tempRandom[] = new int[count - 1];
                Arrays.fill(tempRandom, -1);

                ArrayList<Integer> relationList = mTargetKeywords.get(currentmKeyIndex).getRelationIds();
                ArrayList<Integer> relationIndexs = new ArrayList<>();
                for(Keyword tKey : allKeywords){
                    for(int id : relationList){
                        if(id == tKey.id) {
                            relationIndexs.add(allKeywords.indexOf(tKey));
                            break;
                        }
                    }
                }

                int sizeCount = 0;
                int tempRandomRel[] = new int[tempRandom.length];
                for(int i=0; i<tempRandomRel.length; i++){
                    tempRandomRel[i] = randomGenerator.nextInt(tempRandom.length);
                    for(int j=0; j<i; j++) {
                        if(tempRandomRel[i] == tempRandomRel[j]) i--;
                    }
                }
                for(int i=0; i<relationIndexs.size(); i++) {
                    tempRandom[tempRandomRel[i]] = relationIndexs.get(randomGenerator.nextInt(relationIndexs.size()));
                }

                for(int i=0; i<tempRandom.length; i++) {
                    while (true) {
                        if(tempRandom[i] > -1) break;   //이미 값이 채워져 있으면 패스
                        boolean ck = true;
                        tempRandom[i] = randomGenerator.nextInt(allKeySize);
                        if(tempRandom[i] == keyIndex){
                            tempRandom[i] = -1;
                            continue;
                        } //정답인 키 인덱스와 값이 같으면 안됨.
                        Keyword tKey = allKeywords.get(tempRandom[i]);
                        if(tKey.getDescriptions().isEmpty() && tKey.imagePath.isEmpty()) {
                            tempRandom[i] = -1;
                            continue;
                        }//키워드 내부에 설명이 아무것도 없으면 안됨.
                        for (int j = 0; j < tempRandom.length; j++) {           //원래 있던 값과 값이 같으면 안됨.
                            if(i == j) continue;
                            if (tempRandom[i] == tempRandom[j]) {
                                ck = false;
                                tempRandom[i] = -1;
                                break;
                            }
                        }
                        if (ck) break;
                    }
                }

                for(int i = 0, j = 0; i< EXAM_LAYOUT_COUNT; i++) {
                    examLayoutArray[i] = (ConstraintLayout) examAllLayout.getChildAt(i);
                    examLayoutArray[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_rounded));
                    examLayoutArray[i].setVisibility(View.VISIBLE);
                    if(count-1 < i) {   //사용가능한 문제 개수-1 보다 i가 클경우
//                        examLayoutArray[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_gray_rounded));
                        continue;
                    }
                    if(r==i) {
                        if (key.imagePath.isEmpty()) {
                            String descriptionString ="";
                            for(Description s : key.getDescriptions()){
                                descriptionString += s.description + "\n";
                            }
                            descriptionString = descriptionString.substring(0, descriptionString.length()-1);
                            examLayoutArray[i].getChildAt(0).setVisibility(View.VISIBLE);
                            examLayoutArray[i].getChildAt(1).setVisibility(View.GONE);
                            ((TextView) examLayoutArray[i].getChildAt(0)).setText(descriptionString);
                            examLayoutArray[i].getChildAt(0).setOnLongClickListener(longClickListener);
                            ExamClickListener examClickEvent = new ExamClickListener(i);
                            examLayoutArray[i].getChildAt(0).setOnClickListener(examClickEvent);
                        } else {
                            examLayoutArray[i].getChildAt(0).setVisibility(View.GONE);
                            examLayoutArray[i].getChildAt(1).setVisibility(View.VISIBLE);
                            ((ImageView) examLayoutArray[i].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
                            examLayoutArray[i].getChildAt(1).setOnLongClickListener(longClickListener);
                            ExamClickListener examClickEvent = new ExamClickListener(i);
                            examLayoutArray[i].getChildAt(1).setOnClickListener(examClickEvent);
                        }
                    }else{
                        Keyword examTemp = allKeywords.get(tempRandom[j++]);
                        if (examTemp.imagePath.isEmpty()) {
                            String descriptionString ="";
                            for(Description s : examTemp.getDescriptions()){
                                descriptionString += s.description + "\n";
                            }
                            descriptionString = descriptionString.substring(0, descriptionString.length()-1);
                            examLayoutArray[i].getChildAt(0).setVisibility(View.VISIBLE);
                            examLayoutArray[i].getChildAt(1).setVisibility(View.GONE);
                            ((TextView) examLayoutArray[i].getChildAt(0)).setText(descriptionString);
                            examLayoutArray[i].getChildAt(0).setOnLongClickListener(longClickListener);
                            ExamClickListener examClickEvent = new ExamClickListener(i);
                            examLayoutArray[i].getChildAt(0).setOnClickListener(examClickEvent);
                        } else {
                            examLayoutArray[i].getChildAt(0).setVisibility(View.GONE);
                            examLayoutArray[i].getChildAt(1).setVisibility(View.VISIBLE);
                            ((ImageView) examLayoutArray[i].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(examTemp.imagePath));
                            examLayoutArray[i].getChildAt(1).setOnLongClickListener(longClickListener);
                            ExamClickListener examClickEvent = new ExamClickListener(i);
                            examLayoutArray[i].getChildAt(1).setOnClickListener(examClickEvent);
                        }
                    }
                }
//Constraint 를 활용한 객관식Size를 바꿔버리는 방식의 코드 회색 박스가 아니라 사이즈가 변경된다.
//                switch (count) {
//                    //
//                    case 2:
//                        r = randomGenerator.nextInt(2);
//                        if (r == 1) r++;
//                        for (int j = 0; j < 3; j += 2) {
//                            examLayoutArray[j].setVisibility(View.VISIBLE);
//                            for (int i = 0; i < 2; i++) {
//                                examLayoutArray[j].getChildAt(i).setVisibility(View.VISIBLE);
//                            }
//                            if (key.imagePath.isEmpty()) {
//                                examLayoutArray[j].getChildAt(0).setVisibility(View.VISIBLE);
//                                if(r==j) ((TextView) examLayoutArray[r].getChildAt(0)).setText(key.name);
//                            } else {
//                                examLayoutArray[j].getChildAt(1).setVisibility(View.VISIBLE);
//                                if(r==j) ((ImageView) examLayoutArray[r].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
//                            }
//                        }
//
//
//                        break;
//                    case 3:
//                        for (int j = 0; j < 3; j++) {
//                            examLayoutArray[j].setVisibility(View.VISIBLE);
//                            for (int i = 0; i < 2; i++) {
//                                examLayoutArray[j].getChildAt(i).setVisibility(View.VISIBLE);
//                            }
//                        }
//                        r = randomGenerator.nextInt(3);
//                        if (key.imagePath.isEmpty()) {
//                            ((TextView) examLayoutArray[r].getChildAt(0)).setText(key.name);
//                        } else
//                            ((ImageView) examLayoutArray[r].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
//                        break;
//                    case 4:
//                        for (int j = 0; j < 4; j++) {
//                            examLayoutArray[j].setVisibility(View.VISIBLE);
//                            for (int i = 0; i < 2; i++) {
//                                examLayoutArray[j].getChildAt(i).setVisibility(View.VISIBLE);
//                            }
//                        }
//                        r = randomGenerator.nextInt(4);
//                        if (key.imagePath.isEmpty()) {
//                            ((TextView) examLayoutArray[r].getChildAt(0)).setText(key.name);
//                        } else
//                            ((ImageView) examLayoutArray[r].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
//                        break;
//                    default:
//                        break;
//                }
                examStartTime = System.currentTimeMillis(); //문제의 시작시간을 저장
                return 2;
            }
            //만약 r=2라도 사용자가 작성한 키워드의 갯수가 1개보다 작거나 같으면 r값을 바꿔 while문을 다시 시도한다.
            else if (r == 2){
                r = randomGenerator.nextInt(2)+1;
                if(r==2) r++;
                continue;
            }
            //키워드에 설명이 없으면 빈칸은 만들 수 없으므로 r값을 3보다 작은 수로 while문을 다시 시도.
            else if (key.getDescriptions().isEmpty()){
                r = randomGenerator.nextInt(2)+1;
                continue;
            }
            else {  //r=3일 때를 뜻함.
                //TODO 설명부를 랜덤한 부분을 빈칸으로 만드는 코드 필요
                keywordLayout.getChildAt(0).setVisibility(View.VISIBLE);
                ((TextView) keywordLayout.getChildAt(0)).setText(key.name);
//                textExamScroll = findViewById(R.id.review_scroll_exam);

                ArrayList<String> descriptionString = new ArrayList<String>();
                for(Description s : key.getDescriptions()){
                    descriptionString.add(s.description);
                }
                //descriptionString = descriptionString.substring(0, descriptionString.length()-1);
                r = randomGenerator.nextInt(descriptionString.size());  //r이 재활용되어진다. 재반복되면주의

                TextView[] tempTextView = new TextView[descriptionString.size()];
                for(int i=0; i<tempTextView.length; i++) {
                    tempTextView[i] = new TextView(this);
                    tempTextView[i].setText(descriptionString.remove(0));
                    tempTextView[i].setTextAppearance(R.style.textExamTheme);
                }

                String targetStr = tempTextView[r].getText().toString();

//                int cnt = 0;
//                str = str.trim();               //앞뒤 빈칸 제거
//                Boolean checkEmpty = false;     //빈칸만으로 이루어져있는지 검사
//                for (int i = 0; i < str.length(); i++) {
//                    if (str.charAt(i) == ' ' && str.charAt(i - 1) != ' ') {
//                        //띄어쓰기를 찾고 띄어쓰기 바로 앞의 문자가 빈칸이 아닌경우
//                        cnt++;
//                    }else
//                        checkEmpty=true;
//                }
//                cnt = checkEmpty ? cnt+1 : cnt;
                String[] arrayStr = targetStr.split(" ");
                final int arrayStrLength = arrayStr.length;
                int r2 = randomGenerator.nextInt(arrayStr.length);
                TextView tempTextView2;
                tempTextView2 = new TextView(this);
                answerString = arrayStr[r2];

                String sumStr = "";
                if(r2 == 0){
                    for(int i = 1; i<arrayStrLength;  i++)
                        sumStr += arrayStr[i];
                    tempTextView[r].setText(sumStr);
                } else if(r2 == arrayStrLength - 1){
                    for(int i = 0; i<arrayStrLength - 1;  i++)
                        sumStr += arrayStr[i];
                    tempTextView[r].setText(sumStr);
                }
                else{
                    for(int i = 0; i < r;  i++)
                        sumStr += arrayStr[i];
                    tempTextView[r].setText(sumStr);
                    sumStr = "";
                    for(int i = r + 1; i < arrayStrLength;  i++)
                        sumStr += arrayStr[i];
                    tempTextView2.setText(sumStr);
                    tempTextView2.setTextAppearance(R.style.textExamTheme);
                }
                TextView emptyText = new TextView(this);

                textExamScroll.setVisibility(View.VISIBLE);
                textExamLayout = findViewById(R.id.review_gridLayout_exam);
                textExamLayout.setVisibility(View.VISIBLE);

                examText = new EditText(this);
                examText.setTextAppearance(R.style.textExamTheme);
                examText.setSingleLine(true);
                examText.setLines(1);
                examText.setOnKeyListener(onKeyEvent);

                for(int i=0; i<tempTextView.length; i++){
                    if(i==r) {
                        if (r2 == 0) {
                            textExamLayout.addView(examText);
                            textExamLayout.addView(tempTextView[i]);
                            textExamLayout.addView(emptyText);
                        } else if (r2 == arrayStrLength - 1) {
                            textExamLayout.addView(tempTextView[i]);
                            textExamLayout.addView(examText);
                            textExamLayout.addView(emptyText);
                        } else {
                            textExamLayout.addView(tempTextView[i]);
                            textExamLayout.addView(examText);
                            textExamLayout.addView(tempTextView2);
                        }
                        continue;
                    }
                    textExamLayout.addView(tempTextView[i]);
                    textExamLayout.addView(emptyText);
                    textExamLayout.addView(emptyText);
                }
                examStartTime = System.currentTimeMillis(); //문제의 시작시간을 저장
                return 3;
            }
        }

        //param.topMargin = tMargin;
        //keywordText.setLayoutParams(param);


        //ConstraintLayout.LayoutParams param2 = (ConstraintLayout.LayoutParams) descriptionLayout.getLayoutParams();
        //param2.setMargins(0, 0, 0, bMargin);
    }
    // 분단위로 다음 복습시간을 반환함.
    private long getReviewTime(Keyword key, int answerTime, boolean passed){
        ArrayList<Integer> keyRelList =  key.getRelationIds();
        int relCount = 0;
        for(int i = currentmKeyIndex-1; i > -1; i--) {
            boolean breakCk = true;
            for(Integer relID : keyRelList) {
                if(mTargetKeywords.get(i).id == relID) {
                    relCount++;
                    breakCk = false;
                }
            }
            if(breakCk){
                break;
            }
        }
        if(currentExamType == 2){
            BrainDBHandler dbHandler = new BrainDBHandler(this);
            Keyword k;
            int i = 0;
            for(Integer relID : keyRelList) {
                try {
                    k = dbHandler.findKeyword(BrainDBHandler.FIELD_CATEGORIES_ID, relID);
                    if(!k.imagePath.isEmpty() && !k.getDescriptions().isEmpty()) {
                        relCount++;
                        i++;
                    }
                }
                catch(BrainDBHandler.NoMatchingDataException e){
                    e.printStackTrace();
                }
                if(i == 4) break;
            }
            dbHandler.close();
        }
        int rating;
        int lastInterval = key.interval;
        int nextInterval;
        double ef;
        double relation = Math.pow(1.07 , relCount);
        relation = relation > 1.5 ? 1.5 : relation;


//        Date collectReviewDate;
        int diffInterval;

        //SM-2과는 조금 다르게 5가지 변수 사용.
        if(answerTime < 10000 && passed) rating = 5;        //반응이 즉각적인 맞는 응답.
        else if(answerTime < 30000 && passed) rating = 4;   //반응이 느린 맞는 응답
        else if(answerTime < 60000 && passed) rating = 3;   //반응이 심각히 느린 맞는 응답
        else if(answerTime < 10000) rating = 2;             //빨리 풀었지만 오답
        else rating = 0;                                    //완전히 잊음

        if(rating < 2)
            key.currentLevels = 0;

        if(key.reviewTimes == 0){
            ef = 2.5;
            diffInterval = 0;
        }else {
            ef = key.ef;
            ef = ef+(0.1-(5-rating)*(0.08+(5-rating)*0.02));
            if(ef<1.3) ef=1.3;
            else if(ef>2.5) ef=2.5;

            key.ef = ef;
            //TODO  알림시간을 받아와야함.
            diffInterval = 0;
            //diff_Interval = (int) (correctReviewDate.getTime() - examEndTime) / 60000;
           // //diff_Interval = diff_Interval > 0 ? diff_Interval : -diff_Interval;
        }

        //가정1.예정 복습시간보다 복습을 빨리하거나 늦게하면 그 차이를 다음 복습간격과의 %를 구하여
        //그 %만큼 복습을 더 빨리해야한다. 자주 복습을 하면 약간의 이익이 있고 늦게 복습을 하면 그만큼 이익이 줄어야된다.
        //첫번째 복습은 20분 후이다.
        //두번째 복습은 1440분(24시간) 후이다.
        //세번째 복습은 8640분(6일) 후이다.
        //그 이후 복습간격은 ef에 맞춰 조절된다.
        //예정 복습시간 이전에 복습하는 경우엔 relation만으로 조절되며, 만약 틀리면 currentLevels가 0으로 초기화되버린다.
        //

        key.reviewTimes++;
        if(key.currentLevels == 0) {
            key.currentLevels++;
            return (int)(20*relation);
        }
        else if(key.currentLevels == 1) {
            if(diffInterval < 0) {
                key.currentLevels++;
                return (int)(1440*relation);
            }
            return diffInterval;
        }
        else if(key.currentLevels == 2){
//            nextInterval = 1440 + (8640‬ - 1440)*(1440+diff_Interval)/1440;
//            return nextInterval;
            if(diffInterval < 0) {
                key.currentLevels++;
                return (int)(8640*relation);
            }
            return diffInterval;
        }
        else{
            if(diffInterval < 0) {
                key.currentLevels++;
                nextInterval = (int)(lastInterval * ef * relation);
                return nextInterval;
            }
            return diffInterval;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //설명을 적어서 맞추는 문제일 때 엔터키의 동작을 변경하는 이벤트 리스너
    private View.OnKeyListener onKeyEvent = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
//            if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    //엔터입력시 할일 처리
//                    //없으면 입력 하지 않음.
//                }
//            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //엔터입력시 할일 처리
                //없으면 입력 하지 않음.
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return (keyCode == KeyEvent.KEYCODE_ENTER);
        }
    };
    private View.OnClickListener onAnswerViewClickEvent = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Keyword key = mTargetKeywords.get(currentmKeyIndex);
            examEndTime = System.currentTimeMillis(); //문제의 종료시간을 저장

            //Date nowDate = new Date(examEndTime); 추후 Date 저장으로 바뀌면 이걸로 사용

            boolean passed = false;
            int answerTime = (int)examEndTime;    //int형으로 형변환

            switch(currentExamType){
                case 1:
                    if(answerString.replaceAll("\\p{Z}", "").equals(((EditText) descriptionLayout.getChildAt(0)).getText().toString().replaceAll("\\p{Z}", ""))){
                        passed = true;
                    }
                    ((EditText) descriptionLayout.getChildAt(0)).setText(answerString);
                    ((EditText) descriptionLayout.getChildAt(0)).setTextAppearance(R.style.answerKeywordTheme);
                    ((EditText) descriptionLayout.getChildAt(0)).setCursorVisible(false);
                    descriptionLayout.getChildAt(0).setEnabled(false);
                    descriptionLayout.getChildAt(0).setClickable(false);
                    descriptionLayout.getChildAt(0).setFocusable(false);
                    descriptionLayout.getChildAt(0).setFocusableInTouchMode(false);
                    break;
                case 2:
                    examLayoutArray[answer].setBackground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_green_rounded));
                    for(int i = 0; i < EXAM_LAYOUT_COUNT; i++){
                        examLayoutArray[i].getChildAt(0).setOnClickListener(null);
                        examLayoutArray[i].getChildAt(1).setOnClickListener(null);
                    }
                    if(answer == selectedExam){
                        passed = true;
                    }
                    else{
                        if(!(selectedExam == -1))
                            examLayoutArray[selectedExam].setBackground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_red_rounded));
                    }
                    break;
                case 3:
                    if(answerString.replaceAll("\\p{Z}", "").equals(examText.getText().toString().replaceAll("\\p{Z}", ""))) {
                        passed = true;
                    }
                    examText.setText(answerString);
                    examText.setTextAppearance(R.style.answerTextExamTheme);
                    examText.setCursorVisible(false);
                    examText.setEnabled(false);
                    examText.setClickable(false);
                    examText.setFocusable(false);
                    examText.setFocusableInTouchMode(false);
                    break;
                default:
                    return; //오류
            }
            if(passed){
                Snackbar.make(v, getString(R.string.ReviewActivity_correct), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.Global_OK), new View.OnClickListener() {
                            @Override
                            public void onClick(View v){
                            }
                        }).show();
            }else{
                Snackbar.make(v, getString(R.string.ReviewActivity_wrong), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.Global_OK), new View.OnClickListener() {
                            @Override
                            public void onClick(View v){
                            }
                        }).show();
            }

            //TODO getReviewTime() 함수로 알림 반환=>알림기능에 연계

            BrainDBHandler dbHandler = new BrainDBHandler(ReviewActivity.this);
            Test resultTest = new Test.Builder()
                    .setCid(key.cid)
                    .setKid(key.id)
                    .setAnswerTime(answerTime)
                    .setPassed(passed)
                    .setTestedDate(examEndTime)
                    .setType(currentExamType)
                    .build();
            dbHandler.addTest(resultTest);

            AnswerButton.setOnClickListener(null);
            nextButton.setOnClickListener(onNextClickEvent);
        }
    };

    private  View.OnClickListener onNextClickEvent = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            AnswerButton.setOnClickListener(onAnswerViewClickEvent);
            nextButton.setOnClickListener(null);
            if(++currentmKeyIndex < mKeywordsSize) currentExamType = generate_view(mTargetKeywords.get(currentmKeyIndex));
            else{
                   finish();
                   Toast.makeText(getApplicationContext(), getString(R.string.ReviewActivity_lastKeyword), Toast.LENGTH_LONG).show();
            }
        }
    };

    private class ExamClickListener implements View.OnClickListener {
        private int selected;
        //
        public ExamClickListener(int select) {
            selected = select;
        }
        public void onClick(View v) {
            //---------------------------------------------------------
            // 클릭된 키워드들을 저장함.
            //

            for(int i=0; i<EXAM_LAYOUT_COUNT; i++) {
                if(i==selected) {
                    if (selectedExam == selected) {
                        selectedExam = -1;
                        examLayoutArray[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_rounded));
                    } else {
                        selectedExam = selected;
                        examLayoutArray[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_blue_rounded));
                    }
                    continue;
                }
                examLayoutArray[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_rounded));
            }
        }
    }

    private View.OnLongClickListener longClickListener = v -> {
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.review_constraintLayout_popup);
        constraintLayout.setVisibility(View.VISIBLE);
        if (v instanceof ImageView) {
            final PhotoView photoView = (PhotoView) findViewById(R.id.review_photo_view);
            photoView.setImageDrawable(((ImageView) v).getDrawable());
            photoView.setVisibility(View.VISIBLE);
            photoView.setOnClickListener(view -> {//view를 형변환해서 사용해도 됨.
                photoView.setImageResource(0);
                photoView.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.GONE);
            });
            //
//            new AlertDialog.Builder(mContext)
//                    .setCancelable(false)
////                    .setTitle(getString(R.string.ReviewActivity_alertDialogOK))
//                    .setView(imageView)
//                    //.setView(((ConstraintLayout) v).getChildAt(i))
//                    .setPositiveButton(getString(R.string.ReviewActivity_alertDialogOK),
//                            (dialog, which) -> {
//                                //TODO 할 내용들. 지금은 공백 나중에 채워넣을거 없으면 null로 변경
//                            })
//                    .show();
        } else if (v instanceof TextView) {
            final TextView textView = (TextView) findViewById(R.id.review_text_view);
            final ScrollView scrollView = (ScrollView) findViewById(R.id.review_text_scroll);
            textView.setText(((TextView) v).getText());

            textView.setOnClickListener(view -> {
                ((TextView)view).setText("");
                scrollView.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.GONE);
            });

//            new AlertDialog.Builder(mContext)
//                    .setCancelable(false)
////                    .setTitle(getString(R.string.ReviewActivity_alertDialogOK))
//                    .setView(textView)
//                    //.setView(((ConstraintLayout) v).getChildAt(i))
//                    .setPositiveButton(getString(R.string.ReviewActivity_alertDialogOK),
//                            (dialog, which) -> {
//                                //TODO 할 내용들. 지금은 공백 나중에 채워넣을거 없으면 null로 변경
//                            })
//                    .show();
        }
        return true;
    };
}
