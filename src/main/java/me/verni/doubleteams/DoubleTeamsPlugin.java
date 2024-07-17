package me.verni.doubleteams;

import com.zaxxer.hikari.HikariDataSource;
import me.verni.doubleteams.configuration.ConfigService;
import me.verni.doubleteams.configuration.implementation.PluginConfigImpl;
import me.verni.doubleteams.database.DatabaseService;
import me.verni.doubleteams.member.MemberRepositoryImpl;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.team.TeamRepositoryImpl;
import me.verni.doubleteams.team.TeamService;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class DoubleTeamsPlugin extends JavaPlugin {

    public void onEnable() {
        Server server = this.getServer();

        File dataFolder = this.getDataFolder();
        ConfigService configService = new ConfigService();
        PluginConfigImpl config = configService.create(PluginConfigImpl.class, new File(dataFolder, "config.yml"));

        DatabaseService databaseService = new DatabaseService(config);
        HikariDataSource connect = databaseService.connect(dataFolder);
        TeamRepositoryImpl teamRepository = new TeamRepositoryImpl(connect);
        MemberRepositoryImpl memberRepository = new MemberRepositoryImpl(connect);

        TeamService teamService = new TeamService(config, teamRepository);
        MemberService memberService = new MemberService(config, memberRepository);


    }

    public void onDisable() {
    }
}