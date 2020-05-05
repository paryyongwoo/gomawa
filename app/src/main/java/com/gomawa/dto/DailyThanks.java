package com.gomawa.dto;

import java.time.LocalDateTime;

public class DailyThanks {

    private Long id;
    private String content;
    private LocalDateTime regDate;
    private Member regMember;

    public DailyThanks() {

    }

    public DailyThanks(Long id, String content, LocalDateTime regDate, Member regMember) {
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

    public String getContent1() {
        return content;
    }

    public void setContent1(String content1) {
        this.content = content;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDateTime regDate) {
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
