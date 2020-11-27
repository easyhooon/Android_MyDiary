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
    //getter setter 함수들 모음
    public int get_id() {
        return _id;
    }

    public String getAddress() {
        return address;
    }

    public String getWeather() {
        return weather;
    }

    public String getContents() {
        return contents;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public String getMood() {
        return mood;
    }

    public String getPicture() {
        return picture;
    }

}
