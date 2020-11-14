package kr.ac.konkuk.mydiary;

import android.view.View;

//하나의 fragment에서 다른 fragment로 전환하는 용도로 사용하기 위해 정의한 인터페이스
public interface OnTabItemSelectedListener {
    public void onTabSelected(int position);
    public void showFragment2(Note item);
}
