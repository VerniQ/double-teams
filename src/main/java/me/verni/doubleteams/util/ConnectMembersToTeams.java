package me.verni.doubleteams.util;

import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.team.Team;
import me.verni.doubleteams.team.TeamService;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConnectMembersToTeams {

    private MemberService memberService;
    private TeamService teamService;

    public ConnectMembersToTeams(MemberService memberService, TeamService teamService) {
        this.memberService = memberService;
        this.teamService = teamService;
    }

    public void connect(){

        HashMap<UUID, Member> membersByUniqueId = this.memberService.getMembersByUniqueId();

        HashMap<String, Team> teamsByTag = this.teamService.getTeamsByTag();

        if(membersByUniqueId.isEmpty() || teamsByTag.isEmpty()){
            return;
        }

        for(Team team : teamsByTag.values()){
            List<Member> members = new ArrayList<>();
            for(Member member : membersByUniqueId.values()){
                if(member.getTag().equals(team.getTag())){
                    members.add(member);
                }
            }
            team.setMembers(members);
            List<Member> test = team.getMembers();
            Bukkit.getConsoleSender().sendMessage("Tag:" + team.getTag());
            for(Member member : test){
                Bukkit.getConsoleSender().sendMessage(member.getName());
            }
        }
    }
}
