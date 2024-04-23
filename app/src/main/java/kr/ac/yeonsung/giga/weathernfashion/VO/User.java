package kr.ac.yeonsung.giga.weathernfashion.VO;

import java.util.List;

public class User {
    public String user_email;
    public String user_pw;
    public String user_name;
    public String user_phone;
    public List<String> user_styles;
    public String reg_date;
    public String user_comment;
    public String user_profile;
    public String user_gender;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String user_email, String user_pw, String user_name, String user_phone, List<String> user_styles, String reg_date,
                String user_comment, String user_profile, String user_gender) {
        this.user_email = user_email;
        this.user_pw = user_pw;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.user_styles = user_styles;
        this.reg_date = reg_date;
        this.user_comment = user_comment;
        this.user_profile = user_profile;
        this.user_gender = user_gender;
    }
}
