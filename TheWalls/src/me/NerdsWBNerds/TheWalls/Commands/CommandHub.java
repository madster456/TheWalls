package me.NerdsWBNerds.TheWalls.Commands;

import java.util.ArrayList;

import me.NerdsWBNerds.TheWalls.Game;
import me.NerdsWBNerds.TheWalls.Record;
import me.NerdsWBNerds.TheWalls.Team;
import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHub implements CommandExecutor{
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			
			// ------------- List commands / general info --------------------------- //
			
			if(cmd.getName().equalsIgnoreCase("tw") && args.length > 0){
				if(args[0].equalsIgnoreCase("help")){
					return getHelpInfo(player);
				}
				
				if(args[0].equalsIgnoreCase("join")){
					return joinGame(player);
				}
				
				if(args[0].equalsIgnoreCase("quit")){
					return quitGame(player);
				}
				
				if(args[0].equalsIgnoreCase("lobby") || args[0].equalsIgnoreCase("info")){
					return tellLobbyInfo(player);
				}
				
				if(args[0].equalsIgnoreCase("map")){
					return tellMapInfo(player);
				}
				
				if(args[0].equalsIgnoreCase("top") || args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("leaderboards")){
					return showTop(player);
				}
				
				if(args[0].equalsIgnoreCase("record")){
					String target = null;
					
					if(args.length == 2){
						target = args[1];
					}
					
					return checkRecord(player, target);
				}
				
				if(args[0].equalsIgnoreCase("add")){
					return addWorld(player);
				}
				
				if(args[0].equalsIgnoreCase("setback") || args[0].equalsIgnoreCase("setbackup")){
					return setBackup(player);
				}
				
				if(args[0].equalsIgnoreCase("setwait") || args[0].equalsIgnoreCase("setwaiting")){
					return setWaiting(player);
				}
				
				if(args[0].equalsIgnoreCase("alldm") || args[0].equalsIgnoreCase("dmall") || args[0].equalsIgnoreCase("forcedm")){
					return forceAllDM(player);
				}
				
				if(args[0].equalsIgnoreCase("quitteam") || args[0].equalsIgnoreCase("leaveteam")){
					return quitTeam(player);
				}
				
				if(args[0].equalsIgnoreCase("accept")){
					return acceptInvite(player);
				}
				
				if(args[0].equalsIgnoreCase("invite")){
					if(args.length != 2){
						return false;
					}
					
					return inviteToTeam(player, args[1]);
				}
				
				if(args[0].equalsIgnoreCase("spec")){
					if(args.length != 2){
						return false;
					}
					
					return specPlayer(player, args[1]);
				}
				
				if(args[0].equalsIgnoreCase("wait") || args[0].equalsIgnoreCase("towait")){
					return toWait(player);
				}
			}
		}else{
			
		}
		
		return false;
	}

	public boolean getHelpInfo(Player player){
		if(!player.hasPermission("thewalls.gethelp")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.gethelp");
			return true;
		}
		
		
		ArrayList<String> messages = new ArrayList<String>();
		messages.add(ChatColor.GREEN + "You will " + ChatColor.RED + "AUTOMATICALLY" + ChatColor.GREEN + " be put into a game.");
		messages.add(" ");
		messages.add(ChatColor.AQUA + "/lobby " + ChatColor.GREEN + " - Get general info about your lobby.");
		messages.add(ChatColor.AQUA + "/map " + ChatColor.GREEN + " - Get general info about the map.");
		messages.add(ChatColor.AQUA + "/quit /join " + ChatColor.GREEN + " - Quit and join games/que.");
		messages.add(ChatColor.AQUA + "/team /g " + ChatColor.GREEN + " - Switch between team and global chat.");
		messages.add(ChatColor.AQUA + "/spec <player> " + ChatColor.GREEN + " - Spectate player.");
		messages.add(ChatColor.AQUA + "/record <player> " + ChatColor.GREEN + " - Get a players record.");
		
		player.sendMessage(ChatColor.GOLD + "** THE WALLS HELP **");
		
		for(String m: messages){
			player.sendMessage(m);
		}
		
		return true;	
	}
	
	public boolean joinGame(Player player){
		if(!player.hasPermission("thewalls.join")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.join");
			return true;
		}
		
		if(TheWalls.inGame(player)){
			player.sendMessage(ChatColor.RED + "Error: You are already in a game.");
			return true;
		}
		if(TheWalls.inQue(player)){
			player.sendMessage(ChatColor.RED + "Error: You are already in the waiting.");
			return true;
		}
		
		if(TheWalls.noPlay.contains(player))
			TheWalls.noPlay.remove(player);
		
		TheWalls.addPlayer(player);

		player.sendMessage(ChatColor.GREEN + "You have joined the waiting que.");
		return true;
	}
	
	public boolean quitGame(Player player){
		if(!player.hasPermission("thewalls.quit")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.quit");
			return true;
		}
		
		if(TheWalls.inGame(player)){
			TheWalls.playerLeftGame(player);
		}
		
		TheWalls.removePlayer(player);
		TheWalls.noPlay.add(player);
		
		player.sendMessage(ChatColor.GREEN + "You have left the waiting que.");
		return true;
	}
	
	public boolean tellLobbyInfo(Player player){
		if(!player.hasPermission("thewalls.getlobbyinfo")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.getlobbyinfo");
			return true;
		}
		
		if(!TheWalls.inGame(player)){
			player.sendMessage(ChatColor.GOLD + "** LOBBY INFORMATION **");
			
		}else{
			player.sendMessage(ChatColor.GOLD + "** LOBBY INFORMATION (YOUR LOBBY # IS " + (TheWalls.getGameID(player) + 1)  + ") **");
		}

		if(TheWalls.queCount != null)
			player.sendMessage(ChatColor.GREEN + "Waiting Que: " + ChatColor.AQUA + TheWalls.getQueSize() + " people. " + TheWalls.queCount.time + " second(s).");
		else
			player.sendMessage(ChatColor.GREEN + "Waiting Que: " + ChatColor.AQUA + TheWalls.getQueSize() + " people.");
		
		for(int i = 0; i < TheWalls.getGames().size(); i++){
			player.sendMessage(ChatColor.GREEN + "Lobby #" + (i + 1) + ": " + ChatColor.AQUA + TheWalls.getGames().get(i).getPeople().size() + " / " + TheWalls.max + " people. " + TheWalls.getGames().get(i).getMin() + " min(s) " + TheWalls.getGames().get(i).getSec() + " sec(s)");
		}
		
		return true;
	}
	
	public boolean tellMapInfo(Player player){
		if(!player.hasPermission("thewalls.getmapinfo")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.getmapinfo");
			return true;
		}
		
		String mapLink = "http://tinyurl.com/thewallsmap";

		
		player.sendMessage(ChatColor.GOLD + "** THE WALLS MAP INFORMATION **");
		player.sendMessage(ChatColor.GREEN + "This map is a slightly modified version of " + ChatColor.AQUA + "The Walls" + ChatColor.GREEN + " map by " + ChatColor.AQUA + "Hypixel.");
		player.sendMessage(ChatColor.GREEN + "Map: " + ChatColor.AQUA + mapLink);
		
		return true;
	}
	
	public boolean showTop(Player player){
		if(!player.hasPermission("thewalls.checktop")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.checktop");
			return true;
		}
		
		Record mW = null, mK = null, mD = null;
		
		for(Record r: TheWalls.getRecords()){
			if(mW == null)
				mW = r;
			
			if(mK == null)
				mK = r;
			
			if(mD == null)
				mD = r;

			if(r.getKills() > mK.getKills())
				mK = r;
			
			if(r.getWins() > mW.getWins())
				mW = r;

			if(r.getDeaths() > mD.getDeaths())
				mD = r;
		}
		
		player.sendMessage(ChatColor.GOLD + "** LEADERBOARDS **");
		
		if(mW != null)
			player.sendMessage(ChatColor.GREEN + "Most Wins: " + ChatColor.AQUA + mW.getName() + " with " + mW.getWins() + " win(s).");
		else
			player.sendMessage(ChatColor.GREEN + "Most Wins: " + ChatColor.AQUA + "None.");
		
		if(mK != null)
			player.sendMessage(ChatColor.GREEN + "Most Kills: " + ChatColor.AQUA + mK.getName() + " with " + mK.getKills() + " kill(s).");
		else
			player.sendMessage(ChatColor.GREEN + "Most Kills: " + ChatColor.AQUA + "None.");
		
		if(mD != null)
			player.sendMessage(ChatColor.GREEN + "Most Deaths: " + ChatColor.AQUA + mD.getName() + " with " + mD.getDeaths() + " death(s).");
		else
			player.sendMessage(ChatColor.GREEN + "Most Deaths: " + ChatColor.AQUA + "None.");
		
		return true;
	}
	
	public boolean checkRecord(Player player, String tgt){
		Player target = player;
		
		if(!player.hasPermission("thewalls.checkrecords")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.checkrecords");
			return true;
		}
		
		if(tgt != null){
			target = null;
			target = Bukkit.getServer().getPlayer(tgt);
		}
		
		if(target == null || !target.isOnline()){
			player.sendMessage(ChatColor.RED + "Error: Player not found.");
			return true;
		}

		player.sendMessage(ChatColor.GOLD + "** " + target.getName().toUpperCase() + "'S RECORD **");
		player.sendMessage(ChatColor.GREEN + "Wins: " + ChatColor.AQUA + TheWalls.getRecord(target, false).getWins());
		player.sendMessage(ChatColor.GREEN + "Kills: " + ChatColor.AQUA + TheWalls.getRecord(target, false).getKills());
		player.sendMessage(ChatColor.GREEN + "Deaths: " + ChatColor.AQUA + TheWalls.getRecord(target, false).getDeaths());
	
		return true;
	}
	
	public boolean addWorld(Player player){
		if(!player.hasPermission("thewalls.addworld")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.addworld");
			return true;
		}
		
		TheWalls.addGame(player);
		player.sendMessage(ChatColor.GREEN + "Game # " + TheWalls.getGames().size() + " added.");
		return true;
	}
	
	public boolean setBackup(Player player){
		if(!player.hasPermission("thewalls.setbackup")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.setbackup");
			return true;
		}
		
		TheWalls.backupCenter = player.getLocation().add(0, -1, 0).getBlock();
		player.sendMessage(ChatColor.GREEN + "Backup area set at your position.");
		return true;
	}
	
	public boolean setWaiting(Player player){
		if(!player.hasPermission("thewalls.setwait")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.setwait");
			return true;
		}
		
		TheWalls.setWaiting(player.getLocation());
		player.sendMessage(ChatColor.GREEN + "Waiting location set at your position.");
		return true;
	}
	
	public boolean forceAllDM(Player player){
		if(!player.hasPermission("thewalls.forcedm")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.forcedm");
			return true;
		}
		
		for(Game g: TheWalls.getGames()){
			if(g.getPlayers().size() != 0){
				g.getTimer().time = (60 * -10) + 15;
			}
		}
		
		player.sendMessage(ChatColor.GREEN + "All worlds forced into deathmatch.");
		return true;
	}
	
	public boolean quitTeam(Player player){
		if(!player.hasPermission("thewalls.quitteam")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.quitteam");
			return true;
		}
		
		TheWalls.removePlayer(player);
		TheWalls.addPlayer(player);
		
		player.sendMessage(ChatColor.GOLD + "[TheWalls] " + "You have quit your team.");
		
		return true;
	}
	
	public boolean acceptInvite(Player player){
		if(!player.hasPermission("thewalls.jointeam")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.jointeam");
			return true;
		}
		
		if(TheWalls.invites.containsKey(player)){
			Team invite = TheWalls.invites.get(player);
			
			if(TheWalls.getQue().contains(invite) && !TheWalls.inGame(player)){
				if(invite.team.size() < 3){
					TheWalls.removeFromQue(player);
					TheWalls.invites.remove(player);
					invite.addPlayer(player);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean inviteToTeam(Player player, String tgt){
		if(!player.hasPermission("thewalls.invitetoteam")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.invitetoteam");
			return true;
		}
		
		if(!TheWalls.inGame(player)){
			Player target = Bukkit.getServer().getPlayer(tgt);
			
			if(target == null || !target.isOnline()){
				player.sendMessage(ChatColor.RED + "Player not found.");
				return true;
			}
			
			if(target == player){
				player.sendMessage(ChatColor.RED + "No.");
				return true;
			}
			
			if(TheWalls.noPlay.contains(player)){
				TheWalls.noPlay.remove(player);
				TheWalls.addPlayer(player);
			}
			
			if(TheWalls.inQue(player) && TheWalls.getTeam(player).invitePlayer(target)){
				player.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " invited to team.");
				return true;
			}else{
				player.sendMessage(ChatColor.RED + "Error inviting player to team.");
				return true;
			}
		}
		
		return false;
	}
	
	public boolean specPlayer(Player player, String tgt){
		if(tgt == null)
			return false;
		
		if(!player.hasPermission("thewalls.spectate")){
			player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.spectate");
			return true;
		}
		
		if(!TheWalls.inGame(player)){
			Player target = Bukkit.getServer().getPlayer(tgt);
			
			if(target == null || !target.isOnline()){
				player.sendMessage(ChatColor.RED + "Player not found.");
				return true;
			}
			
			if(TheWalls.inGame(target)){
				TheWalls.tele(player, target.getLocation());
				player.sendMessage(ChatColor.GREEN + "Now spectating " + ChatColor.AQUA + target.getName());
				
				return true;
			}else{
				player.sendMessage(ChatColor.RED + "You can only spectate living players.");
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean toWait(Player player){
		if(!TheWalls.inGame(player)){
			if(!player.hasPermission("thewalls.tptowait")){
				player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.tptowait");
				return true;
			}
			
			TheWalls.tele(player, TheWalls.getWaiting());
			player.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "You have been teleported to the waiting area.");
			
			return true;
		}
		
		return false;
	}
}