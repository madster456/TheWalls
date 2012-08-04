package me.NerdsWBNerds.TheWalls;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class Game {
	private Block center;
	private ArrayList<Person> people = new ArrayList<Person>();
	
	private GameCount timer = null;
	public int sandHeight = 35;
	
	public Game(Block b){
		center = b;
		
		restoreMap();
	}
	
	public ArrayList<Person> getPeople(){
		return people;
	}
	
	public ArrayList<Player> getPlayers(){
		ArrayList<Player> ret = new ArrayList<Player>();
		
		for(Person p: getPeople()){
			ret.add(p.getPlayer());
		}
		
		return ret;
	}
	
	public boolean inBorder(Location l){
		return inBorder(l.getBlock());
	}
	
	public boolean inBorder(Block l){
		if(l.getWorld() != getWorld())
			return false;
		
		if(l.getX() > getLeftCenter().getX() && l.getX() < getRightCenter().getX()){
			if(l.getZ() > getFrontCenter().getZ() && l.getZ() < getBackCenter().getZ()){
				return true;
			}
		}
		
		return false;
	}
	
	public void setCenter(Block b){
		center = b;
	}
	
	public Block getCenter(){
		return center;
	}
	
	public Block getLeftCenter(){
		return getCenter().getLocation().add(-61, 0, 0).getBlock();
	}
	
	public Block getRightCenter(){
		return getCenter().getLocation().add(61, 0, 0).getBlock();
	}
	
	public Block getFrontCenter(){
		return getCenter().getLocation().add(0, 0, -61).getBlock();
	}
	
	public Block getBackCenter(){
		return getCenter().getLocation().add(61, 0, 61).getBlock();
	}
	
	public Location getTeamOneSpawn(){
		return getCenter().getLocation().add(10, 2, 10);
	}
	
	public Location getTeamTwoSpawn(){
		return getCenter().getLocation().add(10, 2, -10);
	}
	
	public Location getTeamThreeSpawn(){
		return getCenter().getLocation().add(-10, 2, 10);
	}
	
	public Location getTeamFourSpawn(){
		return getCenter().getLocation().add(-10, 2, -10);
	}
	
	public World getWorld(){
		return getCenter().getWorld();
	}

	public boolean hasPerson(Player p){
		for(Person pp: people){
			if(pp.getPlayer() == p){
				return true;
			}
		}
		
		return false;
	}
	
	public Person getPerson(Player p){
		for(Person pp: people){
			if(pp.getPlayer() == p){
				return pp;
			}
		}
		
		return null;
	}
	
	public int teamWithLeast(){
		int least = 1;

		if(getTeamSize(2) < getTeamSize(least))
			least = 2;
		if(getTeamSize(3) < getTeamSize(least))
			least = 3;
		if(getTeamSize(4) < getTeamSize(least))
			least = 4;
		
		return least;
	}
	
	public int getTeamSize(int id){
		int count = 0;
		
		for(Person p: getPeople()){
			if(p.getTeam() == id){
				count++;
			}
		}
		
		return count;
	}
	
	public void addPlayer(Player p){
		addPlayer(p, teamWithLeast());
	}
	
	public void addPlayer(Player p, int i){
		if(!hasPerson(p))
			people.add(new Person(p).setTeam(i));
	}
	
	public void removePlayer(Player p){
		if(getPlayers().contains(p))
			people.remove(getPerson(p));
		
		if(people.size() == 1){
			endGame();
		}
	}
	
	public GameCount getTimer(){
		return timer;
	}
	
	public boolean inProg(){
		if(timer == null)
			return false;
		
		return timer.inProg();
	}
	
	public int getMinX(){
		int i = 0;
		
		while(true){
			if(TheWalls.backupCenter.getLocation().add(-i, 0, 0).getBlock().getType() != Material.BEDROCK){
				i++;
				break;
			}
			i++;
		}
		
		return TheWalls.backupCenter.getX() - i;
	}
	
	public int getMaxX(){
		int i = 0;
		while(true){
			if(TheWalls.backupCenter.getLocation().add(i, 0, 0).getBlock().getType() != Material.BEDROCK){
				i++;
				break;
			}
			i++;
		}
		
		return TheWalls.backupCenter.getX() + i;
	}
	
	public int getMinZ(){
		int i = 0;
		while(true){
			if(TheWalls.backupCenter.getLocation().add(0, 0, -i).getBlock().getType() != Material.BEDROCK){
				i++;
				break;
			}
			i++;
		}
		
		return TheWalls.backupCenter.getZ() - i;
	}
	
	public int getMaxZ(){
		int i = 0;
		while(true){
			if(TheWalls.backupCenter.getLocation().add(0, 0, i).getBlock().getType() != Material.BEDROCK){
				i++;
				break;
			}
			i++;
		}
		
		return TheWalls.backupCenter.getZ() + i;
	}
	
	public void restoreMap(){
		int minX = getMinX();
		int minZ = getMinZ();
		
		int maxX = getMaxX();
		int maxZ = getMaxZ();
		
		System.out.println("'" + getWorld().getName() + "' being restored.");
		
		for(int x = minX; x < maxX; x++){
			for(int y = 1; y < 256; y++){
				for(int z = minZ; z < maxZ; z++){
					Block old = new Location(TheWalls.backupCenter.getWorld(), x, y, z).getBlock();
					Block newBlock = getWorld().getBlockAt(x, y, z);					
					
					if(old.getType() != newBlock.getType() || old.getData() != newBlock.getData()){
						newBlock.setType(old.getType());
						newBlock.setData(old.getData());
					}
					
					if(old.getType() == Material.CHEST){
						Chest chest = (Chest) old.getState();
						
						Chest newChest = (Chest) newBlock.getState();
						newChest.getInventory().setContents(chest.getInventory().getContents());
					}	
				}
			}
		}
		
		System.out.println("'" + getWorld().getName() + "' restoration finished.");

		addWall();
		
		getWorld().setTime(0);
	}
	
	public void setupRestore(){
		restoreMap();
		
		return;
		
		//new Thread(new WorldRestore(this)).start();
	}
	
	public void addWall(){
		int chunkSize = 61;
		
		int minY = getCenter().getY() + 1;
		int maxY = minY + sandHeight;
		
		int minX = getCenter().getX() - chunkSize + 1;
		int maxX = getCenter().getX() + chunkSize;
		
		for(int x = minX; x < maxX; x++){
			for(int y = minY; y < maxY; y++){
				if(getWorld().getBlockAt(x, y, getCenter().getZ()).getType() != Material.SAND)
					getWorld().getBlockAt(x, y, getCenter().getZ()).setType(Material.SAND);
			}
		}
		
		int minZ = getCenter().getZ() - chunkSize + 1;
		int maxZ = getCenter().getZ() + chunkSize;
		
		for(int z = minZ; z < maxZ; z++){
			for(int y = minY; y < maxY; y++){
				if(getWorld().getBlockAt(getCenter().getX(), y, z).getType() != Material.SAND);
					getWorld().getBlockAt(getCenter().getX(), y, z).setType(Material.SAND);
			}
		}
	}
	
	public void delWall(){
		int chunkSize = 61;
		
		int minY = getCenter().getY() + 1;
		int maxY = minY + sandHeight;
		
		int minX = getCenter().getX() - chunkSize + 1;
		int maxX = getCenter().getX() + chunkSize;
		
		for(int x = minX; x < maxX; x++){
			for(int y = minY; y < maxY; y++){
				getWorld().getBlockAt(x, y, getCenter().getZ()).setType(Material.AIR);
			}
		}
		
		int minZ = getCenter().getZ() - chunkSize + 1;
		int maxZ = getCenter().getZ() + chunkSize;
		
		for(int z = minZ; z < maxZ; z++){
			for(int y = minY; y < maxY; y++){
				getWorld().getBlockAt(getCenter().getX(), y, z).setType(Material.AIR);
			}
		}
	}
	
	public void startTimer(){
		timer = new GameCount(this);
		timer.id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(new TheWalls(), timer, 20L, 20L);
	}
	
	public void removeEntities(){
		ArrayList<Entity> toDel = new ArrayList<Entity>();
		for(Entity e: getWorld().getEntities()){
			if(e.getType() == EntityType.DROPPED_ITEM || e instanceof Monster || e instanceof Animals)
				toDel.add(e);
		}
		
		for(Entity e: toDel){
			e.remove();
		}
	}
	
	public void startGame(){
		restoreMap();
		removeEntities();
		people.clear();

		for(Player p: TheWalls.noPlay){
			if(TheWalls.getTeam(p) != null)
				TheWalls.removeFromQue(p);
		}

		ArrayList<Team> toAdd = teamsToAdd();
		ArrayList<Team> toDel = new ArrayList<Team>();
		int tN = 0;
		for(Team t: toAdd){
			if(t.team.size() > 1){
				tN++;
				
				if(tN == 5)
					tN = 1;
				
				for(Player p: t.team){
					addPlayer(p, tN);
				}
				
				toDel.add(t);
			}
		}
		
		for(Team t: toDel){
			toAdd.remove(t);
		}
		
		for(Team t: toAdd){
			for(Player p: t.team)
				addPlayer(p);
			
			toDel.add(t);
		}
		
		for(Team t: toDel){
			toAdd.remove(t);
			TheWalls.getQue().remove(t);
		}
		
		toAdd.clear();
		
		for(Player p: getPlayers()){
			TheWalls.tele(p, getSpawn(p));

			if(TheWalls.invites.containsKey(p))
				TheWalls.invites.remove(p);
			
			p.setHealth(20);
			p.setFoodLevel(20);
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			p.setFireTicks(0);
			p.setExp(0);
			p.setGameMode(GameMode.SURVIVAL);
			p.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "You are now in a game, if you aren't in the game area please tell an admin.");
		}
		
		startTimer();
	}
	
	public ArrayList<Team> teamsToAdd(){
		ArrayList<Team> toAdd = new ArrayList<Team>();
		
		int count = 0, sCount = 0;
		
		for(int i = 0; i < TheWalls.getQue().size(); i++){
			if(count + TheWalls.getQue().get(i).team.size() <= TheWalls.max){
				if(sCount < 4 || TheWalls.getQue().get(i).team.size() == 1)
					count += TheWalls.getQue().get(i).team.size();
				
				if(TheWalls.getQue().get(i).team.size() > 1){
					sCount++;
				}
				
				toAdd.add(TheWalls.getQue().get(i));
			}else{
				return toAdd;
			}
		}
		
		return toAdd;
	}
	
	public void startPvP(){
		delWall();
	}

	public boolean inDeathmatch(){
		if(timer == null)
			return false;
		
		if(timer.time < -(TheWalls.minTillDeathmatch) * 60){
			return true;
		}
		
		return false;
	}

	public boolean inPvP(){
		if(timer == null)
			return false;
		
		if(timer.time < 0){
			return true;
		}
		
		return false;
	}
	
	public void startDeathMatch(){
		for(Player p: getPlayers()){
			TheWalls.tele(p, getCenter().getLocation().add(0, 40, 0));
		
			p.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "You are now in deathmatch.");
			
			p.setHealth(20);
			p.setFoodLevel(20);
			p.setFireTicks(0);
			p.setNoDamageTicks(30);
			p.setExp(0);
			p.setGameMode(GameMode.SURVIVAL);
		}
	}
	
	public void endGame(){
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + people.get(0).getPlayer().getName() + ChatColor.GREEN + " has won in lobby # " + ChatColor.AQUA + (TheWalls.getGameID(this) + 1));
		Bukkit.getServer().getScheduler().cancelTask(timer.id);
		
		timer = null;
		
		Player winner = people.get(0).getPlayer();
		
		TheWalls.playerWin(winner);
		TheWalls.removeFromTeamSpeak(winner);

		winner.getInventory().clear();
		winner.getInventory().setArmorContents(null);
		
		List<Player> toTele = getWorld().getPlayers();
		for(Player p: toTele)
			TheWalls.tele(p, TheWalls.getWaiting());
		
		TheWalls.addPlayer(winner);

		people.clear();
		
		restoreMap();
		addWall();
		
		TheWalls.checkQue();
	}
	
	public int getMin(){
		int min = 15;
		
		if(timer != null){
			min = timer.time / 60;
		}
		
		return min;
	}
	
	public int getSec(){
		int sec = 0;
		
		if(timer != null){
			sec = timer.time % 60;
		}
		
		return sec;
	}
	
	public void genTeams(){
		int ii = 0;
		for(int i = 0; i < getPeople().size(); i++){
			Person p = getPeople().get(i);
			
			if(ii > 3)
				ii = 0;
			
			p.setTeam(ii + 1);
			
			ii++;
		}
	}
	
	public Location getSpawn(Player p){
		if(getPerson(p).getTeam() == 1)
			return getTeamOneSpawn();
		if(getPerson(p).getTeam() == 2)
			return getTeamTwoSpawn();
		if(getPerson(p).getTeam() == 3)
			return getTeamThreeSpawn();
		if(getPerson(p).getTeam() == 4)
			return getTeamFourSpawn();
		
		return null;
	}
}
