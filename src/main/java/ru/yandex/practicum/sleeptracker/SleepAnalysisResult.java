package ru.yandex.practicum.sleeptracker;

// Класс-обёртка для результата анализа
public class SleepAnalysisResult<T> {
    private String description;
    private T value;

    public SleepAnalysisResult(String description, T value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", description, value.toString());
    }
}
