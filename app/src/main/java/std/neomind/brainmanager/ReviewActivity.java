package std.neomind.brainmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import androidx.core.content.ContextCompat;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.snackbar.Snackbar;

import std.neomind.brainmanager.data.Category;
import std.neomind.brainmanager.data.Description;
import std.neomind.brainmanager.data.Test;
import std.neomind.brainmanager.utils.BrainSerialDataIO;
import std.neomind.brainmanager.utils.AlarmReceiver;
import std.neomind.brainmanager.utils.BrainDBHandler;
import std.neomind.brainmanager.data.Keyword;

public class ReviewActivity extends AppCompatActivity{
    private static final String TAG = "ReviewActivity";
    private static final int EXAM_LAYOUT_COUNT = 4;

    public static final String EXTRAS_KEYWORD = "keyword";
    public static final String EXTRAS_MODE = "mode";
    public static final String EXPIRED_MODE = "expiredMode";

    private int currentExamType;    // 0이면 오류 1이면 기본문제. 2면 객관식문제 3이면 설명빈칸입력문제
    private Context mContext;

    private ArrayList<Keyword> mTargetKeywords;
    private int mCurrentKeyIndex;
    private int mKeywordsSize;

    private ArrayList<Keyword> mAllKeywords;
    private int mAllKeySize;

    private ConstraintLayout mExamAllLayout;
    private ConstraintLayout[] mExamLayoutArray;
    //    private int tMargin, lrMargin, bMargin, width, height, keyTextSize, desTextSize;
//    Point device_size;
    private ArrayList<Integer> mReviewList;
    private ArrayList<Long> mReviewDateList;

    private int mSelectedExam;   // -1일 때는 선택x상태
    private int mAnswer;
    private String mAnswerString;
    private long mExamStartTime;
    private long examEndTime;

    private ScrollView mTextExamScroll;
    private LinearLayout mTextExamLayout;
    private EditText mExamText;

    private ConstraintLayout mKeywordLayout;
    private ConstraintLayout mDescriptionLayout;

    private Button mNextButton;
    private Button mAnswerButton;

    private boolean mOpenFlag = false;
    private BrainDBHandler mBrainDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mContext = this;
        mBrainDBHandler = new BrainDBHandler(mContext);
        mExamAllLayout = findViewById(R.id.review_layout_examAll);
        mDescriptionLayout = findViewById(R.id.review_layout_description);
        mTextExamScroll = findViewById(R.id.review_scroll_exam);

        //복습리스트(id, date-long) 초기화
        mReviewList = new ArrayList<>();
        mReviewDateList = new ArrayList<>();

        //복습리스트 불러오기.
        try {
            BrainSerialDataIO.getNextReviewTimeInfo(mContext, mReviewList, mReviewDateList);
        } catch (BrainSerialDataIO.LoadFailException e) {   // 오류가 생기면 초기화 한다.
            mReviewList = new ArrayList<>();
            mReviewDateList = new ArrayList<>();
            e.printStackTrace();
        }
        ArrayList<Integer> listID = new ArrayList<>();
        /**복습하기를 눌렸을 때 표시될 객체 불러오기 **/
        if(getIntent().getStringExtra(EXTRAS_MODE) == null){
            listID = (ArrayList<Integer>) getIntent().getSerializableExtra(EXTRAS_KEYWORD);
        }
        else{
            /**알람 배너를 눌렸을 때, 혹은 만기복습을 해야할 때, 표시될 저장된 객체 불러오기 **/
            /** 저장된 객체들 중 알림 시간이 넘은 것들을 불러옴 **/
            long now = System.currentTimeMillis();
            for(long date : mReviewDateList) {
                if (date < now) {
                    listID.add(mReviewList.get(mReviewDateList.indexOf(date)));
                }
            }
        }

        if(listID.isEmpty()){
            finish();
            Toast.makeText(getApplicationContext(), getString(R.string.ReviewActivity_noKeyword), Toast.LENGTH_LONG).show();
        }
        else {
            // 리스트 연성.
            try {
                mAllKeywords = mBrainDBHandler.getAllKeywords();
                mBrainDBHandler.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mTargetKeywords = new ArrayList<>();
            for(Keyword key : mAllKeywords){
                if(listID.contains(key.id))
                    mTargetKeywords.add(key);
            }
            // 임시 리스트 제거
            listID.clear();

            mAllKeySize = mAllKeywords.size();
            mKeywordsSize = mTargetKeywords.size();

            if(mAllKeySize == 0){
                finish();
                Toast.makeText(getApplicationContext(), getString(R.string.ReviewActivity_noKeyword), Toast.LENGTH_LONG).show();
            }

            // 연성된 리스트 정렬
            Collections.sort(mTargetKeywords, (o1, o2) -> {
                String criteria1, criteria2;
                int compareResult;
                criteria1 = o1.name;
                criteria2 = o2.name;
                compareResult = criteria1.compareToIgnoreCase(criteria2);

                return compareResult;
            });

            // 관계성 기반으로 정렬
            ArrayList<Integer> relationList;
            int pos = 0;
            for(Keyword key : mTargetKeywords){
                if(pos == mKeywordsSize) break;
                listID.add(key.id);
                relationList = key.getRelationIds();
                for(int rid : relationList){
                    if(!listID.contains(rid)) {
                        boolean flag = false;
                        for(Keyword tempKey : mTargetKeywords){
                            if(rid == tempKey.id) {
                                Collections.swap(mTargetKeywords, pos + 1, mTargetKeywords.indexOf(tempKey));
                                listID.add(mTargetKeywords.get(pos + 1).id);
                                flag = true;
                                break;
                            }
                        }
                        if(flag) break;
                    }
                }
                pos++;
            }
            // 임시 리스트 제거
            listID.clear();

            mNextButton = findViewById(R.id.review_button_nextExam);
            mAnswerButton = findViewById(R.id.review_button_showAnswer);
            mAnswerButton.setOnClickListener(onAnswerViewClickEvent);

            Toolbar toolbar = findViewById(R.id.main_toolbar);
            setSupportActionBar(toolbar);
            mCurrentKeyIndex = 0;
            currentExamType = generate_view(mTargetKeywords.get(mCurrentKeyIndex));   // TODO 0가 리턴될 경우 오류처리 표시 해야됨.
        }
    }
    // examType 은 0,1,2가 있다. 0.오류 1.기본형, 2.객관식문제형, 3.텍스트문제형
    private int generate_view(Keyword key){
        mSelectedExam = -1;
        Random randomGenerator = new Random();
        int r = randomGenerator.nextInt(3)+1;
        // 테스트
        int keyIndex = mAllKeywords.indexOf(key);

        // 레이아웃 참조를 가져올 지역변수.


        mKeywordLayout = findViewById(R.id.review_layout_keyword);
        for(int i = 0; i< mKeywordLayout.getChildCount(); i++) {
            mKeywordLayout.getChildAt(i).setVisibility(View.GONE);
            mKeywordLayout.getChildAt(i).setOnLongClickListener(null);
        }

        // VISIBLE 초기화

        mExamAllLayout.setVisibility(View.GONE);
        mDescriptionLayout.setVisibility(View.GONE);
        mTextExamScroll.setVisibility(View.GONE);
        ((EditText) mDescriptionLayout.getChildAt(0)).setTextAppearance(R.style.keywordTheme);
        mDescriptionLayout.getChildAt(0).setEnabled(true);
        mDescriptionLayout.getChildAt(0).setClickable(true);
        mDescriptionLayout.getChildAt(0).setFocusable(true);
        mDescriptionLayout.getChildAt(0).setFocusableInTouchMode(true);
        ((EditText) mDescriptionLayout.getChildAt(0)).setCursorVisible(true);
        mDescriptionLayout.getChildAt(0).requestFocus();
        mDescriptionLayout.getChildAt(0).invalidate();
        ((EditText) mDescriptionLayout.getChildAt(0)).setText("");
        for(int j=0; j<EXAM_LAYOUT_COUNT; j++) {
            for(int i = 0; i< ((ConstraintLayout)mExamAllLayout.getChildAt(j)).getChildCount(); i++) {
                ((ConstraintLayout)mExamAllLayout.getChildAt(j)).getChildAt(i).setVisibility(View.GONE);
                ((ConstraintLayout)mExamAllLayout.getChildAt(j)).getChildAt(i).setOnLongClickListener(null);
                ((ConstraintLayout)mExamAllLayout.getChildAt(j)).getChildAt(i).setOnClickListener(null);
            }
        }


        // keysize = key.name.size()
        if(key.name.isEmpty() || (key.imagePath.isEmpty() && (key.getDescriptions() == null || key.getDescriptions().isEmpty()))) {
            // TODO 코드 중복 존재함.
            if(++mCurrentKeyIndex < mKeywordsSize) currentExamType = generate_view(mTargetKeywords.get(mCurrentKeyIndex));
            else{
                finish();
                Toast.makeText(getApplicationContext(), getString(R.string.ReviewActivity_lastKeyword), Toast.LENGTH_LONG).show();
            }
            return currentExamType;
        }
        while(true) {
            if (r == 1) {
                mAnswerString = key.name;

                mDescriptionLayout.setVisibility(View.VISIBLE);
                if (key.imagePath.isEmpty() || (!key.getDescriptions().isEmpty() && randomGenerator.nextInt(2) == 0)) {
                    String descriptionString ="";
                    for(Description s : key.getDescriptions()){
                        descriptionString += s.description + "\n";
                    }
                    descriptionString = descriptionString.substring(0, descriptionString.length()-1);
                    mKeywordLayout.getChildAt(0).setVisibility(View.VISIBLE);
                    mKeywordLayout.getChildAt(1).setVisibility(View.GONE);
                    ((TextView) mKeywordLayout.getChildAt(0)).setText(descriptionString);
                    // mKeywordLayout.setOnLongClickListener();
                    mKeywordLayout.getChildAt(0).setOnLongClickListener(longClickListener);
                }else{
                    mKeywordLayout.getChildAt(0).setVisibility(View.GONE);
                    mKeywordLayout.getChildAt(1).setVisibility(View.VISIBLE);
                    mKeywordLayout.getChildAt(1).setClipToOutline(true);
                    ((ImageView) mKeywordLayout.getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
                    // descriptionImage 객체 내용지정(이미지파일)
                    mKeywordLayout.getChildAt(1).setOnLongClickListener(longClickListener);
                }
                mDescriptionLayout.getChildAt(0).setVisibility(View.VISIBLE);
                mDescriptionLayout.getChildAt(0).setOnKeyListener(onKeyEvent);

                mExamStartTime = System.currentTimeMillis(); // 문제의 시작시간을 저장
                return 1;
            } else if (r == 2 && mAllKeySize > 1) {
                mKeywordLayout.getChildAt(0).setVisibility(View.VISIBLE);
                ((TextView) mKeywordLayout.getChildAt(0)).setText(key.name);
                mExamAllLayout.setVisibility(View.VISIBLE);
                // 객관식 문제를 제출하기 위해 설명들의 갯수를 셈. 관련성도 여기에 구현되어야함.
                int count = 0; // count가 될 수 있는 수는 2 3 4
                for (Keyword tKey : mAllKeywords) {
                    if (count > 4) {
                        count = 4;
                        break;
                    }
                    // count += tkey.name.size(); // 설명의 총 갯수를 구합니다.
                    if (!tKey.getDescriptions().isEmpty()) {
                        count++;
                        continue;
                    }
                    if (!tKey.imagePath.isEmpty()) {
                        count++;
                        continue;
                    }

                }
                if(count<2) {   // 설명의 갯수가 총 2개가 안된다면 다시 r=2가 않게 while문을 돌림
                    r = randomGenerator.nextInt(2)+1;
                    if(r==2) r++;
                    continue;
                }
                // 객관식 문제 초기화. TODO examAllLayout이 한번밖에 안쓰인다면 이를 짧게 수정해야됨. 혹은 확장성을 위해 그대로 두거나

                mExamLayoutArray = new ConstraintLayout[EXAM_LAYOUT_COUNT];
//                for (int i = 0; i < EXAM_LAYOUT_COUNT; i++) {
//                    mExamLayoutArray[i] = (ConstraintLayout) mExamAllLayout.getChildAt(i);
//                    mExamLayoutArray[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_rounded));
//                }
                // 객관식 문제 제출 총 4개 답안중에 하나 선택해서 정답을 골라야함.
                // case는 문제 갯수를 뜻하는 것이다. case 2,3만을 조건으로 잡고 default를 쓸 수 있겠지만 혹시모른 에러를 잡기위해 case 4 활용
                r = randomGenerator.nextInt(count);
                mAnswer = r; // 문제 정답 번호와 answer을 일치시킴

                // 랜덤한 count-1 개의 정답이 아닌 보기
                int tempRandom[] = new int[count - 1];
                Arrays.fill(tempRandom, -1);

                ArrayList<Integer> relationList = mTargetKeywords.get(mCurrentKeyIndex).getRelationIds(); // 릴레이션 리스트는 id로만 이루어져 있다. 이것을 키워드 리스트 객체로 들고와야함.


                ArrayList<Integer> relationIndexs = new ArrayList<>();
                for(Keyword tKey : mAllKeywords){    // 전체 키워드 중에서 릴레이션 리스트 id를 들고와야한다.
                    for(int id : relationList){
                        if(id == tKey.id) {
                            relationIndexs.add(mAllKeywords.indexOf(tKey));
                            break;
                        }
                    }
                }
                relationList.clear();
                // 관계성중에 비어있는 관계성 삭제
                for(int index : relationIndexs){
                    if(mAllKeywords.get(index).imagePath.isEmpty() && mAllKeywords.get(index).getDescriptions().isEmpty())
                        relationIndexs.remove((Integer)index);
                }

                int sizeCount = 0;  // 관계성이 있는 키워드가 지정될 랜덤 위치를 저장할 배열
                int tempRandomRel[] = new int[tempRandom.length];


                for(int i=0; i<tempRandom.length; i++){
                    tempRandomRel[i] = randomGenerator.nextInt(tempRandom.length);
                    for(int j=0; j<i; j++) {
                        if(tempRandomRel[i] == tempRandomRel[j]) i--;
                    }
                }// 랜덤하게 인덱스를 3개 만드는거임.
                // 크기에 맞춰서 사이즈를 제작.
                int tempSize = relationIndexs.size() > 3 ? 4 : relationIndexs.size();
                tempSize = tempSize > tempRandom.length ? tempRandom.length : tempSize;

                for(int i=0; i<tempSize; i++) {
                    tempRandom[tempRandomRel[i]] = relationIndexs.get(randomGenerator.nextInt(relationIndexs.size()));
                    for(int j=0; j<tempSize; j++) {
                        if(j != tempRandomRel[i])
                            if(tempRandom[tempRandomRel[i]] == tempRandom[j]) i--;
                    }
                }// 관계성들 갯수에 맞게 랜덤 인덱스를 생성시켜 allkeywords에서의 인덱스를 저장시킴


                //관계성에 맞게 채워넣었으니 이제 카테고리의 allkeywords에서의 인덱스를 저장시켜야됨.
                ArrayList<Keyword> categoryKeywordList;
                try {
                    categoryKeywordList = mBrainDBHandler.getAllKeywordsOfTheCategory(key.cid);
                    mBrainDBHandler.close();
                } catch (Exception e) {
                    categoryKeywordList = new ArrayList<>();
                    e.printStackTrace();
                }



                int originCategoryKeywordSize = categoryKeywordList.size();
                for (int j = 0, ct = 0; j < tempRandom.length; j++) {
                    int i = randomGenerator.nextInt(tempRandom.length);
                    if (ct == originCategoryKeywordSize - 1) break;
                    if (tempRandom[i] > -1) {
                        j--;
                        boolean fullCk = true;
                        for(int m=0; m<tempRandom.length; m++){
                            if(tempRandom[m] == -1) {
                                fullCk = false;
                                break;
                            }
                        }
                        if(fullCk) break;
                        continue;
                    }
                    if (originCategoryKeywordSize - categoryKeywordList.size() > 3) break;
                    int randIndex = randomGenerator.nextInt(categoryKeywordList.size());
                    Keyword tKey = categoryKeywordList.get(randIndex);
                    int index = -1;
                    for (Keyword tempKey : mAllKeywords) {
                        if (tempKey.id == tKey.id) {
                            index = mAllKeywords.indexOf(tempKey);
                            break;
                        }
                    }
                    if (index == -1) {   //만약 알 수 없는 이유로 전체 리스트에서 찾을 수 없다면
                        categoryKeywordList.remove(randIndex);  //카테리스트에서 그 값 remove
                        j--;    //j 1감소
                        continue;   //다음 반복 실행
                    }
                    if (index == keyIndex) {  //만약 정답이랑 같은 인덱스 값이라면
                        categoryKeywordList.remove(randIndex);
                        j--;
                        continue;
                    }
                    tempRandom[i] = index;  //대입
                    categoryKeywordList.remove(randIndex);
                    boolean flag = true;
                    for (int k = 0; k < tempRandom.length; k++) {
                        if (k == i) continue;
                        if (tempRandom[i] == tempRandom[k]) {//만약 같은 값이 있다면
                            tempRandom[i] = -1;   // -1 대입
                            j--;    //j 1감소
                            flag = false;
                            break;
                        }
                    }
                    if (flag) ct++;
                }


                for(int i=0; i<tempRandom.length; i++) {
                    while (true) {
                        if(tempRandom[i] > -1) break;   // 이미 값이 채워져 있으면 패스
                        boolean ck = true;
                        tempRandom[i] = randomGenerator.nextInt(mAllKeySize);
                        if(tempRandom[i] == keyIndex){
                            tempRandom[i] = -1;
                            continue;
                        } // 정답인 키 인덱스와 값이 같으면 안됨.
                        Keyword tKey = mAllKeywords.get(tempRandom[i]);
                        if(tKey.getDescriptions().isEmpty() && tKey.imagePath.isEmpty()) {
                            tempRandom[i] = -1;
                            continue;
                        }// 키워드 내부에 설명이 아무것도 없으면 안됨.
                        for (int j = 0; j < tempRandom.length; j++) {           // 원래 있던 값과 값이 같으면 안됨.
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
                    mExamLayoutArray[i] = (ConstraintLayout) mExamAllLayout.getChildAt(i);
                    mExamLayoutArray[i].setForeground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_rounded));
                    mExamLayoutArray[i].setVisibility(View.VISIBLE);
                    mExamLayoutArray[i].getChildAt(0).setOnClickListener(null);
                    mExamLayoutArray[i].getChildAt(1).setOnClickListener(null);

                    if(count-1 < i) {   // 사용가능한 문제 개수-1 보다 i가 클경우
//                        mExamLayoutArray[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_gray_rounded));
                        continue;
                    }
                    if(r==i) {
                        if (key.imagePath.isEmpty() || (!key.getDescriptions().isEmpty() && randomGenerator.nextInt(2) == 0)) {
                            String descriptionString ="";
                            for(Description s : key.getDescriptions()){
                                descriptionString += s.description + "\n";
                            }
                            descriptionString = descriptionString.substring(0, descriptionString.length()-1);
                            mExamLayoutArray[i].getChildAt(0).setVisibility(View.VISIBLE);
                            mExamLayoutArray[i].getChildAt(1).setVisibility(View.GONE);
                            ((TextView) mExamLayoutArray[i].getChildAt(0)).setText(descriptionString);
                            mExamLayoutArray[i].getChildAt(0).setOnLongClickListener(longClickListener);
                            ExamClickListener examClickEvent = new ExamClickListener(i);
                            mExamLayoutArray[i].getChildAt(0).setOnClickListener(examClickEvent);
                        } else {
                            mExamLayoutArray[i].getChildAt(0).setVisibility(View.GONE);
                            mExamLayoutArray[i].getChildAt(1).setVisibility(View.VISIBLE);
                            mExamLayoutArray[i].getChildAt(1).setClipToOutline(true);
                            ((ImageView) mExamLayoutArray[i].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
                            mExamLayoutArray[i].getChildAt(1).setOnLongClickListener(longClickListener);
                            ExamClickListener examClickEvent = new ExamClickListener(i);
                            mExamLayoutArray[i].getChildAt(1).setOnClickListener(examClickEvent);
                        }
                    }else{
                        Keyword examTemp = mAllKeywords.get(tempRandom[j++]);
                        if (examTemp.imagePath.isEmpty() || (!examTemp.getDescriptions().isEmpty() && randomGenerator.nextInt(2) == 0)) {
                            String descriptionString ="";
                            for(Description s : examTemp.getDescriptions()){
                                descriptionString += s.description + "\n";
                            }
                            descriptionString = descriptionString.substring(0, descriptionString.length()-1);
                            mExamLayoutArray[i].getChildAt(0).setVisibility(View.VISIBLE);
                            mExamLayoutArray[i].getChildAt(1).setVisibility(View.GONE);
                            ((TextView) mExamLayoutArray[i].getChildAt(0)).setText(descriptionString);
                            mExamLayoutArray[i].getChildAt(0).setOnLongClickListener(longClickListener);
                            ExamClickListener examClickEvent = new ExamClickListener(i);
                            mExamLayoutArray[i].getChildAt(0).setOnClickListener(examClickEvent);
                        } else {
                            mExamLayoutArray[i].getChildAt(0).setVisibility(View.GONE);
                            mExamLayoutArray[i].getChildAt(1).setVisibility(View.VISIBLE);
                            mExamLayoutArray[i].getChildAt(1).setClipToOutline(true);
                            ((ImageView) mExamLayoutArray[i].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(examTemp.imagePath));
                            mExamLayoutArray[i].getChildAt(1).setOnLongClickListener(longClickListener);
                            ExamClickListener examClickEvent = new ExamClickListener(i);
                            mExamLayoutArray[i].getChildAt(1).setOnClickListener(examClickEvent);
                        }
                    }
                }
                /*
                Constraint 를 활용한 객관식Size를 바꿔버리는 방식의 코드 회색 박스가 아니라 사이즈가 변경된다.
                switch (count) {
                    //
                    case 2:
                        r = randomGenerator.nextInt(2);
                        if (r == 1) r++;
                        for (int j = 0; j < 3; j += 2) {
                            mExamLayoutArray[j].setVisibility(View.VISIBLE);
                            for (int i = 0; i < 2; i++) {
                                mExamLayoutArray[j].getChildAt(i).setVisibility(View.VISIBLE);
                            }
                            if (key.imagePath.isEmpty()) {
                                mExamLayoutArray[j].getChildAt(0).setVisibility(View.VISIBLE);
                                if(r==j) ((TextView) mExamLayoutArray[r].getChildAt(0)).setText(key.name);
                            } else {
                                mExamLayoutArray[j].getChildAt(1).setVisibility(View.VISIBLE);
                                if(r==j) ((ImageView) mExamLayoutArray[r].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
                            }
                        }


                        break;
                    case 3:
                        for (int j = 0; j < 3; j++) {
                            mExamLayoutArray[j].setVisibility(View.VISIBLE);
                            for (int i = 0; i < 2; i++) {
                                mExamLayoutArray[j].getChildAt(i).setVisibility(View.VISIBLE);
                            }
                        }
                        r = randomGenerator.nextInt(3);
                        if (key.imagePath.isEmpty()) {
                            ((TextView) mExamLayoutArray[r].getChildAt(0)).setText(key.name);
                        } else
                            ((ImageView) mExamLayoutArray[r].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
                        break;
                    case 4:
                        for (int j = 0; j < 4; j++) {
                            mExamLayoutArray[j].setVisibility(View.VISIBLE);
                            for (int i = 0; i < 2; i++) {
                                mExamLayoutArray[j].getChildAt(i).setVisibility(View.VISIBLE);
                            }
                        }
                        r = randomGenerator.nextInt(4);
                        if (key.imagePath.isEmpty()) {
                            ((TextView) mExamLayoutArray[r].getChildAt(0)).setText(key.name);
                        } else
                            ((ImageView) mExamLayoutArray[r].getChildAt(1)).setImageBitmap(BitmapFactory.decodeFile(key.imagePath));
                        break;
                    default:
                        break;
                }
                **/
                mExamStartTime = System.currentTimeMillis(); // 문제의 시작시간을 저장
                return 2;
            }
            // 만약 r=2라도 사용자가 작성한 키워드의 갯수가 1개보다 작거나 같으면 r값을 바꿔 while문을 다시 시도한다.
            else if (r == 2){
                r = randomGenerator.nextInt(2)+1;
                if(r==2) r++;
                continue;
            }
            // 키워드에 설명이 없으면 빈칸은 만들 수 없으므로 r값을 3보다 작은 수로 while문을 다시 시도.
            else if (key.getDescriptions().isEmpty()){
                r = randomGenerator.nextInt(2)+1;
                continue;
            }
            else {  // r=3일 때를 뜻함.
                // TODO 설명부를 랜덤한 부분을 빈칸으로 만드는 코드 필요
                mKeywordLayout.getChildAt(0).setVisibility(View.VISIBLE);
                ((TextView) mKeywordLayout.getChildAt(0)).setText(key.name);
//                mTextExamScroll = findViewById(R.id.review_scroll_exam);

                ArrayList<String> descriptionString = new ArrayList<>();
                for(Description s : key.getDescriptions()){
                    descriptionString.add(s.description);
                }// 하드카피
                // descriptionString = descriptionString.substring(0, descriptionString.length()-1);
                r = randomGenerator.nextInt(descriptionString.size());  // TODO r이 재활용되어진다. 재반복되면주의
                // r중에 하나의 값을 쓴다.

                TextView[] tempTextView = new TextView[descriptionString.size()];
                for(int i=0; i<tempTextView.length; i++) {                         // length가 곧 descriptionString의 사이즈이다.
                    tempTextView[i] = new TextView(this);
                    tempTextView[i].setText(descriptionString.remove(0));   // 0이 계속 사라지면서 리스트가 하나씩 줄어든다.
                    tempTextView[i].setTextAppearance(R.style.textExamTheme);
                }

                String targetStr = tempTextView[r].getText().toString();    // 전체 설명 리스트중 어절빈칸을 만들 문자열 하나를 고른다.

//                int cnt = 0;
//                str = str.trim();               // 앞뒤 빈칸 제거
//                Boolean checkEmpty = false;     // 빈칸만으로 이루어져있는지 검사
//                for (int i = 0; i < str.length(); i++) {
//                    if (str.charAt(i) == ' ' && str.charAt(i - 1) != ' ') {
//                        // 띄어쓰기를 찾고 띄어쓰기 바로 앞의 문자가 빈칸이 아닌경우
//                        cnt++;
//                    }else
//                        checkEmpty=true;
//                }
//                cnt = checkEmpty ? cnt+1 : cnt;
                String[] arrayStr = targetStr.split(" ");
                final int arrayStrLength = arrayStr.length;
                int r2 = randomGenerator.nextInt(arrayStrLength);   // r2는 어절을 비울 위치이다.
                TextView tempTextView2;
                tempTextView2 = new TextView(this);
                mAnswerString = arrayStr[r2];

                String sumStr = "";
                if(r2 == 0){    // 만약 첫번째 위치인경우
                    for(int i = 1; i<arrayStrLength;  i++)
                        sumStr += arrayStr[i] + " ";
                    tempTextView[r].setText(sumStr);
                } else if(r2 == arrayStrLength - 1){    // 마지막에 위치한 경우
                    for(int i = 0; i<arrayStrLength - 1;  i++)
                        sumStr += arrayStr[i] + " ";
                    tempTextView[r].setText(sumStr);
                }
                else{   // 그 외 중간일 경우
                    for(int i = 0; i < r2;  i++)
                        sumStr += arrayStr[i] + " ";
                    tempTextView[r].setText(sumStr);
                    sumStr = "";
                    for(int i = r2 + 1; i < arrayStrLength;  i++)
                        sumStr += arrayStr[i] + " ";
                    tempTextView2.setText(sumStr);
                    tempTextView2.setTextAppearance(R.style.textExamTheme);
                }

                mTextExamScroll.setVisibility(View.VISIBLE);
                mTextExamLayout = findViewById(R.id.review_scroll_linear_layout);
                mTextExamLayout.setVisibility(View.VISIBLE);

                mExamText = new EditText(this);      // 정답을 넣어야되는 에디트텍스트
                mExamText.setTextAppearance(R.style.textExamTheme);
                mExamText.setSingleLine(true);
                mExamText.setLines(1);
                mExamText.setOnKeyListener(onKeyEvent);

                for(int i=0; i<tempTextView.length; i++){   // 설명의 개수(=Description, tempTextView의 개수)만큼 반복된다.
                    LinearLayout tempLinearLayout = new LinearLayout(this);
                    tempLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    if(i==r) {
                        if (r2 == 0) {
                            tempLinearLayout.addView(mExamText);
                            tempLinearLayout.addView(tempTextView[i]);
                        } else if (r2 == arrayStrLength - 1) {
                            tempLinearLayout.addView(tempTextView[i]);
                            tempLinearLayout.addView(mExamText);
                        } else {
                            tempLinearLayout.addView(tempTextView[i]);
                            tempLinearLayout.addView(mExamText);
                            tempLinearLayout.addView(tempTextView2);
                        }
                        mTextExamLayout.addView(tempLinearLayout);
                        continue;
                    }
                    mTextExamLayout.addView(tempTextView[i]);
                }
                mExamStartTime = System.currentTimeMillis(); // 문제의 시작시간을 저장
                return 3;
            }
        }

        // param.topMargin = tMargin;
        // keywordText.setLayoutParams(param);


        // ConstraintLayout.LayoutParams param2 = (ConstraintLayout.LayoutParams) mDescriptionLayout.getLayoutParams();
        // param2.setMargins(0, 0, 0, bMargin);
    }
    // 분단위로 다음 복습시간을 반환함.
    private long getReviewTime(Keyword key, int answerTime, boolean passed){
        ArrayList<Integer> keyRelList =  key.getRelationIds();
        int relCount = 0;
        for(int i = mCurrentKeyIndex -1; i > -1; i--) {
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
            Keyword k;
            int i = 0;
            for(Integer relID : keyRelList) {
                try {
                    k = mBrainDBHandler.findKeyword(BrainDBHandler.FIELD_KEYWORDS_ID, relID);
                    if(!k.imagePath.isEmpty() || k.getDescriptions() != null) {
                        relCount++;
                        i++;
                    }
                    mBrainDBHandler.close();
                }
                catch(BrainDBHandler.NoMatchingDataException e){
                    e.printStackTrace();
                }
                if(i == 4) break;
            }
        }
        int rating;
        int lastInterval = key.interval;
        int nextInterval;
        double ef;
        double relation = Math.pow(1.07 , relCount);
        relation = relation > 1.5 ? 1.5 : relation;
        long correctReviewDate;


//        Date collectReviewDate;
        int diffInterval;

        // SM-2과는 조금 다르게 5가지 변수 사용.
        if(answerTime < 10000 && passed) rating = 5;        // 반응이 즉각적인 맞는 응답.
        else if(answerTime < 30000 && passed) rating = 4;   // 반응이 느린 맞는 응답
        else if(passed) rating = 3;   // 반응이 심각히 느린 맞는 응답
        else if(answerTime > 5000 && answerTime < 10000) rating = 2;             // 빨리 풀었지만 오답. 너무 빨리풀었는데 오답인경우 그냥 찍은걸로 치부
        else rating = 0;    // 완전히 잊음



        if(key.reviewTimes == 0){
            ef = 2.5;
            key.currentLevels=1;
        }else {
            ef = key.ef;
            ef = ef+(0.1-(5-rating)*(0.08+(5-rating)*0.02));
            if(ef<1.3) ef=1.3;
            else if(ef>2.5) ef=2.5;
        }

        // 가정1.예정 복습시간보다 복습을 빨리하거나 늦게하면 그 차이를 다음 복습간격과의 %를 구하여
        // 그 %만큼 복습을 더 빨리해야한다. 자주 복습을 하면 약간의 이익이 있고 늦게 복습을 하면 그만큼 이익이 줄어야된다.
        // 첫번째 복습은 20분 후이다.
        // 두번째 복습은 1440분(24시간) 후이다.
        // 세번째 복습은 8640분(6일) 후이다.
        // 그 이후 복습간격은 ef에 맞춰 조절된다.
        // 예정 복습시간 이전에 복습하는 경우엔 relation만으로 조절되며, 만약 틀리면 currentLevels가 0으로 초기화되버린다.
        //
        // 만약 첫복습이 아닌데 불구하고 reviewList와 reviewDateList의 크기가 0이거나 key.id의 인덱스를 찾을 수 없는 경우 에러값을 반환한다.
        // => 에러값이 아닌 currentLevels를 0으로 바꾸자
        if(key.reviewTimes > 0 && (mReviewList.isEmpty() || mReviewDateList.isEmpty() || mReviewList.indexOf(key.id) == -1)){
            Toast.makeText(getApplicationContext(), getString(R.string.
                    Global_errorOccurred), Toast.LENGTH_LONG).show();
            key.currentLevels = 0;
        }
        if(rating < 3)
            key.currentLevels = 0;
        key.ef = ef;

        key.reviewTimes++;
        if(key.currentLevels == 0) {
            if(key.reviewTimes > 0 && mReviewList.indexOf(key.id) != -1) {
                correctReviewDate = mReviewDateList.get(mReviewList.indexOf(key.id));
                diffInterval = (int) ((correctReviewDate - examEndTime) / 60000);
                key.currentLevels++;
                if (diffInterval < 0) {
                    key.interval = (int) (20 * relation);
                    return examEndTime + (key.interval * 60000);
                }
                return correctReviewDate;
            }
            key.currentLevels++;
            key.interval = (int) (20 * relation);
            return examEndTime + (key.interval * 60000);
        }
        else if(key.currentLevels == 1 && mReviewList.indexOf(key.id) != -1) {
            correctReviewDate = mReviewDateList.get(mReviewList.indexOf(key.id));
            diffInterval = (int) ((correctReviewDate - examEndTime) / 60000);

            if(diffInterval < 0) {
                key.currentLevels++;
                key.interval = (int)(1440 * relation);
                return examEndTime+(key.interval*60000);
            }
            return correctReviewDate;
        }
        else if(key.currentLevels == 2 && mReviewList.indexOf(key.id) != -1){
            correctReviewDate = mReviewDateList.get(mReviewList.indexOf(key.id));
            diffInterval = (int)((correctReviewDate - examEndTime) / 60000);

            if(diffInterval < 0) {
                key.currentLevels++;
                key.interval = (int)(8640 * relation);
                return examEndTime+(key.interval*60000);
            }
            return correctReviewDate;
        }
        else if(mReviewList.indexOf(key.id) != -1){
            correctReviewDate = mReviewDateList.get(mReviewList.indexOf(key.id));
            diffInterval = (int) ((correctReviewDate - examEndTime) / 60000);

            if(diffInterval < 0) {
                key.currentLevels++;
                nextInterval = (int)(lastInterval * ef * relation);
                key.interval = nextInterval;
                return examEndTime + nextInterval*60000;
            }
            return correctReviewDate;
        }
        else{   // 오류 발생한거임. 의도치 않은 동작
            return Long.MIN_VALUE;
        }
    }

    @Override
    public void onPause(){
        Log.d(TAG, "onPause");
        boolean tempFlag = false;
        if(mReviewDateList != null && mReviewDateList.size() > 0 && mReviewDateList.size() == mReviewList.size()) {
            //현재 복습하고 있는 키워드들 중에서 복습간격이 만기된 알림이 있으면 플래그를 true로 만듬
            for (Keyword key : mTargetKeywords) {
                if (mReviewList.contains(key.id)) {
                    if (mReviewDateList.get(mReviewList.indexOf(key.id)) < mExamStartTime) {
                        tempFlag = true;
                        break;
                    }
                }
            }
        }
        if(tempFlag) {
            Intent intent = new Intent(mContext, AlarmReceiver.class);
            intent.putExtra(AlarmReceiver.EXTRAS_MODE, AlarmReceiver.MODE_CANCEL_REVIEW);
            sendBroadcast(intent);
        }
        super.onPause();
    }
    @Override
    public void onDestroy(){
        if(getIntent().getStringExtra(EXTRAS_MODE) != null) {
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if(mOpenFlag) {
            ConstraintLayout constraintLayout = findViewById(R.id.review_constraintLayout_popup);
            final ScrollView scrollView = findViewById(R.id.review_text_scroll);
            final PhotoView photoView = findViewById(R.id.review_photo_view);
            final TextView textView = findViewById(R.id.review_text_view);

            constraintLayout.setVisibility(View.GONE);
            photoView.setImageResource(0);
            textView.setText("");

            photoView.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);

            mOpenFlag = false;
        }
        else{
            boolean tempFlag = false;
            if(mReviewDateList != null && mReviewDateList.size() > 0 && mReviewDateList.size() == mReviewList.size()) {
                //현재 복습하고 있는 키워드들 중에서 복습간격이 만기된 알림이 있으면 플래그를 true로 만듬
                for (Keyword key : mTargetKeywords) {
                    if (mReviewList.contains(key.id)) {
                        if (mReviewDateList.get(mReviewList.indexOf(key.id)) < mExamStartTime) {
                            tempFlag = true;
                            break;
                        }
                    }
                }
            }
            //상수화
            final boolean showFlag = tempFlag;
            //복습하기 화면을 나가려고 할 때 나타나는 AlertDialog
            //showFlag가 true라면 cancelmoe로 알람리시버를 동작시킴.
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.AlertDialog_askReallyCancel))
                    .setPositiveButton(getString(R.string.AlertDialog_button_yes),
                            (dialog, which) -> {
                                if(showFlag) {
                                    Intent intent = new Intent(mContext, AlarmReceiver.class);
                                    intent.putExtra(AlarmReceiver.EXTRAS_MODE, AlarmReceiver.MODE_CANCEL_REVIEW);
                                    sendBroadcast(intent);
                                }
                                else {
                                    Toast.makeText(this, getString(R.string.Global_canceled), Toast.LENGTH_SHORT).show();
                                }
                                super.onBackPressed();
                                if(getIntent().getStringExtra(EXTRAS_MODE) != null) {
                                    super.onBackPressed();
                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton(getString(R.string.AlertDialog_button_no), null);
            if(showFlag) {
                builder.setMessage((getString(R.string.ReviewActivity_remainReview)));
            }
            builder.show();
        }

    }

    // 설명을 적어서 맞추는 문제일 때 엔터키의 동작을 변경하는 이벤트 리스너
    private View.OnKeyListener onKeyEvent = (v, keyCode, event) -> {
//            if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    // 엔터입력시 할일 처리
//                    // 없으면 입력 하지 않음.
//                }
//            }
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // 엔터입력시 할일 처리
            // 없으면 입력 하지 않음.
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        }
        return false;
    };

    private View.OnClickListener onAnswerViewClickEvent = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Keyword key = mTargetKeywords.get(mCurrentKeyIndex);
            examEndTime = System.currentTimeMillis(); // 문제의 종료시간을 저장

            // Date nowDate = new Date(examEndTime); 추후 Date 저장으로 바뀌면 이걸로 사용

            boolean passed = false;
            int answerTime = (int)(examEndTime - mExamStartTime);    // int형으로 형변환

            switch(currentExamType){
                case 1:
                    if(mAnswerString.replaceAll("\\p{Z}", "").
                            equals(((EditText) mDescriptionLayout.getChildAt(0)).
                                    getText().toString().
                                    replaceAll("\\p{Z}", ""))){
                        passed = true;
                    }
                    ((EditText) mDescriptionLayout.getChildAt(0)).setText(mAnswerString);
                    ((EditText) mDescriptionLayout.getChildAt(0)).setTextAppearance(R.style.answerKeywordTheme);
                    ((EditText) mDescriptionLayout.getChildAt(0)).setCursorVisible(false);
                    mDescriptionLayout.getChildAt(0).setEnabled(false);
                    mDescriptionLayout.getChildAt(0).setClickable(false);
                    mDescriptionLayout.getChildAt(0).setFocusable(false);
                    mDescriptionLayout.getChildAt(0).setFocusableInTouchMode(false);
                    break;
                case 2:
                    mExamLayoutArray[mAnswer].setForeground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_green_rounded));
                    for(int i = 0; i < EXAM_LAYOUT_COUNT; i++){
                        mExamLayoutArray[i].getChildAt(0).setOnClickListener(null);
                        mExamLayoutArray[i].getChildAt(1).setOnClickListener(null);
                    }
                    if(mAnswer == mSelectedExam){
                        passed = true;
                    }
                    else{
                        if(!(mSelectedExam == -1))
                            mExamLayoutArray[mSelectedExam].setForeground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_red_rounded));
                    }
                    break;
                case 3:
                    if(mAnswerString.replaceAll("\\p{Z}", "").
                            equals(mExamText.getText().toString().
                                    replaceAll("\\p{Z}", ""))) {
                        passed = true;
                    }
                    mExamText.setText(mAnswerString);
                    mExamText.setTextAppearance(R.style.answerTextExamTheme);
                    mExamText.setCursorVisible(false);
                    mExamText.setEnabled(false);
                    mExamText.setClickable(false);
                    mExamText.setFocusable(false);
                    mExamText.setFocusableInTouchMode(false);
                    break;
                default:
                    return; // 오류
            }
            if(passed){
                Snackbar.make(v, getString(R.string.ReviewActivity_correct), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.Global_OK), view -> {}).show();
            }else{
                Snackbar.make(v, getString(R.string.ReviewActivity_wrong), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.Global_OK), view -> {}).show();
            }

            // TODO getReviewTime() 함수로 알림 반환=>알림기능에 연계

            long nextReviewTime = getReviewTime(key, answerTime, passed);

            if(nextReviewTime != Long.MIN_VALUE) {
                if (!mReviewList.isEmpty() && !mReviewDateList.isEmpty()) {
                    int temp = mReviewList.indexOf(key.id);
                    if (temp == -1) { // -1은 해당 원소가 reviewList에 없을 때 반환된다
                        mReviewList.add(key.id);
                        mReviewDateList.add(nextReviewTime);
                    } else {   // 만약 있다면 examStartTime보다 작을 경우 복습이 됐다는 의미이므로 리스트에 해당 알림시간을 삭제하고 새로운 복습시간을 할당한다.
                        if (mReviewDateList.get(temp) < mExamStartTime) {
                            mReviewList.remove(temp);
                            mReviewDateList.remove(temp);
                            mReviewList.add(key.id);
                            mReviewDateList.add(nextReviewTime);
                        }
                        // 클경우엔 알림을 그대로 둔다.
                    }
                } else {   // 만약 비었다면 새롭게 추가한다.
                    mReviewList.add(key.id);
                    mReviewDateList.add(nextReviewTime);
                }
                /** 객체 저장 **/
                try {
                    BrainSerialDataIO.saveNextReviewTimeInfo(mContext, mReviewList, mReviewDateList);
                } catch (BrainSerialDataIO.SaveFailException e) {
                    Log.d(TAG, "객체저장에서 에러 발생");
                    e.printStackTrace();
                } catch (BrainSerialDataIO.ListNotEqualSizeException e) {
                    Log.d(TAG, "객체저장에서 에러 발생");
                    e.printStackTrace();
                }
                //알람 리시버에 발신
                Intent intent = new Intent(mContext, AlarmReceiver.class);
                intent.putExtra(AlarmReceiver.EXTRAS_KEY_REVIEW_DATE, nextReviewTime);
                sendBroadcast(intent);
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.
                        Global_errorOccurred), Toast.LENGTH_LONG).show();
            }

            mBrainDBHandler.updateKeyword(key);
            Test resultTest = new Test.Builder()
                    .setCid(key.cid)
                    .setKid(key.id)
                    .setAnswerTime(answerTime)
                    .setPassed(passed)
                    .setTestedDate(examEndTime)
                    .setType(currentExamType)
                    .build();
            mBrainDBHandler.addTest(resultTest);
            mBrainDBHandler.close();

            mAnswerButton.setOnClickListener(null);
            mNextButton.setOnClickListener(onNextClickEvent);
        }
    };

    private  View.OnClickListener onNextClickEvent = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            mAnswerButton.setOnClickListener(onAnswerViewClickEvent);
            mNextButton.setOnClickListener(null);
            // 텍스트 시험 초기화
            if(mTextExamLayout != null)
                mTextExamLayout.removeAllViews();
            if(++mCurrentKeyIndex < mKeywordsSize) currentExamType = generate_view(mTargetKeywords.get(mCurrentKeyIndex));
            else{
                finish();
                Toast.makeText(getApplicationContext(), getString(R.string.
                        ReviewActivity_lastKeyword), Toast.LENGTH_LONG).show();
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
            // ---------------------------------------------------------
            // 클릭된 키워드들을 저장함.
            //

            for(int i=0; i<EXAM_LAYOUT_COUNT; i++) {
                if(i==selected) {
                    if (mSelectedExam == selected) {
                        mSelectedExam = -1;
                        mExamLayoutArray[i].setForeground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_rounded));
                    } else {
                        mSelectedExam = selected;
                        mExamLayoutArray[i].setForeground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_blue_rounded));
                    }
                    continue;
                }
                mExamLayoutArray[i].setForeground(ContextCompat.getDrawable(mContext, R.drawable.review_relation_rounded));
            }
        }
    }

    private View.OnLongClickListener longClickListener = v -> {
        ConstraintLayout constraintLayout = findViewById(R.id.review_constraintLayout_popup);
        constraintLayout.setVisibility(View.VISIBLE);
        mOpenFlag =true;
        if (v instanceof ImageView) {
            final PhotoView photoView = findViewById(R.id.review_photo_view);
            photoView.setImageDrawable(((ImageView) v).getDrawable());
            photoView.setVisibility(View.VISIBLE);
            photoView.setOnClickListener(view -> {// view를 형변환해서 사용해도 됨.
                photoView.setImageResource(0);
                photoView.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.GONE);
                mOpenFlag =false;
            });
        } else if (v instanceof TextView) {
            final TextView textView = findViewById(R.id.review_text_view);
            final ScrollView scrollView = findViewById(R.id.review_text_scroll);
            final LinearLayout linearLayout = findViewById(R.id.review_linear_layout);
            textView.setText(((TextView) v).getText());
            scrollView.setVisibility(View.VISIBLE);

            linearLayout.setOnClickListener(view -> {
                textView.setText("");
                scrollView.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.GONE);
                mOpenFlag =false;
            });
        }
        return true;
    };
}

