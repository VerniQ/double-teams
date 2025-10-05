package me.verni.doubleteams.team;

import me.verni.doubleteams.member.Member;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {
    public String tag;
    public String name;
    public List<Member> members;
    private UUID creatorUUID;
    private List<UUID> viceLeaders;
    private Location homeLocation;

    public Team(String tag, String name, List<Member> members, UUID creatorUUID) {
        this.tag = tag;
        this.name = name;
        this.members = members;
        this.creatorUUID = creatorUUID;
        this.viceLeaders = new ArrayList<>();
        this.homeLocation = null;
    }

    public boolean isLeader(UUID uuid) {
        return creatorUUID.equals(uuid);
    }

    public boolean isViceLeader(UUID uuid) {
        return viceLeaders.contains(uuid);
    }

    public Location getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = homeLocation;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public void addMember(Member member) {
        if (!this.members.contains(member)) {
            this.members.add(member);
        }
    }

    public void removeMember(Member member) {
        this.members.remove(member);
    }

    public UUID getCreatorUUID() {
        return creatorUUID;
    }

    public void setCreatorUUID(UUID creatorUUID) {
        this.creatorUUID = creatorUUID;
    }

    public List<UUID> getViceLeaders() {
        return viceLeaders;
    }

    public void setViceLeaders(List<UUID> viceLeaders) {
        this.viceLeaders = viceLeaders;
    }
}