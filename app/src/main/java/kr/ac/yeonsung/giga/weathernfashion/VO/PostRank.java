package kr.ac.yeonsung.giga.weathernfashion.VO;

public class PostRank {
    String image;
    String max_temp;
    String min_temp;
    String like;
    String post_id;

    public void setMax_temp(String max_temp) {
        this.max_temp = max_temp;
    }

    public void setMin_temp(String min_temp) {
        this.min_temp = min_temp;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getMax_temp() {
        return max_temp;
    }

    public String getMin_temp() {
        return min_temp;
    }


    public PostRank(String image,String max_temp, String min_temp, String like, String post_id) {
        this.image = image;
        this.max_temp = max_temp;
        this.min_temp = min_temp;
        this.like = like;
        this.post_id = post_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
