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
    private final String INIT_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS members (id INT AUTO_INCREMENT PRIMARY KEY, uniqueId VARCHAR(255), name VARCHAR(255), tag VARCHAR(255));";
    private final String LOAD_MEMBERS_QUERY = "SELECT * FROM members;";
    private final String SAVE_MEMBER_QUERY = "INSERT INTO members (uniqueId, name, tag) VALUES (?, ?, ?);";
    private final String REMOVE_MEMBER_QUERY = "DELETE FROM members WHERE uniqueId = ?;";
    private final String UPDATE_MEMBER_QUERY = "UPDATE members SET name = ?, tag = ? WHERE uniqueId = ?;";

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

                    members.add(new Member(uniqueId, name, tag));
                }
                return null;
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
        return members;
    }

    @Override
    public boolean existsMember(Member member) {
        return this.querySync(LOAD_MEMBERS_QUERY, preparedStatement -> {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    UUID uniqueId1 = UUID.fromString(resultSet.getString("uniqueId"));
                    if (uniqueId1.equals(member.getUniqueId())) {
                        return true;
                    }
                }
                return false;
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }


    @Override
    public CompletableFuture<Void> saveMember(Member member) {
        if (existsMember(member)) {
            return this.execute(UPDATE_MEMBER_QUERY, preparedStatement -> {
                try {
                    preparedStatement.setString(1, member.getName());
                    preparedStatement.setString(2, member.getTag());
                    preparedStatement.setString(3, member.getUniqueId().toString());

                    preparedStatement.execute();

                    preparedStatement.getConnection().commit();

                } catch (SQLException exception) {
                    throw new RuntimeException(exception);
                }
            });
        } else {
            return this.execute(SAVE_MEMBER_QUERY, preparedStatement -> {
                try {
                    preparedStatement.setString(1, member.getUniqueId().toString());
                    preparedStatement.setString(2, member.getName());
                    preparedStatement.setString(3, member.getTag());

                    preparedStatement.execute();

                    preparedStatement.getConnection().commit();

                } catch (SQLException exception) {
                    throw new RuntimeException(exception);
                }
            });
        }

    }

    @Override
    public CompletableFuture<Void> removeMember(Member member) {
        return this.execute(REMOVE_MEMBER_QUERY, preparedStatement -> {
            try {
                preparedStatement.setString(1, member.getUniqueId().toString());

                preparedStatement.execute();

                preparedStatement.getConnection().commit();

            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }
}
