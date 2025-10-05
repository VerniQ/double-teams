package me.verni.doubleteams.member;

import me.verni.doubleteams.configuration.implementation.PluginConfigImpl;
import me.verni.doubleteams.team.Team;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemberService {
    private final HashMap<UUID, Member> membersByUniqueId = new HashMap<>();
    private final HashMap<Member, List<Team>> invitesByMember = new HashMap<>();
    private final Map<UUID, Map<UUID, Long>> killCooldowns = new ConcurrentHashMap<>();
    private final PluginConfigImpl configuration;
    private final MemberRepository memberRepository;

    private static final double K_FACTOR = 30.0;

    public MemberService(PluginConfigImpl configuration, MemberRepository memberRepository) {
        this.configuration = configuration;
        this.memberRepository = memberRepository;
    }

    public boolean isOnCooldown(UUID killer, UUID victim) {
        Map<UUID, Long> victimMap = killCooldowns.get(killer);
        if (victimMap == null) {
            return false;
        }

        Long lastKillTime = victimMap.get(victim);
        if (lastKillTime == null) {
            return false;
        }

        long secondsSinceKill = (System.currentTimeMillis() - lastKillTime) / 1000;
        return secondsSinceKill < configuration.rating.cooldownSeconds;
    }

    public void setOnCooldown(UUID killer, UUID victim) {
        killCooldowns.computeIfAbsent(killer, k -> new ConcurrentHashMap<>()).put(victim, System.currentTimeMillis());
    }

    public void updateRatings(Member winner, Member loser) {
        double winnerOldRating = winner.getPoints();
        double loserOldRating = loser.getPoints();

        double expectedWinnerScore = 1.0 / (1.0 + Math.pow(10, (loserOldRating - winnerOldRating) / 400.0));
        double expectedLoserScore = 1.0 / (1.0 + Math.pow(10, (winnerOldRating - loserOldRating) / 400.0));

        double winnerNewRating = winnerOldRating + K_FACTOR * (1.0 - expectedWinnerScore);
        double loserNewRating = loserOldRating + K_FACTOR * (0.0 - expectedLoserScore);

        winner.setPoints(winnerNewRating);
        loser.setPoints(loserNewRating);

        saveMember(winner);
        saveMember(loser);
    }

    public Optional<Member> findMember(UUID uuid) {
        return Optional.ofNullable(membersByUniqueId.get(uuid));
    }

    public List<Team> getInvites(Member member) {
        return invitesByMember.getOrDefault(member, new ArrayList<>());
    }

    public void addInvite(Member member, Team team) {
        invitesByMember.computeIfAbsent(member, k -> new ArrayList<>()).add(team);
    }

    public void removeInvite(Member member, Team team) {
        List<Team> invites = invitesByMember.get(member);
        if (invites != null) {
            invites.remove(team);
        }
    }

    public Member create(UUID uuid, String name, String tag) {
        Member newMember = new Member(uuid, name, tag, 1000.0);
        membersByUniqueId.put(uuid, newMember);
        return newMember;
    }

    public void saveMember(Member member) {
        this.memberRepository.saveMember(member);
    }

    public Member memberFromPlayer(Player player) {
        return this.findMember(player.getUniqueId()).orElseGet(() -> {
            Member newMember = this.create(player.getUniqueId(), player.getName(), "NULL");
            this.saveMember(newMember);
            return newMember;
        });
    }

    public void loadMembers() {
        this.memberRepository.loadMembers().forEach(member -> membersByUniqueId.put(member.getUniqueId(), member));
    }

    public HashMap<UUID, Member> getMembersByUniqueId() {
        return membersByUniqueId;
    }
}