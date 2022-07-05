package space.cubicworld.track.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import space.cubicworld.track.CubicTrack;
import space.cubicworld.track.CubicTrackEntities;
import space.cubicworld.track.action.DefaultAction;
import space.cubicworld.track.database.ActionInsert;

public class PlayerJoinListener extends CubicTrackListener {

    public PlayerJoinListener() {
        super("player-join");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CubicTrack.getInstance().getActionInsertTask().addAction(
                ActionInsert.builder()
                        .action(DefaultAction.PLAYER_JOIN)
                        .invoker(CubicTrackEntities.getName(event.getPlayer()))
                        .location(event.getPlayer().getLocation())
                        .build()
        );
    }

}
