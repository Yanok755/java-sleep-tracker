package ru.yandex.practicum.sleeptracker;

import java.util.List;

// Функция 5: Количество сессий с плохим качеством сна
public class BadQualitySessionsFunction implements SleepAnalysisFunction {
    private static final String ANALYSIS_NAME = "Количество сессий с плохим качеством сна";

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long badSessions = sessions.stream()
            .filter(session -> session.getQuality() == SleepingSession.SleepQuality.BAD)
            .count();
        return new SleepAnalysisResult<>(ANALYSIS_NAME, badSessions);
    }
}
