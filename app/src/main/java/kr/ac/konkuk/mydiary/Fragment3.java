package kr.ac.konkuk.mydiary;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;



public class Fragment3 extends Fragment {
    Context context;

    PieChart chart;
    BarChart chart2;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (context != null) {
            context = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);

        initUI(rootView);

        //세개의 그래프를 위해 데이터베이스로부터 기분 데이터를 조화
        loadStatData();

        return rootView;
    }

    private void initUI(ViewGroup rootView) {
        // setting for first graph
        chart = rootView.findViewById(R.id.chart1);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);

        //소스코드에서 글자를 설정
        chart.setCenterText(getResources().getString(R.string.graph1_title));
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setHighlightPerTapEnabled(true);

        Legend legend1 = chart.getLegend();
        legend1.setEnabled(false);

        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(10f);


        // setting for second graph
        chart2 = rootView.findViewById(R.id.chart2);
        chart2.setDrawValueAboveBar(true);
        chart2.getDescription().setEnabled(false);
        chart2.setDrawGridBackground(false);

        XAxis xAxis = chart2.getXAxis();
        xAxis.setEnabled(false);

        YAxis leftAxis = chart2.getAxisLeft();
        leftAxis.setLabelCount(6, false);
        leftAxis.setAxisMinimum(0.0f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(1f);

        YAxis rightAxis = chart2.getAxisRight();
        rightAxis.setEnabled(false);

        Legend legend2 = chart2.getLegend();
        legend2.setEnabled(false);

        chart2.animateXY(1500, 1500);

    }

    //fist graph set data
    private void setData1(HashMap<String,Integer> dataHash1) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        //기분의 종류 5가지
        String[] keys = {"0", "1", "2", "3", "4"};
        int[] icons = {R.drawable.smile1_24, R.drawable.smile2_24,
                R.drawable.smile3_24, R.drawable.smile4_24,
                R.drawable.smile5_24};

        for (int i = 0; i < keys.length; i++) {
            int value = 0;
            Integer outValue = dataHash1.get(keys[i]);
            if (outValue != null) {
                value = outValue.intValue();
            }

            if (value > 0) {
                entries.add(new PieEntry(value, "",
                        getResources().getDrawable(icons[i])));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, getResources().getString(R.string.graph1_title));

        dataSet.setDrawIcons(true);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, -40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(22.0f);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.invalidate();
    }

    //second graph set data
    private void setData2(HashMap<String,Integer> dataHash2) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        //일주일 원소 7개
        String[] keys = {"0", "1", "2", "3", "4", "5" ,"6"};
        int[] icons = {R.drawable.smile1_24, R.drawable.smile2_24,
                R.drawable.smile3_24, R.drawable.smile4_24,
                R.drawable.smile5_24};

        for (int i = 0; i < keys.length; i++) {
            float value = 0.0f; //기분 default값
            Integer outValue = dataHash2.get(keys[i]);
            AppConstants.println("#" + i + " -> " + outValue);
            if (outValue != null) {
                value = outValue.floatValue();
            }

            Drawable drawable = null;
            if (value < 1.0f) {
                drawable = getResources().getDrawable(icons[0]);
            } else if (value < 2.0f) {
                drawable = getResources().getDrawable(icons[1]);
            } else if (value < 3.0f) {
                drawable = getResources().getDrawable(icons[2]);
            } else if (value < 4.0f) {
                drawable = getResources().getDrawable(icons[3]);
            } else if (value < 5.0f) {
                drawable = getResources().getDrawable(icons[4]);
            }

            entries.add(new BarEntry(Float.valueOf(String.valueOf(i+1)), value+1, drawable));
        }

        //setting for second graph
        BarDataSet dataSet2 = new BarDataSet(entries, getResources().getString(R.string.graph2_title));
        dataSet2.setColor(Color.rgb(240, 120, 124));

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        dataSet2.setColors(colors);
        dataSet2.setIconsOffset(new MPPointF(0, -10));

        BarData data = new BarData(dataSet2);
        data.setValueTextSize(10f);
        data.setDrawValues(false);
        data.setBarWidth(0.8f);

        chart2.setData(data);
        chart2.invalidate();
    }

    //DB로 data load
    public void loadStatData() {
        NoteDatabase database = NoteDatabase.getInstance(context);

        // first graph
        String sql = "select mood " +
                "  , count(mood) " +
                "from " + NoteDatabase.TABLE_NOTE + " " +
                "where create_date > '" + getMonthBefore(1) + "' " +
                "  and create_date < '" + getTomorrow() + "' " +
                "group by mood";

        Cursor cursor = database.rawQuery(sql);
        int recordCount = cursor.getCount();
        AppConstants.println("recordCount : " + recordCount);

        HashMap<String,Integer> dataHash1 = new HashMap<String,Integer>();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();

            String moodName = cursor.getString(0);
            int moodCount = cursor.getInt(1);

            AppConstants.println("#" + i + " -> " + moodName + ", " + moodCount);
            dataHash1.put(moodName, moodCount);
        }

        setData1(dataHash1);

        // second graph
        sql = "select strftime('%w', create_date) " +
                "  , avg(mood) " +
                "from " + NoteDatabase.TABLE_NOTE + " " +
                "where create_date > '" + getMonthBefore(1) + "' " +
                "  and create_date < '" + getTomorrow() + "' " +
                "group by strftime('%w', create_date)";

        cursor = database.rawQuery(sql);
        recordCount = cursor.getCount();
        AppConstants.println("recordCount : " + recordCount);

        HashMap<String,Integer> dataHash2 = new HashMap<String,Integer>();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();

            String weekDay = cursor.getString(0);
            int moodCount = cursor.getInt(1);

            AppConstants.println("#" + i + " -> " + weekDay + ", " + moodCount);
            dataHash2.put(weekDay, moodCount);
        }

        setData2(dataHash2);

    }

    //내일 날짜를 문자열로 가져옴
    public String getTomorrow() {
        Date todayDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);

        return AppConstants.dateFormat5.format(cal.getTime());
    }

    //한달 전의 날짜를 문자열로 가져옴
    public String getMonthBefore(int amount) {
        Date todayDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.add(Calendar.MONTH, (amount * -1));

        return AppConstants.dateFormat5.format(cal.getTime());
    }
}
