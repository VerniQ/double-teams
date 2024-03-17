package me.verni.team;

import me.verni.database.AbstractDatabaseService;

import javax.sql.DataSource;

public class TeamRepositoryImpl extends AbstractDatabaseService implements TeamRepository{

    private static final INIT_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS teams (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), color VARCHAR(255));";
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

}
