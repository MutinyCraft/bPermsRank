package com.mutinycraft.jigsaw.bPermsRank;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class bPermsRankMessages {
	
	bPermsRankCommandExecutor cmdEx;

	
	public void displayHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "--- bPermsRank Help ---");
		sender.sendMessage(ChatColor.YELLOW + "/rank player group");
		sender.sendMessage(ChatColor.YELLOW + "/rank player group world");
		sender.sendMessage(ChatColor.YELLOW + "/rankoffline player group");
		sender.sendMessage(ChatColor.YELLOW + "/rankoffline player group world");
		sender.sendMessage(ChatColor.YELLOW + "/bpermsrank reload");
	}
	
	public void broadcastOffline(int ident, CommandSender sender) {
		if (ident == 0) {
			sender.sendMessage(ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "You have changed the rank of offline player  "
					+ cmdEx.getData(0) + " to " + cmdEx.getData(1));
		} else
			sender.sendMessage(ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "You have changed the rank of offline player "
					+ cmdEx.getData(0) + " to " + cmdEx.getData(0) + " in the world: "
					+ cmdEx.getWorldName());
	}

	public void broadcast(CommandSender sender, Player playerToRank,
			String[] args, int ident) {
		if ((ident == 0) && (cmdEx.getPlugin().hasBroadcast())) {
			cmdEx.getPlugin().getServer().broadcastMessage(
					getMessage("broadcastMessage"));
		} else if (cmdEx.getPlugin().hasBroadcast()) {
			cmdEx.getPlugin().getServer().broadcastMessage(
					getMessageWorld("broadcastMessage"));
		}

		if ((ident == 0) && (cmdEx.getPlugin().hasNotifySender())) {
			sender.sendMessage(getMessage("senderMessage"));
		} else if (cmdEx.getPlugin().hasNotifySender()) {
			sender.sendMessage(getMessageWorld("senderMessage"));
		}

		if ((ident == 0) && (cmdEx.getPlugin().hasNotifyRanked())) {
			playerToRank.sendMessage(getMessage("rankedMessage"));
		} else if (cmdEx.getPlugin().hasNotifyRanked())
			playerToRank.sendMessage(getMessageWorld("rankedMessage"));
	}

	public String getCustomMessage(String msgName) {
		String msg = getMessage(msgName);
		return msg;
	}

	private String getMessage(String msgName) {
		if (msgName.equalsIgnoreCase("broadcastMessage")) {
			return ChatColor.YELLOW + cmdEx.getPlayerToRankName() + ChatColor.YELLOW
					+ " is now a " + cmdEx.getGroup();
		}
		if (msgName.equalsIgnoreCase("senderMessage")) {
			return ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "You have changed to rank of " + cmdEx.getPlayerToRankName()
					+ ChatColor.RED + " to " + cmdEx.getGroup();
		}
		if (msgName.equalsIgnoreCase("rankedMessage")) {
			return ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "Your rank has been changed to " + cmdEx.getGroup() + " by "
					+ cmdEx.getSenderName();
		}

		return "";
	}

	private String getMessageWorld(String msgName) {
		if (msgName.equalsIgnoreCase("senderMessage")) {
			return ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "You have changed to rank of " + cmdEx.getPlayerToRankName()
					+ ChatColor.RED + " to " + cmdEx.getGroup() + " in the world: "
					+ cmdEx.getWorldName();
		}
		if (msgName.equalsIgnoreCase("rankedMessage")) {
			return ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "Your rank has been changed to " + cmdEx.getGroup() + " by "
					+ cmdEx.getSenderName() + ChatColor.RED + " in the world: "
					+ cmdEx.getWorldName();
		}

		return "";
	}
}
