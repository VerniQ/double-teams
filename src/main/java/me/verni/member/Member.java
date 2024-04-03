package me.verni.member;

import java.util.UUID;

public class Member {
    private UUID uniqueId;
    private String name;

    public Member(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public static UUID getUniqueId() {
        return getUniqueId();
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
}
