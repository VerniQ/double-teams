package me.verni.doubleteams.team;

import me.verni.doubleteams.configuration.implementation.PluginConfigImpl;
import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamService {
    private final HashMap<String, Team> teamsByTag = new HashMap<>();
    private final PluginConfigImpl configuration;
    private final TeamRepository teamRepository;
    private final MemberService memberService;

    private static final int MIN_MEMBERS_FOR_RANKING = 3;

    public TeamService(PluginConfigImpl configuration, TeamRepository teamRepository, MemberService memberService) {
        this.configuration = configuration;
        this.teamRepository = teamRepository;
        this.memberService = memberService;
    }

    // TA METODA ZOSTAŁA ZAKTUALIZOWANA
    public double getTeamPoints(Team team) {
        if (team.getMembers() == null || team.getMembers().size() < MIN_MEMBERS_FOR_RANKING) {
            return 0.0;
        }
        return team.getMembers().stream()
                .mapToDouble(Member::getPoints)
                .average()
                .orElse(0.0);
    }

    public Optional<Team> findTeam(String tag) {
        return Optional.ofNullable(this.teamsByTag.get(tag.toUpperCase()));
    }

    public void create(String tag, String name, List<Member> members, UUID creatorUUID) {
        this.teamsByTag.put(tag.toUpperCase(), new Team(tag.toUpperCase(), name, members, creatorUUID));
    }

    public void saveTeam(Team team) {
        this.teamRepository.saveTeam(team);
    }

    public void removeTeam(Team team) {
        this.teamsByTag.remove(team.getTag());
        this.teamRepository.removeTeam(team);
    }

    public void addPlayerToTeam(Team team, Player player) {
        Member member = memberService.memberFromPlayer(player);
        team.addMember(member);
        member.setTag(team.getTag());
        memberService.saveMember(member);
    }

    public void removePlayerFromTeam(Team team, OfflinePlayer player) {
        memberService.findMember(player.getUniqueId()).ifPresent(member -> {
            team.removeMember(member);
            member.setTag("NULL");
            memberService.saveMember(member);
        });
    }

    public void loadTeams() {
        this.teamRepository.loadTeams().forEach(team -> teamsByTag.put(team.getTag(), team));
    }

    public HashMap<String, Team> getTeamsByTag() {
        return teamsByTag;
    }
}