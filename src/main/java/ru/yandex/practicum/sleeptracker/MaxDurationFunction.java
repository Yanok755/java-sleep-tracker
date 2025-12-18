package ru.yandex.practicum.sleeptracker;

import java.util.List;

// Функция 3: Максимальная продолжительность сессии
public class MaxDurationFunction implements SleepAnalysisFunction {
    private static final String ANALYSIS_NAME = "Максимальная продолжительность сессии (минут)";

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        Long maxDuration = sessions.stream()
            .mapToLong(SleepingSession::getDurationInMinutes)
            .max()
            .orElse(0L);
        return new SleepAnalysisResult<>(ANALYSIS_NAME, maxDuration);
    }
}
