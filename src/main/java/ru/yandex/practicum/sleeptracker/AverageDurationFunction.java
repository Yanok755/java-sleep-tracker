package ru.yandex.practicum.sleeptracker;

import java.util.List;

// Функция 4: Средняя продолжительность сессии
public class AverageDurationFunction implements SleepAnalysisFunction {
    private static final String ANALYSIS_NAME = "Средняя продолжительность сессии (минут)";
    private static final int DECIMAL_PLACES = 2;

    @Override
    public SleepAnalysisResult<Double> apply(List<SleepingSession> sessions) {
        Double averageDuration = sessions.stream()
            .mapToLong(SleepingSession::getDurationInMinutes)
            .average()
            .orElse(0.0);

        double roundedAverage = Math.round(averageDuration * Math.pow(10, DECIMAL_PLACES))
                              / Math.pow(10, DECIMAL_PLACES);

        return new SleepAnalysisResult<>(ANALYSIS_NAME, roundedAverage);
    }
}
