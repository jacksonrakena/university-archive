package nz.ac.wgtn.swen301.a3.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record LogEvent(
        @JsonProperty(required = true)
        UUID id,
        @JsonProperty(required = true)
        String message,
        @JsonProperty(required = true)
        Date timestamp,
        @JsonProperty(required = true)
        String thread,
        @JsonProperty(required = true)
        String logger,
        @JsonProperty(required = true)
        String level,
        @JsonProperty(required = true)
        String errorDetails) {

}
