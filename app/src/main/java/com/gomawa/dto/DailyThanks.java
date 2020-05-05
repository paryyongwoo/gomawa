package com.gomawa.dto;

import java.util.Date;

public class DailyThanks {

    private Long id;
    private String content;
    private Date regDate;
    private Member regMember;

    public DailyThanks() {

    }

    public DailyThanks(Long id, String content, Date regDate, Member regMember) {
        this.id = id;
        this.content = content;
        this.regDate = regDate;
        this.regMember = regMember;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Member getRegMember() {
        return regMember;
    }

    public void setRegMember(Member regMember) {
        this.regMember = regMember;
    }

    @Override
    public String toString() {
        return "DailyThanks{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", regDate=" + regDate +
                ", regMember=" + regMember +
                '}';
    }
}
