package com.tavillecode.libra.inventory;

import com.tavillecode.centaurus.utils.builder.ItemBuilder;
import com.tavillecode.itemStorage.utils.ItemGetter;
import com.tavillecode.libra.Libra;
import com.tavillecode.libra.function.recipe.RecipeType;
import com.tavillecode.libra.function.recipe.impl.LShapedRecipe;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Interface39
 * @version 1.0
 * @description: TODO
 * @date 2025/3/5 11:38
 */
public class CreateRecipeInv {
    private static class Setting {
        String recipeName;
        int expCost;
        RecipeType recipeType;

        boolean saved;

        public Setting(String recipeName, int expCost, RecipeType recipeType,boolean saved) {
            this.recipeName = recipeName;
            this.expCost = expCost;
            this.recipeType = recipeType;
            this.saved = saved;
        }
    }

    //Player
    private final Player player;

    //Setting
    private Setting setting;

    //Component
    private Inventory inventory;
    private Listener listener;

    //Button
    private ItemStack air;
    private ItemStack barrier;
    private ItemStack name;
    private ItemStack type;
    private ItemStack cancel;
    private ItemStack confirm;
    private ItemStack expCost;
    private ItemStack tips;

    //Switcher
    private boolean nameSetFlag;
    private boolean expSetFlag;
    private boolean completed;

    public CreateRecipeInv(Player player) {
        this.player = player;
        this.setting = new Setting("unnamed",0,RecipeType.SHAPED,false);
        this.init();
        this.registerListener();
        this.nameSetFlag = false;
        this.expSetFlag = false;
        this.completed = false;
        cacheInv = this;
    }

    private void init() {
        MiniMessage mm = MiniMessage.miniMessage();

        this.inventory = Bukkit.createInventory(null,54,mm.deserialize("创造配方"));

        this.air = new ItemBuilder(Material.AIR)
                .toItemStack();

        this.barrier = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setName(Component.text(""))
                .toItemStack();

        this.name = new ItemBuilder(Material.NAME_TAG)
                .setName(mm.deserialize("<!i><white>配方名称(英文)"))
                .addLoreLine(mm.deserialize("<!i><white>unnamed"))
                .toItemStack();

        this.type = new ItemBuilder(Material.PLAYER_HEAD)
                .setName(mm.deserialize("<!i><gold>选择类型"))
                .setHeadBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzNjA0NTIwOGY5YjVkZGNmOGM0NDMzZTQyNGIxY2ExN2I5NGY2Yjk2MjAyZmIxZTUyNzBlZThkNTM4ODFiMSJ9fX0=")
                .addLoreLine(mm.deserialize("<!i><white>SHAPED"))
                .toItemStack();

        this.cancel = new ItemBuilder(Material.RED_DYE)
                .setName(mm.deserialize("<!i><red>取消并关闭"))
                .toItemStack();

        this.confirm = new ItemBuilder(Material.LIME_DYE)
                .setName(mm.deserialize("<!i><green>确认并保存"))
                .addEnchant(Enchantment.MENDING,1)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .toItemStack();

        this.expCost = new ItemBuilder(Material.PLAYER_HEAD)
                .setName(mm.deserialize("<!i><aqua>经验消耗"))
                .setHeadBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzk5YWQ3YTA0MzE2OTI5OTRiNmM0MTJjN2VhZmI5ZTBmYzQ5OTc1MjQwYjczYTI3ZDI0ZWQ3OTcwMzVmYjg5NCJ9fX0=")
                .addLoreLine(mm.deserialize("<!i><white>0"))
                .toItemStack();

        this.tips = new ItemBuilder(Material.OAK_HANGING_SIGN)
                .setName(mm.deserialize("<!i><yellow>提示"))
                .addEnchant(Enchantment.MENDING,1)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .addLoreLine(mm.deserialize("<!i><yellow>(<bold><aqua>!</bold><yellow>) <white>最左侧放置蓝图"))
                .addLoreLine(mm.deserialize("<!i><yellow>(<bold><aqua>!</bold><yellow>) <white>中间九格摆配方"))
                .addLoreLine(mm.deserialize("<!i><yellow>(<bold><aqua>!</bold><yellow>) <white>右边那格放成品"))
                .addLoreLine(mm.deserialize("<!i><yellow>(<bold><aqua>!</bold><yellow>) <white>最右边的所有按钮按下就可以触发"))
                .addLoreLine(mm.deserialize("<!i><yellow>(<bold><aqua>!</bold><yellow>) <white>名字和经验需要在聊天框更改"))
                .addLoreLine(mm.deserialize("<!i><yellow>(<bold><aqua>!</bold><yellow>) <white>如果你不满意你的创作,请按红色取消按钮"))
                .addLoreLine(mm.deserialize("<!i><yellow>(<bold><aqua>!</bold><yellow>) <white>如果你确认无误,请按绿色保存按钮,自动生效"))
                .toItemStack();

        for (int i = 0;i < 54;i++) {
            this.inventory.setItem(i,barrier);
        }

        for (int i = 0;i < 3;i++) {
            for (int j = 10;j <= 12;j++) {
                this.inventory.setItem(j+(i*9),air);
            }
        }

        this.inventory.setItem(18,air);
        this.inventory.setItem(22,air);

        this.inventory.setItem(15,name);
        this.inventory.setItem(16,type);
        this.inventory.setItem(17,expCost);

        this.inventory.setItem(33,cancel);
        this.inventory.setItem(35,confirm);

        this.inventory.setItem(49,tips);
    }

    public void open() {
        player.openInventory(this.inventory);
    }

    public void changeRecipeType() {
        setting.recipeType=RecipeType.values()[(setting.recipeType.ordinal()+1) % RecipeType.values().length];
        List<Component> lore = this.type.lore();
        lore.set(lore.size()-1,MiniMessage.miniMessage().deserialize("<!i><white><rtype>", Placeholder.unparsed("rtype", String.valueOf(setting.recipeType))));
        this.type.lore(lore);
        this.inventory.setItem(16,this.type);
    }

    public void setRecipeName(String name) {
        setting.recipeName = name;
        List<Component> lore = this.name.lore();
        lore.set(lore.size()-1,MiniMessage.miniMessage().deserialize("<!i><white><rname>",Placeholder.unparsed("rname",setting.recipeName)));
        this.name.lore(lore);
        this.inventory.setItem(15,this.name);
    }

    public void setExpCost(int expCost) {
        setting.expCost = expCost;
        List<Component> lore = this.expCost.lore();
        lore.set(lore.size()-1,MiniMessage.miniMessage().deserialize("<!i><white><rexp>",Placeholder.unparsed("rexp",String.valueOf(setting.expCost))));
        this.expCost.lore(lore);
        this.inventory.setItem(17,this.expCost);
    }

    private ItemStack getResult() {
        return this.inventory.getItem(22);
    }

    private ItemStack getBlueMap() {
        if (this.inventory.getItem(18) == null) {
            return ItemGetter.getItem("空气");
        }
        return this.inventory.getItem(18);
    }

    private List<ItemStack> getIngredients() {
        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0;i < 3;i++) {
            for (int j = 10;j <= 12;j++) {
                ingredients.add(this.inventory.getItem(j+(i*9)));
            }
        }
        return ingredients;
    }


    private void save() {
        switch (setting.recipeType) {
            case SHAPED -> {
                LShapedRecipe shapedRecipe = new LShapedRecipe(setting.recipeName,this.getResult(),this.getBlueMap(),setting.expCost,new ArrayList<>(),this.getIngredients());
                Libra.getYml().addRecipe(shapedRecipe);
                break;
            }
            case SHAPELESS -> {

                break;
            }
        }
    }

    private void registerListener() {
        this.listener = new Listener() {
            @EventHandler
            public void onPlayerChat(AsyncChatEvent e) {
                if (nameSetFlag) {
                    e.setCancelled(true);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            setRecipeName(((TextComponent) e.message()).content());
                            open();
                        }
                    }.runTask(Libra.getInstance());
                    nameSetFlag = false;
                }
                else if (expSetFlag) {
                    e.setCancelled(true);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            setExpCost(Integer.parseInt(((TextComponent)(e.message())).content()));
                            open();
                        }
                    }.runTask(Libra.getInstance());
                    expSetFlag = false;
                }
            }

            @EventHandler
            public void onPlayerClickInventory(InventoryClickEvent e) {
                if (Objects.equals(e.getClickedInventory(), inventory)) {
                    if (e.getCurrentItem()!= null && Objects.requireNonNull(e.getCurrentItem()).hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
                        if (e.getCurrentItem().displayName().equals(barrier.displayName())) {
                            e.setCancelled(true);
                        }
                        else if (e.getCurrentItem().displayName().equals(name.displayName())) {
                            e.setCancelled(true);
                            nameSetFlag = true;
                            player.closeInventory();
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<!i><yellow>请输入名称"));
                        }
                        else if (e.getCurrentItem().displayName().equals(type.displayName())) {
                            e.setCancelled(true);
                            changeRecipeType();
                        }
                        else if (e.getCurrentItem().displayName().equals(cancel.displayName())) {
                            e.setCancelled(true);
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<!i><red>取消编辑!"));
                            player.closeInventory();
                            unregisterListener();
                            cacheInv = null;
                        }
                        else if (e.getCurrentItem().displayName().equals(confirm.displayName())) {
                            e.setCancelled(true);
                            completed = true;
                            save();
                            player.closeInventory();
                        }
                        else if (e.getCurrentItem().displayName().equals(expCost.displayName())) {
                            e.setCancelled(true);
                            expSetFlag = true;
                            player.closeInventory();
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<!i><yellow>请输入消耗经验"));

                        }
                        else if (e.getCurrentItem().displayName().equals(tips.displayName())) {
                            e.setCancelled(true);
                        }
                    }
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent e) {
                if (e.getInventory().equals(inventory)) {
                    if (completed) {
                        e.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<!i><green>已完成编辑!创建完成!"));
                        unregisterListener();
                        cacheInv = null;
                    }
                }
            }
        };
        Bukkit.getServer().getPluginManager().registerEvents(this.listener, Libra.getInstance());
    }

    public void unregisterListener() {
        HandlerList.unregisterAll(this.listener);
    }

    public static CreateRecipeInv cacheInv;
}
