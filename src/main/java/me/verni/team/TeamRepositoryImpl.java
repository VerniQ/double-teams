package me.verni.team;

import me.verni.database.AbstractDatabaseService;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TeamRepositoryImpl extends AbstractDatabaseService implements TeamRepository{

    private static final String INIT_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS teams (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), tag VARCHAR(3));";
    private static final String LOAD_TEAMS_QUERY = "SELECT * FROM teams;";
    protected TeamRepositoryImpl(DataSource dataSource) {
        super(dataSource);

        this.initTable();
    }

    private void initTable() {
        this.execute(INIT_TABLE_QUERY, preparedStatement -> {
            try {
                preparedStatement.execute();
            }
            catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public List<Team> loadTeams() {
        List<Team> teams = new ArrayList<>();

        this.querySync(LOAD_TEAMS_QUERY, preparedStatement -> {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    String name = resultSet.getString("name");
                    String tag = resultSet.getString("tag");

                    teams.add(new Team(name, tag, new ArrayList<>()));
                }
                return null;
            }
            catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
        return teams;
    }

    @Override
    public CompletableFuture<Void> saveTeam(Team team) {
        return null;
    }

    @Override
    public CompletableFuture<Void> removeTeam(Team team) {
        return null;
    }
}
