package services.stores;

import com.sun.istack.internal.Nullable;
import services.entities.MatchStat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MatchStatsStore {
    /**
     * This is very ghetto. Don't do this.
     * TODO: Ideally, we want this to use KafkaConnect and use a real DBstore like MySQL.
     */
    // This is a store of userId -> MatchStat object
    private static Map<Long, MatchStat> statMap = new ConcurrentHashMap<>();

    @Nullable
    public static MatchStat getMatchStat(long userId) {
        return statMap.get(userId);
    }

    public static Map<Long, MatchStat> getForUsers(Set<Long> userIds) {
        return statMap.entrySet().stream()
                .filter(entry -> userIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static void upsertStat(long userId, MatchStat stat) {
        statMap.put(userId, stat);
    }
}
