package space.cubicworld.track.action;

import space.cubicworld.track.CubicTrack;

public interface Action {

    String getName();

    String[] getAliases();

    default void register() {
        CubicTrack.getInstance().getActionContainer().addAction(this);
    }

}
