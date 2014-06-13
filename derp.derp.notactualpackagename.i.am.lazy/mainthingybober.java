package com.gmail.brandonli2010.QuakeCraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class QuakeCraft extends JavaPlugin implements Listener {
	protected HashMap<UUID, Integer> recharge;
	public StateFlag flag;
	 
private WorldGuardPlugin getWorldGuard() {
    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
 
    // WorldGuard may not be loaded
    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
        return null; // Maybe you want throw an exception instead
    }
 
    return (WorldGuardPlugin) plugin;
}
	public void onEnable()
	{
		flag = new StateFlag("Quake", false);
		recharge = new HashMap<UUID, Integer>();
		for (Player p : Bukkit.getOnlinePlayers())
		{
			this.recharge.put(p.getUniqueId(), 20);
		}
		Bukkit.getPluginManager().registerEvents(this, this);
		new Timer(this).runTaskTimer(this, 1, 1);
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		//RegionManager regionManager = getWorldGuard().getRegionManager(event.getPlayer().getWorld());
		//ApplicableRegionSet set = regionManager.getApplicableRegions(event.getPlayer().getLocation());
		if (
				event.getItem() != null &&
				(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
				event.getMaterial() == Material.DIAMOND_HOE &&
				this.recharge.containsKey(event.getPlayer().getUniqueId()) &&
				this.recharge.get(event.getPlayer().getUniqueId()) == 0
				)
		{
			event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLAZE_HIT, 1, 1);
			this.recharge.put(event.getPlayer().getUniqueId(), 20);
			List<LivingEntity> near = new ArrayList<LivingEntity>();
			for (Entity e : event.getPlayer().getNearbyEntities(100, 100, 100))
			{
				if (e instanceof LivingEntity)
				{
					near.add((LivingEntity) e);
				}
			}
			for (Block b : event.getPlayer().getLineOfSight(null, 100))
			{
				event.getPlayer().getWorld().spawnEntity(b.getLocation(), EntityType.FIREWORK).remove();
				for (LivingEntity e : near)
				{
					if (
						(e.getLocation().getX() > b.getLocation().getX() - 1 & e.getLocation().getX() < b.getLocation().getX() + 1) &&
						(e.getLocation().getY() > b.getLocation().getY() - 2 & e.getLocation().getY() < b.getLocation().getY() + 1) &&
						(e.getLocation().getZ() > b.getLocation().getZ() - 1 & e.getLocation().getZ() < b.getLocation().getZ() + 1)
					)
					{
						String hitname = "";
						if (e instanceof Player)
							hitname = ((Player) e).getName();
						else
							if (e.getCustomName() == null)
								hitname = "a " + e.getType().toString().toLowerCase();
							else
								hitname = e.getCustomName();
						Bukkit.broadcastMessage("\u00a77[\u00a7cQuake\u00a77] \u00a7b" +  event.getPlayer().getName() + "\u00a77 has hit " + hitname + ".");
						event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
						event.getPlayer().getWorld().playEffect(e.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
						for (int i = 0; i < 10; i++)
						{
							Location rand = e.getWorld().getHighestBlockAt(e.getLocation().getBlockX() + (((int) Math.floor(Math.random() * 51)) - 25), e.getLocation().getBlockZ() + (((int) Math.floor(Math.random() * 51)) - 25)).getLocation();
							if (rand.getBlockY() > 0)
							{
								e.teleport(rand);
								return;
							}
						}
						if (e instanceof Player)
							((Player) e).sendMessage("\u00a7cError: couldn't find respawn point");
						return;
					}
				}
			}
		}
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		this.recharge.put(event.getPlayer().getUniqueId(), 20);
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		this.recharge.remove(event.getPlayer().getUniqueId());
	}
	/*@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getMaterial() == Material.DIAMOND_HOE)
		{
			Player p = event.getPlayer();
			Snowball arr = (Snowball) p.launchProjectile(Snowball.class);
			Firework f = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
			FireworkMeta fm = f.getFireworkMeta();
			fm.setPower(127);
			f.setFireworkMeta(fm);
			arr.setPassenger(f);
		}
	}
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.SNOWBALL && event.getDamager().getPassenger() != null && event.getDamager().getPassenger().getType() == EntityType.FIREWORK)
		{
			Bukkit.broadcastMessage(((Player) event.getEntity()).getName() + " has been hit.");
		}
	}*/
}

class Timer extends BukkitRunnable
{
	QuakeCraft plugin;
	public Timer(QuakeCraft instance)
	{
		this.plugin = instance;
	}
	@Override
	public void run()
	{
		for (UUID u : this.plugin.recharge.keySet())
		{
			if (this.plugin.recharge.get(u) > 0)
			{
				this.plugin.recharge.put(u, this.plugin.recharge.get(u) - 1);
			}
		}
	}
}
