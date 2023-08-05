package com.alexogden.core.logging;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageLogger {

	public static void sendMessage(CommandSender sender, String message) {
		if (!message.equals("")) {
			var miniMessage = MiniMessage.miniMessage();
			Component parsed = miniMessage.deserialize(message);

			sender.sendMessage(parsed);
		}
	}

	public static void broadcast(String message) {
		if (!message.equals("")) {
			var miniMessage = MiniMessage.miniMessage();
			Component parsed = miniMessage.deserialize("<aqua>[SCAB]</aqua>: " + message);

			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				player.sendMessage(parsed);
			}
			Bukkit.getConsoleSender().sendMessage(parsed);
		}
	}
}
