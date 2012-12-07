package com.mutinycraft.jigsaw.bPermsRank;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class bPermsRankCommandExecutor implements CommandExecutor{
	
	private bPermsRank plugin;
	private Player playerSender;
	private Player playerToRank;
	private CommandSender cmdSender;
	private String[] data;
	
	public bPermsRankCommandExecutor(bPermsRank pl){
		this.plugin = pl;
		this.playerSender = null;
		this.playerToRank = null;
		this.data = null;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		playerSender = null;
		cmdSender = sender;
		data = args;
		if((sender instanceof Player)){
			playerSender = (Player) sender;	
			try{
				if(!playerSender.hasPermission("bpermsrank.rank")){
					throw new CommandException("You may not use this command!");
				}
			}
			catch(CommandException e){
				cmdSender.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
		}
		if(cmd.getName().equalsIgnoreCase("bpermsrank")){
			return commandbPermsRank();
		}
		if(cmd.getName().equalsIgnoreCase("rank")){
			return commandRank();
		}
		if(cmd.getName().equalsIgnoreCase("rankoffline")){
			return commandRankOffline();
		}
		if(cmd.getName().equalsIgnoreCase("rankinfo")){
			return commandRankInfo();
		}
		return false;
	}

	private boolean commandbPermsRank(){
		if(data.length > 0 && (playerSender == null || playerSender.hasPermission("bpermsrank.reload"))){
			plugin.reloadConfig();
			cmdSender.sendMessage(ChatColor.RED + "bPermsRank config.yml reloaded!");
		}
		else{
			displayHelp();
		}
		return true;
	}

	private boolean commandRank() {
		if((data.length != 2) && (data.length != 3)){
			cmdSender.sendMessage(ChatColor.RED + "Usage: /rank name rank [world]");
			return true;
		}
		try{
			if(playerSender != null && !playerSender.hasPermission("bpermsrank.rank")){
				throw new CommandException("You may not use this command!");
			}
			isValidGroup();
			playerToRank = plugin.getServer().getPlayerExact(data[0]);
			isValidPlayer();
			if(playerToRank.hasPermission("bpermsrank.norank")){
				throw new CommandException("You may not change the rank of this player!");
			}
			if(playerSender != null){
				hasPermissionForGroup();
			}
			if(data.length == 2){
				rankPlayerAllWorlds();
				broadcast(true, true);
			}
			else{
				isValidWorld(data[2]);
				rankPlayerInSingleWorld();
				broadcast(false, true);
			}
		} 
		catch(CommandException e){
			cmdSender.sendMessage(ChatColor.RED + e.getMessage());
		}
		return true;
	}
	
	private boolean commandRankOffline() {
		if((data.length != 2) && (data.length != 3)){
			cmdSender.sendMessage(ChatColor.RED + "Usage: /rankoffline name rank [world]");
			return true;
		}
		try{
			if(playerSender != null && !playerSender.hasPermission("bpermsrank.rankoffline")){
				throw new CommandException("You may not rank offline players!");
			}
			isValidGroup();
			if(playerSender != null){
				hasPermissionForGroup();
			}
			if(data.length == 2){
				rankPlayerAllWorlds();
				broadcast(true, false);
			}
			else{
				isValidWorld(data[2]);
				rankPlayerInSingleWorld();
				broadcast(false, false);
			}
		} 
		catch(CommandException e){
			cmdSender.sendMessage(ChatColor.RED + e.getMessage());
		}
		return true;
	}
	
	private boolean commandRankInfo() {
		try{
			if(playerSender != null && !playerSender.hasPermission("bpermsrank.rankinfo")){
					throw new CommandException("You may not use this command!");
			}
			if(data.length != 2){
				throw new CommandException("Usage: /rankinfo name world");
			}
			isValidWorld(data[1]);
			String groups = getPlayerGroups(data[1], data[0]);
			cmdSender.sendMessage(ChatColor.AQUA + "NOTICE: " + ChatColor.RED + data[0] + ChatColor.RED +  " has the group(s): [ " + groups + "] in world: " + data[1]);
		} 
		catch(CommandException e){
			cmdSender.sendMessage(ChatColor.RED + e.getMessage());
		}
		return true;
	}
	
	private void rankPlayerAllWorlds() {
		String playerToRankName = data[0];
		for(int i = 0; i < plugin.getWorlds().size(); i++){
			String worldName = (String) plugin.getWorlds().get(i);
			ApiLayer.setGroup(worldName, CalculableType.USER, playerToRankName, data[1]);
		}
	}
	
	private void rankPlayerInSingleWorld(){
		String playerToRankName = data[0];
		ApiLayer.setGroup(data[2], CalculableType.USER, playerToRankName, data[1]);
	}

	public void isValidPlayer() throws CommandException{
		if((playerToRank == null) || (!playerToRank.isOnline())){
			throw new CommandException("That player is not online! Use: /rankoffline if you are sure you want to rank this player.");
		}
	}
	
	public void isValidGroup() throws CommandException{
		if (plugin.getGroups().indexOf(data[1].toLowerCase()) == -1){
			throw new CommandException("That group/rank does not exist!");
		}
	}
	
	public void isValidWorld(String worldName) throws CommandException {
	    if(this.plugin.getServer().getWorld(worldName) == null){
	    	throw new CommandException("That is not a valid world!");
	    }
	  }
	
	public void hasPermissionForGroup() throws CommandException{
		if (!playerSender.hasPermission("bpermsrank.rank." + data[1])){
			throw new CommandException("You do not have permission to rank to " + data[1]);
		}
	  }
	
	private String getSenderName(){
		if(playerSender != null){
			return playerSender.getDisplayName();
		}
		else{
			return "Console";
		}
	}
	
	private String getPlayerGroups(String worldName, String playerName) throws CommandException {
	    
		String[] groups = ApiLayer.getGroups(worldName, CalculableType.USER, playerName);
	    StringBuffer buffer = new StringBuffer();
	    
	    
	    for (int i = 0; i < groups.length; i++) {
	    	buffer.append(groups[i] + " ");
	    }
	    
	    String results = buffer.toString();

	    return results;
	  }
	
	private void displayHelp() {
		cmdSender.sendMessage(ChatColor.RED + "--- bPermsRank Help ---");
		cmdSender.sendMessage(ChatColor.YELLOW + "/rank player group [world]");
		cmdSender.sendMessage(ChatColor.YELLOW + "/rankoffline player group [world]");
		cmdSender.sendMessage(ChatColor.YELLOW + "/bpermsrank reload");
	}
	
	private void broadcast(boolean isAllWorlds, boolean isOnline){
		if(plugin.isBroadcast() && isAllWorlds){
			plugin.getServer().broadcastMessage(getMessage("broadcast"));
		}
		if(plugin.isNotifyRanked() && isOnline && isAllWorlds){
			plugin.getServer().getPlayer(data[0]).sendMessage(getMessage("rankedmessage"));
		}
		if(plugin.isNotifySender() && isAllWorlds){
			cmdSender.sendMessage(getMessage("sendermessage"));
		}
		else if(plugin.isNotifySender()){
			cmdSender.sendMessage(ChatColor.AQUA + "NOTICE: " + ChatColor.RED + "You have changed to rank of " + data[0] + ChatColor.RED + " to " + data[1]
							  	   + ChatColor.RED + " in the world: " + data[2]);
		}
	}
	
	private String getMessage(String msgName){
		if(msgName.equalsIgnoreCase("broadcast")){
			StringBuffer msg = new StringBuffer(plugin.getBroadcastMessage());
			msg = replaceTags(msg);
			return ChatColor.translateAlternateColorCodes('&', msg.toString());
		}
		else if(msgName.equalsIgnoreCase("rankedmessage")){
			StringBuffer msg = new StringBuffer(plugin.getRankedMessage());
			msg = replaceTags(msg);
			return ChatColor.translateAlternateColorCodes('&', msg.toString());
		}
		else if(msgName.equalsIgnoreCase("sendermessage")){
			StringBuffer msg = new StringBuffer(plugin.getSenderMessage());
			msg = replaceTags(msg);
			return ChatColor.translateAlternateColorCodes('&', msg.toString() );
		}
		return null;
	}

	private StringBuffer replaceTags(StringBuffer msg) {
		String groupTag = "{GROUP}";
		String rankedTag = "{RANKED}";
		String senderTag = "{SENDER}";
	    String group =  data[1];
	    String ranked = data[0];
	    String sender = getSenderName();
	    
	    int position = msg.lastIndexOf(groupTag);
	    
	    if(position != -1){
	    	msg.replace(position, position + groupTag.length(), group);
	    }
	    
	    position = msg.lastIndexOf(rankedTag);
		
	    if(position != -1){
		    msg.replace(position, position + rankedTag.length(), ranked);
	    }
	    
		position = msg.lastIndexOf(senderTag);
	    
		if(position != -1){
		    msg.replace(position, position + senderTag.length(), sender);
	    }
	    
		return msg;
	}
	
}
