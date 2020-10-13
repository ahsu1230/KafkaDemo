package services.stores;

import com.sun.istack.internal.Nullable;
import services.entities.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserStore {
    /**
     * This is very ghetto. Don't do this.
     * TODO: Ideally, we want this to use KafkaConnect and use a real DBstore like MySQL.
     */
    private static Map<Long, User> userMap = new ConcurrentHashMap<>();

    @Nullable
    public static User getUser(long userId) {
        return userMap.get(userId);
    }

    public static List<User> getAllUsers() {
        List<User> users = userMap.entrySet().stream()
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
        users.sort(Comparator.comparing(User::getId));
        return users;
    }

    public static void upsertUser(User user) {
        userMap.put(user.id, user);
    }
}
