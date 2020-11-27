package kr.ac.konkuk.mydiary;

public class Note {
    int _id;
    String weather;
    String address;
    String locationX;
    String locationY;
    String contents;
    String mood;
    String picture;
    String createDateStr;

    public Note(int _id, String weather, String address, String locationX, String locationY, String contents, String mood, String picture, String createDateStr) {
        this._id = _id; // 데이터베이스에서 조회한 _id값
        this.weather = weather;
        this.address = address;
        this.locationX = locationX;
        this.locationY = locationY;
        this.contents = contents; //일기의 내용
        this.mood = mood; //기분
        this.picture = picture; // 사진 이미지 경로
        this.createDateStr = createDateStr; //일기 작성 일자
    }

    //마우스 우클 -> generate로 getter & setter 메소드를 자동으로 만들 수 있음
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
