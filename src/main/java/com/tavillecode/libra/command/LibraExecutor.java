package com.tavillecode.libra.command;

import com.tavillecode.libra.Libra;
import com.tavillecode.libra.inventory.CheckRecipeInv;
import com.tavillecode.libra.inventory.CreateRecipeInv;
import com.tavillecode.libra.utils.MessageSection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Interface39
 * @version 1.0
 * @description: TODO
 * @date 2025/3/6 16:47
 */
public class LibraExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String s, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp()) {
                return true;
            }
            if (args.length == 0) {
                player.performCommand("ultimateqc help");
            } else if (args.length == 1) {
                String parameter = args[0];
                if (parameter.equalsIgnoreCase("help")) {
                    MessageSection.HelpMessages(player);
                } else if (parameter.equalsIgnoreCase("reload")) {
                    Libra.getYml().reload();
                    MessageSection.EnableMessages();
                } else if (parameter.equalsIgnoreCase("create")) {
                    CreateRecipeInv createRecipeInv = new CreateRecipeInv(player);
                    createRecipeInv.open();
                } else if (parameter.equalsIgnoreCase("list")) {
                    CheckRecipeInv checkRecipeInv = new CheckRecipeInv(player);
                    checkRecipeInv.open();
                }
            }
        }
        return true;
    }
}
