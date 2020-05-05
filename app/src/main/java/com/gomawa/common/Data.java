package com.gomawa.common;

import com.gomawa.dto.Member;
import com.gomawa.dto.ShareItem;

import java.util.ArrayList;
import java.util.List;

public class Data {
    // 멤버
    private static Member member;

    public static void setMember(Member m) {
        member = m;
    }
    public static Member getMember() {
        return member;
    }

    //ShareItem List
    public static List<ShareItem> shareItemList = new ArrayList<>();
}
