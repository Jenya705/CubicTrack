package space.cubicworld.track.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import space.cubicworld.track.CubicTrack;

public abstract class CubicTrackListener implements Listener {

    private final String name;
    private boolean enabled = false;

    public CubicTrackListener(String name) {
        this.name = name;
        reload();
    }
    public synchronized void reload() {
        boolean shouldEnable = CubicTrack
                .getInstance()
                .getConfig()
                .getBoolean("listener.%s".formatted(name), true);
        if (shouldEnable && !enabled) {
            enabled = true;
            Bukkit.getServer()
                    .getPluginManager()
                    .registerEvents(this, CubicTrack.getInstance());
        }
        else if (!shouldEnable && enabled) {
            enabled = false;
            HandlerList.unregisterAll(this);
        }
    }

}
