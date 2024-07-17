package me.verni.doubleteams.member;

import java.util.UUID;

public class Member {
    private UUID uniqueId;
    private String name;
    private String tag;

    public Member(UUID uniqueId, String name, String tag) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.tag = tag;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public static String getName() {
        return getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
