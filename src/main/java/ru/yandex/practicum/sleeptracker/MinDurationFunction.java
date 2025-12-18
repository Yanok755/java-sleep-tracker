package ru.yandex.practicum.sleeptracker;

import java.util.List;

// Функция 2: Минимальная продолжительность сессии
public class MinDurationFunction implements SleepAnalysisFunction {
    private static final String ANALYSIS_NAME = "Минимальная продолжительность сессии (минут)";

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        Long minDuration = sessions.stream()
            .mapToLong(SleepingSession::getDurationInMinutes)
            .min()
            .orElse(0L);
        return new SleepAnalysisResult<>(ANALYSIS_NAME, minDuration);
    }
}
