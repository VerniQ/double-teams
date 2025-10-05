package me.verni.doubleteams;

import com.zaxxer.hikari.HikariDataSource;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import me.verni.doubleteams.chat.PlayerChatListener;
import me.verni.doubleteams.combat.PlayerCombatListener;
import me.verni.doubleteams.command.TeamAdminCommand;
import me.verni.doubleteams.command.TeamCommand;
import me.verni.doubleteams.command.TestCommand;
import me.verni.doubleteams.configuration.ConfigService;
import me.verni.doubleteams.configuration.implementation.PluginConfigImpl;
import me.verni.doubleteams.database.DatabaseService;
import me.verni.doubleteams.kill.PlayerKillListener;
import me.verni.doubleteams.member.MemberListener;
import me.verni.doubleteams.member.MemberRepositoryImpl;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.parser.MemberResolver;
import me.verni.doubleteams.team.TeamRepositoryImpl;
import me.verni.doubleteams.team.TeamService;
import me.verni.doubleteams.util.ConnectMembersToTeams;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DoubleTeamsPlugin extends JavaPlugin {

    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        Server server = this.getServer();
        File dataFolder = this.getDataFolder();

        ConfigService configService = new ConfigService();
        PluginConfigImpl config = configService.create(PluginConfigImpl.class, new File(dataFolder, "config.yml"));

        DatabaseService databaseService = new DatabaseService(config);
        HikariDataSource dataSource = databaseService.connect(dataFolder);

        TeamRepositoryImpl teamRepository = new TeamRepositoryImpl(dataSource);
        MemberRepositoryImpl memberRepository = new MemberRepositoryImpl(dataSource);

        MemberService memberService = new MemberService(config, memberRepository);
        TeamService teamService = new TeamService(config, teamRepository, memberService);

        server.getPluginManager().registerEvents(new MemberListener(memberService, teamService), this);
        server.getPluginManager().registerEvents(new PlayerKillListener(memberService, config), this);
        server.getPluginManager().registerEvents(new PlayerChatListener(memberService, teamService), this);
        server.getPluginManager().registerEvents(new PlayerCombatListener(memberService), this);

        MemberResolver memberResolver = new MemberResolver(memberService, teamService);

        memberService.loadMembers();
        teamService.loadTeams();

        ConnectMembersToTeams connectMembersToTeams = new ConnectMembersToTeams(memberService, teamService);
        connectMembersToTeams.connect();

        this.liteCommands = LiteCommandsBukkit.builder("DoubleTeams")
                .commands(
                        new TestCommand(memberService),
                        new TeamCommand(memberService, teamService),
                        new TeamAdminCommand(memberService, teamService)
                )
                .argumentParser(OfflinePlayer.class, memberResolver)
                .argumentSuggester(OfflinePlayer.class, memberResolver)
                .build();
    }

    @Override
    public void onDisable() {
    }
}