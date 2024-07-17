package me.verni.doubleteams.team;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TeamRepository {

    List<Team> loadTeams();

    CompletableFuture<Void> saveTeam(Team team);

    CompletableFuture<Void> removeTeam(Team team);

}
