package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SleepTrackerAppTest {

    @Test
    void testTotalSessionsFunction() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 15),
                              LocalDateTime.of(2025, 10, 2, 8, 0),
                              SleepQuality.GOOD),
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0),
                              LocalDateTime.of(2025, 10, 3, 8, 0),
                              SleepQuality.NORMAL)
        );

        SleepTrackerApp.TotalSessionsFunction function = new SleepTrackerApp.TotalSessionsFunction();
        SleepAnalysisResult<Integer> result = function.apply(sessions);

        assertEquals(2, result.getValue());
        assertEquals("Общее количество сессий сна", result.getDescription());
    }

    @Test
    void testMinDurationFunction() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 0),
                              LocalDateTime.of(2025, 10, 2, 6, 0),
                              SleepQuality.GOOD),
            new SleepingSession(LocalDateTime.of(2025, 10, 3, 14, 30),
                              LocalDateTime.of(2025, 10, 3, 15, 20),
                              SleepQuality.NORMAL)
        );

        SleepTrackerApp.MinDurationFunction function = new SleepTrackerApp.MinDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(sessions);

        assertEquals(50L, result.getValue());
    }

    @Test
    void testMaxDurationFunction() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 0),
                              LocalDateTime.of(2025, 10, 2, 6, 0),
                              SleepQuality.GOOD),
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0),
                              LocalDateTime.of(2025, 10, 3, 10, 0),
                              SleepQuality.NORMAL)
        );

        SleepTrackerApp.MaxDurationFunction function = new SleepTrackerApp.MaxDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(sessions);

        assertEquals(660L, result.getValue());
    }

    @Test
    void testAverageDurationFunction() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 0),
                              LocalDateTime.of(2025, 10, 2, 6, 0),
                              SleepQuality.GOOD),
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0),
                              LocalDateTime.of(2025, 10, 3, 7, 0),
                              SleepQuality.NORMAL)
        );

        SleepTrackerApp.AverageDurationFunction function = new SleepTrackerApp.AverageDurationFunction();
        SleepAnalysisResult<Double> result = function.apply(sessions);

        assertEquals(480.0, result.getValue());
    }

    @Test
    void testBadQualitySessionsFunction() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 15),
                              LocalDateTime.of(2025, 10, 2, 8, 0),
                              SleepQuality.GOOD),
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0),
                              LocalDateTime.of(2025, 10, 3, 8, 0),
                              SleepQuality.NORMAL),
            new SleepingSession(LocalDateTime.of(2025, 10, 3, 23, 30),
                              LocalDateTime.of(2025, 10, 4, 6, 20),
                              SleepQuality.BAD)
        );

        SleepTrackerApp.BadQualitySessionsFunction function = new SleepTrackerApp.BadQualitySessionsFunction();
        SleepAnalysisResult<Long> result = function.apply(sessions);

        assertEquals(1L, result.getValue());
    }

    @Test
    void testSleeplessNightsFunction() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 15),
                              LocalDateTime.of(2025, 10, 2, 8, 0),
                              SleepQuality.GOOD),
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0),
                              LocalDateTime.of(2025, 10, 3, 8, 0),
                              SleepQuality.NORMAL),
            new SleepingSession(LocalDateTime.of(2025, 10, 3, 14, 30),
                              LocalDateTime.of(2025, 10, 3, 15, 20),
                              SleepQuality.NORMAL),
            new SleepingSession(LocalDateTime.of(2025, 10, 4, 23, 30),
                              LocalDateTime.of(2025, 10, 5, 6, 20),
                              SleepQuality.BAD)
        );

        SleepTrackerApp.SleeplessNightsFunction function = new SleepTrackerApp.SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.apply(sessions);

        assertEquals(1L, result.getValue());
    }

    @Test
    void testSleeplessNightsWithEdgeCases() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 0, 30),
                              LocalDateTime.of(2025, 10, 1, 8, 0),
                              SleepQuality.GOOD),
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 22, 0),
                              LocalDateTime.of(2025, 10, 2, 23, 30),
                              SleepQuality.NORMAL)
        );

        SleepTrackerApp.SleeplessNightsFunction function = new SleepTrackerApp.SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.apply(sessions);

        assertNotNull(result.getValue());
    }

    @Test
    void testChronotypeFunctionOwl() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 30),
                              LocalDateTime.of(2025, 10, 2, 9, 30),
                              SleepQuality.GOOD),
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 0, 15),
                              LocalDateTime.of(2025, 10, 2, 10, 0),
                              SleepQuality.NORMAL)
        );

        SleepTrackerApp.ChronotypeFunction function = new SleepTrackerApp.ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(sessions);

        assertEquals("Сова", result.getValue());
    }

    @Test
    void testChronotypeFunctionLark() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 21, 30),
                              LocalDateTime.of(2025, 10, 2, 6, 30),
                              SleepQuality.GOOD),
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 20, 45),
                              LocalDateTime.of(2025, 10, 3, 5, 45),
                              SleepQuality.NORMAL)
        );

        SleepTrackerApp.ChronotypeFunction function = new SleepTrackerApp.ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(sessions);

        assertEquals("Жаворонок", result.getValue());
    }

    @Test
    void testChronotypeFunctionPigeon() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 30),
                              LocalDateTime.of(2025, 10, 2, 7, 30),
                              SleepQuality.GOOD),
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 30),
                              LocalDateTime.of(2025, 10, 3, 6, 30),
                              SleepQuality.NORMAL)
        );

        SleepTrackerApp.ChronotypeFunction function = new SleepTrackerApp.ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(sessions);

        assertEquals("Голубь", result.getValue());
    }
}
