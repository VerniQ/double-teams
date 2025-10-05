package me.verni.doubleteams.member;

import me.verni.doubleteams.database.AbstractDatabaseService;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MemberRepositoryImpl extends AbstractDatabaseService implements MemberRepository {
    private final String INIT_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS members (uniqueId VARCHAR(36) PRIMARY KEY, name VARCHAR(16), tag VARCHAR(3), points DOUBLE DEFAULT 1000.0);";
    private final String LOAD_MEMBERS_QUERY = "SELECT * FROM members;";
    private final String SAVE_MEMBER_QUERY = "INSERT INTO members (uniqueId, name, tag, points) VALUES (?, ?, ?, ?) ON CONFLICT(uniqueId) DO UPDATE SET name=excluded.name, tag=excluded.tag, points=excluded.points;";
    private final String REMOVE_MEMBER_QUERY = "DELETE FROM members WHERE uniqueId = ?;";

    public MemberRepositoryImpl(DataSource dataSource) {
        super(dataSource);
        this.initTable();
    }

    private void initTable() {
        this.execute(INIT_TABLE_QUERY, preparedStatement -> {
            try {
                preparedStatement.execute();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public List<Member> loadMembers() {
        List<Member> members = new ArrayList<>();
        this.querySync(LOAD_MEMBERS_QUERY, preparedStatement -> {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    UUID uniqueId = UUID.fromString(resultSet.getString("uniqueId"));
                    String name = resultSet.getString("name");
                    String tag = resultSet.getString("tag");
                    double points = resultSet.getDouble("points");
                    members.add(new Member(uniqueId, name, tag, points));
                }
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
            return null;
        });
        return members;
    }

    @Override
    public CompletableFuture<Void> saveMember(Member member) {
        return this.execute(SAVE_MEMBER_QUERY, preparedStatement -> {
            try {
                preparedStatement.setString(1, member.getUniqueId().toString());
                preparedStatement.setString(2, member.getName());
                preparedStatement.setString(3, member.getTag());
                preparedStatement.setDouble(4, member.getPoints());
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Void> removeMember(Member member) {
        return this.execute(REMOVE_MEMBER_QUERY, preparedStatement -> {
            try {
                preparedStatement.setString(1, member.getUniqueId().toString());
                preparedStatement.execute();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }
}