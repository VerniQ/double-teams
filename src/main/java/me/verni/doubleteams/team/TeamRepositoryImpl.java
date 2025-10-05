package me.verni.doubleteams.team;

import me.verni.doubleteams.database.AbstractDatabaseService;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TeamRepositoryImpl extends AbstractDatabaseService implements TeamRepository {

    private static final String INIT_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS teams (tag VARCHAR(3) PRIMARY KEY, name VARCHAR(255), creatorUUID VARCHAR(255), viceLeaders TEXT);";
    private static final String LOAD_TEAMS_QUERY = "SELECT * FROM teams;";
    private static final String SAVE_TEAM_QUERY = "INSERT INTO teams (tag, name, creatorUUID, viceLeaders) VALUES (?, ?, ?, ?) ON CONFLICT(tag) DO UPDATE SET name=excluded.name, creatorUUID=excluded.creatorUUID, viceLeaders=excluded.viceLeaders;";
    private static final String REMOVE_TEAM_QUERY = "DELETE FROM teams WHERE tag = ?;";

    public TeamRepositoryImpl(DataSource dataSource) {
        super(dataSource);
        this.initTable();
    }

    private void initTable() {
        this.execute(INIT_TABLE_QUERY, preparedStatement -> {
            try {
                preparedStatement.execute();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public List<Team> loadTeams() {
        List<Team> teams = new ArrayList<>();
        this.querySync(LOAD_TEAMS_QUERY, preparedStatement -> {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String tag = resultSet.getString("tag");
                    String name = resultSet.getString("name");
                    UUID creatorUUID = UUID.fromString(resultSet.getString("creatorUUID"));

                    Team team = new Team(tag, name, new ArrayList<>(), creatorUUID);

                    String viceLeadersStr = resultSet.getString("viceLeaders");
                    if (viceLeadersStr != null && !viceLeadersStr.isEmpty()) {
                        List<UUID> viceLeaders = Arrays.stream(viceLeadersStr.split(","))
                                .map(UUID::fromString)
                                .collect(Collectors.toList());
                        team.setViceLeaders(viceLeaders);
                    }
                    teams.add(team);
                }
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
            return null;
        });
        return teams;
    }

    @Override
    public CompletableFuture<Void> saveTeam(Team team) {
        return this.execute(SAVE_TEAM_QUERY, preparedStatement -> {
            try {
                String viceLeadersStr = team.getViceLeaders().stream()
                        .map(UUID::toString)
                        .collect(Collectors.joining(","));

                preparedStatement.setString(1, team.getTag());
                preparedStatement.setString(2, team.getName());
                preparedStatement.setString(3, team.getCreatorUUID().toString());
                preparedStatement.setString(4, viceLeadersStr);
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Void> removeTeam(Team team) {
        return this.execute(REMOVE_TEAM_QUERY, preparedStatement -> {
            try {
                preparedStatement.setString(1, team.getTag());
                preparedStatement.execute();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }
}