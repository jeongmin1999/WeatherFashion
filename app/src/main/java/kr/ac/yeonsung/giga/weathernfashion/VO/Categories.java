package kr.ac.yeonsung.giga.weathernfashion.VO;

import android.app.Application;

public class Categories extends Application {

    private String etc;
    private String minimal;
    private String street;
    private String american;
    private String casual;

    public void setMinimal(String minimal) {
        this.minimal = minimal;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getEtc() {
        return etc;
    }

    public String getMinimal() {
        return minimal;
    }

    public String getStreet() {
        return street;
    }

    public String getAmerican() {
        return american;
    }

    public String getCasual() {
        return casual;
    }

    public void setAmerican(String american) {
        this.american = american;
    }

    public void setCasual(String casual) {
        this.casual = casual;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
