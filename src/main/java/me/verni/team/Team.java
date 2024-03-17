package me.verni.team;

import org.bukkit.entity.Player;

import java.util.List;

public class Team {
    public String tag;
    public String name;
    public List<Player> members;

    public Team(String tag, String name, List<Player> members) {
        this.tag = tag;
        this.name = name;
        this.members = members;
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

    public List<Player> getMembers() {
        return members;
    }

    public void setMembers(List<Player> members) {
        this.members = members;
    }

    public void addMember(Player player) {
        this.members.add(player);
    }

    public void removeMember(Player player) {
        this.members.removeIf(user -> user.getUniqueId().equals(player.getUniqueId()));
    }


}
