package ru.yandex.practicum.sleeptracker;

import java.time.LocalTime;

public class SleepAnalysisConstants {
    // Константы времени для анализа сна
    public static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    public static final LocalTime MORNING_END = LocalTime.of(8, 0);
    public static final LocalTime EVENING_START = LocalTime.of(20, 0);
    public static final LocalTime LARK_BEDTIME_LIMIT = LocalTime.of(22, 0);
    public static final LocalTime LARK_WAKETIME_LIMIT = LocalTime.of(7, 0);
    public static final LocalTime OWL_BEDTIME_LIMIT = LocalTime.of(23, 0);
    public static final LocalTime OWL_WAKETIME_LIMIT = LocalTime.of(9, 0);
    public static final LocalTime MIDNIGHT = LocalTime.MIDNIGHT;
    public static final LocalTime LATE_EVENING = LocalTime.of(22, 0);

    // Константы продолжительности
    public static final long MIN_NIGHT_SESSION_DURATION_MINUTES = 240; // 4 часа
    public static final long HOURS_4_IN_MINUTES = 240;

    // Хронотипы
    public static final String CHRONOTYPE_OWL = "Сова";
    public static final String CHRONOTYPE_LARK = "Жаворонок";
    public static final String CHRONOTYPE_PIGEON = "Голубь";
    public static final String CHRONOTYPE_NOT_ENOUGH_DATA = "Недостаточно данных";

    private SleepAnalysisConstants() {
        // Приватный конструктор для предотвращения создания экземпляров
    }
}
