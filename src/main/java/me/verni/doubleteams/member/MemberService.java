package me.verni.doubleteams.member;

import me.verni.doubleteams.configuration.implementation.PluginConfigImpl;
import me.verni.doubleteams.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class MemberService {
    private final HashMap<UUID, Member> membersByUniqueId = new HashMap<>();
    private final HashMap<Member, List<Team>> invitesByMember = new HashMap<>();
    private final PluginConfigImpl configuration;
    private final MemberRepository memberRepository;

    public MemberService(PluginConfigImpl configuration, MemberRepository memberRepository) {
        this.configuration = configuration;
        this.memberRepository = memberRepository;
    }

    public Optional<Member> findMember(UUID uuid) {
        return Optional.ofNullable(membersByUniqueId.get(uuid));
    }

    public List<Team> getInvites(Member member) {
        if (invitesByMember.get(member) == null) {
            return new ArrayList<>();
        } else {
            return invitesByMember.get(member);
        }

    }

    public void addInvite(Member member, Team team) {
        List<Team> invites = invitesByMember.get(member);
        if (invites == null) {
            invites = new ArrayList<>();
        }
        invites.add(team);
        invitesByMember.put(member, invites);
    }

    public void removeInvite(Member member, Team team) {
        List<Team> invites = invitesByMember.get(member);
        if (invites != null) {
            invites.remove(team);
            invitesByMember.put(member, invites);
        }
    }

    public void create(UUID uuid, String name, String tag) {
        membersByUniqueId.put(uuid, new Member(uuid, name, tag));
    }

    public void saveMember(Member member) {
       this.memberRepository.saveMember(member);
    }

    public Member memberFromPlayer(Player player) {
        if (this.findMember(player.getUniqueId()).isEmpty()) {
            this.create(player.getUniqueId(), player.getName(), "NULL");
            this.saveMember(this.findMember(player.getUniqueId()).get());
        }
        return this.findMember(player.getUniqueId()).get();
    }

    public Member memberFromOfflinePlayer(OfflinePlayer player) {
        if (this.findMember(player.getUniqueId()).isEmpty()) {
            this.create(player.getUniqueId(), player.getName(), "NULL");
            this.saveMember(this.findMember(player.getUniqueId()).get());
        }
        return this.findMember(player.getUniqueId()).get();
    }

    public Player playerFromMember(Member member) {
        return Bukkit.getPlayer(member.getUniqueId());
    }

    public OfflinePlayer offlinePlayerFromMember(Member member) {
        return Bukkit.getOfflinePlayer(member.getUniqueId());
    }

    public void loadMembers() {
        this.memberRepository.loadMembers().forEach(member -> membersByUniqueId.put(member.getUniqueId(), member));
    }

    public HashMap<UUID, Member> getMembersByUniqueId() {
        return membersByUniqueId;
    }


}
