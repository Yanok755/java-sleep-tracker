package ru.yandex.practicum.sleeptracker;

public class SleepAnalysisResult<T> {
    private final String analysisName;
    private final T result;

    public SleepAnalysisResult(String analysisName, T result) {
        this.analysisName = analysisName;
        this.result = result;
    }

    public String getAnalysisName() {
        return analysisName;
    }

    public T getResult() {
        return result;
    }

    @Override
    public String toString() {
        return analysisName + ": " + result;
    }
}
