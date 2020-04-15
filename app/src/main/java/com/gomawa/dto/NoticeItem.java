package com.gomawa.dto;

import java.util.Date;

// 공지사항 리스트에 뜨는 Item
public class NoticeItem {
    private String title;
    private String dsc;
    // TODO: 2020-04-15 date 를 적절한 변수로 변환 
    private String date;

    public NoticeItem(String title, String dsc, String date) {
        this.title = title;
        this.dsc = dsc;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDsc() {
        return dsc;
    }

    public void setDsc(String dsc) {
        this.dsc = dsc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
