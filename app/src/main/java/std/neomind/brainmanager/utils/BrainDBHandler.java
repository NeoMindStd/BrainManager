package std.neomind.brainmanager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;

import std.neomind.brainmanager.data.Category;
import std.neomind.brainmanager.data.Description;
import std.neomind.brainmanager.data.Keyword;
import std.neomind.brainmanager.data.Test;

public class BrainDBHandler extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "BrainManager.db";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_KEYWORDS = "keywords";
    public static final String TABLE_KEYWORD_DESCRIPTIONS = "keyword_descriptions";
    public static final String TABLE_RELATIONS = "relations";
    public static final String TABLE_TESTS = "tests";

    // '_' means super key
    public static final String FIELD_CATEGORIES_ID = "_id";
    public static final String FIELD_CATEGORIES_NAME = "name";

    public static final String FIELD_KEYWORDS_ID = "_id";
    public static final String FIELD_KEYWORDS_CID = "cid";
    public static final String FIELD_KEYWORDS_NAME = "name";
    public static final String FIELD_KEYWORDS_IMAGE_PATH = "image_path";
    public static final String FIELD_KEYWORDS_CURRENT_LEVELS = "current_levels";
    public static final String FIELD_KEYWORDS_REVIEW_TIMES = "review_times";
    public static final String FIELD_KEYWORDS_REGISTRATION_DATE = "registration_date";
    public static final String FIELD_KEYWORDS_EF = "ef";
    public static final String FIELD_KEYWORDS_INTERVAL = "interval";

    public static final String FIELD_KEYWORD_DESCRIPTIONS_ID = "_id";
    public static final String FIELD_KEYWORD_DESCRIPTIONS_DESCRIPTION = "description";
    public static final String FIELD_KEYWORD_DESCRIPTIONS_KID = "kid";

    public static final String FIELD_RELATIONS_KID1 = "kid1";
    public static final String FIELD_RELATIONS_KID2 = "kid2";

    public static final String FIELD_TESTS_ID = "_id";
    public static final String FIELD_TESTS_CID = "cid";
    public static final String FIELD_TESTS_KID = "kid";
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
                FIELD_CATEGORIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                FIELD_CATEGORIES_NAME + " TEXT" + ")";

        final String CREATE_KEYWORDS_TABLE = "CREATE TABLE " +
                TABLE_KEYWORDS + "(" +
                FIELD_KEYWORDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                FIELD_KEYWORDS_CID + " INTEGER NOT NULL REFERENCES " + TABLE_CATEGORIES + "(" + FIELD_CATEGORIES_ID + "), " +
                FIELD_KEYWORDS_NAME + " TEXT, " + FIELD_KEYWORDS_IMAGE_PATH + " TEXT, " +
                FIELD_KEYWORDS_CURRENT_LEVELS + " INTEGER, " + FIELD_KEYWORDS_REVIEW_TIMES + " INTEGER, " +
                FIELD_KEYWORDS_REGISTRATION_DATE + " INTEGER, " + FIELD_KEYWORDS_EF + " REAL, " +
                FIELD_KEYWORDS_INTERVAL + " INTEGER" + ")";

        final String CREATE_KEYWORD_DESCRIPTIONS_TABLE = "CREATE TABLE " +
                TABLE_KEYWORD_DESCRIPTIONS + "(" +
                FIELD_KEYWORD_DESCRIPTIONS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                FIELD_KEYWORD_DESCRIPTIONS_DESCRIPTION + " TEXT, " +
                FIELD_KEYWORD_DESCRIPTIONS_KID + " INTEGER NOT NULL REFERENCES " + TABLE_KEYWORDS + "(" + FIELD_KEYWORDS_ID + ") ON DELETE CASCADE" + ")";

        final String CREATE_RELATIONS_TABLE = "CREATE TABLE " +
                TABLE_RELATIONS + "(" +
                FIELD_RELATIONS_KID1 + " INTEGER NOT NULL, " + FIELD_RELATIONS_KID2 + " INTEGER NOT NULL, " +
                "PRIMARY KEY (" + FIELD_RELATIONS_KID1 + ", " + FIELD_RELATIONS_KID2 + "))";

        final String CREATE_TESTS_TABLE = "CREATE TABLE " +
                TABLE_TESTS + "(" +
                FIELD_TESTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                FIELD_TESTS_CID + " INTEGER NOT NULL REFERENCES " + TABLE_CATEGORIES + "(" + FIELD_CATEGORIES_ID + "), " +
                FIELD_TESTS_KID + " INTEGER NOT NULL REFERENCES " + TABLE_KEYWORDS + "(" + FIELD_KEYWORDS_ID + "), " +
                FIELD_TESTS_TESTED_DATE + " INTEGER, " + FIELD_TESTS_PASSED + " BOOLEAN," +
                FIELD_TESTS_ANSWER_TIME + " INTEGER," + FIELD_TESTS_TYPE + " INTEGER" + ")";

        db.execSQL(CREATE_FOLDER_TABLE);
        db.execSQL(CREATE_KEYWORDS_TABLE);
        db.execSQL(CREATE_KEYWORD_DESCRIPTIONS_TABLE);
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

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_CATEGORIES, null, values);
        db.close();
    }

    public void addKeyword(Keyword keyword) {
        ContentValues values = new ContentValues();
        values.put(FIELD_KEYWORDS_CID, keyword.cid);
        values.put(FIELD_KEYWORDS_NAME, keyword.name);
        values.put(FIELD_KEYWORDS_IMAGE_PATH, keyword.imagePath);
        values.put(FIELD_KEYWORDS_CURRENT_LEVELS, keyword.currentLevels);
        values.put(FIELD_KEYWORDS_REVIEW_TIMES, keyword.reviewTimes);
        values.put(FIELD_KEYWORDS_REGISTRATION_DATE, keyword.registrationDate);
        values.put(FIELD_KEYWORDS_EF, keyword.ef);
        values.put(FIELD_KEYWORDS_INTERVAL, keyword.interval);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_KEYWORDS, null, values);
        db.close();
    }

    public void addDescription(Description description, int kid) {
        ContentValues values = new ContentValues();
        values.put(FIELD_KEYWORD_DESCRIPTIONS_DESCRIPTION, description.description);
        values.put(FIELD_KEYWORD_DESCRIPTIONS_KID, kid);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_CATEGORIES, null, values);
        db.close();
    }

    public void addRelation(int kid1, int kid2) throws DataDuplicationException {
        if (kid1 == kid2) throw new DataDuplicationException();
        else {
            ContentValues values = new ContentValues();

            values.put(FIELD_RELATIONS_KID1, Math.min(kid1, kid2));
            values.put(FIELD_RELATIONS_KID2, Math.max(kid1, kid2));

            SQLiteDatabase db = this.getWritableDatabase();

            db.insert(TABLE_RELATIONS, null, values);
            db.close();
        }
    }

    public void addTest(Test test) {
        ContentValues values = new ContentValues();
        values.put(FIELD_TESTS_CID, test.cid);
        values.put(FIELD_TESTS_KID, test.kid);
        values.put(FIELD_TESTS_TESTED_DATE, test.testedDate);
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
                        .setName(cursor.getString(2))
                        .setDescriptions(
                                getAllDescriptionsOfTheKeyword(cursor.getInt(0)))
                        .setImagePath(cursor.getString(3))
                        .setCurrentLevels(cursor.getInt(4))
                        .setReviewTimes(cursor.getInt(5))
                        .setRegistrationDate(cursor.getLong(6))
                        .setRelationIds(
                                getAllRelationsOfTheKeyword(cursor.getInt(0)))
                        .setEF(cursor.getDouble(7))
                        .setInterval(cursor.getInt(8))
                        .build();
                keywords.add(keyword);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return keywords;
    }

    public ArrayList<Keyword> getAllKeywordsOfTheCategory(int cid) {
        if(cid == Category.CATEGORY_ALL) {
          return getAllKeywords();
        } else {
            ArrayList<Keyword> keywords = new ArrayList<>();

            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_KEYWORDS + " WHERE " +
                    FIELD_KEYWORDS_CID + " = " + cid;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Keyword keyword = new Keyword.Builder()
                            .setId(cursor.getInt(0))
                            .setCid(cursor.getInt(1))
                            .setName(cursor.getString(2))
                            .setDescriptions(
                                    getAllDescriptionsOfTheKeyword(cursor.getInt(0)))
                            .setImagePath(cursor.getString(3))
                            .setCurrentLevels(cursor.getInt(4))
                            .setReviewTimes(cursor.getInt(5))
                            .setRegistrationDate(cursor.getLong(6))
                            .setRelationIds(
                                    getAllRelationsOfTheKeyword(cursor.getInt(0)))
                            .setEF(cursor.getDouble(7))
                            .setInterval(cursor.getInt(8))
                            .build();
                    keywords.add(keyword);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            db.close();

            return keywords;
        }
    }

    public ArrayList<Description> getAllDescriptionsOfTheKeyword(int kid) {
        ArrayList<Description> descriptions = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_KEYWORD_DESCRIPTIONS + " WHERE " +
                FIELD_KEYWORD_DESCRIPTIONS_KID + " = " + kid;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Description description = new Description();
                description.id = cursor.getInt(0);
                description.description = cursor.getString(1);
                descriptions.add(description);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return descriptions;
    }

    public ArrayList<Integer> getAllRelationsOfTheKeyword(int kid) {
        ArrayList<Integer> relationKids = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_RELATIONS + " WHERE " +
                FIELD_RELATIONS_KID1 + " = " + kid +" OR " +
                FIELD_RELATIONS_KID2 + " = " + kid;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int relationKid = cursor.getInt(0);
                if(relationKid == kid) cursor.getInt(1);
                relationKids.add(relationKid);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return relationKids;
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
                        .setTestedDate(cursor.getLong(2))
                        .setPassed(cursor.getInt(3) > 0)
                        .setAnswerTime(cursor.getInt(4))
                        .setType(cursor.getInt(5))
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
    private Category findCategory(String query) throws NoMatchingDataException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Category category;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            category = new Category.Builder()
                    .setId(cursor.getInt(0))
                    .setName(cursor.getString(1))
                    .build();
            cursor.close();
        } else { throw new NoMatchingDataException(); }
        db.close();
        return category;
    }

    public Category findCategory(String field, int value) throws NoMatchingDataException {
        String query = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " +
                field + " = " + value;
        return findCategory(query);
    }

    public Category findCategory(String field, String value) throws NoMatchingDataException {
        String query = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " +
                field + " = \"" + value + "\"";
        return findCategory(query);
    }

    private Keyword findKeyword(String query) throws NoMatchingDataException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Keyword keyword;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            keyword = new Keyword.Builder()
                    .setId(cursor.getInt(0))
                    .setCid(cursor.getInt(1))
                    .setName(cursor.getString(2))
                    .setImagePath(cursor.getString(3))
                    .setCurrentLevels(cursor.getInt(4))
                    .setReviewTimes(cursor.getInt(5))
                    .setRegistrationDate(cursor.getLong(6))
                    .setEF(cursor.getDouble(7))
                    .setInterval(cursor.getInt(8))
                    .build();
            cursor.close();
        } else { throw new NoMatchingDataException(); }
        db.close();
        return keyword;
    }

    public Keyword findKeyword(String field, int value) throws NoMatchingDataException {
        String query = "SELECT * FROM " + TABLE_KEYWORDS + " WHERE " +
                field + " = " + value;
        return findKeyword(query);
    }

    public Keyword findKeyword(String field, String value) throws NoMatchingDataException {
        String query = "SELECT * FROM " + TABLE_KEYWORDS + " WHERE " +
                field + " = \"" + value + "\"";
        return findKeyword(query);
    }

    private Description findDescription(String query) throws NoMatchingDataException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Description description;
        if (cursor.moveToFirst()) {
            description = new Description();
            description.id = cursor.getInt(0);
            description.description = cursor.getString(1);
            cursor.close();
        } else { throw new NoMatchingDataException(); }
        db.close();
        return description;
    }

    public Description findDescription(String field, int value) throws NoMatchingDataException {
        String query = "SELECT * FROM " + TABLE_KEYWORD_DESCRIPTIONS + " WHERE " +
                field + " = " + value;
        return findDescription(query);
    }

    public Description findDescription(String field, String value) throws NoMatchingDataException {
        String query = "SELECT * FROM " + TABLE_KEYWORD_DESCRIPTIONS + " WHERE " +
                field + " = \"" + value + "\"";
        return findDescription(query);
    }

    private Test findTest(String query) throws NoMatchingDataException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Test test;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            test = new Test.Builder()
                    .setId(cursor.getInt(0))
                    .setCid(cursor.getInt(1))
                    .setKid(cursor.getInt(2))
                    .setTestedDate(cursor.getLong(2))
                    .setPassed(cursor.getInt(3) > 0)
                    .setAnswerTime(cursor.getInt(4))
                    .setType(cursor.getInt(5))
                    .build();
            cursor.close();
        } else { throw new NoMatchingDataException(); }
        db.close();
        return test;
    }

    public Test findTest(String field, int value) throws NoMatchingDataException {
        String query = "SELECT * FROM " + TABLE_TESTS + " WHERE " +
                field + " = " + value;
        return findTest(query);
    }

    public Test findTest(String field, boolean value) throws NoMatchingDataException {
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
        updateObject(TABLE_CATEGORIES, values,
                FIELD_CATEGORIES_ID + "=" + category.id, null);
    }

    public void updateKeyword(Keyword keyword) {
        ContentValues values = new ContentValues();
        values.put(FIELD_KEYWORDS_CID, keyword.cid);
        values.put(FIELD_KEYWORDS_NAME, keyword.name);
        values.put(FIELD_KEYWORDS_IMAGE_PATH, keyword.imagePath);
        values.put(FIELD_KEYWORDS_CURRENT_LEVELS, keyword.currentLevels);
        values.put(FIELD_KEYWORDS_REVIEW_TIMES, keyword.reviewTimes);
        values.put(FIELD_KEYWORDS_REGISTRATION_DATE, keyword.registrationDate);
        values.put(FIELD_KEYWORDS_EF, keyword.ef);
        values.put(FIELD_KEYWORDS_INTERVAL, keyword.interval);
        updateObject(TABLE_KEYWORDS, values,
                FIELD_KEYWORDS_ID + "=" + keyword.id, null);
    }

    public void updateDescription(Description description, int kid) {
        ContentValues values = new ContentValues();
        values.put(FIELD_KEYWORD_DESCRIPTIONS_DESCRIPTION, description.description);
        values.put(FIELD_KEYWORD_DESCRIPTIONS_KID, kid);
        updateObject(TABLE_RELATIONS, values,
                FIELD_KEYWORD_DESCRIPTIONS_ID + " = " + description.id, null);
    }

    // Relations 테이블은 레코드를 갱신하지 않음.

    public void updateTest(Test test) {
        ContentValues values = new ContentValues();
        values.put(FIELD_TESTS_CID, test.cid);
        values.put(FIELD_TESTS_KID, test.kid);
        values.put(FIELD_TESTS_TESTED_DATE, test.testedDate);
        values.put(FIELD_TESTS_PASSED, test.isPassed());
        values.put(FIELD_TESTS_ANSWER_TIME, test.answerTime);
        values.put(FIELD_TESTS_TYPE, test.type);
        updateObject(TABLE_TESTS, values,
                FIELD_TESTS_ID + "=" + test.id, null);
    }

    /**
     * DB 데이터 삭제 메소드
     * @param id 삭제할 레코드 id
     * @return result = 성공여부
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

    public boolean removeDescription(int id) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_KEYWORD_DESCRIPTIONS + " WHERE " +
                FIELD_KEYWORD_DESCRIPTIONS_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            db.delete(TABLE_KEYWORD_DESCRIPTIONS,
                    FIELD_KEYWORD_DESCRIPTIONS_ID + " = ?",
                    new String[]{String.valueOf(id)});
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean removeRelation(int kid1, int kid2) {
        boolean result = false;

        String query = "SELECT * FROM " + TABLE_RELATIONS + " WHERE " +
                FIELD_RELATIONS_KID1 + " = " + kid1 +" AND " +
                FIELD_RELATIONS_KID2 + " = " + kid2;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            db.delete(TABLE_RELATIONS,
                    FIELD_RELATIONS_KID1 + " = ? AND " + FIELD_RELATIONS_KID2 + " = ?",
                    new String[]{String.valueOf(kid1), String.valueOf(kid2)});
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

    public class NoMatchingDataException extends Exception { }
    public class DataDuplicationException extends Exception { }
}