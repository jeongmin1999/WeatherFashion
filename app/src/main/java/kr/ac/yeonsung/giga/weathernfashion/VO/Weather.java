package kr.ac.yeonsung.giga.weathernfashion.VO;

public class Weather {

    String time;
    String sky;
    String pty;
    String now_Temp;

    public Weather(String time, String sky, String pty, String now_Temp) {
        this.time = time;
        this.sky = sky;
        this.pty = pty;
        this.now_Temp = now_Temp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSky() {
        return sky;
    }

    public void setSky(String sky) {
        this.sky = sky;
    }

    public String getPty() {
        return pty;
    }

    public void setPty(String pty) {
        this.pty = pty;
    }

    public String getNow_Temp() {
        return now_Temp;
    }

    public void setNow_Temp(String now_Temp) {
        this.now_Temp = now_Temp;
    }
}
