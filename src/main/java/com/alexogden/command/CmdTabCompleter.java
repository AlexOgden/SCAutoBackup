package com.alexogden.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CmdTabCompleter implements TabCompleter {
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
		List<String> completions = new ArrayList<>();

		if (sender instanceof Player) {
			if (args.length == 1) {
				completions.add("save");
				completions.add("backup");
			}
			// Filter and return the tab-completion options based on what the player has typed so far
			return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
		}

		return Collections.emptyList();
	}
}

