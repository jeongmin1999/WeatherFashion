package kr.ac.yeonsung.giga.weathernfashion.VO;

import java.util.ArrayList;
import java.util.HashMap;

public class Post {
    private String post_content;
    private String post_image;
    private String post_user_name;
    private String post_min_temp;
    private String post_max_temp;
    private String post_temp;
    private String post_location;
    private String post_date;
    private String post_user_id;
    private String post_now_date;
    private String post_gender;
    private Long post_likeCount;
    private HashMap<String, Boolean> post_likes = new HashMap<>();
    private ArrayList<String> post_categories;

    public Post(String post_content, String post_image, String post_user_name, String post_min_temp, String post_max_temp,String post_temp, String post_location, String post_date, String post_now_date, Long post_likeCount, HashMap<String, Boolean> post_likes, ArrayList<String> post_categories,String post_user_id, String post_gender) {
        this.post_content = post_content;
        this.post_image = post_image;
        this.post_user_name = post_user_name;
        this.post_min_temp = post_min_temp;
        this.post_max_temp = post_max_temp;
        this.post_temp = post_temp;
        this.post_location = post_location;
        this.post_date = post_date;
        this.post_now_date = post_now_date;
        this.post_likeCount = post_likeCount;
        this.post_likes = post_likes;
        this.post_categories = post_categories;
        this.post_user_id = post_user_id;
        this.post_gender = post_gender;
    }
    public Post(){

    }

    public String getPost_gender() {
        return post_gender;
    }

    public void setPost_gender(String post_gender) {
        this.post_gender = post_gender;
    }

    public String getPost_user_id() {
        return post_user_id;
    }

    public void setPost_user_id(String post_user_id) {
        this.post_user_id = post_user_id;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getPost_image() {
        return post_image;
    }

    public String getPost_temp() {
        return post_temp;
    }

    public void setPost_temp(String post_temp) {
        this.post_temp = post_temp;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getPost_user_name() {
        return post_user_name;
    }

    public void setPost_user_name(String post_user_name) {
        this.post_user_name = post_user_name;
    }

    public String getPost_min_temp() {
        return post_min_temp;
    }

    public void setPost_min_temp(String post_min_temp) {
        this.post_min_temp = post_min_temp;
    }

    public String getPost_max_temp() {
        return post_max_temp;
    }

    public void setPost_max_temp(String post_max_temp) {
        this.post_max_temp = post_max_temp;
    }

    public String getPost_location() {
        return post_location;
    }

    public void setPost_location(String post_location) {
        this.post_location = post_location;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getPost_now_date() {
        return post_now_date;
    }

    public void setPost_now_date(String post_now_date) {
        this.post_now_date = post_now_date;
    }

    public Long getPost_likeCount() {
        return post_likeCount;
    }

    public void setPost_likeCount(Long post_likeCount) {
        this.post_likeCount = post_likeCount;
    }

    public HashMap<String, Boolean> getPost_likes() {
        return post_likes;
    }

    public void setPost_likes(HashMap<String, Boolean> post_likes) {
        this.post_likes = post_likes;
    }

    public ArrayList<String> getPost_categories() {
        return post_categories;
    }

    public void setPost_categories(ArrayList<String> post_categories) {
        this.post_categories = post_categories;
    }
}
