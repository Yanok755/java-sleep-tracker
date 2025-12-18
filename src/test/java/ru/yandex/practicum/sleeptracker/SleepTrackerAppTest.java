package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SleepTrackerAppTest {

    @Test
    void testTotalSessionsFunction() {
        // Создаем тестовые сессии
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(
                LocalDateTime.of(2024, 1, 1, 22, 0),
                LocalDateTime.of(2024, 1, 2, 6, 0),
                SleepingSession.SleepQuality.GOOD
            ),
            new SleepingSession(
                LocalDateTime.of(2024, 1, 2, 23, 0),
                LocalDateTime.of(2024, 1, 3, 7, 0),
                SleepingSession.SleepQuality.NORMAL
            )
        );

        // Используем новый класс вместо внутреннего класса
        TotalSessionsFunction function = new TotalSessionsFunction();
        SleepAnalysisResult<Integer> result = function.apply(sessions);

        // Используем правильные геттеры
        assertEquals(2, result.getResult());
        assertEquals("Общее количество сессий сна", result.getAnalysisName());
    }

    @Test
    void testMinDurationFunction() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(
                LocalDateTime.of(2024, 1, 1, 22, 0),
                LocalDateTime.of(2024, 1, 1, 22, 50),
                SleepingSession.SleepQuality.GOOD
            ),
            new SleepingSession(
                LocalDateTime.of(2024, 1, 2, 23, 0),
                LocalDateTime.of(2024, 1, 3, 10, 0),
                SleepingSession.SleepQuality.NORMAL
            )
        );

        MinDurationFunction function = new MinDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(sessions);

        assertEquals(50L, result.getResult());
    }

    @Test
    void testMaxDurationFunction() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(
                LocalDateTime.of(2024, 1, 1, 22, 0),
                LocalDateTime.of(2024, 1, 2, 7, 0),
                SleepingSession.SleepQuality.GOOD
            ),
            new SleepingSession(
                LocalDateTime.of(2024, 1, 2, 23, 0),
                LocalDateTime.of(2024, 1, 3, 10, 0),
                SleepingSession.SleepQuality.NORMAL
            )
        );

        MaxDurationFunction function = new MaxDurationFunction();
        SleepAnalysisResult<Long> result = function.apply(sessions);

        assertEquals(660L, result.getResult());
    }

    @Test
    void testAverageDurationFunction() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(
                LocalDateTime.of(2024, 1, 1, 22, 0),
                LocalDateTime.of(2024, 1, 2, 6, 0),
                SleepingSession.SleepQuality.GOOD
            ),
            new SleepingSession(
                LocalDateTime.of(2024, 1, 2, 23, 0),
                LocalDateTime.of(2024, 1, 3, 7, 0),
                SleepingSession.SleepQuality.NORMAL
            )
        );

        AverageDurationFunction function = new AverageDurationFunction();
        SleepAnalysisResult<Double> result = function.apply(sessions);

        assertEquals(480.0, result.getResult());
    }

    @Test
    void testBadQualitySessionsFunction() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(
                LocalDateTime.of(2024, 1, 1, 22, 0),
                LocalDateTime.of(2024, 1, 2, 6, 0),
                SleepingSession.SleepQuality.GOOD
            ),
            new SleepingSession(
                LocalDateTime.of(2024, 1, 2, 23, 0),
                LocalDateTime.of(2024, 1, 3, 7, 0),
                SleepingSession.SleepQuality.BAD
            ),
            new SleepingSession(
                LocalDateTime.of(2024, 1, 3, 22, 0),
                LocalDateTime.of(2024, 1, 4, 6, 0),
                SleepingSession.SleepQuality.NORMAL
            )
        );

        BadQualitySessionsFunction function = new BadQualitySessionsFunction();
        SleepAnalysisResult<Long> result = function.apply(sessions);

        assertEquals(1L, result.getResult());
    }

    @Test
    void testSleeplessNightsFunction() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(
                LocalDateTime.of(2024, 1, 1, 22, 0),
                LocalDateTime.of(2024, 1, 2, 6, 0),
                SleepingSession.SleepQuality.GOOD
            ),
            // Пропущена ночь 2-3 января
            new SleepingSession(
                LocalDateTime.of(2024, 1, 3, 23, 0),
                LocalDateTime.of(2024, 1, 4, 7, 0),
                SleepingSession.SleepQuality.NORMAL
            )
        );

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.apply(sessions);

        assertEquals(1L, result.getResult());
    }

    @Test
    void testSleeplessNightsFunctionWithEmptyList() {
        List<SleepingSession> sessions = Arrays.asList();

        SleeplessNightsFunction function = new SleeplessNightsFunction();
        SleepAnalysisResult<Long> result = function.apply(sessions);

        assertNotNull(result.getResult());
        assertEquals(0L, result.getResult());
    }

    @Test
    void testChronotypeFunctionOwl() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(
                LocalDateTime.of(2024, 1, 1, 23, 30),
                LocalDateTime.of(2024, 1, 2, 8, 0),
                SleepingSession.SleepQuality.GOOD
            ),
            new SleepingSession(
                LocalDateTime.of(2024, 1, 2, 23, 45),
                LocalDateTime.of(2024, 1, 3, 9, 30),
                SleepingSession.SleepQuality.NORMAL
            )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(sessions);

        assertEquals("Сова", result.getResult());
    }

    @Test
    void testChronotypeFunctionLark() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(
                LocalDateTime.of(2024, 1, 1, 21, 0),
                LocalDateTime.of(2024, 1, 2, 5, 0),
                SleepingSession.SleepQuality.GOOD
            ),
            new SleepingSession(
                LocalDateTime.of(2024, 1, 2, 21, 30),
                LocalDateTime.of(2024, 1, 3, 6, 0),
                SleepingSession.SleepQuality.NORMAL
            )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(sessions);

        assertEquals("Жаворонок", result.getResult());
    }

    @Test
    void testChronotypeFunctionPigeon() {
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(
                LocalDateTime.of(2024, 1, 1, 22, 30),
                LocalDateTime.of(2024, 1, 2, 7, 30),
                SleepingSession.SleepQuality.GOOD
            ),
            new SleepingSession(
                LocalDateTime.of(2024, 1, 2, 22, 15),
                LocalDateTime.of(2024, 1, 3, 7, 45),
                SleepingSession.SleepQuality.NORMAL
            )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(sessions);

        assertEquals("Голубь", result.getResult());
    }

    @Test
    void testChronotypeFunctionNotEnoughData() {
        // Создаем дневные сессии, которые не будут считаться ночными
        List<SleepingSession> sessions = Arrays.asList(
            new SleepingSession(
                LocalDateTime.of(2024, 1, 1, 14, 0),
                LocalDateTime.of(2024, 1, 1, 15, 0), // Только 1 час
                SleepingSession.SleepQuality.GOOD
            )
        );

        ChronotypeFunction function = new ChronotypeFunction();
        SleepAnalysisResult<String> result = function.apply(sessions);

        assertEquals("Недостаточно данных", result.getResult());
    }

    @Test
    void testSleepingSessionDuration() {
        SleepingSession session = new SleepingSession(
            LocalDateTime.of(2024, 1, 1, 22, 0),
            LocalDateTime.of(2024, 1, 2, 6, 0),
            SleepingSession.SleepQuality.GOOD
        );

        assertEquals(480L, session.getDurationInMinutes());
    }

    @Test
    void testSleepTrackerAppMainWithNoArgs() {
        // Проверяем, что приложение не падает при запуске без аргументов
        SleepTrackerApp.main(new String[]{});
        // Если не было исключения - тест пройден
    }
}
