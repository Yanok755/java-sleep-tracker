package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        LocalDateTime firstNightStart = startDate.toLocalDate().atStartOfDay();
        if (startDate.getHour() < NIGHT_END.getHour()) {
            // Если сессия началась до 6 утра, это предыдущая ночь
            firstNightStart = firstNightStart.minusDays(1);
        }

        // Определяем последнюю ночь для анализа
        LocalDateTime lastNightStart = endDate.toLocalDate().atStartOfDay();
        if (endDate.getHour() >= NIGHT_END.getHour()) {
            // Если сессия закончилась после 6 утра, это текущая ночь
            // (уже учтена в lastNightStart)
        }

        // Собираем все ночи со сном
        Set<LocalDateTime> nightsWithSleep = new HashSet<>();

        for (SleepingSession session : sessions) {
            if (isNightSleep(session)) {
                LocalDateTime nightStart = session.getSleepStart().toLocalDate().atStartOfDay();
                // Если засыпание после 00:00 и до 6:00, это предыдущая ночь
                if (session.getSleepStart().getHour() < NIGHT_END.getHour()) {
                    nightStart = nightStart.minusDays(1);
                }
                nightsWithSleep.add(nightStart);
            }
        }

        // Считаем общее количество ночей в периоде (включительно)
        long totalNights = 0;
        LocalDateTime currentNight = firstNightStart;

        while (!currentNight.isAfter(lastNightStart)) {
            totalNights++;
            currentNight = currentNight.plusDays(1);
        }

        // Бессонные ночи = общее количество ночей - ночи со сном
        long sleeplessNights = Math.max(0, totalNights - nightsWithSleep.size());

        return new SleepAnalysisResult<>(ANALYSIS_NAME, sleeplessNights);
    }

    private boolean isNightSleep(SleepingSession session) {
        LocalTime sleepStart = session.getSleepStart().toLocalTime();
        LocalTime sleepEnd = session.getSleepEnd().toLocalTime();

        // Проверяем, пересекает ли сессия сна ночной интервал (00:00-06:00)
        // Или полностью находится в ночное время
        return (sleepStart.isBefore(NIGHT_END) && sleepEnd.isAfter(MIDNIGHT)) ||
               (sleepStart.isBefore(NIGHT_END) && sleepEnd.isBefore(sleepStart)) || // переходит через полночь
               (sleepStart.isAfter(MIDNIGHT) && sleepStart.isBefore(NIGHT_END)) || // начинается между 00:00 и 06:00
               (sleepEnd.isAfter(MIDNIGHT) && sleepEnd.isBefore(NIGHT_END)); // заканчивается между 00:00 и 06:00
    }
}
