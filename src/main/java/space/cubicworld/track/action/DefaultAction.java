package space.cubicworld.track.action;

import lombok.Getter;

@Getter
public enum DefaultAction implements Action {

    PLAYER_JOIN("player-join", "join")
    ;

    private final String name;
    private final String[] aliases;

    DefaultAction(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
        register();
    }
}
