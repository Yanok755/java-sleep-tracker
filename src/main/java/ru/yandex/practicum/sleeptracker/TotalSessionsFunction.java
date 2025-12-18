package ru.yandex.practicum.sleeptracker;

import java.util.List;

// Функция 1: Общее количество сессий сна
public class TotalSessionsFunction implements SleepAnalysisFunction {
    private static final String ANALYSIS_NAME = "Общее количество сессий сна";

    @Override
    public SleepAnalysisResult<Integer> apply(List<SleepingSession> sessions) {
        int count = sessions.size();
        return new SleepAnalysisResult<>(ANALYSIS_NAME, count);
    }
}
