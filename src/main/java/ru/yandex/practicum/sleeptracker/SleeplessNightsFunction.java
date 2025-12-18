package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ru.yandex.practicum.sleeptracker.SleepAnalysisConstants.*;

// Функция 6: Количество бессонных ночей
public class SleeplessNightsFunction implements SleepAnalysisFunction {
    private static final String ANALYSIS_NAME = "Количество бессонных ночей";

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>(ANALYSIS_NAME, 0L);
        }

        // Получаем дату начала и окончания периода логирования
        LocalDateTime startDate = sessions.get(0).getSleepStart();
        LocalDateTime endDate = sessions.get(sessions.size() - 1).getSleepEnd();

        // Определяем первую ночь для анализа (ночь с 00:00 до 06:00 следующего дня)
        LocalDateTime firstNight = startDate.toLocalDate().atStartOfDay();
        if (startDate.getHour() < NIGHT_END.getHour()) {
            // Если сессия началась до 6 утра, это текущая ночь
            firstNight = firstNight.minusDays(1);
        }

        // Определяем последнюю ночь для анализа
        LocalDateTime lastNight = endDate.toLocalDate().atStartOfDay();
        if (endDate.getHour() >= NIGHT_END.getHour()) {
            // Если сессия закончилась после 6 утра, это следующая ночь
            lastNight = lastNight.plusDays(1);
        }

        // Считаем количество ночей в периоде
        long totalNights = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(
            firstNight.toLocalDate(), lastNight.toLocalDate()));

        // Считаем ночи со сном
        long nightsWithSleep = sessions.stream()
            .filter(this::isNightSleep)
            .map(session -> {
                LocalDateTime nightStart = session.getSleepStart().toLocalDate().atStartOfDay();
                // Если засыпание после 00:00 и до 6:00, это предыдущая ночь
                if (session.getSleepStart().getHour() < NIGHT_END.getHour()) {
                    nightStart = nightStart.minusDays(1);
                }
                return nightStart;
            })
            .distinct()
            .count();

        // Бессонные ночи = общее количество ночей - ночи со сном
        long sleeplessNights = Math.max(0, totalNights - nightsWithSleep);

        return new SleepAnalysisResult<>(ANALYSIS_NAME, sleeplessNights);
    }

    private boolean isNightSleep(SleepingSession session) {
        LocalTime sleepStart = session.getSleepStart().toLocalTime();
        LocalTime sleepEnd = session.getSleepEnd().toLocalTime();

        // Проверяем, пересекает ли сессия сна ночной интервал (00:00-06:00)
        return (sleepStart.isBefore(NIGHT_END) || sleepEnd.isAfter(MIDNIGHT));
    }
}
