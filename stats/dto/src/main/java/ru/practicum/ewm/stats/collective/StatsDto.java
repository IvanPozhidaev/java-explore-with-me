package ru.practicum.ewm.stats.collective;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class StatsDto {
    private String app;
    private String uri;
    private Long hits;
}