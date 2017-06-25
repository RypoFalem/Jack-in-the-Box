package io.github.rypofalem.jackinthebox;

import com.winthier.exploits.bukkit.BukkitExploits;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JackInTheBoxPlugin extends JavaPlugin implements Listener{
	final Random random = new Random();
	List<String> worlds;
	double popChance;
	float helmetDropChance;

	@Override
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
		loadConfig();
	}

	private void loadConfig(){
		saveDefaultConfig();
		Configuration config = getConfig();
		worlds = config.getStringList("worlds");
		if(worlds == null) worlds = new ArrayList<>();
		popChance = config.getDouble("spawnChance", 0);
		helmetDropChance = (float) config.getDouble("helmetDropChance", 0);
	}

	@EventHandler
	public void onChestOpen(InventoryOpenEvent e){
		if(!(e.getInventory().getHolder() instanceof Chest)) return;
		Chest chest = (Chest) e.getInventory().getHolder();
		if(worlds.contains(chest.getWorld())) return;
		if(BukkitExploits.getInstance().isPlayerPlaced(chest.getLocation())) return;
		BukkitExploits.getInstance().setPlayerPlaced(chest.getLocation(), true);
		if(popChance <= random.nextDouble()) return;

		Location location = chest.getLocation().clone().add(.5, .5, .5);
		location.setDirection(e.getPlayer().getEyeLocation().toVector().subtract(location.toVector()));
		chest.getWorld().spawn(location, Zombie.class, zombie -> {
			zombie.getEquipment().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
			zombie.getEquipment().setHelmetDropChance(helmetDropChance);
			zombie.setCustomName("Jack-in-the-Box");
			zombie.setRemoveWhenFarAway(true);
			zombie.setBaby(true);
			Vector velocity = location.getDirection().normalize().multiply(.3).setY(.5);
			zombie.setVelocity(velocity);
			zombie.setTarget(e.getPlayer());
		});
	}
}
