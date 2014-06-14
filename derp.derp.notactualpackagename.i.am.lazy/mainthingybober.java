package com.gmail.brandonli2010.QuakeCraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;



/*import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

import org.bukkit.plugin.Plugin;*/
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
	static HashSet<Byte> transparent;
/*	public StateFlag flag;
	 
private WorldGuardPlugin getWorldGuard() {
    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
 
    // WorldGuard may not be loaded
    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
        return null; // Maybe you want throw an exception instead
    }
 
    return (WorldGuardPlugin) plugin;
}*/
	public void onEnable()
	{
		Byte[] arr = {0,6,8,9,27,28,31,32,37,38,39,40,50,51,55,59,63,65,66,68,69,70,72,75,76,77,78,83,93,94,104,105,106,111,115,127/*,131,132,141,142,143,147,148,149,150,157,171,175*/};
		//Material[] arr = {Material.AIR, Material.SAPLING, Material.WATER, Material.STATIONARY_WATER, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.LONG_GRASS, Material.DEAD_BUSH, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.TORCH, Material.FIRE, Material.REDSTONE_WIRE, Material.CROPS, Material.SIGN_POST, Material.LADDER, Material.RAILS, Material.WALL_SIGN, Material.SIGN, Material.LEVER, Material.STONE_PLATE, Material.WOOD_PLATE, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON, Material.STONE_BUTTON, Material.SNOW, Material.SUGAR_CANE_BLOCK, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.PUMPKIN_STEM, Material.MELON_STEM, Material.VINE, Material.WATER_LILY, Material.NETHER_STALK, Material.COCOA, Material.TRIPWIRE_HOOK, Material.TRIPWIRE, Material.CARROT, Material.POTATO, Material.WOOD_BUTTON, Material.GOLD_PLATE, Material.IRON_PLATE, Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON, Material.CARPET, Material.DOUBLE_PLANT};
		transparent =  new HashSet<Byte>(Arrays.asList(arr));
		//flag = new StateFlag("Quake", false);
		recharge = new HashMap<UUID, Integer>();
		for (Player p : Bukkit.getOnlinePlayers())
		{
			this.recharge.put(p.getUniqueId(), 34);
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
			this.recharge.put(event.getPlayer().getUniqueId(), 34);
			List<LivingEntity> near = new ArrayList<LivingEntity>();
			for (Entity e : event.getPlayer().getNearbyEntities(100, 100, 100))
			{
				if (e instanceof LivingEntity)
				{
					near.add((LivingEntity) e);
				}
			}
			// TODO WILL SOON BE MAGIC VALUES
			for (Block b : event.getPlayer().getLineOfSight(transparent, 100))
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
								if (e instanceof Player)
									((Player) e).playSound(e.getLocation(), Sound.FALL_BIG, 1, 1);
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
