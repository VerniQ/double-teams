package me.verni.doubleteams.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.verni.doubleteams.member.Member;
import me.verni.doubleteams.member.MemberService;
import me.verni.doubleteams.member.MemberType;
import me.verni.doubleteams.team.Team;
import me.verni.doubleteams.team.TeamService;
import me.verni.doubleteams.util.GuiUtil;
import me.verni.doubleteams.util.SkullUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.verni.doubleteams.util.GuiUtil.textComponent;
public class TeamGui {
    private int[] SLOTS_TO_FILL_CLASIC = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 24, 25, 26};
    private int[] SLOTS_TO_FILL = {0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 19, 20, 24, 25, 26};
    private MemberService memberService;
    private TeamService teamService;

    public TeamGui(MemberService memberService, TeamService teamService) {
        this.memberService = memberService;
        this.teamService = teamService;
    }

    public Gui teamGui(Player executor) {

        Member member = memberService.memberFromPlayer(executor);

        if (member.getTag().equals("NULL")) {
            executor.performCommand("team join");
        }

        Team team = teamService.findTeam(member.getTag()).get();


        Gui gui = Gui.gui().type(GuiType.CHEST).rows(3)
                .title(textComponent("ᴢᴀʀᴢᴀ̨ᴅᴢᴀɴɪᴇ ᴛᴇᴀᴍᴇᴍ » ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                        .append(textComponent("" + team.getTag(), NamedTextColor.GREEN, TextDecoration.ITALIC)))
                .disableAllInteractions().create();

        List<Component> loreInfo = new ArrayList<>();
        loreInfo.add(textComponent("ᴛᴀɢ:", NamedTextColor.YELLOW, TextDecoration.ITALIC).append(textComponent(" " + team.getTag(), NamedTextColor.GREEN, TextDecoration.ITALIC)));
        loreInfo.add(textComponent("ɴᴀᴢᴡᴀ:", NamedTextColor.YELLOW, TextDecoration.ITALIC).append(textComponent(" " + team.getName(), NamedTextColor.GREEN, TextDecoration.ITALIC)));
        loreInfo.add(textComponent("ʟɪᴅᴇʀ:", NamedTextColor.YELLOW, TextDecoration.ITALIC).append(textComponent(" " + Bukkit.getOfflinePlayer(team.getCreatorUUID()).getName(), NamedTextColor.GREEN, TextDecoration.ITALIC)));

        List<Component> loreMembers = new ArrayList<>();
        loreMembers.add(textComponent("ʟɪᴅᴇʀ:", NamedTextColor.YELLOW, TextDecoration.ITALIC).append(textComponent(" " + Bukkit.getOfflinePlayer(team.getCreatorUUID()).getName(), NamedTextColor.GREEN, TextDecoration.ITALIC)));
        loreMembers.add(textComponent("ᴄᴢʟᴏɴᴋᴏᴡɪᴇ:", NamedTextColor.YELLOW, TextDecoration.ITALIC).append(textComponent(" " + team.getMembers().size(), NamedTextColor.GREEN, TextDecoration.ITALIC)));


        GuiItem info = ItemBuilder.from(Material.RED_BANNER)
                .name(textComponent("ɪɴꜰᴏʀᴍᴀᴄᴊᴇ ᴏ ᴛᴇᴀᴍɪᴇ", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .lore(loreInfo).glow().asGuiItem();

        GuiItem members = ItemBuilder.from(Material.RED_BANNER)
                .name(textComponent("ᴄᴢᴌᴏɴᴋᴏᴡɪᴇ ᴛᴇᴀᴍᴜ", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .lore(loreMembers).glow().asGuiItem(event -> {
                    openPaginatedGui(executor, membersGui(executor));
                });


        gui.setItem(13, info);
        gui.setItem(11, members);

        gui.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(textComponent(" ", NamedTextColor.GREEN, TextDecoration.BOLD)).asGuiItem());

        return gui;

    }

    private PaginatedGui membersGui(Player executor) {

        Member member = memberService.memberFromPlayer(executor);
        Team team = teamService.findTeam(member.getTag()).get();

        List<Member> members = teamService.findTeam(member.getTag()).get().getMembers();
        List<GuiItem> membersHeads = new ArrayList<>();

        for (Member target : members) {
            OfflinePlayer p = memberService.offlinePlayerFromMember(target);
            if (team.getCreatorUUID().equals(p.getUniqueId())) {
                GuiItem item = ItemBuilder.from(SkullUtil.getOfflinePlayerSkull(p, MemberType.LEADER)).asGuiItem(event -> {
                });
                membersHeads.add(item);
            } else {
                GuiItem item = ItemBuilder.from(SkullUtil.getOfflinePlayerSkull(p, MemberType.MEMBER)).asGuiItem(event -> {

                    team.removeMember(p);
                    target.setTag("NULL");
                    memberService.saveMember(target);
                    executor.updateInventory();

                });
                membersHeads.add(item);
            }



        }


        PaginatedGui guiTest = Gui.paginated()
                .title(textComponent("ᴄᴢᴌᴏɴᴋᴏᴡɪᴇ ᴛᴇᴀᴍᴜ", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .rows(3).pageSize(7).disableAllInteractions().create();

        guiTest.setItem(4, ItemBuilder.from(Material.NETHER_STAR)
                .name(textComponent("ᴅᴏᴅᴀᴊ ɢʀᴀᴄᴢᴀ", NamedTextColor.GREEN, TextDecoration.BOLD))
                .lore(textComponent("Kliknij aby zaprosić graczy do teamu.", NamedTextColor.GRAY, TextDecoration.ITALIC))
                .asGuiItem(event -> {
                    openPaginatedGui(executor, onlineGui(executor));
                }));

        GuiUtil.addButtons(guiTest);

        for (int slot : SLOTS_TO_FILL) {
            guiTest.setItem(slot, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE)
                    .name(textComponent(" ", NamedTextColor.BLACK, TextDecoration.BOLD))
                    .asGuiItem(event -> event.setCancelled(true)));
        }

        for (GuiItem guiItem : membersHeads) {
            guiTest.addItem(guiItem);
        }


        return guiTest;
    }

    private PaginatedGui onlineGui(Player executor) {
        Member member = memberService.memberFromPlayer(executor);

        List<GuiItem> membersHeads = new ArrayList<>();
        List<Member> members = teamService.findTeam(member.getTag()).get().getMembers();

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            executor.sendMessage(" + " + p.getName());
            if (members.contains(memberService.findMember(p.getUniqueId()).get())) {
            } else {
                GuiItem item = ItemBuilder.from(SkullUtil.getPlayerSkull(p)).asGuiItem(event -> {
                    executor.sendMessage(" + " + p.getName());
                    executor.performCommand("team invite " + p.getName());
                    executor.closeInventory();
                });
                membersHeads.add(item);
            }
        }


        PaginatedGui guiTest = Gui.paginated()
                .title(textComponent("ɢʀᴀᴄᴢᴇ ᴏɴʟɪɴᴇ", NamedTextColor.GREEN, TextDecoration.BOLD))
                .rows(3).pageSize(7).disableAllInteractions().create();

        GuiUtil.addButtons(guiTest);

        for (int slot : SLOTS_TO_FILL_CLASIC) {
            guiTest.setItem(slot, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGuiItem(event -> event.setCancelled(true)));
        }

        for (GuiItem guiItem : membersHeads) {
            guiTest.addItem(guiItem);
        }


        return guiTest;
    }

    public void openGui(Player executor, Gui gui) {
        gui.open(executor);
    }
    public void openPaginatedGui(Player executor, PaginatedGui gui) {
        gui.open(executor);
    }

}
