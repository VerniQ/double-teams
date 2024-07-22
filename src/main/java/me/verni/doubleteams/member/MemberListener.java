package me.verni.doubleteams.member;

import me.verni.doubleteams.team.Team;
import me.verni.doubleteams.team.TeamService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MemberListener implements Listener {
    private final MemberService memberService;
    private final TeamService teamService;

    public MemberListener(MemberService memberService, TeamService teamService) {
        this.memberService = memberService;
        this.teamService = teamService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uniqueId = p.getUniqueId();

        if(this.memberService.findMember(uniqueId).isEmpty()){
            this.memberService.create(uniqueId, p.getName(), "NULL");
            p.sendMessage("created");
        }

        HashMap<String, Team> teamsByTag = teamService.getTeamsByTag();
        for(Team team : teamsByTag.values()){
            p.sendMessage(team.getTag() + " " + team.getName());
            List<Member> members = team.getMembers();
            for(Member member : members){
                p.sendMessage(member.getName());
            }
        }

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        UUID uniqueId = p.getUniqueId();

        this.memberService.findMember(uniqueId).ifPresent(this.memberService::saveMember);
    }
}
