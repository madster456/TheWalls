package me.NerdsWBNerds.TheWalls;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
			
			TheWalls.sendTeamChat(e.getPlayer(), e.getFormat());
		}else{
			if(!e.getPlayer().hasPermission("thewalls.chat.global")){
				e.getPlayer().sendMessage(ChatColor.RED + "Error, requires permission thewalls.chat.global");
				e.setCancelled(true);
				return;
			}
			
			e.setCancelled(false);
			
			System.out.println(ChatColor.stripColor(e.getFormat()));
		}
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e){
		if(e.getPlayer().hasPermission("thewalls.joinonlogin")){
			if(TheWalls.noPlay.contains(e.getPlayer())){
				TheWalls.noPlay.remove(e.getPlayer());
			}

			TheWalls.hidePlayer(e.getPlayer());
			
			if(!e.getPlayer().isDead() && e.getPlayer().getHealth() > 0 && e.getPlayer().isOnline()){
				TheWalls.addPlayer(e.getPlayer(), false);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new LoginTP(e.getPlayer(), TheWalls.getWaiting()), 20L);
			}
		}else{
			if(!TheWalls.noPlay.contains(e.getPlayer()))
				TheWalls.noPlay.add(e.getPlayer());
		}
	}

	@EventHandler
	public void playerRespawn(PlayerRespawnEvent e){
		e.setRespawnLocation(TheWalls.getWaiting());

		if(e.getPlayer().hasPermission("thewalls.joinonrespawn")){
			TheWalls.hidePlayer(e.getPlayer());
			
			if(!e.getPlayer().isDead() && e.getPlayer().getHealth() > 0 && e.getPlayer().isOnline()){
				TheWalls.addPlayer(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent e){
		TheWalls.showPlayer(e.getPlayer());
		
		if(TheWalls.inGame(e.getPlayer()))
			e.getPlayer().setHealth(0);
		else
			TheWalls.removePlayer(e.getPlayer());
		
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
			TheWalls.removePlayer(e.getPlayer());
		
		if(TheWalls.noPlay.contains(e.getPlayer())){
			TheWalls.noPlay.remove(e.getPlayer());
		}
	}
	
	@EventHandler
	public void playerDie(PlayerDeathEvent e){
		if(TheWalls.inGame(e.getEntity())){
			plugin.playerDie(e.getEntity());
			
			if(TheWalls.getGame(e.getEntity()).getPlayers().size() == 2)
				e.getDrops().clear();
				
			TheWalls.removePlayer(e.getEntity());
		}

		if(TheWalls.getQue().contains(e.getEntity())){
			TheWalls.removeFromQue(e.getEntity());
			e.setDeathMessage(null);
			e.getDrops().clear();
		}else{
			if(e.getEntity() != null && e.getEntity().getLastDamageCause() != null && e.getEntity().getLastDamageCause().getCause() != null && e.getEntity().getKiller() != null){
				if(e.getEntity().getLastDamageCause().getCause() == DamageCause.ENTITY_ATTACK){
					if(e.getEntity().getKiller() instanceof Player || (e.getEntity().getKiller() instanceof Arrow && ((Arrow) e.getEntity().getKiller()).getShooter() instanceof Player)){
						Player killer = null;
						
						if(e.getEntity().getKiller() instanceof Player)
							killer = e.getEntity().getKiller();
						else
							killer = (Player) ((Arrow) e.getEntity().getKiller()).getShooter();
						
						e.setDeathMessage(ChatColor.DARK_PURPLE + e.getEntity().getName() + ChatColor.GRAY + " was murdered by " + ChatColor.DARK_PURPLE + killer.getName());
						plugin.playerKill(killer);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void playerInteract(PlayerInteractEvent e){
		if(TheWalls.noPlay.contains(e.getPlayer()) && ((e.getAction() == Action.LEFT_CLICK_BLOCK && e.getPlayer().hasPermission("thewalls.noplay.break")) || (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getPlayer().hasPermission("thewalls.noplay.place")))){
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

			if(e.getDamager() instanceof Player || (e.getDamager() instanceof Arrow && ((Arrow) e.getDamager()).getShooter() instanceof Player)){
				Player hitter = null;
				
				if(e.getDamager() instanceof Player)
					hitter = (Player)e.getDamager();
				else
					hitter = (Player) ((Arrow) e.getDamager()).getShooter();
				
				if(!TheWalls.inGame(hitter))
					e.setCancelled(true);
				
				if(TheWalls.inGame(hitter) && TheWalls.inGame(hit) && TheWalls.getGame(hitter) == TheWalls.getGame(hit) && TheWalls.getGame(hitter).getPerson(hitter).getTeam() == TheWalls.getGame(hit).getPerson(hit).getTeam() && !TheWalls.getGame(hitter).inPvP()){
					if(!hitter.hasPermission("thewalls.teamkill")){
						e.setCancelled(true);
						hitter.sendMessage(ChatColor.RED + "You cannot hurt your team-mate until the wall has fallen.");
					}
				}
			}
		}

		if(e.getDamager() instanceof Player){
			if(!TheWalls.inGame((Player)e.getDamager()))
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerHurt(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player player = (Player) e.getEntity();
			
			if(!TheWalls.inGame(player)){
				e.setDamage(0);
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent e){
		if(TheWalls.noPlay.contains(e.getPlayer()) && e.getPlayer().hasPermission("thewalls.noplay.break")){
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
			if(TheWalls.getGame(e.getPlayer()).inDeathmatch()){
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
		if(TheWalls.noPlay.contains(e.getPlayer()) && e.getPlayer().hasPermission("thewalls.noplay.place")){
			e.setCancelled(false);
			return;
		}
		
		if(TheWalls.getQue().contains(e.getPlayer())){
			e.setCancelled(true);
			return;
		}
		
		if(TheWalls.inGame(e.getPlayer())){
			Game game = TheWalls.getGame(e.getBlock().getLocation());
			
			if(!game.inBorder(e.getBlock())){
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "You cannot build here.");
				return;
			}
			
			if(TheWalls.getGame(e.getPlayer()).inDeathmatch()){
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
		
			Game game = TheWalls.getGame(e.getBlock().getLocation());
	
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
