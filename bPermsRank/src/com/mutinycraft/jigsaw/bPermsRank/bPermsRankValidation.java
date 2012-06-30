package com.mutinycraft.jigsaw.bPermsRank;

import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

public class bPermsRankValidation {
	
	private bPermsRankCommandExecutor cmdEx;

	public void isValidWorld(String worldName) throws CommandException {
		if (cmdEx.getPlugin().getServer().getWorld(worldName) == null)
			throw new CommandException("That is not a valid world!");
	}

	public void isValidGroup(String group) throws CommandException {
		if (cmdEx.getPlugin().groups.indexOf(group.toLowerCase()) == -1)
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
