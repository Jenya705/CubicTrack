package space.cubicworld.track;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@UtilityClass
public class CubicTrackEntities {

    public final String CONSOLE = "#console";
    public final String ENVIRONMENT = "#environment";

    public String getName(Entity entity) {
        if (entity instanceof Player) {
            return entity.getName();
        }
        return "#" + entity.getType().getName();
    }

}
