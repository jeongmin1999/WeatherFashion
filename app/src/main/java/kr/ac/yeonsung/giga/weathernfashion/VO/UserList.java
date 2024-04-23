package kr.ac.yeonsung.giga.weathernfashion.VO;

public class UserList {
    String user_id;
    String user_name;
    String user_profile;

    public UserList(String user_id, String user_name, String user_profile) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_profile = user_profile;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public void setUser_profile(String user_profile) {
        this.user_profile = user_profile;
    }
}
