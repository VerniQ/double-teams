package me.verni.doubleteams.team;

import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Team {
    public String tag;
    public String name;
    public List<Member> members;
    private UUID creatorUUID;

    private MemberService memberService;
    public Team(String tag, String name, List<Member> members, UUID creatorUUID) {
        this.tag = tag;
        this.name = name;
        this.members = members;
        this.creatorUUID = creatorUUID;
    }

    public Team(MemberService memberService) {
        this.memberService = memberService;
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

    public void addMember(OfflinePlayer player) {
        Optional<Member> optionalMember = memberService.findMember(player.getUniqueId());
        optionalMember.ifPresent(member -> this.members.add(member));
    }

    public void removeMember(OfflinePlayer player) {
        this.members.removeIf(user -> user.getUniqueId().equals(player.getUniqueId()));
    }

    public UUID getCreatorUUID() {
        return creatorUUID;
    }
    public void setCreatorUUID(UUID creatorUUID) {
        this.creatorUUID = creatorUUID;
    }

}
