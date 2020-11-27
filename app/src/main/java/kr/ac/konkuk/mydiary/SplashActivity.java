package kr.ac.konkuk.mydiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

public class SplashActivity extends Activity {

    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //1초 동안 스플래쉬 화면을 보여주기 위한 핸들러
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, 1000);
    }

}
