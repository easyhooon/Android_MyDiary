package kr.ac.konkuk.mydiary;

import android.view.View;

//onitemClick 메소드 구현을 위한 인터페이스
public interface OnNoteItemClickListener {
    public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position);
}
