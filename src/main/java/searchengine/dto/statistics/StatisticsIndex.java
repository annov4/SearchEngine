package searchengine.dto.statistics;


import lombok.Value;

@Value
public class StatisticsIndex {
    Integer pageID;
    Integer lemmaID;
    Float rank;
}