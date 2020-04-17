package com.gomawa.dto;

import androidx.annotation.NonNull;

import java.util.Date;

public class ShareItem {
    private long id;
    private Member member;
    private Date date; // LocalDateTime이 지원하는 api레벨이 높아서 Date 객체를 사용.. String 고려..
    private String content;
    private String backgroundUrl;
    private int likeNum;


    public ShareItem() {

    }

    public ShareItem(long id, Member member, Date date, String content, String backgroundUrl, int likeNum) {
        this.id = id;
        this.member = member;
        this.date = date;
        this.content = content;
        this.backgroundUrl = backgroundUrl;
        this.likeNum = likeNum;
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

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
