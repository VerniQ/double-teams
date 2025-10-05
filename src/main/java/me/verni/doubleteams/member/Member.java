package me.verni.doubleteams.member;

import java.util.UUID;

public class Member {
    private UUID uniqueId;
    private String name;
    private String tag;
    private double points;

    public Member(UUID uniqueId, String name, String tag, double points) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.tag = tag;
        this.points = points;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }
}