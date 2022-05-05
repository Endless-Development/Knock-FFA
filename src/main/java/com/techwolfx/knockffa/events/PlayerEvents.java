package com.techwolfx.knockffa.events;

import com.cryptomorin.xseries.XMaterial;
import com.techwolfx.knockffa.Knockffa;
import com.techwolfx.knockffa.arena.Arena;
import com.techwolfx.knockffa.arena.ArenaManager;
import com.techwolfx.knockffa.data.ArenaPlayer;
import com.techwolfx.knockffa.data.TempBlock;
import com.techwolfx.knockffa.database.User;
import com.techwolfx.knockffa.enums.DeathCause;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerEvents implements Listener {

    static final ArenaManager arenaManager = ArenaManager.getManager();
    static final Knockffa plugin = Knockffa.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        e.setJoinMessage("");

        plugin.getDbCache().getUserOrCreate(player);

        if (!arenaManager.addPlayerToArena(player)) {
            player.sendMessage(ChatColor.RED + "This KnockFFA Server is bad configured, please contact an administrator :(");
        }
    }

    @EventHandler
    public void onPlayerLeft(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        e.setQuitMessage("");

        if (arenaManager.isInArena(player)) {
            Arena arena = arenaManager.getPlayerArena(player);
            // This will call ArenaLeftEvent
            // And restore inventory
            arenaManager.removePlayer(player, arena);
        }
    }

    @EventHandler
    public void onPlayerVoid(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (arenaManager.isInArena(player)) {
            Arena arena = arenaManager.getPlayerArena(player);
            if (player.getLocation().getBlockY() < 0) {

                if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent cause = (EntityDamageByEntityEvent) player.getLastDamageCause();
                    if (cause.getDamager() instanceof Player) {
                        Bukkit.getPluginManager().callEvent(new ArenaDeathEvent(arena, arena.getArenaPlayer(player), DeathCause.PLAYER, arena.getArenaPlayer( (Player) cause.getDamager())));
                    } else {
                        Bukkit.getPluginManager().callEvent(new ArenaDeathEvent(arena, arena.getArenaPlayer(player), DeathCause.VOID, null));
                    }
                } else {
                    Bukkit.getPluginManager().callEvent(new ArenaDeathEvent(arena, arena.getArenaPlayer(player), DeathCause.VOID, null));
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player receiver = (Player) e.getEntity();
        if (arenaManager.isInArena(receiver)) {
            Arena arena = arenaManager.getPlayerArena(receiver);

            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                    e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) e;

                Player attacker;
                if (entityEvent.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) entityEvent.getDamager();
                    if (arrow.getShooter() instanceof Player) {
                        attacker = (Player) arrow.getShooter();
                    } else {
                        e.setCancelled(true);
                        return;
                    }
                } else if (entityEvent.getDamager() instanceof Player) {
                    attacker = (Player) entityEvent.getDamager();
                } else {
                    e.setCancelled(true);
                    return;
                }

                // Attacker not in arena, cancel
                if (!arenaManager.isInArena(attacker)) {
                    e.setCancelled(true);
                    return;
                }

                //
                // Receiver and attacker are inside the arena
                //

                // Player can't damage him self with arrow
                if (attacker.equals(receiver)) {
                    e.setCancelled(true);
                    return;
                }

                // If the attacker or the receiver are inside the lobby, cancel damage
                if (arena.isInLobby(receiver) || arena.isInLobby(attacker)) {
                    e.setCancelled(true);
                    return;
                }

                ItemStack item = attacker.getItemInHand();
                if (item.getType() == Material.STICK) {
                    e.setDamage(0);
                    return;
                }

                if (receiver.getHealth() - e.getDamage() <= 0) {
                    e.setDamage(0);
                    Bukkit.getPluginManager().callEvent(new ArenaDeathEvent(arena, arena.getArenaPlayer(receiver), DeathCause.PLAYER, arena.getArenaPlayer(attacker)));
                    return;
                }

                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickupItemByArenaPlayer(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();
        if (arenaManager.isInArena(player))
            e.setCancelled(true);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e) {
        e.setDroppedExp(0);
        e.setDeathMessage("");
        e.getDrops().clear();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (arenaManager.isInArena(p)) {
            Arena arena = arenaManager.getPlayerArena(p);
            Block block = e.getBlock();

            if (arena.isInLobby(p) || arena.isInLobby(e.getBlock().getLocation())) {
                e.setCancelled(true);
                return;
            }
            if (block.getType() != plugin.getConfigManager().getPlaceMaterial() &&
                    block.getType() != XMaterial.COBWEB.parseMaterial()) {
                e.setCancelled(true);
                return;
            }
            if (block.getType() == plugin.getConfigManager().getPlaceMaterial()) {
                // reset block amount
                e.getItemInHand().setAmount(64);
            }
            arena.getArenaBlockList().add(new TempBlock(block.getLocation(), block.getType()));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (arenaManager.isInArena(p)) {
            Arena arena = arenaManager.getPlayerArena(p);
            Block block = e.getBlock();

            if (arena.isInLobby(p) || arena.isInLobby(e.getBlock().getLocation())) {
                e.setCancelled(true);
                return;
            }
            if (block.getType() != plugin.getConfigManager().getPlaceMaterial() &&
                    block.getType() != plugin.getConfigManager().getExpiringMaterial() &&
                    block.getType() != XMaterial.COBWEB.parseMaterial()) {
                e.setCancelled(true);
                return;
            }
            e.getBlock().setType(Material.AIR);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (arenaManager.isInArena(player)) {
            Arena arena = arenaManager.getPlayerArena(player);

            ArenaPlayer arenaPlayer = arena.getArenaPlayer(player);
            if (arenaPlayer == null) {
                return;
            }
            if (plugin.getConfig().getBoolean("disable-chat-format")) {
                return;
            }
            // Use chat format
            String msg = e.getMessage();
            e.setCancelled(true);

            arena.broadcast(plugin.getConfig().getString("messages.arena-chat-format").replace("{username}", player.getDisplayName()).replace("{message}", msg));
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        Player player = (Player) e.getEntity();
        if (arenaManager.isInArena(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onArenaRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (arenaManager.isInArena(player)) {
            Arena arena = arenaManager.getPlayerArena(player);
            arena.setupPlayer(player);
        }
    }

    @EventHandler
    public void onLeftArena(ArenaLeftEvent e) {
        Arena arena = e.getArena();
        arena.broadcast(plugin.getConfig().getString("messages.arena-left")
                .replace("{username}", e.getPlayer().getName())
                .replace("{min-players}", arena.getPlayers().size()+"")
                .replace("{max-players}", arena.getMaxPlayers()+"")
        );
    }

    @EventHandler
    public void onJoinArena(ArenaJoinEvent e) {
        Arena arena = e.getArena();
        arena.broadcast(plugin.getConfig().getString("messages.arena-join")
                .replace("{username}", e.getPlayer().getName())
                .replace("{min-players}", arena.getPlayers().size()+"")
                .replace("{max-players}", arena.getMaxPlayers()+"")
        );
    }

    @EventHandler
    public void onDeathArena(ArenaDeathEvent e) {
        Arena arena = e.getArena();
        Player p = e.getArenaPlayer().getPlayer();
        arena.setupPlayer(p);

        plugin.getDbCache().addDeaths(p, 1);
        //plugin.getDb().addDeaths(e.getKiller().getPlayer(), 1);

        if (e.getDeathCause() == DeathCause.VOID) {
            arena.broadcast(plugin.getConfig().getString("messages.killed-by-void").replace("{killed}", p.getName()));
        } else {
            p.setLastDamageCause(null);
            arena.broadcast(
                    plugin.getConfig().getString("messages.killed-by-player")
                            .replace("{killed}", p.getName())
                            .replace("{killer}", e.getKiller().getPlayer().getName()));

            User user = plugin.getDbCache().addKills(e.getKiller().getPlayer(), 1);
            if (plugin.getConfigManager().getStreaks().contains(user.getStreak())) {
                arena.broadcast(plugin.getConfig().getString("messages.kill-streak")
                        .replace("{kills}", user.getStreak()+"")
                        .replace("{username}", e.getKiller().getPlayer().getName()));
            }
            //plugin.getDb().addKills(e.getKiller().getPlayer(), 1);
            e.getKiller().getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            e.getKiller().getPlayer().getInventory().addItem(new ItemStack(Material.ARROW));
            e.getKiller().getPlayer().setHealth(20);
        }

    }

}
