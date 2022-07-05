package space.cubicworld.track.database;

import lombok.Data;
import space.cubicworld.track.action.insert.ActionInsertBuilder;

@Data
public class ActionInsert {

    public static ActionInsertBuilder builder() {
        return new ActionInsertBuilder();
    }

    private final long epoch;
    private final short actionId;
    private final short worldId;
    private final int locationX;
    private final int locationY;
    private final int locationZ;
    private final int invokerId;
    private final int targetId;
    private final short oldMaterialId;
    private final String oldMaterialData;
    private final short newMaterialId;
    private final String newMaterialData;

}
