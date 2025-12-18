package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.sleeptracker.SleepAnalysisConstants.*;

// Функция 6: Количество бессонных ночей
public class SleeplessNightsFunction implements SleepAnalysisFunction {
    private static final String ANALYSIS_NAME = "Количество бессонных ночей";
    private static final long MIN_SLEEP_DURATION_FOR_NIGHT = 60; // Минимум 1 час сна для учета ночи

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult<>(ANALYSIS_NAME, 0L);
        }

        // Находим минимальную и максимальную даты из всех сессий
        LocalDateTime minDateTime = sessions.stream()
            .map(SleepingSession::getSleepStart)
            .min(LocalDateTime::compareTo)
            .orElseThrow();

        LocalDateTime maxDateTime = sessions.stream()
            .map(SleepingSession::getSleepEnd)
            .max(LocalDateTime::compareTo)
            .orElseThrow();

        // Определяем период анализа: от даты начала первой сессии до даты окончания последней
        LocalDate startDate = minDateTime.toLocalDate();
        LocalDate endDate = maxDateTime.toLocalDate();

        // Если первая сессия началась после 6 утра, мы пропускаем предыдущую ночь
        if (minDateTime.toLocalTime().isAfter(NIGHT_END)) {
            startDate = startDate.plusDays(1);
        }

        // Если последняя сессия закончилась до 6 утра, мы пропускаем текущую ночь
        if (maxDateTime.toLocalTime().isBefore(NIGHT_END)) {
            endDate = endDate.minusDays(1);
        }

        // Если после корректировок endDate стал раньше startDate, значит нет полных ночей
        if (endDate.isBefore(startDate)) {
            return new SleepAnalysisResult<>(ANALYSIS_NAME, 0L);
        }

        // Собираем все ночи, в которые был сон
        Set<LocalDate> nightsWithSleep = new HashSet<>();

        for (SleepingSession session : sessions) {
            // Проверяем, является ли сессия ночной (пересекает ночное время)
            if (isNightSession(session)) {
                // Определяем, к какой ночи относится сессия
                LocalDate nightDate = getNightDate(session);
                nightsWithSleep.add(nightDate);
            }
        }

        // Считаем общее количество ночей в периоде
        long totalNights = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            totalNights++;
            currentDate = currentDate.plusDays(1);
        }

        // Бессонные ночи = общее количество ночей - ночи со сном
        long sleeplessNights = totalNights - nightsWithSleep.size();

        return new SleepAnalysisResult<>(ANALYSIS_NAME, Math.max(0, sleeplessNights));
    }

    private boolean isNightSession(SleepingSession session) {
        // Проверяем, пересекает ли сессия ночное время (00:00-06:00)
        LocalTime sleepStart = session.getSleepStart().toLocalTime();
        LocalTime sleepEnd = session.getSleepEnd().toLocalTime();

        // Если продолжительность меньше минимальной, не считаем как ночную сессию
        if (session.getDurationInMinutes() < MIN_SLEEP_DURATION_FOR_NIGHT) {
            return false;
        }

        // Сессия считается ночной, если она:
        // 1. Пересекает полночь (начало до полуночи, конец после)
        boolean crossesMidnight = sleepStart.isAfter(sleepEnd);

        // 2. Начинается до 6 утра
        boolean startsBefore6AM = sleepStart.isBefore(NIGHT_END);

        // 3. Заканчивается после полуночи
        boolean endsAfterMidnight = sleepEnd.isAfter(MIDNIGHT) || sleepEnd.equals(MIDNIGHT);

        // 4. Или полностью находится в ночном времени
        boolean entirelyAtNight = sleepStart.isAfter(MIDNIGHT) && sleepStart.isBefore(NIGHT_END) &&
                                 sleepEnd.isAfter(MIDNIGHT) && sleepEnd.isBefore(NIGHT_END);

        return crossesMidnight || (startsBefore6AM && endsAfterMidnight) || entirelyAtNight;
    }

    private LocalDate getNightDate(SleepingSession session) {
        LocalDateTime sleepStart = session.getSleepStart();

        // Если сессия началась до 6 утра, она относится к предыдущей ночи
        if (sleepStart.toLocalTime().isBefore(NIGHT_END)) {
            return sleepStart.toLocalDate().minusDays(1);
        } else {
            return sleepStart.toLocalDate();
        }
    }
}
