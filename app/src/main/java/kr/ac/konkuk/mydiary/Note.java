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
        this.weather = weather; //날씨
        this.address = address; //주소
        this.locationX = locationX; //x좌표
        this.locationY = locationY; //y좌표
        this.contents = contents; //일기의 내용
        this.mood = mood; //기분
        this.picture = picture; // 사진 이미지 경로
        this.createDateStr = createDateStr; //일기 작성 일자
    }

    //마우스 우클 -> generate로 getter & setter 메소드를 자동으로 만들 수 있음
    //getter setter 함수들 모음
    public int get_id() {
        return _id;
    } //데이터베이스에서 조회한 id값을 가져옴

    public String getAddress() {
        return address;
    } //주소를 가져옴

    public String getWeather() {
        return weather;
    } //날씨를 가져옴

    public String getContents() {
        return contents;
    } // 일기 내용을 가져옴

    public String getCreateDateStr() {
        return createDateStr;
    } //문자열로된 날짜를 가져옴

    public String getMood() {
        return mood;
    } //기분을 가져옴

    public String getPicture() {
        return picture;
    } // 사진을 가져옴

}
