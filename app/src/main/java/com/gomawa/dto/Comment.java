package com.gomawa.dto;

import java.util.Date;

public class Comment {
    private Long id;
    private String content;
    private Date regDate;
    private Member member;
    private ShareItem shareItem;

    public Comment() {}

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

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public ShareItem getShareItem() {
        return shareItem;
    }

    public void setShareItem(ShareItem shareItem) {
        this.shareItem = shareItem;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", regDate=" + regDate +
                ", member=" + member +
                ", shareItem=" + shareItem +
                '}';
    }
}
