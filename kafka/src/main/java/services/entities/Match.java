package services.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.Nullable;

import java.util.Date;
import java.util.List;

public class Match {
    @JsonProperty("id")
    public String id;

    @JsonProperty("startedAt")
    public Date startedAt;

    @Nullable
    @JsonProperty("endedAt")
    public Date endedAt;

    @JsonProperty("userIds")
    public List<Long> userIds;

    public Date getStartedAt() {
        return startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public List<Long> getUserIds() {
        return userIds;
    }
}
