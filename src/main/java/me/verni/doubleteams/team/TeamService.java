package me.verni.doubleteams.team;

import me.verni.doubleteams.configuration.implementation.PluginConfigImpl;
import me.verni.doubleteams.member.Member;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamService {
    private final HashMap<String, Team> teamsbyTag = new HashMap<>();

    private final PluginConfigImpl configuration;
    private final TeamRepository teamRepository;

    public TeamService(PluginConfigImpl configuration, TeamRepository teamRepository) {
        this.configuration = configuration;
        this.teamRepository = teamRepository;
    }

    public Optional<Team> findTeam(String tag) {
        return Optional.ofNullable(this.teamsbyTag.get(tag));
    }

    public void create(String tag, String name, List<Member> members, UUID creatorUUID) {
        this.teamsbyTag.put(tag, new Team(tag, name, members, creatorUUID));
    }

    public void saveTeam(Team team) {
        this.teamRepository.saveTeam(team);
    }

    public void removeTeam(Team team) {
        this.teamsbyTag.remove(team.getTag());
        this.teamRepository.removeTeam(team);
    }


    public void loadTeams(){
        this.teamRepository.loadTeams().forEach(team -> teamsbyTag.put(team.getTag(), team));
    }

    public HashMap<String, Team> getTeamsByTag() {
        return teamsbyTag;
    }
}
