package lordpipe.terracottascrewdriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin to add a tool (brick) to rotate blocks
 *
 * @author Copyright (c) lordpipe. Licensed GPLv3
 */
public class TerracottaScrewdriver extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
    }
}
