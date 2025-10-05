package me.verni.doubleteams.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.team.Team;
import me.verni.doubleteams.team.TeamService;
import me.verni.doubleteams.util.ColorUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@Command(name = "teamadmin", aliases = "ta")
@Permission("doubleteams.admin")
public class TeamAdminCommand {

    private final MemberService memberService;
    private final TeamService teamService;
    private final String PREFIX = "&c&lADMIN &8» ";

    public TeamAdminCommand(MemberService memberService, TeamService teamService) {
        this.memberService = memberService;
        this.teamService = teamService;
    }

    @Execute(name = "delete")
    void deleteTeam(@Context CommandSender sender, @Arg String tag) {
        Optional<Team> teamOptional = teamService.findTeam(tag);
        if (teamOptional.isEmpty()) {
            sender.sendMessage(color("&cTeam o tagu " + tag.toUpperCase() + " nie istnieje."));
            return;
        }
        Team team = teamOptional.get();
        team.getMembers().forEach(member -> {
            member.setTag("NULL");
            memberService.saveMember(member);
        });
        teamService.removeTeam(team);
        sender.sendMessage(color(PREFIX + "&aPomyślnie usunięto team &e" + tag.toUpperCase()));
    }

    @Execute(name = "forcejoin")
    void forceJoin(@Context CommandSender sender, @Arg Player target, @Arg String tag) {
        Optional<Team> teamOptional = teamService.findTeam(tag);
        if (teamOptional.isEmpty()) {
            sender.sendMessage(color("&cTeam o tagu " + tag.toUpperCase() + " nie istnieje."));
            return;
        }
        Team team = teamOptional.get();
        teamService.addPlayerToTeam(team, target);
        sender.sendMessage(color(PREFIX + "&aDodano gracza &e" + target.getName() + " &ado teamu &e" + tag.toUpperCase()));
        target.sendMessage(color(PREFIX + "&aZostałeś dodany do teamu &e" + tag.toUpperCase() + " &aprzez administratora."));
    }

    @Execute(name = "forceleave")
    void forceLeave(@Context CommandSender sender, @Arg OfflinePlayer target) {
        Optional<Member> memberOptional = memberService.findMember(target.getUniqueId());
        if (memberOptional.isEmpty() || memberOptional.get().getTag().equals("NULL")) {
            sender.sendMessage(color(PREFIX + "&cGracz " + target.getName() + " nie jest w żadnym teamie."));
            return;
        }
        Member member = memberOptional.get();
        teamService.findTeam(member.getTag()).ifPresent(team -> teamService.removePlayerFromTeam(team, target));
        sender.sendMessage(color(PREFIX + "&aUsunięto gracza &e" + target.getName() + " &az jego teamu."));
    }

    @Execute(name = "setpoints")
    void setPoints(@Context CommandSender sender, @Arg OfflinePlayer target, @Arg double points) {
        Optional<Member> memberOptional = memberService.findMember(target.getUniqueId());
        if (memberOptional.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie znaleziono gracza " + target.getName() + " w bazie danych."));
            return;
        }
        Member member = memberOptional.get();
        member.setPoints(points);
        memberService.saveMember(member);
        sender.sendMessage(color(PREFIX + "&aUstawiono punkty gracza &e" + target.getName() + " &ana &b" + points));
    }

    private String color(String message) {
        return ColorUtil.colorize(message);
    }
}