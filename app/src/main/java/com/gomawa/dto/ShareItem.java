package com.gomawa.dto;

import androidx.annotation.NonNull;

import java.util.Date;

public class ShareItem {
    private long id;
    private long key;
    private Date date; // LocalDateTime이 지원하는 api레벨이 높아서 Date 객체를 사용.. String 고려..
    private String content;
    private String backgroundUrl;
    private int like;

    public ShareItem() {

    }

    public ShareItem(long id, long key, Date date, String content, String backgroundUrl, int like) {
        this.id = id;
        this.key = key;
        this.date = date;
        this.content = content;
        this.backgroundUrl = backgroundUrl;
        this.like = like;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    // 좋아요 +1 해주는 함수수
}
