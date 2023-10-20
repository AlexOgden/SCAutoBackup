package com.alexogden.core.logging;

import com.alexogden.core.SCAutoBackup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageLogger {

	private MessageLogger() {
		throw new IllegalStateException("Static Class");
	}

	public static void sendPlayerMessage(final CommandSender sender, final String message) {
		if (!message.isEmpty()) {
			var miniMessage = MiniMessage.miniMessage();
			Component parsed = miniMessage.deserialize(message);
			sender.sendMessage(parsed);
		}
	}

	public static void sendConsoleMessage(final Level logLevel, final String message) {
		if (!message.isEmpty()) {
			final Logger consoleLogger = SCAutoBackup.getInstance().getLogger();
			consoleLogger.log(logLevel, message);
		}
	}

	public static void broadcast(final String message) {
		if (!message.isEmpty()) {
			var miniMessage = MiniMessage.miniMessage();
			String prefix = "<aqua>[SCAB]</aqua>: ";
			Component parsed = miniMessage.deserialize(prefix + message);

			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				player.sendMessage(parsed);
			}
			Bukkit.getConsoleSender().sendMessage(parsed);
		}
	}
}
