package me.verni.doubleteams;

import com.zaxxer.hikari.HikariDataSource;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import me.verni.doubleteams.command.TeamCommand;
import me.verni.doubleteams.command.TestCommand;
import me.verni.doubleteams.configuration.ConfigService;
import me.verni.doubleteams.configuration.implementation.PluginConfigImpl;
import me.verni.doubleteams.database.DatabaseService;
import me.verni.doubleteams.gui.JoinGui;
import me.verni.doubleteams.gui.TeamGui;
import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberListener;
import me.verni.doubleteams.member.MemberRepositoryImpl;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.team.Team;
import me.verni.doubleteams.team.TeamRepositoryImpl;
import me.verni.doubleteams.team.TeamService;
import me.verni.doubleteams.util.ConnectMembersToTeams;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class DoubleTeamsPlugin extends JavaPlugin {

    private LiteCommands<CommandSender> liteCommands;

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
        Team team = new Team(memberService);

        server.getPluginManager().registerEvents(new MemberListener(memberService, teamService), this);


        memberService.loadMembers();
        teamService.loadTeams();

        for(Team tempTeam : teamService.getTeamsByTag().values()){
            Bukkit.getConsoleSender().sendMessage("Tag: " + tempTeam.getTag() + " Name: " + tempTeam.getName());
        }

        ConnectMembersToTeams connectMembersToTeams = new ConnectMembersToTeams(memberService, teamService);
        connectMembersToTeams.connect();

        JoinGui joinGui = new JoinGui(memberService);
        TeamGui teamGui = new TeamGui(memberService, teamService);

        this.liteCommands = LiteCommandsBukkit.builder("DoubleTeams")
                .commands(
                        new TestCommand(memberService),
                        new TeamCommand(memberService, teamService, joinGui, teamGui))
                .build();





    }

    public void onDisable() {
    }
}