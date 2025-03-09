package com.tavillecode.libra.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Interface39
 * @version 1.0
 * @description: TODO
 * @date 2025/3/6 16:47
 */
public class LibraCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("create");
            list.add("help");
            list.add("reload");
            list.add("list");
        }
        return list;
    }
}
