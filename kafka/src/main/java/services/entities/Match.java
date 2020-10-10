package services.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.Nullable;

import java.util.Date;
import java.util.List;

public class Match {
    @JsonProperty("id")
    public String id;

    @JsonProperty("users")
    public List<User> users;

    @JsonProperty("createdAt")
    public Date createdAt;

    @Nullable
    @JsonProperty("endedAt")
    public Date endedAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }
}
