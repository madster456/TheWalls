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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TheWalls extends JavaPlugin{
	public Logger log;
	public Server server;

	private static ArrayList<Player> que = new ArrayList<Player>();
	static ArrayList<Player> noPlay = new ArrayList<Player>();
	private static ArrayList<Game> games = new ArrayList<Game>();

	private static ArrayList<Record> records = new ArrayList<Record>();
	
	public static ArrayList<LoginTP> tps = new ArrayList<LoginTP>();

	private static String Path = "plugins/TheWalls" + File.separator + "Worlds.dat";
	private static String PathTwo = "plugins/TheWalls" + File.separator + "Records.dat";
	
	private static HashMap<String, Boolean> teamSpeak = new HashMap<String, Boolean>();
	
	private static Location waiting = null;
	public static Block backupCenter = null;
	
	public QueCount queCount = null;
	
	public static int min = 4;
	public static int max = 12;
	
	public static boolean debug = false;
	
	public void onEnable(){
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
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]){
		if(sender instanceof Player){
			Player player = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("tw") && args.length > 0 && args[0].equalsIgnoreCase("help")){
				
				ArrayList<String> messages = new ArrayList<String>();
				
				messages.add(ChatColor.GREEN + "You will " + ChatColor.RED + "AUTOMATICALLY" + ChatColor.GREEN + " be put into a game.");
				messages.add(" ");
				messages.add(ChatColor.AQUA + "/lobby " + ChatColor.GREEN + " - Get general info about your lobby.");
				messages.add(ChatColor.AQUA + "/map " + ChatColor.GREEN + " - Get general info about the map.");
				messages.add(ChatColor.AQUA + "/donate " + ChatColor.GREEN + " - Get info on how to donate.");
				messages.add(ChatColor.AQUA + "/spec <player> " + ChatColor.GREEN + " - Spectate player.");
				messages.add(ChatColor.AQUA + "/record <player> " + ChatColor.GREEN + " - Get a players record.");
				messages.add(ChatColor.AQUA + "/team /g " + ChatColor.GREEN + " - Switch between team and global chat.");
				
				player.sendMessage(ChatColor.GOLD + "** THE WALLS HELP **");
				
				for(String m: messages){
					player.sendMessage(m);
				}
				
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("map")){
				String mapLink = "http://tinyurl.com/thewallsmap";

				
				player.sendMessage(ChatColor.GOLD + "** THE WALLS MAP INFORMATION **");
				player.sendMessage(ChatColor.GREEN + "This map is a slightly modified version of " + ChatColor.AQUA + "The Walls" + ChatColor.GREEN + " map by " + ChatColor.AQUA + "Rezz.");
				player.sendMessage(ChatColor.GREEN + "Map: " + ChatColor.AQUA + mapLink);
				
				return true;
			}

			if(cmd.getName().equalsIgnoreCase("spec")){
				if(args.length != 1)
					return false;
				
				if(!inGame(player)){
					Player target = getServer().getPlayer(args[0]);
					
					if(target == null || !target.isOnline()){
						player.sendMessage(ChatColor.RED + "Player not found.");
						return true;
					}
					
					if(inGame(target)){
						tele(player, target.getLocation());
						player.sendMessage(ChatColor.GREEN + "Now spectating " + ChatColor.AQUA + target.getName());
					}else{
						player.sendMessage(ChatColor.RED + "You can only spectate living players.");
					}
				}
			}


			if(cmd.getName().equalsIgnoreCase("info")){
				if(!inGame(player)){
					player.sendMessage(ChatColor.GOLD + "** LOBBY INFORMATION **");
					
				}else{
					player.sendMessage(ChatColor.GOLD + "** LOBBY INFORMATION (YOUR LOBBY # IS " + (getGameID(player) + 1)  + ") **");
				}

				if(queCount != null)
					player.sendMessage(ChatColor.GREEN + "Waiting Que: " + ChatColor.AQUA + que.size() + " people. " + queCount.time + " second(s).");
				else
					player.sendMessage(ChatColor.GREEN + "Waiting Que: " + ChatColor.AQUA + que.size() + " people.");
				
				for(int i = 0; i < games.size(); i++){
					player.sendMessage(ChatColor.GREEN + "Lobby #" + (i + 1) + ": " + ChatColor.AQUA + games.get(i).getPeople().size() + " / " + max + " people. " + games.get(i).getMin() + " min(s) " + games.get(i).getSec() + " sec(s)");
				}
				
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("record")){
				Player target = player;
				
				if(args.length == 1){
					target = null;
					target = getServer().getPlayer(args[0]);
				}
				
				if(target == null || !target.isOnline()){
					player.sendMessage(ChatColor.RED + "Error: Player not found.");
					return true;
				}

				player.sendMessage(ChatColor.GOLD + "** " + target.getName().toUpperCase() + "'S RECORD **");
				player.sendMessage(ChatColor.GREEN + "Wins: " + ChatColor.AQUA + getRecord(target, false).getWins());
				player.sendMessage(ChatColor.GREEN + "Kills: " + ChatColor.AQUA + getRecord(target, false).getKills());
				player.sendMessage(ChatColor.GREEN + "Deaths: " + ChatColor.AQUA + getRecord(target, false).getDeaths());
			
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("top")){
				Record mW = null, mK = null, mD = null;
				
				for(Record r: records){
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

			if(cmd.getName().equalsIgnoreCase("g")){
				if(args.length == 0){
					removeFromTeamSpeak(player);
					
					player.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "Now in global chat. Use /team to switch to team chat.");
				}else{
					ChatColor clr = ChatColor.WHITE;
					
					if(player.isOp())
						clr = ChatColor.RED;
					
					String xtra = "";					
					
					if(!inGame(player))
						xtra = TWListener.spec + "[SPEC]";
					
					String msg = xtra + ChatColor.GRAY + "<" + clr + player.getName() + ChatColor.GRAY + "> " + ChatColor.WHITE;
					
					for(String s: args){
						msg += s + " ";
					}
					
					sendGlobalChat(player, msg);
				}
				
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("team")){
				if(args.length == 0){
					if(inGame(player)){
						addtoTeamSpeak(player);
						
						player.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "Now in team chat. Use /g to switch to global chat.");
					}else{
						player.sendMessage(ChatColor.RED + "You must be in a game to switch to team chat.");
					}
				}else{
					ChatColor clr = ChatColor.WHITE;
					
					if(player.isOp())
						clr = ChatColor.RED;
					
					String msg = TWListener.team + "[TEAM]" + ChatColor.GRAY + "<" + clr + player.getName() + ChatColor.GRAY + "> " + ChatColor.WHITE;
					
					for(String s: args){
						msg += s + " ";
					}
					
					sendTeamChat(player, msg);
				}
				
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("donate")){
				player.sendMessage(ChatColor.GOLD + "** DONATION INFO **");
				player.sendMessage(ChatColor.GREEN + "Go to " + ChatColor.AQUA + "www.nerdswbnerds.com/donate.php" + ChatColor.GREEN + " to donate! Donating allows me to upgrade the server and get more slots, and less lag.");
				
				return true;
			}
			

			///////////////////////////////////////////////////////////////////
			////////////////////////////REQUIRE OP/////////////////////////////
			///////////////////////////////////////////////////////////////////
			
			if(!player.isOp())
				return false;
			
			if(cmd.getName().equalsIgnoreCase("tw")){
				if(args[0].equalsIgnoreCase("quit")){
					if(inGame(player)){
						removePlayer(player);
					}
					
					if(!noPlay.contains(player))
						noPlay.add(player);
					
					player.sendMessage(ChatColor.GREEN + "You have left the waiting que.");
					return true;
				}
				
				if(args[0].equalsIgnoreCase("join")){
					if(noPlay.contains(player))
						noPlay.remove(player);
					
					addPlayer(player);

					player.sendMessage(ChatColor.GREEN + "You have joined the waiting que.");
					return true;
				}
			}

			if(cmd.getName().equalsIgnoreCase("alldm")){
				for(Game g: getGames()){
					if(g.getPlayers().size() != 0){
						g.getTimer().time = (60 * -10) + 15;
					}
					
				}
				
				player.sendMessage(ChatColor.GREEN + "All worlds forced into deathmatch.");
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("setwait")){
				setWaiting(player.getLocation());
				player.sendMessage(ChatColor.GREEN + "Waiting location set at your position.");
				return true;
			}

			if(cmd.getName().equalsIgnoreCase("add")){
				addGame(player);
				player.sendMessage(ChatColor.GREEN + "Game # " + getGames().size() + " added.");
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("setback")){
				backupCenter = player.getLocation().add(0, -1, 0).getBlock();
				player.sendMessage(ChatColor.GREEN + "Back area set at your position.");
				return true;
			}
			
			/*if(cmd.getName().equalsIgnoreCase("settime")){
				try{
					if(getLobby(player.getWorld()) != null){
						if(getLobby(player.getWorld()).timer.time > 0){
							getLobby(player.getWorld()).timer.time = Integer.parseInt(args[0]);
							player.sendMessage(ChatColor.GREEN + "Game time now set to " + args[0] + " second(s).");
						}
					}
				}catch(Exception e){
					player.sendMessage(ChatColor.RED + "Error tring to change game time.");
				}
				return true;
			}*/
			
			return true;
			
		}else{

		}
		
		return false;
	}
	
	public void sendTeamChat(Player p, String s){
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
	
	public void sendGlobalChat(Player p, String s){
		for(Player pp: getServer().getOnlinePlayers()){
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
	
	public Record getRecord(Player p, boolean create){
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
	
	public void playerDie(Player p){
		getRecord(p, true).die();
	}
	
	public void playerWin(Player p){
		getRecord(p, true).winGame();
	}
	
	public void playerKill(Player p){
		getRecord(p, true).getKill();
	}
	
	public void shuffle(){
		for(Player p: getServer().getOnlinePlayers()){
			if(!p.isDead())
				addPlayer(p, true);
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
			games.add(toGame(this, s));
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
	
	public static ArrayList<Player> getQue(){
		ArrayList<Player> toRem = new ArrayList<Player>();
		
		for(Player p: que){
			if(p.isDead() || p.getHealth() <= 0){
				toRem.add(p);				
			}
		}
		
		for(Player p: toRem){
			que.remove(p);
		}
		
		return que;
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
	
	public void addGame(Player p){
		Game toAdd = new Game(this);

		toAdd.setCenter(p.getLocation().add(0, -1, 0).getBlock());
			
		games.add(toAdd);
	}
	
	public int getGameID(Game g){
		for(int i = 0; i < games.size(); i++){
			if(games.get(i) == g)
				return i;
		}
		
		return 0;
	}
	
	public int getGameID(Player p){
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
	
	public Game getGame(Player p){
		for(Game g: games){
			if(g.getPlayers().contains(p)){
				return g;
			}
		}
		
		return null;
	}
	
	public Game getGame(Location l){
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
	
	public void addPlayer(Player p, boolean tp){
		if(noPlay.contains(p))
			return;
		
		que.add(p);
		
		if (tp){
			tele(p, getWaiting());
		}
		
		checkQue();
	}
	
	public void addPlayer(Player p){
		if(noPlay.contains(p))
			return;
		
		addPlayer(p, false);
	}
	
	public void removePlayer(Player p){
		if(inGame(p)){
			Game removeFrom = getGame(p);
			
			removeFrom.removePlayer(p);
			
			removeFromTeamSpeak(p);
		}
		
		if(que.contains(p))
			que.remove(p);
	}
	
	public void removeFromQue(Player p){
		if(getQue().contains(p))
			getQue().remove(p);
	}
		
	public boolean checkQue(){
		for(int i = 0; i < que.size(); i++){
			if(que.get(i).isDead())
				que.remove(i);
		}
		
		for(Player p: noPlay){
			if(que.contains(p))
				que.remove(p);
		}
				
		if(que.size() == max){
			Game next = getNextGame();
			
			if(next != null){
				next.startGame();
				stopQueCount();
				checkQue();
				return true;
			}
		}

		if(queCount == null && que.size() >= min && que.size() < max){
			startQueCount();
		}
		
		return false;
	}
	
	public void startQueCount(){
		stopQueCount();
		
		queCount = new QueCount(this);
		queCount.id = getServer().getScheduler().scheduleSyncRepeatingTask(this, queCount, 20L, 20L);
	}
	
	public void stopQueCount(){
		if(queCount != null){
			getServer().getScheduler().cancelTask(queCount.id);
			queCount = null;
		}
	}
	
	public Game getNextGame(){
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
	
	public Game toGame(TheWalls p, String s){
		Game ret = new Game(p);
		ret.setCenter(toLocation(s).getBlock());
		
		return ret;
	}

	public static void hidePlayer(Player p){
		for(Player pp: Bukkit.getServer().getOnlinePlayers()){
			if(p!=pp)
				pp.hidePlayer(p);
		}
	}
	
	public static void showPlayer(Player p){
		for(Player pp: Bukkit.getServer().getOnlinePlayers()){
			if(p!=pp)
				pp.showPlayer(p);
		}
	}
}
