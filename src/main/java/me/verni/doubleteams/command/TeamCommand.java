package me.verni.doubleteams.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import me.verni.doubleteams.gui.JoinGui;
import me.verni.doubleteams.gui.TeamGui;
import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.team.Team;
import me.verni.doubleteams.team.TeamService;
import me.verni.doubleteams.util.ColorUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Command(name = "team")
public class TeamCommand {
    private final String PREFIX = "&e&lᴛᴇᴀᴍʏ &8» ";
    private HashMap<Player, Boolean> confirmationStatus = new HashMap<Player, Boolean>();
    private final MemberService memberService;
    private final TeamService teamService;
    private JoinGui joinGui;
    private TeamGui teamGui;


    public TeamCommand(MemberService memberService, TeamService teamService, JoinGui joinGui, TeamGui teamGui) {
        this.memberService = memberService;
        this.teamService = teamService;
        this.joinGui = joinGui;
        this.teamGui = teamGui;
    }

    @Execute
    void execute(@Context Player sender) {
        teamGui.openGui(sender, teamGui.teamGui(sender));
    }

    @Execute(name = "create")
    void executeCreate(@Context Player sender, @Arg("tag") String tag, @Arg("name") String name) {
        Member creator = memberService.memberFromPlayer(sender);

        if (!creator.getTag().equals("NULL")) {
            sender.sendMessage(color(PREFIX + "&cJesteś już członkiem teamu! Aby utworzyć nowy team, opuść obecny."));
            return;
        }
        if (tag.length() != 3 || !tag.matches("^[a-zA-Z]*$")) {
            sender.sendMessage(color(PREFIX + "&cTag musi składać się z 3 liter!"));
            return;
        }
        if (teamService.findTeam(tag).isPresent()) {
            sender.sendMessage(color(PREFIX + "&cTeam o podanym tagu już istnieje!"));
            return;
        }
        if (name.length() > 20 || name.length() < 5) {
            sender.sendMessage(color(PREFIX + "&cNazwa teamu musi zawierać od 5 do 20 znaków!"));
            return;
        }
        if (!name.matches("^[a-zA-Z]*$")) {
            sender.sendMessage(color(PREFIX + "&cNazwa teamu musi składać się z liter!"));
            return;
        }
        if (name.split(" ").length > 1) {
            sender.sendMessage(color(PREFIX + "&cNazwa teamu może składać się z maksymalnie dwóch członów!"));
        }
        List<Member> members = new ArrayList<>();
        members.add(creator);

        teamService.create(tag.toUpperCase(), capitalizeEachWord(name), members, creator.getUniqueId());
        creator.setTag(tag.toUpperCase());

        Team team = teamService.findTeam(tag.toUpperCase()).get();
        teamService.saveTeam(team);

        memberService.saveMember(creator);

        sender.sendMessage(color(PREFIX + "&aUtworzono team o tagu: &e" + tag.toUpperCase() + " &ai nazwie: &e" + capitalizeEachWord(name)));

    }

    @Execute(name = "delete")
    void executeDelete(@Context Player sender) {
        Member member = memberService.memberFromPlayer(sender);

        Team team = teamService.findTeam(member.getTag()).get();

        if (team.getCreatorUUID().equals(member.getUniqueId())) {

            if (this.confirmationStatus.containsKey(sender) && this.confirmationStatus.get(sender)) {
                this.confirmationStatus.remove(sender);
                for (Member tempMember : team.getMembers()) {
                    tempMember.setTag("NULL");
                    memberService.saveMember(tempMember);
                }

                teamService.removeTeam(team);

                member.setTag("NULL");

                memberService.saveMember(member);
                sender.sendMessage(color(PREFIX + "&aUsunięto team!"));

            } else {

                confirmationStatus.put(sender, false);

                TextComponent confirmation = new TextComponent(color(PREFIX + "&eKliknij aby potwierdzić usunięcie teamu o tagu: &c" + team.getTag()));
                confirmation.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team confirm delete"));
                confirmation.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color("&eKliknij aby potwierdzić usunięcie teamu!")).create()));
                sender.spigot().sendMessage(confirmation);
            }
        } else {
            sender.sendMessage(color(PREFIX + "&cNie jesteś liderem teamu!"));
        }
    }
    @Execute(name = "leave")
    void executeLeave(@Context Player sender) {
        Member member = memberService.memberFromPlayer(sender);

        Team team = teamService.findTeam(member.getTag()).get();

        if (team.getCreatorUUID().equals(member.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cNie możesz opuścić teamu, ponieważ jesteś liderem!"));
            return;
        }

        member.setTag("NULL");
        team.removeMember(sender);
        memberService.saveMember(member);

        sender.sendMessage(color(PREFIX + "&aOpuściłeś team!"));
    }

    @Execute(name = "invite")
    void executeInvite(@Context Player sender, @Arg Player target) {
        Member inviter = memberService.memberFromPlayer(sender);
        Member invited = memberService.memberFromPlayer(target);

        sender.sendMessage("" + target.getName());

        Team team = teamService.findTeam(inviter.getTag()).get();

        if (team.getCreatorUUID().equals(inviter.getUniqueId())) {

            confirmationStatus.put(target, false);
            sender.sendMessage(confirmationStatus.get(target).toString());

            TextComponent confirmation = new TextComponent(color(PREFIX + "&eOtrzymałeś zaproszenie do teamu: &c" + team.getTag() + " &eod gracza: &c" + inviter.getName()));
            confirmation.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team confirm invite " + team.getTag()));
            confirmation.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color("&eKliknij aby potwierdzić zaproszenie!")).create()));
            target.spigot().sendMessage(confirmation);

            memberService.addInvite(invited, team);

        } else {
            sender.sendMessage(color(PREFIX + "&cNie jesteś liderem teamu!"));
        }
    }

    @Execute(name = "join")
    void executeJoin(@Context Player sender) {
        Member member = memberService.memberFromPlayer(sender);
        if (!member.getTag().equals("NULL")) {
            sender.sendMessage(color(PREFIX + "&cMusisz najpierw opuścić obecny team!"));
            return;
        } else {
            joinGui.open(sender, joinGui.joinGui(sender));
        }
    }

    @Execute(name = "confirm")
    void executeConfirm(@Context Player sender, @Arg("Typ") String type, @OptionalArg("tag") String tag) {
        if (!confirmationStatus.containsKey(sender)) {
            sender.sendMessage(PREFIX + "&cNie masz żadnego oczekującego potwierdzenia!");
        } else {
            switch (type) {
                case "delete" -> {
                    confirmationStatus.put(sender, true);
                    sender.performCommand("team delete");
                    break;
                }
                case "invite" -> {
                    confirmationStatus.remove(sender);
                    Member member = memberService.memberFromPlayer(sender);
                    Team team = teamService.findTeam(tag.toUpperCase()).get();
                    if (!member.getTag().equals("NULL")) {
                        sender.sendMessage(PREFIX + "&cMusisz najpierw opuścić obecny team!");
                        break;
                    }
                    if (memberService.getInvites(member).isEmpty()) {
                        sender.sendMessage(PREFIX + "&cNie masz żadnych zaproszeń!");
                        break;
                    }

                    if (memberService.getInvites(member).size() > 1) {
                        sender.sendMessage(PREFIX + "&cMasz więcej niż jedno zaproszenie! Użyj /team join aby wybrac team!");
                        break;
                    }

                    List<Member> members = team.getMembers();
                    members.add(member);
                    team.setMembers(members);
                    member.setTag(tag);

                    sender.sendMessage(color(PREFIX + "&aDołączono do teamu o tagu: &e" + tag));

                }
                default -> sender.sendMessage(PREFIX + "&cBŁĄD! Zgłoś go najszybciej administracji!");

            }
        }

    }


    private static String color(String message) {
        return ColorUtil.colorize(message);
    }


    public String capitalizeEachWord(String str) {
        String[] words = str.split(" ");
        StringBuilder capitalizedStr = new StringBuilder();

        for (String word : words) {
            String firstLetter = word.substring(0, 1).toUpperCase();
            String restOfWord = word.substring(1).toLowerCase();
            capitalizedStr.append(firstLetter).append(restOfWord).append(" ");
        }

        return capitalizedStr.toString().trim();
    }
}
