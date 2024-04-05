package me.verni.doubleteams.relational;

import me.verni.doubleteams.database.AbstractDatabaseService;

import javax.sql.DataSource;
import java.sql.SQLException;

public class RelationalDatabaseImpl extends AbstractDatabaseService implements RelationalDatabase {

    private static final String INIT_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS team_members (team_tag VARCHAR(255) NOT NULL, member_tag VARCHAR(255) NOT NULL, PRIMARY KEY (team_tag, member_tag), FOREIGN KEY (team_tag) REFERENCES teams(tag), FOREIGN KEY (member_tag) REFERENCES members(tag));";

    public RelationalDatabaseImpl(DataSource dataSource) {
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
}
