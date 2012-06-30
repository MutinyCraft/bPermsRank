package com.mutinycraft.jigsaw.bPermsRank;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class bPermsRankCommandExecutor implements CommandExecutor {
	private bPermsRank plugin;
	private String[] data;
	private Player playerToRank;
	private Player player;

	public bPermsRankCommandExecutor(bPermsRank plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		this.player = null;
		this.data = args;
		if ((sender instanceof Player)) {
			this.player = ((Player) sender);
		}
		if (cmd.getName().equalsIgnoreCase("bpermsrank")) {
			return commandbPermsRank(sender);
		}
		if (cmd.getName().equalsIgnoreCase("rank")) {
			return commandRank(sender);
		}
		if (cmd.getName().equalsIgnoreCase("rankoffline")) {
			return commandRankOffline(sender);
		}
		if (cmd.getName().equalsIgnoreCase("rankinfo")) {
			return commandRankInfo(sender);
		}
		return false;
	}

	private boolean commandbPermsRank(CommandSender sender) {
		if ((this.data.length == 1)
				&& (this.data[0].equalsIgnoreCase("reload"))) {
			if (this.player == null) {
				this.plugin.reloadConfig();
				this.plugin.log.info("bPermsRank config.yml reloaded");
			} else if (this.player.hasPermission("bpermsrank.reload")) {
				this.plugin.reloadConfig();
				this.player.sendMessage(ChatColor.RED
						+ "bPermsRank config.yml reloaded");
			} else {
				displayHelp(sender);
			}
			return true;
		}

		displayHelp(sender);
		return true;
	}

	private boolean commandRank(CommandSender sender) {
		if ((this.data.length != 2) && (this.data.length != 3)) {
			sender.sendMessage(ChatColor.RED + "Usage: /rank name rank [world]");
			return true;
		}

		if (this.player == null)
			try {
				isValidGroup(this.data[1].toLowerCase());
				this.playerToRank = this.plugin.getServer().getPlayerExact(
						this.data[0]);
				isValidPlayer(this.playerToRank);
				if (this.data.length == 2) {
					rankPlayerAllWorlds(this.playerToRank.getName(),
							this.data[1].toLowerCase());
					broadcast(sender, this.playerToRank, this.data, 0);
				} else {
					isValidWorld(this.data[2].toLowerCase());
					rankPlayerInSingleWorld(this.playerToRank.getName(),
							this.data[1].toLowerCase(),
							this.data[2].toLowerCase());
					broadcast(sender, this.playerToRank, this.data, 1);
				}
			} catch (CommandException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
		else {
			try {
				hasPermissionForPlugin(this.player);
				isValidGroup(this.data[1].toLowerCase());
				this.playerToRank = this.plugin.getServer().getPlayerExact(
						this.data[0]);
				isValidPlayer(this.playerToRank);
				hasPermissionForGroup(this.player, this.data[1].toLowerCase());
				if (this.data.length == 2) {
					rankPlayerAllWorlds(this.playerToRank.getName(),
							this.data[1].toLowerCase());
					broadcast(this.player, this.playerToRank, this.data, 0);
				} else {
					isValidWorld(this.data[2].toLowerCase());
					rankPlayerInSingleWorld(this.playerToRank.getName(),
							this.data[1].toLowerCase(),
							this.data[2].toLowerCase());
					broadcast(this.player, this.playerToRank, this.data, 1);
				}
			} catch (CommandException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
		}
		return true;
	}

	private boolean commandRankOffline(CommandSender sender) {
		if ((this.data.length != 2) && (this.data.length != 3)) {
			sender.sendMessage(ChatColor.RED
					+ "Usage: /rankoffline name rank [world]");
			return true;
		}
		if (this.player == null)
			try {
				isValidGroup(this.data[1].toLowerCase());
				if (this.data.length == 2) {
					rankPlayerAllWorlds(this.data[0], this.data[1]);
					broadcastOffline(0, sender);
				}
				else{
					isValidWorld(this.data[2].toLowerCase());
					rankPlayerInSingleWorld(this.data[0],
							this.data[1].toLowerCase(), this.data[2].toLowerCase());
					broadcastOffline(1, sender);
				}
			} catch (CommandException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
		else {
			try {
				hasPermissionForPlugin(this.player);
				if (!this.player.hasPermission("bpermsrank.rankoffline")) {
					throw new CommandException(
							"You may not rank offline players!");
				}
				isValidGroup(this.data[1].toLowerCase());
				hasPermissionForGroup(this.player, this.data[1].toLowerCase());
				if (this.data.length == 2) {
					rankPlayerAllWorlds(this.data[0], this.data[1]);
					broadcastOffline(0, sender);
				} else {
					isValidWorld(this.data[2].toLowerCase());
					rankPlayerInSingleWorld(this.data[0],
							this.data[1].toLowerCase(),
							this.data[2].toLowerCase());
					broadcastOffline(1, sender);
				}
			} catch (CommandException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
		}
		return true;
	}

	private boolean commandRankInfo(CommandSender sender) {
		try {
			if ((this.player != null)
					&& (!this.player.hasPermission("bpermsrank.rankinfo"))) {
				throw new CommandException("You may not use this command!");
			}

			if (this.data.length != 2) {
				throw new CommandException("Usage: /rankinfo name world");
			}
			String playerName = this.data[0];
			String worldName = this.data[1];
			isValidWorld(worldName);
			String groups = getPlayerGroups(worldName, playerName);
			sender.sendMessage(ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ playerName + ChatColor.RED + " has the group(s): [ "
					+ groups + "] in world: " + worldName);
		} catch (CommandException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}
		return true;
	}

	private void broadcastOffline(int ident, CommandSender sender) {
		if (ident == 0) {
			sender.sendMessage(ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "You have changed the rank of offline player  "
					+ this.data[0] + " to " + this.data[1]);
		} else
			sender.sendMessage(ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "You have changed the rank of offline player "
					+ this.data[0] + " to " + this.data[1] + " in the world: "
					+ getWorldName());
	}

	private void broadcast(CommandSender sender, Player playerToRank,
			String[] args, int ident) {
		if ((ident == 0) && (this.plugin.hasBroadcast())) {
			this.plugin.getServer().broadcastMessage(
					getMessage("broadcastMessage"));
		} else if (this.plugin.hasBroadcast()) {
			this.plugin.getServer().broadcastMessage(
					getMessageWorld("broadcastMessage"));
		}

		if ((ident == 0) && (this.plugin.hasNotifySender())) {
			sender.sendMessage(getMessage("senderMessage"));
		} else if (this.plugin.hasNotifySender()) {
			sender.sendMessage(getMessageWorld("senderMessage"));
		}

		if ((ident == 0) && (this.plugin.hasNotifyRanked())) {
			playerToRank.sendMessage(getMessage("rankedMessage"));
		} else if (this.plugin.hasNotifyRanked())
			playerToRank.sendMessage(getMessageWorld("rankedMessage"));
	}

	public String getCustomMessage(String msgName) {
		String msg = getMessage(msgName);
		return msg;
	}

	private String getMessage(String msgName) {
		if (msgName.equalsIgnoreCase("broadcastMessage")) {
			return ChatColor.YELLOW + getPlayerToRankName() + ChatColor.YELLOW
					+ " is now a " + getGroup();
		}
		if (msgName.equalsIgnoreCase("senderMessage")) {
			return ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "You have changed to rank of " + getPlayerToRankName()
					+ ChatColor.RED + " to " + getGroup();
		}
		if (msgName.equalsIgnoreCase("rankedMessage")) {
			return ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "Your rank has been changed to " + getGroup() + " by "
					+ getSenderName();
		}

		return "";
	}

	private String getMessageWorld(String msgName) {
		if (msgName.equalsIgnoreCase("senderMessage")) {
			return ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "You have changed to rank of " + getPlayerToRankName()
					+ ChatColor.RED + " to " + getGroup() + " in the world: "
					+ getWorldName();
		}
		if (msgName.equalsIgnoreCase("rankedMessage")) {
			return ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ "Your rank has been changed to " + getGroup() + " by "
					+ getSenderName() + ChatColor.RED + " in the world: "
					+ getWorldName();
		}

		return "";
	}

	private String getGroup() {
		return this.data[1];
	}

	private String getSenderName() {
		if (this.player == null) {
			return "Console";
		}

		return this.player.getDisplayName();
	}

	private String getPlayerToRankName() {
		return this.playerToRank.getDisplayName();
	}

	private String getWorldName() {
		if (this.data.length == 3) {
			return this.data[2];
		}

		return null;
	}

	private void displayHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "--- bPermsRank Help ---");
		sender.sendMessage(ChatColor.YELLOW + "/rank player group");
		sender.sendMessage(ChatColor.YELLOW + "/rank player group world");
		sender.sendMessage(ChatColor.YELLOW + "/rankoffline player group");
		sender.sendMessage(ChatColor.YELLOW + "/rankoffline player group world");
		sender.sendMessage(ChatColor.YELLOW + "/bpermsrank reload");
	}

	private String getPlayerGroups(String worldName, String playerName)
			throws CommandException {
		String[] groups = ApiLayer.getGroups(worldName, CalculableType.USER,
				playerName);

		if (groups == null) {
			throw new CommandException("Player not found!");
		}

		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < groups.length; i++) {
			temp.append(groups[i] + " ");
		}
		String results = temp.toString();

		return results;
	}

	private void rankPlayerAllWorlds(String playerToRankName, String group) {
		for (int i = 0; i < this.plugin.worlds.size(); i++) {
			String worldName = (String) this.plugin.worlds.get(i);
			ApiLayer.setGroup(worldName, CalculableType.USER, playerToRankName,
					group);
		}
	}

	private void rankPlayerInSingleWorld(String playerToRankName, String group,
			String worldName) {
		ApiLayer.setGroup(worldName, CalculableType.USER, playerToRankName,
				group);
	}

	public void isValidWorld(String worldName) throws CommandException {
		if (this.plugin.getServer().getWorld(worldName) == null)
			throw new CommandException("That is not a valid world!");
	}

	public void isValidGroup(String group) throws CommandException {
		if (this.plugin.groups.indexOf(group.toLowerCase()) == -1)
			throw new CommandException("That group/rank does not exist!");
	}

	public void isValidPlayer(Player player) throws CommandException {
		if ((player == null) || (!player.isOnline()))
			throw new CommandException(
					"That player is not online! Use: /rankoffline if you are sure you want to rank this player");
	}

	public void hasPermissionForGroup(Player player, String group)
			throws CommandException {
		if (!player.hasPermission("bpermsrank.rank." + group))
			throw new CommandException("You do not have permission to rank to "
					+ group);
	}

	public void hasPermissionForPlugin(Player player) {
		if (!player.hasPermission("bpermsrank.rank"))
			throw new CommandException(
					"You do not have permission to use this command!");
	}
}
