package ru.yandex.practicum.sleeptracker;

import java.util.List;

@FunctionalInterface
public interface SleepAnalysisFunction {
    SleepAnalysisResult<?> apply(List<SleepingSession> sessions);
}
