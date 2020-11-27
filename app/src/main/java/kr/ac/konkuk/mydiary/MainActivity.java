package kr.ac.konkuk.mydiary;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;


import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.ac.konkuk.mydiary.data.WeatherItem;
import kr.ac.konkuk.mydiary.data.WeatherResult;

public class MainActivity extends AppCompatActivity implements OnTabItemSelectedListener, OnRequestListener, AutoPermissionsListener, MyApplication.OnResponseListener{
    private static final String TAG = "MainActivity"; // 테그 네임

    Fragment1 fragment1; //첫번째 화면
    Fragment2 fragment2; //두번째 화면
    Fragment3 fragment3; //세번째 화면

    BottomNavigationView bottomNavigation; // 하단바

    Location currentLocation; // 현재위치 객체
    GPSListener gpsListener; // gps 객체

    int locationCount = 0; //위치 정보를 확인한 횟수
    String currentWeather; //현재 날씨
    String currentAddress; //현재 주소
    String currentDateString; //현재 날짜를 스트링 형식으로
    Date currentDate; // 현재 날짝 객체

    // 데이터베이스 객체
    public static NoteDatabase mDatabase = null; // 초기화

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //객체 생성
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();

        //fragment transaction 화면 전환
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

        bottomNavigation = findViewById(R.id.bottom_navigation);
        //클릭하면 그 화면으로 전환
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab1:
                        //화면 전환 메소드
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment1).commit();
                        return true;

                    case R.id.tab2:

                        fragment2 = new Fragment2();

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment2).commit();
                        return true;

                    case R.id.tab3:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment3).commit();
                        return true;
                }
                return false;
            }
        });

        //권한 허용
        AutoPermissions.Companion.loadAllPermissions(this, 101);

        //사진 경로 설정
        setPicturePath();

        // 데이버베이스 열기
        openDatabase();

    }

    @Override
    //close database
    protected void onDestroy() {
        super.onDestroy();

        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    // open database 없으면 새로 만듬
    public void openDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = NoteDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Note database is open.");
        } else {
            Log.d(TAG, "Note database is not open.");
        }
    }

    //사진 경로 설정
    public void setPicturePath() {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        AppConstants.FOLDER_PHOTO = sdcardPath + File.separator + "photo";
    }

    public void onTabSelected(int position) {
        if(position == 0){
            bottomNavigation.setSelectedItemId(R.id.tab1);
        }else if(position == 1){
            bottomNavigation.setSelectedItemId(R.id.tab2);
        }else if(position == 2){
            bottomNavigation.setSelectedItemId(R.id.tab3);
        }
    }

    //Fragement2로 이동
    public void showFragment2(Note item) {

        fragment2 = new Fragment2();
        fragment2.setItem(item);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment2).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int requestCode, @NotNull String[] permissions) {
    }

    @Override
    public void onGranted(int requestCode, @NotNull String[] permissions) {
    }

    public void onRequest(String command) {
        if (command != null) {
            if (command.equals("getCurrentLocation")) {
                getCurrentLocation(); //위치확인 시작
            }
        }
    }

    // 현재 일자를 확인하여 두번째 fragment에 set
    public void getCurrentLocation() {
        currentDate = new Date(); //
        currentDateString = AppConstants.dateFormat3.format(currentDate);
        if (fragment2 != null) {
            fragment2.setDateString(currentDateString);
        }

        //LocationManager 객체에게 현재 위치를 요청
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currentLocation != null) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                String message = "Last Location -> Latitude : " + latitude + "\nLongitude:" + longitude;
                println(message);

                getCurrentWeather(); //현재위치를 이용해서 날씨를 확인
                getCurrentAddress(); //현재위치를 이용해서 주소를 확인
            }

            gpsListener = new GPSListener(); //요청된 위치를 수신하려고 만든 객체
            long minTime = 10000;
            float minDistance = 0;

            //10초마다 장소 정보를 계속 업데이트 따라서 10초마다 로그가 출력됨
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime, minDistance, gpsListener);

            println("Current location requested.");

        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    //LocationManager에게 위치를 요청했던 것을 stopLocatioService메소드를 호출하여 취소
    public void stopLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            manager.removeUpdates(gpsListener);

            println("Current location requested.");

        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    //locationManager 객체에게 현재 위치를 요청받은 그 요청된 위치를 수신하기위한  객체
    class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            currentLocation = location;

            locationCount++; //위치를 한번 확인한 후에는 위치 요청을 취소할 수 옸도록 위치 정보를 확인한 횟수를 담고 있을 변수

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String message = "Current Location -> Latitude : " + latitude + "\nLongitude:" + longitude;
            println(message);

            getCurrentWeather(); //현재위치를 이용해서 날씨를 확인
            getCurrentAddress(); //현재위치를 이용해서 주소를 확인

        }

        public void onProviderDisabled(String provider) { }

        public void onProviderEnabled(String provider) { }

        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    //위치가 확인되면 현재위치를 이용해서 주소를 확인
    private void getCurrentAddress() {
        //Geocoder 클래스를 이용해 현재 위치를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            currentAddress = address.getLocality() + " " + address.getSubLocality();
            String adminArea = address.getAdminArea();
            String country = address.getCountryName();
            println("Address : " + country + " " + adminArea + " " + currentAddress);

            if (fragment2 != null) {
                fragment2.setAddress(currentAddress);
            }
        }
    }

    //현재 위치를 이용해서 날씨를 확인
    private void getCurrentWeather() {
        Map<String, Double> gridMap = GridUtil.getGrid(currentLocation.getLatitude(), currentLocation.getLongitude());
        double gridX = gridMap.get("x");
        double gridY = gridMap.get("y");
        println("x -> " + gridX + ", y -> " + gridY);

        sendLocalWeatherReq(gridX, gridY);
    }

    //기상청 날씨 서버로 요청을 전송
    private void sendLocalWeatherReq(double gridX, double gridY) {
        String url = "http://www.kma.go.kr/wid/queryDFS.jsp";
        url += "?gridx=" + Math.round(gridX);
        url += "&gridy=" + Math.round(gridY);

        Map<String,String> params = new HashMap<String,String>();

        MyApplication.send(AppConstants.REQ_WEATHER_BY_GRID, Request.Method.GET, url, params, this);
    }

    //응답을 받으면 호출
    public void processResponse(int requestCode, int responseCode, String response) {
        if (responseCode == 200) {
            if (requestCode == AppConstants.REQ_WEATHER_BY_GRID) {
                // Grid 좌표를 이용한 날씨 정보 처리 응답
                //println("response -> " + response);

                XmlParserCreator parserCreator = new XmlParserCreator() {
                    @Override
                    public XmlPullParser createParser() {
                        try {
                            return XmlPullParserFactory.newInstance().newPullParser();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                //xml 응답 데티러를 자바 객체로 만들어줌
                GsonXml gsonXml = new GsonXmlBuilder()
                        .setXmlParserCreator(parserCreator)
                        .setSameNameLists(true)
                        .create();

                WeatherResult weather = gsonXml.fromXml(response, WeatherResult.class);

                // 현재 기준 시간
                try {
                    Date tmDate = AppConstants.dateFormat.parse(weather.header.tm);
                    String tmDateText = AppConstants.dateFormat2.format(tmDate);
                    println("기준 시간 : " + tmDateText);

                    for (int i = 0; i < weather.body.datas.size(); i++) {
                        WeatherItem item = weather.body.datas.get(i);
                        println("#" + i + " 시간 : " + item.hour + "시, " + item.day + "일째");
                        println("  날씨 : " + item.wfKor);
                        println("  기온 : " + item.temp + " C");
                        println("  강수확률 : " + item.pop + "%");

                        println("debug 1 : " + (int)Math.round(item.ws * 10));
                        float ws = Float.valueOf(String.valueOf((int)Math.round(item.ws * 10))) / 10.0f;
                        println("  풍속 : " + ws + " m/s");
                    }

                    // set current weather
                    WeatherItem item = weather.body.datas.get(0);
                    currentWeather = item.wfKor;
                    if (fragment2 != null) {
                        fragment2.setWeather(item.wfKor);
                    }

                    // stop request location service after 2 times
                    if (locationCount > 1) {
                        stopLocationService();
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }


            } else {
                // Unknown request code
                println("Unknown request code : " + requestCode);

            }

        } else {
            println("Failure response code : " + responseCode);

        }

    }
    //로그 출력을 위한 메소드
    private void println(String data) { Log.d(TAG, data); }

}

