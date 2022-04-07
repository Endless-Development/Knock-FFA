package com.techwolfx.knockffa;

import com.cryptomorin.xseries.XMaterial;
import com.techwolfx.knockffa.utils.ChatUtils;
import com.techwolfx.knockffa.utils.ConsoleUtils;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import me.clip.placeholderapi.libs.kyori.adventure.platform.facet.Facet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ConfigManager {

    private final Knockffa plugin;

    public Material getPlaceMaterial() {
        return Material.SANDSTONE;
    }

    public Material getExpiringMaterial() {
        return XMaterial.TERRACOTTA.parseMaterial();
    }

    public int getDbUpdateInterval() {
        return plugin.getConfig().getInt("database-update-interval");
    }

    public ConfigManager(Knockffa plugin) {
        this.plugin = plugin;
        fetchData();
    }

    public void reload() {
        inventoryItems.clear();
        Arrays.fill(armorItems, null);

        fetchData();
    }

    @Getter
    private final HashMap<Integer, ItemStack> inventoryItems = new HashMap<>();

    @Getter
    private final ItemStack[] armorItems = new ItemStack[4];

    private void fetchData() {
        Objects.requireNonNull(plugin.getConfig().getConfigurationSection("items"))
                .getKeys(false).forEach(object -> {
                    String itemName = plugin.getConfig().getString("items."+object+".material").toUpperCase();
                    Material mat = XMaterial.valueOf(itemName).parseMaterial();
                    if (mat == null) {
                        ConsoleUtils.coloredLog("Can't load mat called " + itemName, ChatColor.RED);
                        return;
                    }

                    int amount = plugin.getConfig().getInt("items."+object+".amount");

                    ItemStack item = new ItemStack(mat, amount);

                    if (itemName.contains("SWORD") || itemName.contains("PICKAXE") || itemName.contains("BOW")) {
                        NBTItem nbti = new NBTItem(item);
                        nbti.setByte("Unbreakable", (byte) 1);
                        item = nbti.getItem();
                    }
                    String name = plugin.getConfig().getString("items."+object+".name");
                    if (name != null && !name.isEmpty()) {
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName(ChatUtils.colorMsg(name));
                        item.setItemMeta(im);
                    }
                    if (!plugin.getConfig().getStringList("items."+object+".enchants").isEmpty()) {
                        addEnchantments(item, plugin.getConfig().getStringList("items." + object + ".enchants"));
                    }
                    inventoryItems.put(Integer.parseInt(object), item);
                });

        Objects.requireNonNull(plugin.getConfig().getConfigurationSection("armor"))
                .getKeys(false).forEach(object -> {
                    String itemName = plugin.getConfig().getString("armor."+object+".material").toUpperCase();
                    Material mat = XMaterial.valueOf(itemName).parseMaterial();
                    if (mat == null) {
                        ConsoleUtils.coloredLog("Can't load mat called " + itemName, ChatColor.RED);
                        return;
                    }
                    ItemStack item = new ItemStack(mat);

                    if (itemName.contains("HELMET") || itemName.contains("LEGGINGS") || itemName.contains("CHESTPLATE") || itemName.contains("BOOTS")) {
                        NBTItem nbti = new NBTItem(item);
                        nbti.setByte("Unbreakable", (byte) 1);
                        item = nbti.getItem();
                    }
                    String name = plugin.getConfig().getString("armor."+object+".name");
                    if (name != null && !name.isEmpty()) {
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName(ChatUtils.colorMsg(name));
                        item.setItemMeta(im);
                    }
                    if (!plugin.getConfig().getStringList("armor."+object+".enchants").isEmpty()) {
                        addEnchantments(item, plugin.getConfig().getStringList("armor." + object + ".enchants"));
                    }
                    armorItems[Integer.parseInt(object)] = item;
                });
    }

    private void addEnchantments(ItemStack item, List<String> enchants) {
        ItemMeta im = item.getItemMeta();

        enchants.forEach(enchantment -> {
            String[] split = enchantment.split("#");
            Enchantment ench = Enchantment.getByName(split[0]);
            if (ench == null) {
                ConsoleUtils.coloredLog("Failed to load enchantment called: " +split[0], ChatColor.RED);
                return;
            }
            int level = Integer.parseInt(split[1]);
            assert im != null;
            im.addEnchant(ench, level, true);
        });
        item.setItemMeta(im);
    }


}
