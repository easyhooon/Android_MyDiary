package kr.ac.konkuk.mydiary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//메모 데이터베이스
public class NoteDatabase {
    private static final String TAG = "NoteDatabase";

    //싱글톤 인스턴스
    private static NoteDatabase database;

    //테이블 이름
    public static String TABLE_NOTE = "NOTE";
    //버전
    public static int DATABASE_VERSION = 1;

    //Helper class 정의
    private DatabaseHelper dbHelper;
    //SQLiteDatabase 인스턴스
    private SQLiteDatabase db;
    //Context 객체
    private Context context;

    //생성자
    private NoteDatabase(Context context) {
        this.context = context;
    }

    //인스턴스 가져오기
    public static NoteDatabase getInstance(Context context) {
        if (database == null) {
            database = new NoteDatabase(context);
        }

        return database;
    }

    //데이터베이스 열기
    public boolean open() {
        println("opening database [" + AppConstants.DATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    //데이터 베이스 닫기
    public void close() {
        println("closing database [" + AppConstants.DATABASE_NAME + "].");
        db.close();

        database = null;
    }

    //execute raw query using the input SQL
    //close the cursor after fetching any result
    public Cursor rawQuery(String SQL) {
        println("\nexecuteQuery called.\n");

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SQL, null);
            println("cursor count : " + cursor.getCount());
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
        }

        return cursor;
    }

    //exec SQL
    public boolean execSQL(String SQL) {
        println("\nexecute called.\n");

        try {
            Log.d(TAG, "SQL : " + SQL);
            db.execSQL(SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
            return false;
        }

        return true;
    }

    //Database Helper inner class

    //SQLiteOpenHelper를 상속받기 때문에 새로 데이터베이스가 만들어져야 하는 상태에서는 onCreate 메소드가 자동으로 호출 됨
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, AppConstants.DATABASE_NAME, null, DATABASE_VERSION);
        }

        //Note테이블을 만들고 CREATE_TABLE칼럼에 인덱스를 생성
        public void onCreate(SQLiteDatabase db) {
            println("creating database [" + AppConstants.DATABASE_NAME + "].");

            //Note 테이블 생성
            println("creating table [" + TABLE_NOTE + "].");

            //기존의 존재하는 table을 drop
            String DROP_SQL = "drop table if exists " + TABLE_NOTE;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in DROP_SQL", ex);
            }

            //create table
            String CREATE_SQL = "create table " + TABLE_NOTE + "("
                    + "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "  WEATHER TEXT DEFAULT '', "
                    + "  ADDRESS TEXT DEFAULT '', "
                    + "  LOCATION_X TEXT DEFAULT '', "
                    + "  LOCATION_Y TEXT DEFAULT '', "
                    + "  CONTENTS TEXT DEFAULT '', "
                    + "  MOOD TEXT, "
                    + "  PICTURE TEXT DEFAULT '', "
                    + "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "  MODIFY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                    + ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            //CREATE_DATE 칼럼에 인덱스를 생성
            String CREATE_INDEX_SQL = "create index " + TABLE_NOTE + "_IDX ON " + TABLE_NOTE + "("
                    + "CREATE_DATE"
                    + ")";
            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_INDEX_SQL", ex);
            }
        }

        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + AppConstants.DATABASE_NAME + "].");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");
        }
    }

    private void println(String msg) {
        Log.d(TAG, msg);
    }


}