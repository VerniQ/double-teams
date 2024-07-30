package me.verni.doubleteams.gui;

import dev.rollczi.litecommands.annotations.join.Join;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.team.Team;
import me.verni.doubleteams.util.GuiUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static me.verni.doubleteams.util.GuiUtil.textComponent;


public class JoinGui {

    MemberService memberService;

    public JoinGui(MemberService memberService) {
        this.memberService = memberService;
    }

    private int[] slotsToFill = {0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 19, 20, 24, 25, 26};
    private final String PREFIX = "&e&lᴛᴇᴀᴍʏ &8» ";
    Material[] teamBanner = {Material.BLACK_BANNER, Material.BLUE_BANNER, Material.BROWN_BANNER, Material.CYAN_BANNER, Material.GRAY_BANNER, Material.GREEN_BANNER, Material.LIGHT_BLUE_BANNER, Material.LIGHT_GRAY_BANNER, Material.LIME_BANNER, Material.MAGENTA_BANNER, Material.ORANGE_BANNER, Material.PINK_BANNER, Material.PURPLE_BANNER, Material.RED_BANNER, Material.WHITE_BANNER, Material.YELLOW_BANNER};
    private int[] slotsToFillAnother = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 24, 25, 26};

    public PaginatedGui joinGui(Player executor) {
        Member member = memberService.memberFromPlayer(executor);

        List<Team> invites = memberService.getInvites(member);

        List<GuiItem> teams = new ArrayList<>();

        Team team1 = new Team("team1", "Team 1",  new ArrayList<>(), executor.getUniqueId());
        Team team2 = new Team("team2", "Team 2",  new ArrayList<>(), executor.getUniqueId());
        Team team3 = new Team("team3", "Team 3",  new ArrayList<>(), executor.getUniqueId());

        if(invites.isEmpty()){
            invites.add(team1);
            invites.add(team2);
            invites.add(team3);
        }

        for (Team team : invites) {
            Random random = new Random();
            int index = random.nextInt(teamBanner.length);

            GuiItem item = ItemBuilder.from(teamBanner[index]).name(textComponent(team.getName(), NamedTextColor.GREEN, TextDecoration.BOLD)).lore(textComponent("Kliknij aby dołączyć do teamu!", NamedTextColor.GRAY, TextDecoration.ITALIC)).asGuiItem(event -> {
                if (member.getTag().equals("NULL")) {

                    memberService.removeInvite(member, team);
                    List<Member> members = team.getMembers();
                    members.add(member);
                    team.setMembers(members);
                    member.setTag(team.getTag());

                    executor.closeInventory();
                    executor.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "&eDołączyłeś do teamu &a" + team.getTag()));
                } else {
                    executor.closeInventory();
                    executor.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "&cNajpierw musisz opuścić obecny team!!"));
                }
            });

            teams.add(item);
        }

        PaginatedGui gui = Gui.paginated().title(textComponent("ᴛᴇᴀᴍʏ » ", NamedTextColor.YELLOW, TextDecoration.BOLD).append(textComponent("Zaproszenia", NamedTextColor.GREEN, TextDecoration.ITALIC))).rows(3).pageSize(7).disableAllInteractions().create();

        GuiUtil.addButtons(gui);

        for(GuiItem item : teams){
            gui.addItem(item);
        }


        for (int slot : slotsToFillAnother) {
            gui.setItem(slot, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(textComponent(" ", NamedTextColor.BLACK, TextDecoration.BOLD)).asGuiItem(event -> event.setCancelled(true)));
        }

        return gui;

    }
    public void open(Player player, PaginatedGui gui) {
        gui.open(player);
    }
}
