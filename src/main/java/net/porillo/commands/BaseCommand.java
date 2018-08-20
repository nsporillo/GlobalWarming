package net.porillo.commands;

import net.porillo.GlobalWarming;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public abstract class BaseCommand implements Command {

	GlobalWarming plugin;
	private String name;
	private String permission;
	private int required = 0;
	private List<String> usages = new ArrayList<>();

	BaseCommand(final GlobalWarming plugin) {
		this.plugin = plugin;
	}

	final void addUsage(String desc, String... uses) {
		final StringBuilder usage = new StringBuilder().append(BLUE).append(String.format("%1$-" + 8 + "s", this.name));
		boolean color = true;

		for (String use : uses) {
			if (color) {
				usage.append(YELLOW);
			} else {
				usage.append(AQUA);
			}

			color = !color;
			usage.append(String.format("%1$-" + 8 + "s", use));
		}

		usage.append(GREEN);
		usage.append(desc);
		this.usages.add(usage.toString());
	}

	@Override
	public boolean checkPermission(final CommandSender sender) {
		return sender.hasPermission(this.permission);
	}

	@Override
	public int getRequiredArgs() {
		return this.required;
	}

	protected final void setRequiredArgs(final int req) {
		this.required = req;
	}

	void noPermission(final CommandSender sender) {
		sender.sendMessage(RED + "You do not have permission to use that command!");
	}

	final void setName(final String name) {
		this.name = name;
	}

	final void setPermission(final String perm) {
		this.permission = perm;
	}

	@Override
	public void showHelp(final CommandSender sender, final String label) {
		for (final String usage : this.usages) {
			sender.sendMessage(GRAY + String.format("%1$-" + 10 + "s", label) + ChatColor.translateAlternateColorCodes('&', usage));
		}
	}
}