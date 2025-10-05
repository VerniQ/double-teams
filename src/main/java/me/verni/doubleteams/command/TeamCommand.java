package me.verni.doubleteams.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg; // <-- POPRAWIONY IMPORT
import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.team.Team;
import me.verni.doubleteams.team.TeamService;
import me.verni.doubleteams.util.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Command(name = "team")
public class TeamCommand {
    private final String PREFIX = "&e&lᴛᴇᴀᴍʏ &8» ";
    private final HashMap<Player, Boolean> confirmationStatus = new HashMap<>();
    private final MemberService memberService;
    private final TeamService teamService;

    public TeamCommand(MemberService memberService, TeamService teamService) {
        this.memberService = memberService;
        this.teamService = teamService;
    }

    @Execute
    void execute(@Context Player sender) {
        sender.performCommand("team info");
    }

    @Execute(name = "top")
    void executeTop(@Context Player sender, @OptionalArg Integer page) {
        int currentPage = (page == null || page < 1) ? 1 : page;
        int teamsPerPage = 10;

        List<Team> allTeams = new ArrayList<>(teamService.getTeamsByTag().values());

        if (allTeams.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie ma żadnych teamów na serwerze."));
            return;
        }

        allTeams.sort((team1, team2) -> Double.compare(teamService.getTeamPoints(team2), teamService.getTeamPoints(team1)));

        int totalPages = (int) Math.ceil((double) allTeams.size() / teamsPerPage);
        if (currentPage > totalPages) {
            sender.sendMessage(color(PREFIX + "&cTa strona rankingu nie istnieje. Dostępne strony: 1 - " + totalPages));
            return;
        }

        sender.sendMessage(color("&e&l--- Ranking Teamów (Strona " + currentPage + "/" + totalPages + ") ---"));

        int startIndex = (currentPage - 1) * teamsPerPage;
        for (int i = 0; i < teamsPerPage; i++) {
            int currentIndex = startIndex + i;
            if (currentIndex >= allTeams.size()) {
                break;
            }

            Team team = allTeams.get(currentIndex);
            int rank = currentIndex + 1;
            double totalPoints = teamService.getTeamPoints(team);

            sender.sendMessage(color("&e" + rank + ". &7[&a" + team.getTag() + "&7] &f" + team.getName() + " &7- &b" + String.format("%.1f", totalPoints) + " pkt"));
        }
    }

    // ... Reszta klasy TeamCommand pozostaje bez zmian ...
    // ... wklej tutaj całą resztę metod z poprzedniej odpowiedzi ...
    @Execute(name = "info")
    void executeInfo(@Context Player sender) {
        Member member = memberService.memberFromPlayer(sender);
        Optional<Team> teamOptional = teamService.findTeam(member.getTag());

        if (teamOptional.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie należysz do żadnego teamu. Twój rating: &e" + String.format("%.1f", member.getPoints())));
            return;
        }

        Team team = teamOptional.get();
        sender.sendMessage(color("&e&l--- Informacje o Teamie ---"));
        sender.sendMessage(color("&aTag: &7" + team.getTag()));
        sender.sendMessage(color("&aNazwa: &7" + team.getName()));
        sender.sendMessage(color("&aRating Drużyny: &7" + String.format("%.1f", teamService.getTeamPoints(team))));

        List<String> formattedMembers = new ArrayList<>();
        team.getMembers().stream()
                .sorted((m1, m2) -> {
                    if (team.isLeader(m1.getUniqueId())) return -1;
                    if (team.isLeader(m2.getUniqueId())) return 1;
                    if (team.isViceLeader(m1.getUniqueId()) && !team.isViceLeader(m2.getUniqueId())) return -1;
                    if (!team.isViceLeader(m1.getUniqueId()) && team.isViceLeader(m2.getUniqueId())) return 1;
                    return Double.compare(m2.getPoints(), m1.getPoints());
                })
                .forEach(teamMember -> {
                    String coloredName;
                    if (team.isLeader(teamMember.getUniqueId())) {
                        coloredName = "&a" + teamMember.getName();
                    } else if (team.isViceLeader(teamMember.getUniqueId())) {
                        coloredName = "&e" + teamMember.getName();
                    } else {
                        coloredName = "&7" + teamMember.getName();
                    }
                    coloredName += " &7(&b" + String.format("%.1f", teamMember.getPoints()) + "&7)";
                    formattedMembers.add(coloredName);
                });

        String memberList = String.join("&7, ", formattedMembers);
        sender.sendMessage(color("&aCzłonkowie (" + team.getMembers().size() + "): " + memberList));
    }

    @Execute(name = "promote")
    void executePromote(@Context Player sender, @Arg OfflinePlayer target) {
        Member leader = memberService.memberFromPlayer(sender);
        Optional<Team> teamOptional = teamService.findTeam(leader.getTag());

        if (teamOptional.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie jesteś w żadnym teamie."));
            return;
        }

        Team team = teamOptional.get();
        if (!team.isLeader(leader.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cNie jesteś liderem teamu!"));
            return;
        }

        Optional<Member> targetMemberOptional = memberService.findMember(target.getUniqueId());
        if (targetMemberOptional.isEmpty() || !team.getMembers().contains(targetMemberOptional.get())) {
            sender.sendMessage(color(PREFIX + "&cTego gracza nie ma w Twoim teamie."));
            return;
        }

        if (team.isViceLeader(target.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cTen gracz jest już zastępcą."));
            return;
        }

        if (team.isLeader(target.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cNie możesz awansować lidera."));
            return;
        }

        team.getViceLeaders().add(target.getUniqueId());
        teamService.saveTeam(team);
        sender.sendMessage(color(PREFIX + "&aAwansowano gracza &e" + target.getName() + " &ana rangę zastępcy."));
    }

    @Execute(name = "demote")
    void executeDemote(@Context Player sender, @Arg OfflinePlayer target) {
        Member leader = memberService.memberFromPlayer(sender);
        Optional<Team> teamOptional = teamService.findTeam(leader.getTag());

        if (teamOptional.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie jesteś w żadnym teamie."));
            return;
        }

        Team team = teamOptional.get();
        if (!team.isLeader(leader.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cNie jesteś liderem teamu!"));
            return;
        }

        if (!team.isViceLeader(target.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cTen gracz nie jest zastępcą."));
            return;
        }

        team.getViceLeaders().remove(target.getUniqueId());
        teamService.saveTeam(team);
        sender.sendMessage(color(PREFIX + "&aZdegradowano gracza &e" + target.getName() + " &ado rangi członka."));
    }

    @Execute(name = "invites")
    void executeInvites(@Context Player sender) {
        Member member = memberService.memberFromPlayer(sender);
        List<Team> invites = memberService.getInvites(member);

        if (invites.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie masz żadnych oczekujących zaproszeń."));
            return;
        }

        sender.sendMessage(color(PREFIX + "&eTwoje zaproszenia (" + invites.size() + "):"));

        for (Team team : invites) {
            String leaderName = Bukkit.getOfflinePlayer(team.getCreatorUUID()).getName();
            if (leaderName == null) leaderName = "Nieznany Gracz";

            Component teamMessage = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(" &7- &a" + team.getName() + " &7[&e" + team.getTag() + "&7] (od: " + leaderName + ") ")
                    .append(
                            Component.text("[DOŁĄCZ]")
                                    .color(NamedTextColor.GREEN)
                                    .decorate(TextDecoration.BOLD)
                                    .clickEvent(ClickEvent.runCommand("/team join " + team.getTag()))
                                    .hoverEvent(HoverEvent.showText(Component.text("Kliknij, aby dołączyć do teamu " + team.getName())))
                    );
            sender.sendMessage(teamMessage);
        }
    }

    @Execute(name = "create")
    void executeCreate(@Context Player sender, @Arg("Tag") String tag, @Arg("Name") String name) {
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
        if (!name.matches("^[a-zA-Z\\s]*$")) {
            sender.sendMessage(color(PREFIX + "&cNazwa teamu może zawierać tylko litery i spacje!"));
            return;
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
        Optional<Team> teamOptional = teamService.findTeam(member.getTag());

        if (teamOptional.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie jesteś w żadnym teamie."));
            return;
        }

        Team team = teamOptional.get();
        if (!team.getCreatorUUID().equals(member.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cNie jesteś liderem teamu!"));
            return;
        }

        if (confirmationStatus.getOrDefault(sender, false)) {
            confirmationStatus.remove(sender);
            team.getMembers().forEach(tempMember -> {
                tempMember.setTag("NULL");
                memberService.saveMember(tempMember);
            });
            teamService.removeTeam(team);
            sender.sendMessage(color(PREFIX + "&aUsunięto team!"));
        } else {
            confirmationStatus.put(sender, false);
            Component confirmation = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(PREFIX + "&eKliknij aby potwierdzić usunięcie teamu o tagu: &c" + team.getTag())
                    .clickEvent(ClickEvent.runCommand("/team confirm delete"))
                    .hoverEvent(HoverEvent.showText(Component.text("Kliknij aby potwierdzić usunięcie teamu!")));
            sender.sendMessage(confirmation);
        }
    }

    @Execute(name = "leave")
    void executeLeave(@Context Player sender) {
        Member member = memberService.memberFromPlayer(sender);
        Optional<Team> teamOptional = teamService.findTeam(member.getTag());

        if (teamOptional.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie jesteś w żadnym teamie."));
            return;
        }

        Team team = teamOptional.get();
        if (team.getCreatorUUID().equals(member.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cNie możesz opuścić teamu, ponieważ jesteś liderem! Użyj /team delete."));
            return;
        }

        teamService.removePlayerFromTeam(team, sender);
        sender.sendMessage(color(PREFIX + "&aOpuściłeś team!"));
    }

    @Execute(name = "invite")
    void executeInvite(@Context Player sender, @Arg Player target) {
        Member inviter = memberService.memberFromPlayer(sender);
        Member invited = memberService.memberFromPlayer(target);
        Optional<Team> teamOptional = teamService.findTeam(inviter.getTag());

        if (teamOptional.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie jesteś w żadnym teamie."));
            return;
        }

        Team team = teamOptional.get();
        if (!team.isLeader(inviter.getUniqueId()) && !team.isViceLeader(inviter.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cNie masz uprawnień, aby zapraszać do teamu!"));
            return;
        }

        if (!invited.getTag().equals("NULL")) {
            sender.sendMessage(color(PREFIX + "&cGracz " + target.getName() + " jest już w innym teamie."));
            return;
        }

        memberService.addInvite(invited, team);
        sender.sendMessage(color(PREFIX + "&aWysłano zaproszenie do gracza &e" + target.getName()));

        Component invitation = LegacyComponentSerializer.legacyAmpersand()
                .deserialize(PREFIX + "&eOtrzymałeś zaproszenie do teamu: &c" + team.getTag() + " &eod gracza: &c" + inviter.getName() + ". ")
                .append(Component.text("[DOŁĄCZ]")
                        .color(NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/team join " + team.getTag()))
                        .hoverEvent(HoverEvent.showText(Component.text("Kliknij, aby dołączyć do teamu " + team.getTag()))));
        target.sendMessage(invitation);
    }

    @Execute(name = "join")
    void executeJoin(@Context Player sender, @Arg String tag) {
        Member member = memberService.memberFromPlayer(sender);
        if (!member.getTag().equals("NULL")) {
            sender.sendMessage(color(PREFIX + "&cJuż jesteś w teamie!"));
            return;
        }

        Optional<Team> teamToJoinOptional = teamService.findTeam(tag.toUpperCase());
        if (teamToJoinOptional.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cTeam o podanym tagu nie istnieje."));
            return;
        }

        Team teamToJoin = teamToJoinOptional.get();
        if (!memberService.getInvites(member).contains(teamToJoin)) {
            sender.sendMessage(color(PREFIX + "&cNie masz zaproszenia do tego teamu."));
            return;
        }

        memberService.removeInvite(member, teamToJoin);
        teamService.addPlayerToTeam(teamToJoin, sender);
        sender.sendMessage(color(PREFIX + "&aDołączyłeś do teamu &e" + teamToJoin.getTag()));
    }

    @Execute(name = "confirm")
    void executeConfirm(@Context Player sender, @Arg String type) {
        if ("delete".equalsIgnoreCase(type)) {
            if (confirmationStatus.containsKey(sender)) {
                confirmationStatus.put(sender, true);
                sender.performCommand("team delete");
            } else {
                sender.sendMessage(color(PREFIX + "&cNie masz żadnego oczekującego potwierdzenia usunięcia."));
            }
        } else {
            sender.sendMessage(color(PREFIX + "&cNieznany typ potwierdzenia."));
        }
    }

    @Execute(name = "kick")
    void executeKick(@Context Player sender, @Arg OfflinePlayer target) {
        Member member = memberService.memberFromPlayer(sender);
        Optional<Team> teamOptional = teamService.findTeam(member.getTag());

        if (teamOptional.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie jesteś w żadnym teamie."));
            return;
        }

        Team team = teamOptional.get();
        if (!team.isLeader(member.getUniqueId()) && !team.isViceLeader(member.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cNie masz uprawnień, aby wyrzucać z teamu!"));
            return;
        }

        if (team.isLeader(target.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cNie możesz wyrzucić lidera teamu!"));
            return;
        }

        Optional<Member> targetMemberOptional = memberService.findMember(target.getUniqueId());
        if (targetMemberOptional.isEmpty() || !team.getMembers().contains(targetMemberOptional.get())) {
            sender.sendMessage(color(PREFIX + "&cTego gracza nie ma w Twoim teamie."));
            return;
        }

        teamService.removePlayerFromTeam(team, target);
        sender.sendMessage(color(PREFIX + "&aWyrzucono gracza &e" + target.getName() + "&a z teamu."));
    }

    @Execute(name = "sethome")
    void executeSetHome(@Context Player sender) {
        Member member = memberService.memberFromPlayer(sender);
        Optional<Team> teamOptional = teamService.findTeam(member.getTag());

        if (teamOptional.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie jesteś w żadnym teamie."));
            return;
        }

        Team team = teamOptional.get();
        if (!team.isLeader(member.getUniqueId()) && !team.isViceLeader(member.getUniqueId())) {
            sender.sendMessage(color(PREFIX + "&cNie masz uprawnień, aby ustawić dom drużyny!"));
            return;
        }

        team.setHomeLocation(sender.getLocation());
        teamService.saveTeam(team);
        sender.sendMessage(color(PREFIX + "&aPomyślnie ustawiono dom drużyny."));
    }

    @Execute(name = "home")
    void executeHome(@Context Player sender) {
        Member member = memberService.memberFromPlayer(sender);
        Optional<Team> teamOptional = teamService.findTeam(member.getTag());

        if (teamOptional.isEmpty()) {
            sender.sendMessage(color(PREFIX + "&cNie jesteś w żadnym teamie."));
            return;
        }

        Team team = teamOptional.get();
        if (team.getHomeLocation() == null) {
            sender.sendMessage(color(PREFIX + "&cTwoja drużyna nie ma ustawionego domu."));
            return;
        }

        sender.teleport(team.getHomeLocation());
        sender.sendMessage(color(PREFIX + "&aPrzeteleportowano do domu drużyny."));
    }

    private String color(String message) {
        return ColorUtil.colorize(message);
    }

    public String capitalizeEachWord(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        String[] words = str.split(" ");
        StringBuilder capitalizedStr = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                String firstLetter = word.substring(0, 1).toUpperCase();
                String restOfWord = word.substring(1).toLowerCase();
                capitalizedStr.append(firstLetter).append(restOfWord).append(" ");
            }
        }
        return capitalizedStr.toString().trim();
    }
}