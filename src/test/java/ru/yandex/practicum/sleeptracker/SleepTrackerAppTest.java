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
            new
