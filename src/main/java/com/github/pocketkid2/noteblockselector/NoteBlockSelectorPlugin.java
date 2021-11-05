package com.github.pocketkid2.noteblockselector;

import org.bukkit.plugin.java.JavaPlugin;

public class NoteBlockSelectorPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new NoteBlockListener(this), this);
		getLogger().info("Enabled!");
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabled!");
	}
}
