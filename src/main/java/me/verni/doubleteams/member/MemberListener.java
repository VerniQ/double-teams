package me.verni.doubleteams.member;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class MemberListener implements Listener {
    private final MemberService memberService;

    public MemberListener(MemberService memberService) {
        this.memberService = memberService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uniqueId = p.getUniqueId();

        this.memberService.findMember(uniqueId)
                .ifPresentOrElse(
                        member -> member.setName(p.getName()),
                        () -> this.memberService.create(uniqueId, p.getName(), null));

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        UUID uniqueId = p.getUniqueId();

        this.memberService.findMember(uniqueId).ifPresent(this.memberService::saveMember);
    }
}
