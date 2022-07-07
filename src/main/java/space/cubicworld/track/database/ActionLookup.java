package space.cubicworld.track.database;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ActionLookup extends ActionInsert {

    private final long id;

    public ActionLookup(long id, long epoch, short actionId, short worldId, int locationX, int locationY, int locationZ, int invokerId, int targetId, short oldMaterialId, String oldMaterialData, short newMaterialId, String newMaterialData) {
        super(epoch, actionId, worldId, locationX, locationY, locationZ, invokerId, targetId, oldMaterialId, oldMaterialData, newMaterialId, newMaterialData);
        this.id = id;
    }
}
