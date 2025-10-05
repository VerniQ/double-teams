package me.verni.doubleteams.kill;

import me.verni.doubleteams.configuration.implementation.PluginConfigImpl;
import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillListener implements Listener {

    private final MemberService memberService;
    private final PluginConfigImpl config;

    public PlayerKillListener(MemberService memberService, PluginConfigImpl config) {
        this.memberService = memberService;
        this.config = config;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!config.rating.enabled) {
            return;
        }

        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null || killer.equals(victim)) {
            return;
        }

        Member winner = memberService.memberFromPlayer(killer);
        Member loser = memberService.memberFromPlayer(victim);

        if (!winner.getTag().equals("NULL") && winner.getTag().equals(loser.getTag())) {
            return; // Nie przyznawaj punktów za zabicie członka teamu
        }

        if (memberService.isOnCooldown(killer.getUniqueId(), victim.getUniqueId())) {
            killer.sendMessage(ColorUtil.colorize(config.rating.cooldownMessage));
            return;
        }

        double winnerOldRating = winner.getPoints();
        double loserOldRating = loser.getPoints();

        memberService.updateRatings(winner, loser);
        memberService.setOnCooldown(killer.getUniqueId(), victim.getUniqueId());

        double winnerNewRating = winner.getPoints();
        double loserNewRating = loser.getPoints();

        double winnerChange = winnerNewRating - winnerOldRating;
        double loserChange = loserNewRating - loserOldRating;

        String killerMessage = config.rating.killerMessage
                .replace("{victim}", loser.getName())
                .replace("{new_rating}", String.format("%.1f", winnerNewRating))
                .replace("{points_change}", String.format("%.1f", winnerChange));

        String victimMessage = config.rating.victimMessage
                .replace("{killer}", winner.getName())
                .replace("{new_rating}", String.format("%.1f", loserNewRating))
                .replace("{points_change}", String.format("%.1f", Math.abs(loserChange)));

        killer.sendMessage(ColorUtil.colorize(killerMessage));
        victim.sendMessage(ColorUtil.colorize(victimMessage));
    }
}