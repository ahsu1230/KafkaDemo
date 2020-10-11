package services.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchHistory {
    @JsonProperty("matchId")
    public String matchId;

    @JsonProperty("userId")
    public long userId;

    @JsonProperty("isWinner")
    public boolean isWinner;

    @JsonProperty("abandoned")
    public boolean abandoned;
}
