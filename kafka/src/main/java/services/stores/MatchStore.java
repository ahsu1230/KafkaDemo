package services.stores;

import com.sun.istack.internal.Nullable;
import services.entities.Match;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MatchStore {
    /**
     * This is very ghetto. Don't do this.
     * TODO: Ideally, we want this to use KafkaConnect and use a real DBstore like MySQL.
     */
    private static Map<String, Match> matchMap = new ConcurrentHashMap<>();

    @Nullable
    public static Match getMatch(String matchId) {
        return matchMap.get(matchId);
    }

    public static List<Match> getAllMatches() {
        List<Match> matches = matchMap.entrySet().stream()
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
        matches.sort(Comparator.comparing(Match::getStartedAt));
        return matches;
    }

    public static void upsertMatch(Match match) {
        match.id = match.id == null ? UUID.randomUUID().toString() : match.id;
        matchMap.put(match.id, match);
    }
}
