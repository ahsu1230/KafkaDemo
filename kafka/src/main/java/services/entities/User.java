package services.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class User {
    @JsonProperty("id")
    public long id;

    @JsonProperty("username")
    public String username;

    @JsonProperty("level")
    public int level;

    @JsonProperty("isOnline")
    public boolean isOnline;

    @JsonProperty("queueState")
    public String queueState;

    @JsonProperty("queuedAt")
    public Date queuedAt;

    public static String QUEUE_STATE_NORMAL = "normal";
    public static String QUEUE_STATE_QUEUED = "queued";
    public static String QUEUE_STATE_MATCHED = "matched";

    public Long getId() {
        return id;
    }
}
