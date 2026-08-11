package com.randomidentity;

import org.bukkit.plugin.java.JavaPlugin;

public final class RandomIdentityPlugin extends JavaPlugin {

    private IdentityManager identityManager;
    private NameGenerator nameGenerator;
    private SkinManager skinManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        nameGenerator = new NameGenerator(this);
        skinManager = new SkinManager(this);

        identityManager = new IdentityManager(
                this,
                nameGenerator,
                skinManager
        );

        getServer().getPluginManager().registerEvents(
                identityManager,
                this
        );

        IdentityCommand identityCommand =
                new IdentityCommand(this, identityManager);

        if (getCommand("identity") != null) {
            getCommand("identity").setExecutor(identityCommand);
            getCommand("identity").setTabCompleter(identityCommand);
        }

        getLogger().info("RandomIdentity enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("RandomIdentity disabled.");
    }

    public IdentityManager getIdentityManager() {
        return identityManager;
    }
}