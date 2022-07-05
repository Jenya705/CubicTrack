package space.cubicworld.track.action;

import space.cubicworld.track.CubicTrack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ActionContainer {

    private final Map<String, List<Action>> actions = new ConcurrentHashMap<>();

    public void addAction(Action action) {
        addAction(action.getName(), action);
        for (String alias: action.getAliases()) {
            addAction(alias, action);
        }
        CubicTrack.getInstance().getActions().getId(action.getName());
    }

    private void addAction(String key, Action action) {
        actions.computeIfAbsent(key, it -> new CopyOnWriteArrayList<>());
        actions.get(key).add(action);
    }

    public List<Action> getActions(String name) {
        return Collections.unmodifiableList(
                actions.getOrDefault(name, Collections.emptyList())
        );
    }

}
