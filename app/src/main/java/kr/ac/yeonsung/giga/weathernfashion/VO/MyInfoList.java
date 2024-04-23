package kr.ac.yeonsung.giga.weathernfashion.VO;

public class MyInfoList {
    String post_image;
    String post_id;

    public MyInfoList(String post_image, String post_id) {
        this.post_image = post_image;
        this.post_id = post_id;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
