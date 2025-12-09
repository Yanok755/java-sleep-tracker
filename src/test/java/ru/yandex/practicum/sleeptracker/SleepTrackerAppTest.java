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
                              SleepQuality.GOOD), // 8 часов = 480 минут
            new SleepingSession(LocalDateTime.of(2025, 10, 3, 14, 30),
                              LocalDateTime.of(2025, 10, 3, 15, 20),
                              SleepQuality.NORMAL) // 50 минут
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
                              SleepQuality.GOOD), // 8 часов = 480 минут
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0),
                              LocalDateTime.of(2025, 10, 3, 10, 0),
                              SleepQuality.NORMAL) // 11 часов = 660 минут
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
                              SleepQuality.GOOD), // 8 часов = 480 минут
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0),
                              LocalDateTime.of(2025, 10, 3, 7, 0),
                              SleepQuality.NORMAL) // 8 часов = 480 минут
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
            // Ночь с 1 на 2 октября - есть сон
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 15),
                              LocalDateTime.of(2025, 10, 2, 8, 0),
                              SleepQuality.GOOD),
            // Ночь со 2 на 3 октября - есть сон
            new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0),
                              LocalDateTime.of(2025, 10, 3, 8, 0),
                              SleepQuality.NORMAL),
            // Ночь с 3 на 4 октября - бессонная (только дневной сон)
            new SleepingSession(LocalDateTime.of(2025, 10, 3, 14, 30),
                              LocalDateTime.of(2025, 10, 3, 15, 20),
                              SleepQuality.NORMAL),
            // Ночь с 4 на 5 октября - есть сон
            new SleepingSession(LocalDateTime.of(2025, 10, 4, 23, 30),
                              LocalDateTime.of(2025, 10, 5, 6, 20),
                              SleepQuality.BAD)
        );
        
        SleepTrackerApp.SleeplessNightsFunction function = new SleepTrackerApp.SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.apply(sessions);
        
        // Должна быть 1 бессонная ночь (с 3 на 4 октября)
        assertEquals(1L, result.getValue());
    }
    
    @Test
    void testSleeplessNightsWithEdgeCases() {
        // Тест с граничными случаями
        List<SleepingSession> sessions = Arrays.asList(
            // Засыпание после полуночи
            new SleepingSession(LocalDateTime.of(2025, 10, 1, 0, 30),
                              LocalDateTime.of(2025, 10, 1, 8, 0),
                              SleepQuality.GOOD),
            // Просыпание до полуночи
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
            // Сова: засыпание после 23:00, пробуждение после 9:00
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
            // Жаворонок: засыпание до 22:00, пробуждение до 7:00
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
            // Голубь: смешанные времена
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
