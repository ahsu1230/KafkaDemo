package services.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchStat {
    @JsonProperty("numMatches")
    public int numMatches;

    @JsonProperty("numWins")
    public int numWins;

    @JsonProperty("numAbandons")
    public int numAbandons;

    public MatchStat(int numMatches, int numWins, int numAbandons) {
        this.numMatches = numMatches;
        this.numWins = numWins;
        this.numAbandons = numAbandons;
    }
}
