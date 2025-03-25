package com.tavillecode.libra.inventory.check;

import com.tavillecode.centaurus.utils.builder.ItemBuilder;
import com.tavillecode.libra.Libra;
import com.tavillecode.libra.function.recipe.impl.LShapedRecipe;
import net.kyori.adventure.text.Component;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Interface39
 * @version 1.0
 * @description: TODO
 * @date 2025/3/6 15:32
 */
public class CheckShapedRecipeInv {
    private static class SimpleRecipeViewer {
        Inventory viewer;

        public SimpleRecipeViewer(String recipeName, List<ItemStack> itemList) {
            viewer = Bukkit.createInventory(null,InventoryType.DISPENSER,Component.text("Tag: ").append(Component.text(recipeName)));

            for (int i = 0;i <itemList.size();i++) {
                viewer.setItem(i,itemList.get(i));
            }
        }

        void open(Player player) {
            player.openInventory(viewer);
        }
    }

    private Inventory inventory;
    private Listener listener;
    private SimpleRecipeViewer recipeViewer;

    private final Player player;

    private ItemStack barrier;
    private ItemStack nextPage;
    private ItemStack backPage;
    private ItemStack statement;

    //parameter
    private int maxPage;
    private int currentPage;

    public CheckShapedRecipeInv(Player player) {
        this.player = player;
        this.init();
        this.registerListener();
        cacheCheckInv = this;
    }

    private void init() {
        MiniMessage mm = MiniMessage.miniMessage();

        this.inventory = Bukkit.createInventory(null,54,mm.deserialize("配方列表 - SHAPED"));

        this.barrier = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setName(mm.deserialize(""))
                .toItemStack();

        this.nextPage = new ItemBuilder(Material.ARROW)
                .setName(mm.deserialize("<!i><gray>下一页 <dark_gray><obf>|||"))
                .addEnchant(Enchantment.MENDING,1)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .toItemStack();

        this.backPage = new ItemBuilder(Material.ARROW)
                .setName(mm.deserialize("<!i><gray>上一页 <dark_gray><obf>|||"))
                .addEnchant(Enchantment.MENDING,1)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .toItemStack();

        this.statement = new ItemBuilder(Material.PLAYER_HEAD)
                .setName(mm.deserialize("<!i><gray>第 <white>1 <gray>页"))
                .setHeadBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmE5Yjk2MmQzODM4OWI3Y2I3YTk5NWNiZWY0N2Y0YWQ1NDI2YTBjNGFmMzJhMTg2MjZiY2JiN2RlMGYzYTNiMSJ9fX0=")
                .toItemStack();

        for (int i = 0,j = 53;i <= 8;i++,j--) {
            this.inventory.setItem(i,this.barrier);
            this.inventory.setItem(j,this.barrier);
        }

        this.inventory.setItem(4,this.statement);

        this.currentPage = 1;

        computeMaxPage();
        refreshInv();
    }

    private void refreshInv() {
        for (int i = 9;i < 44;i++) {
            this.inventory.setItem(i,null);
        }

        ItemMeta im = this.statement.getItemMeta();
        im.displayName(MiniMessage.miniMessage().deserialize("<!i><gray>第 <white><cpage> <gray>页", Placeholder.unparsed("cpage",String.valueOf(this.currentPage))));
        this.statement.setItemMeta(im);
        this.statement.setAmount(currentPage);

        this.inventory.setItem(4,this.statement);

        this.inventory.setItem(45,this.barrier);
        this.inventory.setItem(53,this.barrier);

        if (currentPage != 1) {
            this.inventory.setItem(45,backPage);
        }
        if ((currentPage + 1) <= maxPage) {
            this.inventory.setItem(53,nextPage);
        }

        addItem();
    }

    private void addItem() {
        int var1 = 0;

        if (currentPage == maxPage) {
            var1 = (maxPage * 36) - LShapedRecipe.GLOBAL_SHAPED_RECIPE_LIST.size();
        }

        int firstItemIndex = (currentPage - 1) * 36;
        int lastItemIndex = (currentPage * 36) - 1 - var1;
        int currentIndex = 9;

        ItemStack tempRecipe = null;
        ItemMeta im;
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(""));

        for (int i = firstItemIndex; i <= lastItemIndex; i++) {
            tempRecipe = LShapedRecipe.GLOBAL_SHAPED_RECIPE_LIST.get(i).getResult().clone();
            lore.set(0,MiniMessage.miniMessage().deserialize("<gray>名称: <iname>",Placeholder.component("iname",tempRecipe.displayName())));
            im = tempRecipe.getItemMeta();
            im.lore(lore);
            im.setDisplayName("§f# "+LShapedRecipe.GLOBAL_SHAPED_RECIPE_LIST.get(i).getNamespaceKey().getKey());
            tempRecipe.setItemMeta(im);
            inventory.setItem(currentIndex, tempRecipe);

            currentIndex += 1;
        }
    }

    private void computeMaxPage() {
        int mp = 0;
        int size = LShapedRecipe.GLOBAL_SHAPED_RECIPE_LIST.size();
        int nsize = size / 36;
        nsize = (int) Math.floor(nsize);

        if (size % 36 != 0) {
            mp = mp + 1;
        }

        mp = mp + nsize;
        this.maxPage = mp;
    }

    private void pageUp() {
        this.currentPage--;
        refreshInv();
    }

    private void pageDown() {
        this.currentPage++;
        refreshInv();
    }

    public void open() {
        this.player.openInventory(inventory);
    }

    private void registerListener() {
        this.listener = new Listener() {
            @EventHandler
            public void onPlayerClickInventory(InventoryClickEvent e) {
                if (recipeViewer != null && Objects.equals(e.getClickedInventory(), recipeViewer.viewer)) {
                    e.setCancelled(true);
                }
                else if (Objects.equals(e.getClickedInventory(), inventory)) {
                    if (e.getCurrentItem()!=null && Objects.requireNonNull(e.getCurrentItem()).hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) {
                        if (e.getCurrentItem().displayName().equals(barrier.displayName())) {
                            e.setCancelled(true);
                        }
                        else if (e.getCurrentItem().displayName().equals(nextPage.displayName())) {
                            e.setCancelled(true);
                            pageDown();
                        }
                        else if (e.getCurrentItem().displayName().equals(backPage.displayName())) {
                            e.setCancelled(true);
                            pageUp();
                        }
                        else if (e.getCurrentItem().displayName().equals(statement.displayName())) {
                            e.setCancelled(true);
                        }
                        else {
                            if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§f# ")) {
                                e.setCancelled(true);
                                String name = e.getCurrentItem().getItemMeta().getDisplayName().replace("§f# ","");
                                LShapedRecipe recipe = LShapedRecipe.getByRecipeName(name);
                                if (e.isLeftClick()) {
                                    SimpleRecipeViewer simpleRecipeViewer = new SimpleRecipeViewer(name,recipe.getItemIngredients());
                                    recipeViewer = simpleRecipeViewer;
                                    simpleRecipeViewer.open(player);
                                }
                                else if (e.isRightClick() && e.isShiftClick()) {
                                    Bukkit.removeRecipe(recipe.getNamespaceKey());
                                    Libra.getYml().removeRecipe(recipe);
                                    LShapedRecipe.GLOBAL_SHAPED_RECIPE_LIST.remove(recipe);
                                    refreshInv();
                                    player.sendMessage(MiniMessage.miniMessage().deserialize("<!i><red>删除成功!"));
                                }
                            }
                        }
                    }
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent e)
            {
                if (recipeViewer != null &&e.getInventory().equals(recipeViewer.viewer)) {
                    recipeViewer = null;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            open();
                        }
                    }.runTaskLater(Libra.getInstance(),3L);
                }
                else if (e.getInventory().equals(inventory) && recipeViewer == null) {
                    unregisterListener();
                    cacheCheckInv = null;
                }
            }
        };
        Bukkit.getServer().getPluginManager().registerEvents(this.listener, Libra.getInstance());
    }

    public void unregisterListener() {
        HandlerList.unregisterAll(this.listener);
    }

    public static CheckShapedRecipeInv cacheCheckInv;
}
