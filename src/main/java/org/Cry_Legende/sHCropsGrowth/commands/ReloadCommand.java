package org.Cry_Legende.sHCropsGrowth.commands;

import org.Cry_Legende.sHCropsGrowth.SHCropsGrowth;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final SHCropsGrowth plugin;

    public ReloadCommand(SHCropsGrowth plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // La commande de base est 'shcropsgrowth'
        // On vérifie maintenant le premier argument

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            // L'utilisateur a tapé "/shcropsgrowth reload" (ou un alias)

            if (!sender.hasPermission("shcropsgrowth.reload")) {
                sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission de recharger la configuration.");
                return true;
            }

            plugin.reloadPluginConfig();
            sender.sendMessage(ChatColor.GREEN + "La configuration de SHCropsGrowth a été rechargée.");
            return true;

        } else {
            // L'utilisateur a tapé "/shcropsgrowth" sans argument, ou avec un argument incorrect
            // Tu peux afficher un message d'aide ou la version du plugin ici si tu veux.
            // Pour l'instant, on va juste afficher l'usage correct pour 'reload'.
            if (sender.hasPermission("shcropsgrowth.reload")) { // N'afficher l'aide que s'ils ont la perm
                sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " reload");
            } else {
                sender.sendMessage(ChatColor.RED + "Commande inconnue ou arguments incorrects.");
            }
            return true;
        }
        // Si tu avais d'autres sous-commandes, tu ajouterais des 'else if' ici:
        // else if (args.length > 0 && args[0].equalsIgnoreCase("autrecommande")) { ... }
    }
}