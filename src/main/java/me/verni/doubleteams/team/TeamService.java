package me.verni.doubleteams.team;

import me.verni.doubleteams.configuration.implementation.PluginConfigImpl;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TeamService {
    private final Map<String, Team> teamsbyTag = new HashMap<>();

    private final PluginConfigImpl configuration;
    private final TeamRepository teamRepository;

    public TeamService(PluginConfigImpl configuration, TeamRepository teamRepository) {
        this.configuration = configuration;
        this.teamRepository = teamRepository;
    }

    public Optional<Team> findTeam(String tag) {
        return Optional.ofNullable(this.teamsbyTag.get(tag));
    }

    public void create(String tag, String name, List<Player> members) {
        this.teamsbyTag.put(tag, new Team(tag, name, members));
    }
}
