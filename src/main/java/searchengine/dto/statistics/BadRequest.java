package searchengine.dto.statistics;

import lombok.Value;

@Value
public class BadRequest {
    boolean getResult;
    String errorMessage;

}