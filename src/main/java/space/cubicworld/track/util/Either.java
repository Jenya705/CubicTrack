package space.cubicworld.track.util;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Either<T, V> {

    private final T first;
    private final V second;

    public static <T, V> Either<T, V> first(T first) {
        return new Either<>(first, null);
    }

    public static <T, V> Either<T, V> second(V second) {
        return new Either<>(null, second);
    }

}
