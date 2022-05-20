package fr.blendman974.kinventory;

import fr.blendman974.kinventory.inventories.KInventoryService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Blendman974
 */
public class KInventoryManager {

    private static KInventoryService service;
    private static JavaPlugin plugin;

    public static KInventoryService getService() {
        return service;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static void init(JavaPlugin plugin) {
        KInventoryManager.plugin = plugin;
        service = new KInventoryService();
        Bukkit.getPluginManager().registerEvents(new KInventoryListener(service), plugin);
    }
}
