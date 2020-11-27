package kr.ac.konkuk.mydiary;

import android.os.Handler;
import android.util.Log;

import java.text.SimpleDateFormat;

//소스 코드에서 사용하는 주요 상수들
public class AppConstants {
    private static final String TAG = "AppConstants";

    public static final int REQ_LOCATION_BY_ADDRESS = 101;
    public static final int REQ_WEATHER_BY_GRID = 102;

    public static final int REQ_PHOTO_CAPTURE = 103; //사진 촬영
    public static final int REQ_PHOTO_SELECTION = 104; //사진 앨범

    public static final int CONTENT_PHOTO = 105;
    public static final int CONTENT_PHOTO_EX = 106;

    public static String FOLDER_PHOTO;//사진 저장 경로

    public static String DATABASE_NAME = "note.db";

    public static final int MODE_INSERT = 1; //일기 작성
    public static final int MODE_MODIFY = 2;//일기 수정

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    public static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH시");
    public static SimpleDateFormat dateFormat3 = new SimpleDateFormat("MM월 dd일");
    public static SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat dateFormat5 = new SimpleDateFormat("yyyy-MM-dd");


    private static Handler handler = new Handler();

    public static void println(final String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, data);
            }
        });
    }
}
