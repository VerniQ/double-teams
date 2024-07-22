package me.verni.doubleteams.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;

import org.bukkit.entity.Player;

@Command(name = "info")
public class TestCommand {
    private MemberService memberService;

    public TestCommand(MemberService memberService){
        this.memberService = memberService;
    }

    @Execute
    public void execute(@Context Player sender){

        Member member = memberService.memberFromPlayer(sender);

        sender.sendMessage("Name: " + member.getName() + " Tag: " + member.getTag() + " UUID: " + member.getUniqueId());
    }
}
