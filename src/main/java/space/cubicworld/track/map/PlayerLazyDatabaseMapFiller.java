package space.cubicworld.track.map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import space.cubicworld.track.CubicTrack;
import space.cubicworld.track.CubicTrackEntities;

public class PlayerLazyDatabaseMapFiller implements Listener {

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        CubicTrack.getInstance().getEntities()
                .cache(CubicTrackEntities.getName(event.getPlayer()));
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        CubicTrack.getInstance().getEntities()
                .unCache(CubicTrackEntities.getName(event.getPlayer()));
    }

}
