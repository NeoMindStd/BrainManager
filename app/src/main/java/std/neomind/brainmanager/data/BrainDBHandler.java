package std.neomind.brainmanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class BrainDBHandler extends SQLiteOpenHelper {

    public static final String NO_MATCHING_DATA = "NoMatchingData";

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "BrainManager.db";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_KEYWORDS = "keywords";
    public static final String TABLE_RELATIONS = "relations";
    public static final String TABLE_TESTS = "tests";

    public static final String FIELD_CATEGORIES_ID = "_id";
    public static final String FIELD_CATEGORIES_NAME = "name";
    public static final String FIELD_CATEGORIES_DESCRIPTION = "description";

    public static final String FIELD_KEYWORDS_ID = "_id";
    public static final String FIELD_KEYWORDS_CID = "cid";
    public static final String FIELD_KEYWORDS_TEXT = "text";
    public static final String FIELD_KEYWORDS_IMAGE_PATH = "image_path";
    public static final String FIELD_KEYWORDS_CURRENT_LEVELS = "current_levels";
    public static final String FIELD_KEYWORDS_REVIEW_TIMES = "review_times";
    public static final String FIELD_KEYWORDS_REGISTRATION_DATE = "registration_date";

    public static final String FIELD_RELATIONS_ID = "_id";
    public static final String FIELD_RELATIONS_KID1 = "kid1";
    public static final String FIELD_RELATIONS_KID2 = "kid2";

    public static final String FIELD_TESTS_ID = "_id";
    public static final String FIELD_TESTS_CID = "cid";
    public static final String FIELD_TESTS_KID = "kid";
    public static final String FIELD_TESTS_NAME = "name";
    public static final String FIELD_TESTS_DESCRIPTION = "description";
    public static final String FIELD_TESTS_TESTED_DATE = "tested_date";
    public static final String FIELD_TESTS_PASSED = "passed";
    public static final String FIELD_TESTS_ANSWER_TIME = "answer_time";
    public static final String FIELD_TESTS_TYPE = "type";

    public BrainDBHandler(@Nullable Context context) {
        this(context, null, BrainDBHandler.DB_VERSION, null);
    }

    public BrainDBHandler(@Nullable Context context,
                           @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, version);
    }

    public BrainDBHandler(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory,
                           int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, DB_NAME, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_FOLDER_TABLE = "CREATE TABLE " +
                TABLE_CATEGORIES + "(" +
                FIELD_CATEGORIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FIELD_CATEGORIES_NAME + " TEXT, " +
                FIELD_CATEGORIES_DESCRIPTION + " TEXT" + ")";
        final String CREATE_KEYWORDS_TABLE = "CREATE TABLE " +
                TABLE_KEYWORDS + "(" +
                FIELD_KEYWORDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FIELD_KEYWORDS_CID + " INTEGER," + FIELD_KEYWORDS_TEXT + " TEXT, " +
                FIELD_KEYWORDS_IMAGE_PATH + " TEXT, " + FIELD_KEYWORDS_CURRENT_LEVELS + " INTEGER, " +
                FIELD_KEYWORDS_REVIEW_TIMES + " INTEGER, " + FIELD_KEYWORDS_REGISTRATION_DATE + " TEXT" + ")";
        final String CREATE_RELATIONS_TABLE = "CREATE TABLE " +
                TABLE_RELATIONS + "(" +
                FIELD_RELATIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FIELD_RELATIONS_KID1 + " INTEGER, " +
                FIELD_RELATIONS_KID2 + " INTEGER" + ")";
        final String CREATE_TESTS_TABLE = "CREATE TABLE " +
                TABLE_TESTS + "(" +
                FIELD_TESTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FIELD_TESTS_CID + " INTEGER, " + FIELD_TESTS_KID + " INTEGER, " +
                FIELD_TESTS_NAME + " TEXT, " + FIELD_TESTS_DESCRIPTION + " TEXT, " +
                FIELD_TESTS_TESTED_DATE + " TEXT, " + FIELD_TESTS_PASSED + " BOOLEAN," +
                FIELD_TESTS_ANSWER_TIME + " INTEGER," + FIELD_TESTS_TYPE + " INTEGER" + ")";

        db.execSQL(CREATE_FOLDER_TABLE);
        db.execSQL(CREATE_KEYWORDS_TABLE);
        db.execSQL(CREATE_RELATIONS_TABLE);
        db.execSQL(CREATE_TESTS_TABLE);
    }

    // TODO 만약 db 구조를 변경한다면, 이전 db 내용을 복사하고 기존 db를 삭제한 후 새로운 양식대로 만드는 루틴 필요
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    /**
     * DB 데이터 추가 메서드
     */
    public void addCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put(FIELD_CATEGORIES_NAME, category.name);
        values.put(FIELD_CATEGORIES_DESCRIPTION, category.description);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_CATEGORIES, null, values);
        db.close();
    }

    public void addKeyword(Keyword keyword) {
        ContentValues values = new ContentValues();
        values.put(FIELD_KEYWORDS_CID, keyword.cid);
        values.put(FIELD_KEYWORDS_TEXT, keyword.text);
        values.put(FIELD_KEYWORDS_IMAGE_PATH, keyword.imagePath);
        values.put(FIELD_KEYWORDS_CURRENT_LEVELS, keyword.currentLevels);
        values.put(FIELD_KEYWORDS_REVIEW_TIMES, keyword.reviewTimes);
        values.put(FIELD_KEYWORDS_REGISTRATION_DATE, keyword.getRegistrationDate());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_KEYWORDS, null, values);
        db.close();
    }

    public void addRelation(Relation relation) {
        ContentValues values = new ContentValues();
        values.put(FIELD_RELATIONS_KID1, relation.kid1);
        values.put(FIELD_RELATIONS_KID2, relation.kid2);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_RELATIONS, null, values);
        db.close();
    }

    public void addTest(Test test) {
        ContentValues values = new ContentValues();
        values.put(FIELD_TESTS_CID, test.cid);
        values.put(FIELD_TESTS_KID, test.kid);
        values.put(FIELD_TESTS_NAME, test.name);
        values.put(FIELD_TESTS_DESCRIPTION, test.description);
        values.put(FIELD_TESTS_TESTED_DATE, test.getTestedDate());
        values.put(FIELD_TESTS_PASSED, test.isPassed());
        values.put(FIELD_TESTS_ANSWER_TIME, test.answerTime);
        values.put(FIELD_TESTS_TYPE, test.type);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_TESTS, null, values);
        db.close();
    }

    /**
     * DB 전체 탐색
     */
    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Category category = new Category.Builder()
                        .setId(cursor.getInt(0))
                        .setName(cursor.getString(1))
                        .build();
                categories.add(category);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return categories;
    }

    public ArrayList<Keyword> getAllKeywords() {
        ArrayList<Keyword> keywords = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(("SELECT * FROM " + TABLE_KEYWORDS), null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Keyword keyword = new Keyword.Builder()
                        .setId(cursor.getInt(0))
                        .setCid(cursor.getInt(1))
                        .setText(cursor.getString(2))
                        .setImagePath(cursor.getString(3))
                        .setCurrentLevels(cursor.getInt(4))
                        .setReviewTimes(cursor.getInt(5))
                        .setRegistrationDate(cursor.getString(6))
                        .build();
                keywords.add(keyword);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return keywords;
    }

    public ArrayList<Relation> getAllRelations() {
        ArrayList<Relation> relations = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RELATIONS, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Relation relation = new Relation.Builder()
                        .setId(cursor.getInt(0))
                        .setKid1(cursor.getInt(1))
                        .setKid2(cursor.getInt(2))
                        .build();
                relations.add(relation);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return relations;
    }

    public ArrayList<Test> getAllTests() {
        ArrayList<Test> tests= new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RELATIONS, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Test test = new Test.Builder()
                        .setId(cursor.getInt(0))
                        .setCid(cursor.getInt(1))
                        .setKid(cursor.getInt(2))
                        .setName(cursor.getString(3))
                        .setDescription(cursor.getString(4))
                        .setTestedDate(cursor.getString(5))
                        .setPassed(cursor.getInt(6) > 0)
                        .setAnswerTime(cursor.getInt(7))
                        .setType(cursor.getInt(8))
                        .build();
                tests.add(test);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return tests;
    }

    /**
     * DB 탐색
     */
    private Category findCategory(String query) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Category category;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            category = new Category.Builder()
                    .setId(cursor.getInt(0))
                    .setName(cursor.getString(1))
                    .setDescription(cursor.getString(2))
                    .build();
            cursor.close();
        } else { throw new Exception(NO_MATCHING_DATA); }
        db.close();
        return category;
    }

    public Category findCategory(String field, int value) throws Exception {
        String query = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " +
                field + " = " + value;
        return findCategory(query);
    }

    public Category findCategory(String field, String value) throws Exception {
        String query = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " +
                field + " = \"" + value + "\"";
        return findCategory(query);
    }

    private Keyword findKeyword(String query) throws Exception {
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor = db.rawQuery(query, null);

    Keyword keyword;
    if (cursor.moveToFirst()) {
        cursor.moveToFirst();
        keyword = new Keyword.Builder()
                .setId(cursor.getInt(0))
                .setCid(cursor.getInt(1))
                .setText(cursor.getString(2))
                .setImagePath(cursor.getString(3))
                .setCurrentLevels(cursor.getInt(4))
                .setReviewTimes(cursor.getInt(5))
                .setRegistrationDate(cursor.getString(6))
                .build();
        cursor.close();
    } else { throw new Exception(NO_MATCHING_DATA); }
    db.close();
    return keyword;
}

    public Keyword findKeyword(String field, int value) throws Exception {
        String query = "SELECT * FROM " + TABLE_KEYWORDS + " WHERE " +
                field + " = " + value;
        return findKeyword(query);
    }

    public Keyword findKeyword(String field, String value) throws Exception {
        String query = "SELECT * FROM " + TABLE_KEYWORDS + " WHERE " +
                field + " = \"" + value + "\"";
        return findKeyword(query);
    }

    private Relation findRelation(String query) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Relation relation;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            relation = new Relation.Builder()
                    .setId(cursor.getInt(0))
                    .setKid1(cursor.getInt(1))
                    .setKid2(cursor.getInt(2))
                    .build();
            cursor.close();
        } else { throw new Exception(NO_MATCHING_DATA); }
        db.close();
        return relation;
    }

    public Relation findRelation(String field, int value) throws Exception {
        String query = "SELECT * FROM " + TABLE_RELATIONS + " WHERE " +
                field + " = " + value;
        return findRelation(query);
    }

    public Relation findRelation(String field, String value) throws Exception {
        String query = "SELECT * FROM " + TABLE_RELATIONS + " WHERE " +
                field + " = \"" + value + "\"";
        return findRelation(query);
    }

    private Test findTest(String query) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Test test;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            test = new Test.Builder()
                    .setId(cursor.getInt(0))
                    .setCid(cursor.getInt(1))
                    .setKid(cursor.getInt(2))
                    .setName(cursor.getString(3))
                    .setDescription(cursor.getString(4))
                    .setTestedDate(cursor.getString(5))
                    .setPassed(cursor.getInt(6) > 0)
                    .setAnswerTime(cursor.getInt(7))
                    .setType(cursor.getInt(8))
                    .build();
            cursor.close();
        } else { throw new Exception(NO_MATCHING_DATA); }
        db.close();
        return test;
    }

    public Test findTest(String field, int value) throws Exception {
        String query = "SELECT * FROM " + TABLE_TESTS + " WHERE " +
                field + " = " + value;
        return findTest(query);
    }

    public Test findTest(String field, String value) throws Exception {
        String query = "SELECT * FROM " + TABLE_TESTS + " WHERE " +
                field + " = \"" + value + "\"";
        return findTest(query);
    }

    public Test findTest(String field, boolean value) throws Exception {
        String query = "SELECT * FROM " + TABLE_TESTS + " WHERE " +
                field + " = " + (value ? 1 : 0);
        return findTest(query);
    }

    /**
     * DB 업데이트 메소드
     *
     * @param table       TABLE_CATEGORIES, TABLE_KEYWORDS, TABLE_RELATIONS, TABLE_TESTS
     * @param values      want to change to.
     * @param whereClause change condition.
     * @param whereArgs   change conditions.
     *                    ex)
     *                    db.update("테이블명", value, where 구문 "컬럼명 = 키", null);
     */
    public void updateObject(String table, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.update(table, values, whereClause, whereArgs);
        db.close();
    }

    public void updateCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put(FIELD_CATEGORIES_NAME, category.name);
        values.put(FIELD_CATEGORIES_DESCRIPTION, category.description);
        updateObject(TABLE_CATEGORIES, values,
                FIELD_CATEGORIES_ID + "=" + category.id, null);
    }

    public void updateKeyword(Keyword keyword) {
        ContentValues values = new ContentValues();
        values.put(FIELD_KEYWORDS_CID, keyword.cid);
        values.put(FIELD_KEYWORDS_TEXT, keyword.text);
        values.put(FIELD_KEYWORDS_IMAGE_PATH, keyword.imagePath);
        values.put(FIELD_KEYWORDS_CURRENT_LEVELS, keyword.currentLevels);
        values.put(FIELD_KEYWORDS_REVIEW_TIMES, keyword.reviewTimes);
        values.put(FIELD_KEYWORDS_REGISTRATION_DATE, keyword.getRegistrationDate());
        updateObject(TABLE_KEYWORDS, values,
                FIELD_KEYWORDS_ID + "=" + keyword.id, null);
    }

    public void updateRelation(Relation relation) {
        ContentValues values = new ContentValues();
        values.put(FIELD_RELATIONS_KID1, relation.kid1);
        values.put(FIELD_RELATIONS_KID2, relation.kid2);
        updateObject(TABLE_RELATIONS, values,
                FIELD_RELATIONS_ID + "=" + relation.id, null);
    }

    public void updateTest(Test test) {
        ContentValues values = new ContentValues();
        values.put(FIELD_TESTS_CID, test.cid);
        values.put(FIELD_TESTS_KID, test.kid);
        values.put(FIELD_TESTS_NAME, test.name);
        values.put(FIELD_TESTS_DESCRIPTION, test.description);
        values.put(FIELD_TESTS_TESTED_DATE, test.getTestedDate());
        values.put(FIELD_TESTS_PASSED, test.isPassed());
        values.put(FIELD_TESTS_ANSWER_TIME, test.answerTime);
        values.put(FIELD_TESTS_TYPE, test.type);
        updateObject(TABLE_TESTS, values,
                FIELD_TESTS_ID + "=" + test.id, null);
    }

    /**
     * DB 데이터 삭제 메소드
     * @param id
     * @return
     */
    public boolean removeCategory(int id) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " +
                FIELD_CATEGORIES_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            db.delete(TABLE_CATEGORIES, FIELD_CATEGORIES_ID + " = ?",
                    new String[]{String.valueOf(id)});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean removeKeyword(int id) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_KEYWORDS + " WHERE " +
                FIELD_KEYWORDS_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            db.delete(TABLE_KEYWORDS, FIELD_KEYWORDS_ID + " = ?",
                    new String[]{String.valueOf(id)});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean removeRelation(int id) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_RELATIONS + " WHERE " +
                FIELD_RELATIONS_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            db.delete(TABLE_RELATIONS, FIELD_RELATIONS_ID + " = ?",
                    new String[]{String.valueOf(id)});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean removeTest(int id) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_TESTS + " WHERE " +
                FIELD_TESTS_ID+ " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            db.delete(TABLE_TESTS, FIELD_TESTS_ID + " = ?",
                    new String[]{String.valueOf(id)});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }
}