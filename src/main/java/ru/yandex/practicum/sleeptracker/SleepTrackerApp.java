package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Основной класс приложения
public class SleepTrackerApp {
    private List<SleepAnalysisFunction> analysisFunctions;

    public SleepTrackerApp() {
        // Инициализация списка аналитических функций
        this.analysisFunctions = Arrays.asList(
            new TotalSessionsFunction(),
            new MinDurationFunction(),
            new MaxDurationFunction(),
            new AverageDurationFunction(),
            new BadQualitySessionsFunction(),
            new SleeplessNightsFunction(),
            new ChronotypeFunction()
        );
    }

    // Функция 1: Общее количество сессий сна
    static class TotalSessionsFunction implements SleepAnalysisFunction {
        @Override
        public SleepAnalysisResult<Integer> apply(List<SleepingSession> sessions) {
            int count = sessions.size();
            return new SleepAnalysisResult<>("Общее количество сессий сна", count);
        }
    }

    // Функция 2: Минимальная продолжительность сессии
    static class MinDurationFunction implements SleepAnalysisFunction {
        @Override
        public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
            Long minDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .min()
                .orElse(0L);
            return new SleepAnalysisResult<>("Минимальная продолжительность сессии (минут)", minDuration);
        }
    }

    // Функция 3: Максимальная продолжительность сессии
    static class MaxDurationFunction implements SleepAnalysisFunction {
        @Override
        public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
            Long maxDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .max()
                .orElse(0L);
            return new SleepAnalysisResult<>("Максимальная продолжительность сессии (минут)", maxDuration);
        }
    }

    // Функция 4: Средняя продолжительность сессии
    static class AverageDurationFunction implements SleepAnalysisFunction {
        @Override
        public SleepAnalysisResult<Double> apply(List<SleepingSession> sessions) {
            Double averageDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .average()
                .orElse(0.0);
            return new SleepAnalysisResult<>("Средняя продолжительность сессии (минут)",
                Math.round(averageDuration * 100.0) / 100.0);
        }
    }

    // Функция 5: Количество сессий с плохим качеством сна
    static class BadQualitySessionsFunction implements SleepAnalysisFunction {
        @Override
        public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
            long badSessions = sessions.stream()
                .filter(session -> session.getQuality() == SleepingSession.SleepQuality.BAD)
                .count();
            return new SleepAnalysisResult<>("Количество сессий с плохим качеством сна", badSessions);
        }
    }

    // Функция 6: Количество бессонных ночей
    static class SleeplessNightsFunction implements SleepAnalysisFunction {
        @Override
        public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
            if (sessions.isEmpty()) {
                return new SleepAnalysisResult<>("Количество бессонных ночей", 0L);
            }

            // Получаем дату начала и окончания периода логирования
            LocalDateTime startDate = sessions.get(0).getSleepStart();
            LocalDateTime endDate = sessions.get(sessions.size() - 1).getSleepEnd();

            // Определяем первую ночь для анализа (ночь с 00:00 до 06:00 следующего дня)
            LocalDateTime firstNight = startDate.toLocalDate().atStartOfDay();
            if (startDate.getHour() < 6) {
                // Если сессия началась до 6 утра, это текущая ночь
                firstNight = firstNight.minusDays(1);
            }

            // Определяем последнюю ночь для анализа
            LocalDateTime lastNight = endDate.toLocalDate().atStartOfDay();
            if (endDate.getHour() >= 6) {
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
                    if (session.getSleepStart().getHour() < 6) {
                        nightStart = nightStart.minusDays(1);
                    }
                    return nightStart;
                })
                .distinct()
                .count();

            // Бессонные ночи = общее количество ночей - ночи со сном
            long sleeplessNights = Math.max(0, totalNights - nightsWithSleep);

            return new SleepAnalysisResult<>("Количество бессонных ночей", sleeplessNights);
        }

        private boolean isNightSleep(SleepingSession session) {
            LocalTime sleepStart = session.getSleepStart().toLocalTime();
            LocalTime sleepEnd = session.getSleepEnd().toLocalTime();

            // Проверяем, пересекает ли сессия сна ночной интервал (00:00-06:00)
            return (sleepStart.isBefore(LocalTime.of(6, 0)) || sleepEnd.isAfter(LocalTime.MIDNIGHT));
        }
    }

    // Функция 7: Определение хронотипа пользователя
    static class ChronotypeFunction implements SleepAnalysisFunction {
        @Override
        public SleepAnalysisResult<String> apply(List<SleepingSession> sessions) {
            // Фильтруем только ночные сессии сна
            List<SleepingSession> nightSessions = sessions.stream()
                .filter(this::isNightSession)
                .collect(Collectors.toList());

            if (nightSessions.isEmpty()) {
                return new SleepAnalysisResult<>("Хронотип пользователя", "Недостаточно данных");
            }

            // Считаем среднее время засыпания
            double avgSleepStart = nightSessions.stream()
                .mapToDouble(session -> session.getSleepStart().getHour() + session.getSleepStart().getMinute() / 60.0)
                .average()
                .orElse(0.0);

            // Считаем среднее время пробуждения
            double avgSleepEnd = nightSessions.stream()
                .mapToDouble(session -> {
                    LocalTime end = session.getSleepEnd().toLocalTime();
                    // Если пробуждение до 12:00, считаем как есть, иначе считаем как время следующего дня
                    if (end.getHour() < 12) {
                        return end.getHour() + end.getMinute() / 60.0;
                    } else {
                        return end.getHour() + end.getMinute() / 60.0;
                    }
                })
                .average()
                .orElse(0.0);

            // Определяем хронотип
            String chronotype;
            if (avgSleepStart >= 23.5 || avgSleepEnd >= 9.0) { // Совы: поздно ложатся и поздно встают
                chronotype = "Сова";
            } else if (avgSleepStart <= 22.0 && avgSleepEnd <= 7.0) { // Жаворонки: рано ложатся и рано встают
                chronotype = "Жаворонок";
            } else { // Голуби: все остальные
                chronotype = "Голубь";
            }

            return new SleepAnalysisResult<>("Хронотип пользователя", chronotype);
        }

        private boolean isNightSession(SleepingSession session) {
            // Ночная сессия - продолжительность >= 4 часов и основная часть сна в ночное время
            LocalTime sleepStart = session.getSleepStart().toLocalTime();
            LocalTime sleepEnd = session.getSleepEnd().toLocalTime();

            // Проверяем, пересекает ли сессия ночной период (22:00-08:00)
            boolean crossesNight = (sleepStart.isBefore(LocalTime.of(8, 0)) && sleepEnd.isAfter(LocalTime.of(22, 0))) ||
                               (sleepStart.isAfter(LocalTime.of(22, 0)) && sleepEnd.isBefore(LocalTime.of(8, 0))) ||
                               (sleepStart.isBefore(LocalTime.of(8, 0)) && sleepEnd.isBefore(LocalTime.of(8, 0)) && sleepStart.isBefore(sleepEnd)) ||
                               (sleepStart.isAfter(LocalTime.of(22, 0)) && sleepEnd.isAfter(LocalTime.of(22, 0)) && sleepStart.isBefore(sleepEnd));

            return session.getDurationInMinutes() >= 240 && crossesNight;
        }

        private boolean isOwl(SleepingSession session) {
            LocalTime sleepStart = session.getSleepStart().toLocalTime();
            LocalTime sleepEnd = session.getSleepEnd().toLocalTime();
            // Сова: ложится после 23:00 и встает после 9:00
            return sleepStart.isAfter(LocalTime.of(23, 0)) &&
                   sleepEnd.isAfter(LocalTime.of(9, 0));
        }

        private boolean isLark(SleepingSession session) {
            LocalTime sleepStart = session.getSleepStart().toLocalTime();
            LocalTime sleepEnd = session.getSleepEnd().toLocalTime();
            // Жаворонок: ложится до 22:00 и встает до 7:00
            return sleepStart.isBefore(LocalTime.of(22, 0)) &&
                   sleepEnd.isBefore(LocalTime.of(7, 0));
        }
    }

    // Метод для чтения файла с логом сна
    private List<SleepingSession> readSleepLog(String filePath) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines
                .map(line -> line.split(";"))
                .map(parts -> {
                    try {
                        LocalDateTime sleepStart = LocalDateTime.parse(parts[0], formatter);
                        LocalDateTime sleepEnd = LocalDateTime.parse(parts[1], formatter);
                        SleepingSession.SleepQuality quality = SleepingSession.SleepQuality.valueOf(parts[2]);
                        return new SleepingSession(sleepStart, sleepEnd, quality);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Ошибка парсинга строки: " + Arrays.toString(parts));
                    }
                })
                .collect(Collectors.toList());
        }
    }

    // Метод для запуска всех аналитических функций
    public void analyzeSleep(String filePath) {
        try {
            List<SleepingSession> sessions = readSleepLog(filePath);
            System.out.println("Загружено " + sessions.size() + " сессий сна");
            System.out.println("=".repeat(50));

            // Выполняем все аналитические функции
            analysisFunctions.forEach(function -> {
                SleepAnalysisResult<?> result = function.apply(sessions);
                System.out.println(result);
            });

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка анализа: " + e.getMessage());
        }
    }

    // Основной метод приложения
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Использование: java SleepTrackerApp <путь_к_файлу_лога>");
            System.out.println("Пример: java SleepTrackerApp sleep_log.txt");
            return;
        }

        SleepTrackerApp app = new SleepTrackerApp();
        app.analyzeSleep(args[0]);
    }
}
