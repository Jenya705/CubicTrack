package space.cubicworld.track.action.insert;

import org.bukkit.Location;
import org.bukkit.Material;
import space.cubicworld.track.CubicTrack;
import space.cubicworld.track.action.Action;
import space.cubicworld.track.database.ActionInsert;

public class ActionInsertBuilder {

    private final long epoch;
    private Action action;
    private Location location;
    private String invoker;
    private String target;
    private Material oldMaterial;
    private String oldMaterialData;
    private Material newMaterial;
    private String newMaterialData;

    public ActionInsertBuilder() {
        epoch = System.currentTimeMillis();
    }

    public ActionInsertBuilder action(Action action) {
        this.action = action;
        return this;
    }

    public ActionInsertBuilder location(Location location) {
        this.location = location;
        return this;
    }

    public ActionInsertBuilder invoker(String invoker) {
        this.invoker = invoker;
        return this;
    }

    public ActionInsertBuilder target(String target) {
        this.target = target;
        return this;
    }

    public ActionInsertBuilder oldMaterial(Material material) {
        this.oldMaterial = material;
        return this;
    }

    public ActionInsertBuilder oldMaterialData(String data) {
        this.oldMaterialData = data;
        return this;
    }

    public ActionInsertBuilder newMaterial(Material material) {
        this.newMaterial = material;
        return this;
    }

    public ActionInsertBuilder newMaterialData(String data) {
        this.newMaterialData = data;
        return this;
    }

    public ActionInsert build() {
        CubicTrack plugin = CubicTrack.getInstance();
        return new ActionInsert(
                epoch,
                (short) plugin.getActions().getId(action.getName()),
                (short) plugin.getWorlds().getId(location.getWorld().getName()),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                plugin.getEntities().getId(invoker),
                plugin.getEntities().getId(target),
                (short) plugin.getMaterials().getId(oldMaterial == null ? "" : oldMaterial.name()),
                oldMaterialData,
                (short) plugin.getMaterials().getId(newMaterial == null ? "" : newMaterial.name()),
                newMaterialData
        );
    }

}
