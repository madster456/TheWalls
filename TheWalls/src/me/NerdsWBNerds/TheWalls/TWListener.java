package me.NerdsWBNerds.TheWalls;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;

public class TWListener implements Listener {
	public TheWalls plugin;

	public static ChatColor team = ChatColor.AQUA;
	public static ChatColor spec = ChatColor.LIGHT_PURPLE;
	
	int max = 147;
	
	public TWListener(TheWalls pt) {
		plugin = pt;
	}
	
	@EventHandler
	public void playerCommand(PlayerCommandPreprocessEvent e){
		System.out.println("[COMMAND] " + e.getPlayer().getName() + ": " + e.getMessage());
	}
	
	@EventHandler
	public void playerChat(PlayerChatEvent e){
		ChatColor clr = ChatColor.WHITE;
		String pre = "";
		
		if(e.getPlayer().isOp())
			clr = ChatColor.RED;
		
		if(!TheWalls.inGame(e.getPlayer()))
			pre = spec + "[SPEC]";
		
		e.setFormat(pre + ChatColor.GRAY + "<" + clr + e.getPlayer().getName() + ChatColor.GRAY + "> " + ChatColor.WHITE + e.getMessage());
		
		if(TheWalls.inTeamSpeak(e.getPlayer())){
			e.setCancelled(true);
			e.setFormat(team + "[TEAM]" + e.getFormat());
			
			plugin.sendTeamChat(e.getPlayer(), e.getFormat());
		}else{
			e.setCancelled(false);
			
			System.out.println(ChatColor.stripColor(e.getFormat()));
		}
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e){
		TheWalls.hidePlayer(e.getPlayer());
		
		if(!e.getPlayer().isDead() && e.getPlayer().getHealth() > 0 && e.getPlayer().isOnline()){
			plugin.addPlayer(e.getPlayer(), false);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new LoginTP(e.getPlayer(), TheWalls.getWaiting()), 20L);
		}
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent e){
		TheWalls.showPlayer(e.getPlayer());
		
		if(TheWalls.inGame(e.getPlayer()))
			e.getPlayer().setHealth(0);
		else
			plugin.removePlayer(e.getPlayer());
		
		if(TheWalls.noPlay.contains(e.getPlayer())){
			TheWalls.noPlay.remove(e.getPlayer());
		}
	}
	
	@EventHandler
	public void playerKick(PlayerKickEvent e){
		TheWalls.showPlayer(e.getPlayer());
		
		if(TheWalls.inGame(e.getPlayer()))
			e.getPlayer().setHealth(0);
		else
			plugin.removePlayer(e.getPlayer());
		
		if(TheWalls.noPlay.contains(e.getPlayer())){
			TheWalls.noPlay.remove(e.getPlayer());
		}
	}

	@EventHandler
	public void playerRespawn(PlayerRespawnEvent e){
		e.setRespawnLocation(TheWalls.getWaiting());
		
		plugin.addPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void playerDie(PlayerDeathEvent e){
		if(TheWalls.inGame(e.getEntity())){
			plugin.playerDie(e.getEntity());
			plugin.removePlayer(e.getEntity());
		}

		if(TheWalls.getQue().contains(e.getEntity())){
			plugin.removeFromQue(e.getEntity());
			e.setDeathMessage(null);
			e.getDrops().clear();
		}else{
			if(e.getEntity() != null && e.getEntity().getLastDamageCause() != null && e.getEntity().getLastDamageCause().getCause() != null && e.getEntity().getKiller() != null && e.getEntity().getLastDamageCause().getCause() == DamageCause.ENTITY_ATTACK && e.getEntity().getKiller() instanceof Player){
				e.setDeathMessage(ChatColor.RED + e.getEntity().getName() + ChatColor.WHITE + " was murdered by " + ChatColor.RED + ((Player) e.getEntity().getKiller()).getName());
				plugin.playerKill(((Player) e.getEntity().getKiller()));
			}
		}
	}
	
	@EventHandler
	public void playerInteract(PlayerInteractEvent e){
		if(TheWalls.noPlay.contains(e.getPlayer())){
			e.setCancelled(false);
			return;
		}
		
		
		if(!TheWalls.inGame(e.getPlayer())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerInteract(PlayerInteractEntityEvent e){
		if(!TheWalls.inGame(e.getPlayer())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerDamage(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player){
			Player hit = (Player)e.getEntity();
			
			if(!TheWalls.inGame(hit))
				e.setCancelled(true);

			if(e.getDamager() instanceof Player){
				Player hitter = (Player)e.getDamager();
				
				if(!TheWalls.inGame(hitter))
					e.setCancelled(true);
				
				if(TheWalls.inGame(hitter) && TheWalls.inGame(hit) && plugin.getGame(hitter) == plugin.getGame(hit) && plugin.getGame(hitter).getPerson(hitter).getTeam() == plugin.getGame(hit).getPerson(hit).getTeam() && !plugin.getGame(hitter).inPvP()){
					e.setCancelled(true);
					hitter.sendMessage(ChatColor.RED + "You cannot hit your team-mate until the wall has fallen.");
				}
			}
		}

		if(e.getDamager() instanceof Player){
			if(!TheWalls.inGame((Player)e.getDamager()))
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent e){
		if(TheWalls.noPlay.contains(e.getPlayer())){
			e.setCancelled(false);
			return;
		}
		
		if(e.getBlock().getType() == Material.SAND || e.getBlock().getType() == Material.GLASS){
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You cannot break " + e.getBlock().getType().name().toUpperCase() + ".");
			return;
		}
		
		if(TheWalls.getQue().contains(e.getPlayer())){
			e.setCancelled(true);
			return;
		}
		
		if(TheWalls.inGame(e.getPlayer())){
			if(plugin.getGame(e.getPlayer()).inDeathmatch()){
				e.setCancelled(true);
				return;
			}
		}
		
		if(e.getBlock().getLocation().getY() > max){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void blockPlace(BlockPlaceEvent e){
		if(TheWalls.noPlay.contains(e.getPlayer())){
			e.setCancelled(false);
			return;
		}
		
		if(TheWalls.getQue().contains(e.getPlayer())){
			e.setCancelled(true);
			return;
		}
		
		if(TheWalls.inGame(e.getPlayer())){
			Game game = plugin.getGame(e.getBlock().getLocation());
			
			if(!game.inBorder(e.getBlock())){
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "You cannot build here.");
				return;
			}
			
			if(plugin.getGame(e.getPlayer()).inDeathmatch()){
				e.setCancelled(true);
				return;
			}
		}
		
		if(e.getBlock().getY() > max){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onVehicleBreak(VehicleDamageEvent e){
		if(e.getAttacker() instanceof Player){
			Player player = (Player) e.getAttacker();
			
			if(!TheWalls.inGame(player))
				e.setCancelled(true);
		}
	}
	
	public void startQueCount(){
		
	}
	
	@EventHandler
	public void blockExplode(EntityExplodeEvent e){
		ArrayList<Block> toRemove = new ArrayList<Block>();
		
		for(Block b: e.blockList()){
			if(b.getType() == Material.SAND || b.getType() == Material.GLASS){
				toRemove.add(b);
			}
		}
		
		for(Block b: toRemove){
			e.blockList().remove(b);
		}
	}
	
	@EventHandler
	public void blockPushed(BlockPistonExtendEvent e){
		for(Block b: e.getBlocks()){
			if(b.getType() == Material.SAND || b.getType() == Material.GLASS){
				e.setCancelled(true);
				return;
			}
		
			Game game = plugin.getGame(e.getBlock().getLocation());
	
			int minX = game.getCenter().getX() - 60;
			int maxX = game.getCenter().getX() + 60;
			int minZ = game.getCenter().getZ() - 60;
			int maxZ = game.getCenter().getZ() + 60;
			
			if(b.getX() < minX || b.getX() > maxX || b.getZ() < minZ || b.getZ() > maxZ){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void blockPulled(BlockPistonRetractEvent e){
		if(e.isSticky()){
			if(e.getRetractLocation().getBlock().getType() == Material.GLASS || e.getRetractLocation().getBlock().getType() == Material.SAND)
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void monsterSpawn(CreatureSpawnEvent e){
		if(e.getEntity() instanceof Monster){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		if(!TheWalls.inGame(e.getPlayer()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		if(!TheWalls.inGame(e.getPlayer()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(!TheWalls.inGame(e.getPlayer()))
			e.setCancelled(true);
	}
}
