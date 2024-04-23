package kr.ac.yeonsung.giga.weathernfashion.VO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TempReply implements Serializable {

    private String reply_id; // 비밀 방법은 id 만드는 방법은 많다.
    private String post_id; // 댓글이 속한 게시물
    private String content;// 댓글내용
    private String user_id;// 댓글 작성자
    private String time;// 댓글 작성 및 수정 일시
    private String parent; //댓글의 답글 일시 댓글 달 해당 db id
    private String name;
    // 댓글의 답글의 답글..답글.. 댓글속의 댓글 일때 맨 꼭대기 댓글을 적음 depth가 1이면 parent와 같음
    private String root;
    private Long reply_likeCount;

    private boolean mode; // 댓글의 답글 달은 댓글인지 체크 //true댓글 안의 댓글 //false 일반 게시물 댓글
    private HashMap<String, Boolean> likeList = new HashMap<>();// 댓글 좋아요수
    private ArrayList<String> ToReplys;// 댓글의 답글 리스트 //답글갯수로도 작용할것

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public boolean isMode() {
        return mode;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }

    public HashMap<String, Boolean> getLikeList() {
        return likeList;
    }

    public void setLikeList(HashMap<String, Boolean> likeList) {
        this.likeList = likeList;
    }

    public ArrayList<String> getToReplys() {
        return ToReplys;
    }

    public void setToReplys(ArrayList<String> toReplys) {
        ToReplys = toReplys;
    }

    public TempReply(String content, String user_id, String time, String name, Long reply_likeCount, boolean mode) {//데이터를 넣을 때 호출하는 생성자

        this.content = content;
        this.user_id = user_id;
        this.time = time;
        this.name = name;
        this.reply_likeCount = reply_likeCount;
        this.mode = mode;
    }
    public TempReply(){

    }
    public TempReply(String content, String user_id, String time, String name, Long reply_likeCount,
                     String root_id, String parent_id, boolean mode){
        this.post_id = post_id;
        this.content = content;
        this.user_id = user_id;
        this.time = time;
        this.name = name;
        this.reply_likeCount = reply_likeCount;
        this.reply_id = reply_id;
        this.root = root_id;
        this.parent = parent_id;
        this.mode = mode;

    }
    public TempReply(String post_id, String content, String user_id, String time, String name, String reply_id) { //가져올 때 호출하는 생성자
        this.post_id = post_id;
        this.content = content;
        this.user_id = user_id;
        this.time = time;
        this.name = name;
        this.reply_id = reply_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getReply_likeCount() {
        return reply_likeCount;
    }

    public void setReply_likeCount(Long reply_likeCount) {
        this.reply_likeCount = reply_likeCount;
    }
}
