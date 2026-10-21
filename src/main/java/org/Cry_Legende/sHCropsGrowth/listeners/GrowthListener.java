package org.Cry_Legende.sHCropsGrowth.listeners;

import org.Cry_Legende.sHCropsGrowth.SHCropsGrowth;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.Random;

public class GrowthListener implements Listener {

    private final SHCropsGrowth plugin;
    private final Random random = new Random();

    public GrowthListener(SHCropsGrowth plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        Block block = event.getBlock();
        Material material = block.getType();
        FileConfiguration config = plugin.getConfig();

        String materialName = material.name();
        // Pour les tiges de melon/citrouille, elles sont distinctes de leurs fruits
        if (material == Material.ATTACHED_MELON_STEM) materialName = "MELON_STEM";
        if (material == Material.ATTACHED_PUMPKIN_STEM) materialName = "PUMPKIN_STEM";


        // Vérifier si un modificateur est défini pour ce type de plante
        if (!config.isSet("growth-modifiers." + materialName)) {
            return; // Pas de modificateur défini, laisser Minecraft gérer
        }

        double modifier = config.getDouble("growth-modifiers." + materialName, 1.0);

        if (modifier == 1.0) {
            return; // Modificateur normal, laisser Minecraft gérer
        }

        // Gestion pour les cultures "Ageable" (blé, carottes, pommes de terre, etc.)
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Ageable) {
            Ageable ageable = (Ageable) blockData;
            int currentAge = ageable.getAge();
            int maxAge = ageable.getMaximumAge();

            if (currentAge == maxAge) {
                return; // Déjà à maturité
            }

            if (modifier < 1.0) { // Ralentir la croissance
                // Chance d'annuler la croissance : 1.0 - modifier
                // Si modifier = 0.5, 50% de chance d'annuler
                if (random.nextDouble() > modifier) {
                    event.setCancelled(true);
                }
                // Si non annulé, la croissance se fait d'un seul stade (comportement vanilla)
            } else { // Accélérer la croissance (modifier > 1.0)
                event.setCancelled(true); // On prend le contrôle total

                int stagesToGrow = 0;
                double effectiveModifier = modifier;

                // Calcule le nombre d'étapes entières à faire pousser
                stagesToGrow += (int) Math.floor(effectiveModifier);
                // Gère la partie fractionnaire comme une chance pour une étape supplémentaire
                if (random.nextDouble() < (effectiveModifier - Math.floor(effectiveModifier))) {
                    stagesToGrow++;
                }

                // Assurer une croissance d'au moins 1 si le modificateur est > 0 mais < 1 après la chance
                // et qu'on est dans la branche d'accélération (donc modifier était > 1 initialement)
                if (stagesToGrow == 0 && modifier > 0) {
                    stagesToGrow = 1;
                }


                if (stagesToGrow > 0) {
                    int newAge = Math.min(currentAge + stagesToGrow, maxAge);
                    if (newAge > currentAge) {
                        ageable.setAge(newAge);
                        block.setBlockData(ageable);
                    }
                }
            }
        } else if (material == Material.CACTUS || material == Material.SUGAR_CANE || material == Material.BAMBOO || material == Material.KELP_PLANT || material == Material.VINE || material == Material.TWISTING_VINES || material == Material.WEEPING_VINES) {
            // Pour cactus, canne à sucre, bambou, kelp... (qui ne sont pas 'Ageable' de la même manière)
            // On ne peut pas facilement les faire grandir de plusieurs blocs en un seul tick avec BlockGrowEvent
            // car l'événement concerne la croissance du *prochain* bloc.
            // On va donc se contenter de modifier la *chance* que la croissance se produise.
            // Si modifier > 1.0, on ne fait rien de spécial, on laisse la croissance se faire (elle sera
            // déjà plus fréquente si randomTickSpeed est normal et que Spigot ne la modifie pas).

            if (modifier < 1.0) {
                // Chance d'annuler la croissance : 1.0 - modifier
                if (random.nextDouble() > modifier) {
                    event.setCancelled(true);
                }
            }
            // Si modifier > 1.0, on laisse l'événement vanilla se produire.
            // Le cactus devrait se casser normalement car c'est la logique vanilla qui s'exécute.
        }
        // Pour les autres types de croissance non-Ageable (champignons, etc.), on pourrait ajouter des logiques spécifiques si besoin.
    }
}