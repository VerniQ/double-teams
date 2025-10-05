package me.verni.doubleteams.chat;

import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.team.Team;
import me.verni.doubleteams.team.TeamService;
import me.verni.doubleteams.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class PlayerChatListener implements Listener {

    private final MemberService memberService;
    private final TeamService teamService;

    public PlayerChatListener(MemberService memberService, TeamService teamService) {
        this.memberService = memberService;
        this.teamService = teamService;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!message.startsWith("!")) {
            return;
        }

        event.setCancelled(true);

        Member member = memberService.memberFromPlayer(player);
        Optional<Team> teamOptional = teamService.findTeam(member.getTag());

        if (teamOptional.isEmpty()) {
            player.sendMessage(ColorUtil.colorize("&cNie jesteś w żadnym teamie, aby pisać na czacie drużynowym."));
            return;
        }

        Team team = teamOptional.get();
        String chatMessage = message.substring(1).trim();

        if (chatMessage.isEmpty()) {
            return;
        }

        String format = "&a[TEAM] &7" + player.getName() + ": &f" + chatMessage;
        String formattedMessage = ColorUtil.colorize(format);

        team.getMembers().stream()
                .map(m -> Bukkit.getPlayer(m.getUniqueId()))
                .filter(p -> p != null && p.isOnline())
                .forEach(onlineMember -> onlineMember.sendMessage(formattedMessage));
    }
}