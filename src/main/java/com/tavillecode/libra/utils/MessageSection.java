package com.tavillecode.libra.utils;

import org.bukkit.entity.Player;

/**
 * @author Interface39
 * @version 1.0
 * @description: TODO
 * @date 2025/3/6 17:03
 */
public class MessageSection {
    private final static String heading = "[Libra]";
    public static void EnableMessages() {
        System.out.print("\n" + heading + " 自定义合成配方");
        System.out.print("\n" + heading + " 作者: 凉呈哟");
        System.out.print("\n" + heading + " 工作室: TavilleTribe\n");
    }
    public static void HelpMessages(Player p) {
        p.sendMessage("§8§m                                                                            ");
        p.sendMessage(" ");
        p.sendMessage("   §f“闲着没事”");
        p.sendMessage("      §f“随便搓搓”");
        p.sendMessage(" ");
        p.sendMessage("§7- §fAuthor: [§nTavilleTribe.凉呈哟§r]");
        p.sendMessage("§7- §fVersion: [§n1.0§r]");
        p.sendMessage(" ");
        p.sendMessage("§7/§flibra reload §7##重新读取所有配置");
        p.sendMessage(" ");
        p.sendMessage("§8§m                                                                            ");
    }
}
