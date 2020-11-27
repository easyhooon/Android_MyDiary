package kr.ac.konkuk.mydiary;

import android.app.Application;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        if (requestQueue == null) {
            //requestQueue 객체 생성 , 변수 할당
            requestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack() {
                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    HttpURLConnection connection = super.createConnection(url);
                    connection.setInstanceFollowRedirects(false);

                    return connection;
                }
            });
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    } //종료

    //volley에서 응답을 받는 경우
    public static interface OnResponseListener {
        //호출
        //send()메소드를 호출한 쪽에서 응답 결과를 처리할 수 있도록
        public void processResponse(int requestCode, int responseCode, String response);
    }

    //요청 객체 생성, 요청 처리
    public static void send(final int requestCode, final int requestMethod, final String url, final Map<String,String> params,
                            final OnResponseListener listener) {

        StringRequest request = new StringRequest(
                requestMethod,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response for " + requestCode + " -> " + response);

                        if (listener != null) {
                            listener.processResponse(requestCode, 200, response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error for " + requestCode + " -> " + error.getMessage());

                        if (listener != null) {
                            listener.processResponse(requestCode, 400, error.getMessage());
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.requestQueue.add(request);
        Log.d(TAG, "Request sent : " + requestCode);
        Log.d(TAG, "Request url : " + url);
    }
}
