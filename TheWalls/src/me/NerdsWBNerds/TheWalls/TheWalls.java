package me.NerdsWBNerds.TheWalls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import me.NerdsWBNerds.TheWalls.Commands.ChatCMD;
import me.NerdsWBNerds.TheWalls.Commands.GeneralCMD;
import me.NerdsWBNerds.TheWalls.Commands.RecordsCMD;
import me.NerdsWBNerds.TheWalls.Commands.SetupCMD;
import me.NerdsWBNerds.TheWalls.Commands.TeamCMD;
import me.NerdsWBNerds.TheWalls.Commands.TeleportCMD;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TheWalls extends JavaPlugin{
	public Logger log;
	public Server server;

	private static ArrayList<Team> que = new ArrayList<Team>();
	public static HashMap<Player, Team> invites = new HashMap<Player, Team>();
	
	public static ArrayList<Player> noPlay = new ArrayList<Player>();
	private static ArrayList<Game> games = new ArrayList<Game>();

	private static ArrayList<Record> records = new ArrayList<Record>();
	
	public static ArrayList<LoginTP> tps = new ArrayList<LoginTP>();

	private static String Path = "plugins/TheWalls" + File.separator + "Worlds.dat";
	private static String PathTwo = "plugins/TheWalls" + File.separator + "Records.dat";
	
	private static HashMap<String, Boolean> teamSpeak = new HashMap<String, Boolean>();
	
	private static Location waiting = null;
	public static Block backupCenter = null;
	
	public static QueCount queCount = null;
	
	public static int min = 4;
	public static int max = 12;

	public static int gameLength = 15;
	public static int minTillDeathmatch = 10;
	
	public void onEnable(){
		getCommand("tw").setExecutor(new GeneralCMD());
		getCommand("map").setExecutor(new GeneralCMD());
		getCommand("lobby").setExecutor(new GeneralCMD());
		getCommand("join").setExecutor(new GeneralCMD());
		getCommand("quit").setExecutor(new GeneralCMD());

		getCommand("spec").setExecutor(new TeleportCMD());
		getCommand("wait").setExecutor(new TeleportCMD());

		getCommand("setwait").setExecutor(new SetupCMD());
		getCommand("setback").setExecutor(new SetupCMD());
		getCommand("add").setExecutor(new SetupCMD());
		getCommand("alldm").setExecutor(new SetupCMD());

		getCommand("record").setExecutor(new RecordsCMD());
		getCommand("top").setExecutor(new RecordsCMD());

		getCommand("invite").setExecutor(new TeamCMD());
		getCommand("accept").setExecutor(new TeamCMD());
		getCommand("quitteam").setExecutor(new TeamCMD());
		
		getCommand("g").setExecutor(new ChatCMD());
		getCommand("team").setExecutor(new ChatCMD());
		
		log = getServer().getLogger();
		
		getServer().getPluginManager().registerEvents(new TWListener(this), this);
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new ConstantCheck(this), 20L, 20L);
		
		load();
		shuffle();
	}
	
	public void onDisable(){
		this.getServer().getScheduler().cancelAllTasks();
		
		save();
	}
	
	public static Team getTeam(Player p){
		for(Team t: que){
			if(t.team.contains(p)){
				return t;
			}
		}
		
		return null;
	}
	
	public static void sendTeamChat(Player p, String s){
		if(inGame(p)){
			Game g = getGame(p);
			
			for(Player pp: g.getPlayers()){
				if(g.getPerson(pp).getTeam() == g.getPerson(p).getTeam())
					pp.sendMessage(s);
			}
			
			System.out.println(ChatColor.stripColor(s));
		}else{
			p.sendMessage(ChatColor.RED + "You must be in a game to use team speak.");
		}
	}
	
	public static void sendGlobalChat(Player p, String s){
		for(Player pp: Bukkit.getServer().getOnlinePlayers()){
			pp.sendMessage(s);
		}

		System.out.println(ChatColor.stripColor(s));
	}
	
	public static boolean inTeamSpeak(Player p){
		if(teamSpeak.containsKey(p.getName())){
			return teamSpeak.get(p.getName());
		}
		
		return false;
	}
	
	public static void addtoTeamSpeak(Player p){
		teamSpeak.put(p.getName(), true);
	}
	
	public static void removeFromTeamSpeak(Player p){
		if(teamSpeak.containsKey(p.getName())){
			teamSpeak.remove(p.getName());
		}
	}
	
	public static void tele(Player p, Location l){
		p.setSprinting(false);
		p.setSneaking(false);
		
		p.teleport(l);
	}
	
	public static Record getRecord(Player p, boolean create){
		for(Record r: records){
			if(r.getName().equalsIgnoreCase(p.getName())){
				return r;
			}
		}
		
		Record newR = new Record(p.getName());
		
		if(create)
			records.add(newR);
		
		return newR;
	}
	
	public static ArrayList<Record> getRecords(){
		return records;
	}
	
	public void playerDie(Player p){
		getRecord(p, true).die();
	}
	
	public static void playerWin(Player p){
		getRecord(p, true).winGame();
	}
	
	public void playerKill(Player p){
		getRecord(p, true).getKill();
	}
	
	public void shuffle(){
		for(Player p: getServer().getOnlinePlayers()){
			if(!p.isDead()){
				addPlayer(p, true);

				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
		}
	}
	
	public void save(){
		save_worlds();

		save_records();
		
		getConfig().set("WAITING", toString(getWaiting()));
		getConfig().set("BACKUP", toString(backupCenter.getLocation()));
		
		saveConfig();
	}
	
	public static void save_worlds(){
		if(games.size() == 0)
			return;
		
		ArrayList<String> formatted = new ArrayList<String>();
		
		for(Game g: games){
			formatted.add(toString(g.getCenter().getLocation()));
		}
		
		File file = new File(Path);
		new File("plugins/").mkdir();
	    if(!file.exists()){
	    	try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }

		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Path));
			oos.writeObject(formatted);
			oos.flush();
			oos.close();
			//Handle I/O exceptions
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void save_records(){
		if(games.size() == 0)
			return;
		
		ArrayList<String> formatted = new ArrayList<String>();
		
		for(Record r: records){
			formatted.add(r.toString());
		}
		
		File file = new File(PathTwo);
		new File("plugins/").mkdir();
	    if(!file.exists()){
	    	try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }

		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PathTwo));
			oos.writeObject(formatted);
			oos.flush();
			oos.close();
			//Handle I/O exceptions
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void load(){
		ArrayList<String> form = load_worlds();
		
		for(String s: form){
			games.add(toGame(s));
		}

		form = load_records();
		
		for(String s: form){
			records.add(Record.getRecord(s));
		}

		if(getConfig().contains("WAITING")){
			waiting = getCenter(toLocation(getConfig().getString("WAITING")));
		}
		if(getConfig().contains("BACKUP")){
			backupCenter = toLocation(getConfig().getString("BACKUP")).getBlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> load_worlds(){
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Path));
			Object result = ois.readObject();

			ois.close();
			if(result != null)
				return (ArrayList<String>)result;
			else
				return new ArrayList<String>();
		}catch(Exception e){
			return new ArrayList<String>();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> load_records(){
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PathTwo));
			Object result = ois.readObject();

			ois.close();
			if(result != null)
				return (ArrayList<String>)result;
			else
				return new ArrayList<String>();
		}catch(Exception e){
			return new ArrayList<String>();
		}
	}
	
	public static ArrayList<Team> getQue(){
		return que;
	}
	
	public static int getQueSize(){
		int size = 0;
		
		for(Team t: getQue()){
			size+=t.team.size();
		}
		
		return size;
	}
	
	public static ArrayList<Game> getGames(){
		return games;
	}
	
	public static void setWaiting(Location l){
		waiting = l;
	}
	
	public static Location getWaiting(){
		return waiting;
	}
	
	public static void addGame(Player p){
		Game toAdd = new Game();

		toAdd.setCenter(p.getLocation().add(0, -1, 0).getBlock());
			
		games.add(toAdd);
	}
	
	public static int getGameID(Game g){
		for(int i = 0; i < games.size(); i++){
			if(games.get(i) == g)
				return i;
		}
		
		return 0;
	}
	
	public static int getGameID(Player p){
		if(inGame(p)){
			return getGameID(getGame(p));
		}
		
		return 0;
	}
	
	public static ArrayList <Player> getActives(){
		ArrayList<Player> people = new ArrayList<Player>();
		
		for(Game g: games){
			for(Person p: g.getPeople()){
				people.add(p.getPlayer());
			}
		}
		
		return people;
	}
	
	public static Game getGame(Player p){
		for(Game g: games){
			if(g.getPlayers().contains(p)){
				return g;
			}
		}
		
		return null;
	}
	
	public static Game getGame(Location l){
		for(Game g: getGames()){
			if(g.inBorder(l)){
				return g;
			}
		}
		
		return null;
	}
	
	public static boolean inGame(Player p){
		if(getActives().contains(p))
			return true;
					
		return false;
	}
	
	public static void addPlayer(Player p, boolean tp){
		if(noPlay.contains(p))
			return;
		
		que.add(new Team(p));
		
		if (tp){
			tele(p, getWaiting());
		}
		
		checkQue();
	}
	
	public static void addPlayer(Player p){
		if(noPlay.contains(p))
			return;
		
		addPlayer(p, false);
	}
	
	public static void removePlayer(Player p){
		if(inGame(p)){
			Game removeFrom = getGame(p);
			
			removeFrom.removePlayer(p);
			
			removeFromTeamSpeak(p);
		}
		
		removeFromQue(p);
	}
	
	public static void removeFromQue(Player p){
		if(getTeam(p) != null){
			Team t = getTeam(p);
			t.removePlayer(p);
		}
	}
		
	public static boolean checkQue(){
		for(Player p: noPlay){
			for(Team t: que){
				if(t.team.contains(p))
					t.team.remove(p);
			}
		}
				
		if(queCount == null && que.size() >= min){
			startQueCount();
		}
		
		return false;
	}
	
	public static void startQueCount(){
		stopQueCount();
		
		queCount = new QueCount();
		queCount.id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(new TheWalls(), queCount, 20L, 20L);
	}
	
	public static void startQueCount(int i){
		stopQueCount();
		
		queCount = new QueCount(i);
		queCount.id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(new TheWalls(), queCount, 20L, 20L);
	}
	
	public static void stopQueCount(){
		if(queCount != null){
			Bukkit.getServer().getScheduler().cancelTask(queCount.id);
			queCount = null;
		}
	}
	
	public static Game getNextGame(){
		for(int i = 0; i < games.size(); i++){
			Game game = games.get(i);
			
			if(!game.inProg() && game.getPeople().isEmpty()){
				return game;
			}
		}
		
		return null;
	}
	
	public static String toString(Location l){
		String loc = "";
		

		loc += l.getWorld().getName() + ",";
		loc += l.getBlockX()+ ",";
		loc += l.getBlockY() + ",";
		loc += l.getBlockZ();
		
		return loc;
	}

	public static Location getCenter(Location l){
		return getBlockLoc(l).add(0.5, 0.5, 0.5);
	}
	
	public static Location getBlockLoc(Location l){
		return new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	public static Location toLocation(String n){
		String[] split = n.split(",");
		
		return getCenter( new Location(Bukkit.getServer().getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])) );
	}
	
	public Game toGame(String s){
		Game ret = new Game();
		ret.setCenter(toLocation(s).getBlock());
		
		return ret;
	}

	public static void hidePlayer(Player p){
		for(Player pp: Bukkit.getServer().getOnlinePlayers()){
			if(p!=pp && pp.canSee(p))
				pp.hidePlayer(p);
		}
	}
	
	public static void showPlayer(Player p){
		for(Player pp: Bukkit.getServer().getOnlinePlayers()){
			if(p!=pp && !pp.canSee(p))
				pp.showPlayer(p);
		}
	}
}
