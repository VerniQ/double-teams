package me.verni.doubleteams.parser;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.team.TeamService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MemberResolver extends ArgumentResolver<CommandSender, OfflinePlayer> {

    private final MemberService memberService;
    private final TeamService teamService;

    public MemberResolver(MemberService memberService, TeamService teamService) {
        this.memberService = memberService;
        this.teamService = teamService;
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<OfflinePlayer> argument, SuggestionContext context) {
        List<String> teamMembers = teamService.getTeamsByTag().values().stream()
                .flatMap(team -> team.getMembers().stream())
                .map(Member::getName)
                .collect(Collectors.toList());

        return SuggestionResult.of(teamMembers);
    }

    @Override
    protected ParseResult<OfflinePlayer> parse(Invocation<CommandSender> invocation, Argument<OfflinePlayer> argument, String s) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
        if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
            return ParseResult.success(offlinePlayer);
        }
        return ParseResult.failure("Player not found: " + argument);
    }
}
