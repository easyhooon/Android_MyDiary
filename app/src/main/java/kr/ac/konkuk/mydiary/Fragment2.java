package kr.ac.konkuk.mydiary;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.channguyen.rsv.RangeSliderView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Fragment2 extends Fragment {
    private static final String TAG = "Fragment2";

    Context context;
    OnTabItemSelectedListener listener;
    OnRequestListener requestListener;

    ImageView weatherIcon;
    TextView dateTextView;
    TextView locationTextView;

    EditText contentsInput;
    ImageView pictureImageView;

    boolean isPhotoCaptured;
    boolean isPhotoFileSaved;
    boolean isPhotoCanceled;

    int selectedPhotoMenu;

    File file;
    Bitmap resultPhotoBitmap;

    int mMode = AppConstants.MODE_INSERT; //일기를 새로 만드는지 아니면 기존 일기를 수정하는지를 구분하기 위한 구분자 값을 담고 있음

    int _id = -1; //where절에 들어가는 키

    int weatherIndex = 0;

    RangeSliderView moodSlider;
    int moodIndex = 2;

    Note item;

    //onAttach-> 전달받은 액티비티 객체가 OnRequestListener 인터페이스를 구현하고 있을 경우 requestListener변수에 할당
    //그리고 onCreateView()안에서 OnRequestListener 객체의 onRequest()메소드를 호출하도록 함
    //이것으로 입력화면이 보일때마다 현재 위치를 확인할 수 있도록 MainActivity클래스의 onRequest()메서드가 호출됨
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;

        if (context instanceof OnTabItemSelectedListener) {
            listener = (OnTabItemSelectedListener) context;
        }

        if (context instanceof  OnRequestListener){
            requestListener = (OnRequestListener) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (context != null) {
            context = null;
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        initUI(rootView);

        // check current location
        if (requestListener != null) {
            requestListener.onRequest("getCurrentLocation");
        }

        applyItem();

        return rootView;

    }

    private void initUI(ViewGroup rootView) {

        weatherIcon = rootView.findViewById(R.id.weatherIcon);
        dateTextView = rootView.findViewById(R.id.dateTextView);
        locationTextView = rootView.findViewById(R.id.locationTextView);

        contentsInput = rootView.findViewById(R.id.contentsInput);
        pictureImageView = rootView.findViewById(R.id.pictureImageView);
        pictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPhotoCaptured || isPhotoFileSaved) {
                    showDialog(AppConstants.CONTENT_PHOTO_EX);
                } else {
                    showDialog(AppConstants.CONTENT_PHOTO);
                }
            }
        });

        moodSlider = rootView.findViewById(R.id.sliderView);

        Button saveButton = rootView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mMode == AppConstants.MODE_INSERT) {
                    saveNote();
                } else if(mMode == AppConstants.MODE_MODIFY) {
                    modifyNote();
                }


                if (listener != null) {
                    listener.onTabSelected(0);
                }
            }
        });

        Button deleteButton = rootView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteNote();

                if (listener != null) {
                    listener.onTabSelected(0);
                }
            }
        });

        Button closeButton = rootView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTabSelected(0);
                }
            }
        });


        RangeSliderView sliderView = rootView.findViewById(R.id.sliderView);
        final RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                AppConstants.println("moodIndex changed to " + index);
                moodIndex = index;
            }
        };

        sliderView.setOnSlideListener(listener);
        sliderView.setInitialIndex(2);
    }


//     0. 맑음
//     1. 구름 조금
//     2. 구름 많음
//     3. 흐림
//     4. 비
//     5. 눈/비
//     6.  눈


    //기상청의 현재 날씨 문자열을 받아 아이콘을 설정하는 역할
    public void setWeather(String data) {
        if (data != null) {
            if (data.equals("맑음")) {
                weatherIcon.setImageResource(R.drawable.weather_1);
            } else if (data.equals("구름 조금")) {
                weatherIcon.setImageResource(R.drawable.weather_2);
            } else if (data.equals("구름 많음")) {
                weatherIcon.setImageResource(R.drawable.weather_3);
            } else if (data.equals("흐림")) {
                weatherIcon.setImageResource(R.drawable.weather_4);
            } else if (data.equals("비")) {
                weatherIcon.setImageResource(R.drawable.weather_5);
            } else if (data.equals("눈/비")) {
                weatherIcon.setImageResource(R.drawable.weather_6);
            } else if (data.equals("눈")) {
                weatherIcon.setImageResource(R.drawable.weather_7);
            } else {
                Log.d("Fragment2", "Unknown weather string : " + data);
            }
        }
    }

    public void setWeatherIndex(int index) {
        if (index == 0) {
            weatherIcon.setImageResource(R.drawable.weather_1);
            weatherIndex = 0;
        } else if (index == 1) {
            weatherIcon.setImageResource(R.drawable.weather_2);
            weatherIndex = 1;
        } else if (index == 2) {
            weatherIcon.setImageResource(R.drawable.weather_3);
            weatherIndex = 2;
        } else if (index == 3) {
            weatherIcon.setImageResource(R.drawable.weather_4);
            weatherIndex = 3;
        } else if (index == 4) {
            weatherIcon.setImageResource(R.drawable.weather_5);
            weatherIndex = 4;
        } else if (index == 5) {
            weatherIcon.setImageResource(R.drawable.weather_6);
            weatherIndex = 5;
        } else if (index == 6) {
            weatherIcon.setImageResource(R.drawable.weather_7);
            weatherIndex = 6;
        } else {
            Log.d("Fragment2", "Unknown weather index : " + index);
        }
    }

    //주소 문자열을 받아 텍스트뷰에 보여주는 역할
    public void setAddress(String data) {
        locationTextView.setText(data);
    }

    //현재 일자를 표시
    public void setDateString(String dateString) {
        dateTextView.setText(dateString);
    }

    public void setContents(String data) {
        contentsInput.setText(data);
    }

    public void setPicture(String picturePath, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        resultPhotoBitmap = BitmapFactory.decodeFile(picturePath, options);

        pictureImageView.setImageBitmap(resultPhotoBitmap);
    }

    public void setMood(String mood) {
        try {
            moodIndex = Integer.parseInt(mood);
            moodSlider.setInitialIndex(moodIndex);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setItem(Note item) {
        this.item = item;
    }

    public void applyItem() {
        AppConstants.println("applyItem called.");

        if (item != null) {
            mMode = AppConstants.MODE_MODIFY;

            setWeatherIndex(Integer.parseInt(item.getWeather()));
            setAddress(item.getAddress());
            setDateString(item.getCreateDateStr());
            setContents(item.getContents());

            String picturePath = item.getPicture();
            if (picturePath == null || picturePath.equals("")) {
                pictureImageView.setImageResource(R.drawable.noimagefound);
            } else {
                setPicture(item.getPicture(), 1);
            }

            setMood(item.getMood());
        } else {
            mMode = AppConstants.MODE_INSERT;

            setWeatherIndex(0);
            setAddress("");

            Date currentDate = new Date();
            String currentDateString = AppConstants.dateFormat3.format(currentDate);
            setDateString(currentDateString);

            contentsInput.setText("");
            pictureImageView.setImageResource(R.drawable.noimagefound);
            setMood("2");
        }

    }

    //다이얼로그 보여줌
    public void showDialog(int id) {
        AlertDialog.Builder builder = null;

        switch(id) {
            case AppConstants.CONTENT_PHOTO:
                builder = new AlertDialog.Builder(context);

                builder.setTitle("사진 메뉴 선택");
                builder.setSingleChoiceItems(R.array.array_photo, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        selectedPhotoMenu = whichButton;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(selectedPhotoMenu == 0 ) {
                            showPhotoCaptureActivity();
                        } else if(selectedPhotoMenu == 1) {
                            showPhotoSelectionActivity();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;

            case AppConstants.CONTENT_PHOTO_EX:
                builder = new AlertDialog.Builder(context);

                builder.setTitle("사진 메뉴 선택");
                builder.setSingleChoiceItems(R.array.array_photo_ex, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        selectedPhotoMenu = whichButton;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(selectedPhotoMenu == 0) {
                            showPhotoCaptureActivity();
                        } else if(selectedPhotoMenu == 1) {
                            showPhotoSelectionActivity();
                        } else if(selectedPhotoMenu == 2) {
                            isPhotoCanceled = true;
                            isPhotoCaptured = false;

                            pictureImageView.setImageResource(R.drawable.noimagefound);

                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) { }
                });

                break;

            default:
                break;
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //사진 촬영
    public void showPhotoCaptureActivity() {
        if (file == null) {
            file = createFile();
        }

        Uri fileUri = FileProvider.getUriForFile(context,"kr.ac.konkuk.mydiary.fileprovider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivityForResult(intent, AppConstants.REQ_PHOTO_CAPTURE);
        }
    }

    //촬영한 사진을 파일로
    private File createFile() {
        String filename = "capture.jpg";
        File storageDir = Environment.getExternalStorageDirectory();
        File outFile = new File(storageDir, filename);

        return outFile;
    }

    //사진 앨범에서 선택
    public void showPhotoSelectionActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, AppConstants.REQ_PHOTO_SELECTION);
    }

    // 다른 액티비티로부터의 응답 처리
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (intent != null) {
            switch (requestCode) {
                case AppConstants.REQ_PHOTO_CAPTURE:  // 사진 찍는 경우
                    Log.d(TAG, "onActivityResult() for REQ_PHOTO_CAPTURE.");

                    Log.d(TAG, "resultCode : " + resultCode);

                    resultPhotoBitmap = decodeSampledBitmapFromResource(file, pictureImageView.getWidth(), pictureImageView.getHeight());
                    pictureImageView.setImageBitmap(resultPhotoBitmap);

                    break;

                case AppConstants.REQ_PHOTO_SELECTION:  // 사진을 앨범에서 선택하는 경우
                    Log.d(TAG, "onActivityResult() for REQ_PHOTO_SELECTION.");

                    Uri selectedImage = intent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    resultPhotoBitmap = decodeSampledBitmapFromResource(new File(filePath), pictureImageView.getWidth(), pictureImageView.getHeight());
                    pictureImageView.setImageBitmap(resultPhotoBitmap);
                    isPhotoCaptured = true;

                    break;
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(File res, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res.getAbsolutePath(),options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(res.getAbsolutePath(),options);
    }

    //사진 크기 계산
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height;
            final int halfWidth = width;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private String createFilename() {
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());

        return curDateStr;
    }

    //사진 저장
    private String savePicture() {
        if (resultPhotoBitmap == null) {
            AppConstants.println("No picture to be saved.");
            return "";
        }

        File photoFolder = new File(AppConstants.FOLDER_PHOTO);

        if(!photoFolder.isDirectory()) {
            Log.d(TAG, "creating photo folder : " + photoFolder);
            photoFolder.mkdirs();
        }

        String photoFilename = createFilename();
        String picturePath = photoFolder + File.separator + photoFilename;

        try {
            FileOutputStream outstream = new FileOutputStream(picturePath);
            resultPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return picturePath;
    }

    //데이터베이스 레코드 추가

    //새로운 일기장을 만드는 경우
    private void saveNote() {
        String address = locationTextView.getText().toString();
        String contents = contentsInput.getText().toString();

        String picturePath = savePicture();

        String sql = "insert into " + NoteDatabase.TABLE_NOTE +
                "(WEATHER, ADDRESS, LOCATION_X, LOCATION_Y, CONTENTS, MOOD, PICTURE) values(" +
                "'"+ weatherIndex + "', " +
                "'"+ address + "', " +
                "'"+ "" + "', " +
                "'"+ "" + "', " +
                "'"+ contents + "', " +
                "'"+ moodIndex + "', " +
                "'"+ picturePath + "')";

        Log.d(TAG, "sql : " + sql);
        NoteDatabase database = NoteDatabase.getInstance(context);
        database.execSQL(sql);

    }

    //데이터베이스 레코드 수정

    //기존 일기를 수정하는 경우
    private void modifyNote() {
        if (item != null) {
            String address = locationTextView.getText().toString();
            String contents = contentsInput.getText().toString();

            String picturePath = savePicture();

            // update note
            String sql = "update " + NoteDatabase.TABLE_NOTE +
                    " set " +
                    "   WEATHER = '" + weatherIndex + "'" +
                    "   ,ADDRESS = '" + address + "'" +
                    "   ,LOCATION_X = '" + "" + "'" +
                    "   ,LOCATION_Y = '" + "" + "'" +
                    "   ,CONTENTS = '" + contents + "'" +
                    "   ,MOOD = '" + moodIndex + "'" +
                    "   ,PICTURE = '" + picturePath + "'" +
                    " where " +
                    "   _id = " + item._id;

            Log.d(TAG, "sql : " + sql);
            NoteDatabase database = NoteDatabase.getInstance(context);
            database.execSQL(sql);
        }
    }


    //레코드 삭제
    private void deleteNote() {
        AppConstants.println("deleteNote called.");

        if (item != null) {
            // delete note
            String sql = "delete from " + NoteDatabase.TABLE_NOTE +
                    " where " +
                    "   _id = " + item._id;

            Log.d(TAG, "sql : " + sql);
            NoteDatabase database = NoteDatabase.getInstance(context);
            database.execSQL(sql);
        }
    }
}
