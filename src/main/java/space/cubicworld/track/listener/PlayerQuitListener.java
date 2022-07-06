package space.cubicworld.track.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import space.cubicworld.track.CubicTrack;
import space.cubicworld.track.CubicTrackEntities;
import space.cubicworld.track.action.DefaultAction;
import space.cubicworld.track.database.ActionInsert;

public class PlayerQuitListener extends CubicTrackListener {

    public PlayerQuitListener() {
        super("player-quit");
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        CubicTrack.getInstance().getActionInsertTask().addAction(
                ActionInsert.builder()
                        .action(DefaultAction.PLAYER_QUIT)
                        .invoker(CubicTrackEntities.getName(event.getPlayer()))
                        .location(event.getPlayer().getLocation())
        );
    }

}
