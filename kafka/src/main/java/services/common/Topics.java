package services.common;

public class Topics {
    /**
     * This topic handles User information.
     * Send an event everytime a User's state changes
     * i.e. username, level, status, etc.
     */
    public static final String USER_INPUT = "mmr-user-input";
    public static final String USER_OUTPUT = "mmr-user-output";

    /**
     * This topic handles Match information.
     * Send an event everytime a Match is created, updated or deleted.
     */
    public static final String MATCH_INPUT = "mmr-match-input";
    public static final String MATCH_OUTPUT = "mmr-match-output";

    /**
     * This topic handles Match history information.
     * When a match ends, for each user in the match, we'll need a match-history event.
     * i.e.
     * Did user {userId} win match {matchId}?
     * Did user {userId} abandon match {matchId}?
     */
    public static final String MATCH_HISTORY = "mmr-match-history";
}
