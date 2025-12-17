package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

// Функциональный интерфейс для анализа сна
public interface SleepAnalysisFunction extends Function<List<SleepingSession>, SleepAnalysisResult<?>> {
    // Базовый метод по умолчанию для удобства
    default String getName() {
        return this.getClass().getSimpleName();
    }
}
