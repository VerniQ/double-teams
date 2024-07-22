package me.verni.doubleteams.team;

import me.verni.doubleteams.database.AbstractDatabaseService;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TeamRepositoryImpl extends AbstractDatabaseService implements TeamRepository {

    private static final String INIT_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS teams (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), tag VARCHAR(255), creatorUUID VARCHAR(255));";
    private static final String LOAD_TEAMS_QUERY = "SELECT * FROM teams;";
    private static final String SAVE_TEAM_QUERY = "INSERT INTO teams (name, tag, creatorUUID) VALUES (?, ?, ?);";
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
                    String name = resultSet.getString("name");
                    String tag = resultSet.getString("tag");
                    UUID creatorUUID = UUID.fromString(resultSet.getString("creatorUUID"));

                    teams.add(new Team(tag, name, new ArrayList<>(), creatorUUID));
                }
                return null;
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
        return teams;
    }

    @Override
    public CompletableFuture<Void> saveTeam(Team team) {
        return this.execute(SAVE_TEAM_QUERY, preparedStatement -> {
            try {
                preparedStatement.setString(1, team.getName());
                preparedStatement.setString(2, team.getTag());
                preparedStatement.setString(3, team.getCreatorUUID().toString());

                preparedStatement.execute();

                preparedStatement.getConnection().commit();

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
                preparedStatement.getConnection().commit();

            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }
}
