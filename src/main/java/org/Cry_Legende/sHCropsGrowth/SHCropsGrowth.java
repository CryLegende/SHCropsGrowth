package org.Cry_Legende.sHCropsGrowth;

import org.bukkit.plugin.java.JavaPlugin;
import org.Cry_Legende.sHCropsGrowth.commands.ReloadCommand;
import org.Cry_Legende.sHCropsGrowth.listeners.GrowthListener;

import java.io.File;

public final class SHCropsGrowth extends JavaPlugin {

    private static SHCropsGrowth instance;

    @Override
    public void onEnable() {
        instance = this;

        // Configuration
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        saveDefaultConfig(); // Crée config.yml si elle n'existe pas

        // Enregistrement des listeners et commandes
        getServer().getPluginManager().registerEvents(new GrowthListener(this), this);
        getCommand("shcropsgrowthreload").setExecutor(new ReloadCommand(this));

        getLogger().info("SHCropsGrowth a été activé !");
        getLogger().info("Rappel : Assurez-vous que les modificateurs de croissance dans spigot.yml sont à 100.0 pour les cultures gérées par ce plugin.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SHCropsGrowth a été désactivé !");
    }

    public static SHCropsGrowth getInstance() {
        return instance;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        // Si tu as d'autres logiques de rechargement (ex: vider des caches), ajoute-les ici
        getLogger().info("Configuration de SHCropsGrowth rechargée.");
    }
}