package me.verni.doubleteams.combat;

import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Optional;

public class PlayerCombatListener implements Listener {

    private final MemberService memberService;

    public PlayerCombatListener(MemberService memberService) {
        this.memberService = memberService;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victimEntity = event.getEntity();
        Entity damagerEntity = event.getDamager();

        if (!(victimEntity instanceof Player && damagerEntity instanceof Player)) {
            return;
        }

        Player victim = (Player) victimEntity;
        Player damager = (Player) damagerEntity;

        Optional<Member> victimMemberOpt = memberService.findMember(victim.getUniqueId());
        Optional<Member> damagerMemberOpt = memberService.findMember(damager.getUniqueId());

        if (victimMemberOpt.isEmpty() || damagerMemberOpt.isEmpty()) {
            return;
        }

        Member victimMember = victimMemberOpt.get();
        Member damagerMember = damagerMemberOpt.get();

        if (victimMember.getTag().equals("NULL") || !victimMember.getTag().equals(damagerMember.getTag())) {
            return;
        }

        event.setCancelled(true);
    }
}