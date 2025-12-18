package ru.yandex.practicum.sleeptracker;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.sleeptracker.SleepAnalysisConstants.*;

// Функция 7: Определение хронотипа пользователя
public class ChronotypeFunction implements SleepAnalysisFunction {
    private static final String ANALYSIS_NAME = "Хронотип пользователя";

    @Override
    public SleepAnalysisResult<String> apply(List<SleepingSession> sessions) {
        // Фильтруем только ночные сессии сна
        List<SleepingSession> nightSessions = sessions.stream()
            .filter(this::isNightSession)
            .collect(Collectors.toList());

        if (nightSessions.isEmpty()) {
            return new SleepAnalysisResult<>(ANALYSIS_NAME, CHRONOTYPE_NOT_ENOUGH_DATA);
        }

        // Считаем количество каждого типа
        long owlCount = nightSessions.stream()
            .filter(this::isOwl)
            .count();

        long larkCount = nightSessions.stream()
            .filter(this::isLark)
            .count();

        long pigeonCount = nightSessions.stream()
            .filter(session -> !isOwl(session) && !isLark(session))
            .count();

        // Определяем преобладающий тип
        String chronotype;
        if (owlCount > larkCount && owlCount > pigeonCount) {
            chronotype = CHRONOTYPE_OWL;
        } else if (larkCount > owlCount && larkCount > pigeonCount) {
            chronotype = CHRONOTYPE_LARK;
        } else {
            chronotype = CHRONOTYPE_PIGEON;
        }

        return new SleepAnalysisResult<>(ANALYSIS_NAME, chronotype);
    }

    private boolean isNightSession(SleepingSession session) {
        LocalTime sleepStart = session.getSleepStart().toLocalTime();
        LocalTime sleepEnd = session.getSleepEnd().toLocalTime();

        // Упрощаем проверку: считаем ночной сессией, если:
        // 1. Длительность >= 4 часов ИЛИ
        // 2. Начинается после 20:00 ИЛИ заканчивается до 8:00

        boolean isLongSession = session.getDurationInMinutes() >= MIN_NIGHT_SESSION_DURATION_MINUTES;
        boolean isEveningStart = sleepStart.isAfter(EVENING_START);
        boolean isMorningEnd = sleepEnd.isBefore(MORNING_END);
        boolean isOvernight = sleepStart.isBefore(NIGHT_END) && sleepEnd.isAfter(LATE_EVENING);

        return isLongSession || isEveningStart || isMorningEnd || isOvernight;
    }

    private boolean isOwl(SleepingSession session) {
        LocalTime sleepStart = session.getSleepStart().toLocalTime();
        LocalTime sleepEnd = session.getSleepEnd().toLocalTime();

        // Сова: ложится после 23:00 ИЛИ встает после 9:00
        return sleepStart.isAfter(OWL_BEDTIME_LIMIT) ||
               sleepEnd.isAfter(OWL_WAKETIME_LIMIT);
    }

    private boolean isLark(SleepingSession session) {
        LocalTime sleepStart = session.getSleepStart().toLocalTime();
        LocalTime sleepEnd = session.getSleepEnd().toLocalTime();

        // Жаворонок: ложится до 22:00 И встает до 7:00
        return sleepStart.isBefore(LARK_BEDTIME_LIMIT) &&
               sleepEnd.isBefore(LARK_WAKETIME_LIMIT);
    }
}
