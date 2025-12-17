package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
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
                .filter(session -> session.getQuality() == SleepQuality.BAD)
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

            // Определяем первую ночь для анализа
            LocalDateTime firstNight = startDate.toLocalDate().atStartOfDay();
            if (startDate.getHour() >= 12) {
                firstNight = firstNight.plusDays(1);
            } else {
                firstNight = firstNight.minusDays(1);
            }

            // Определяем последнюю ночь для анализа
            LocalDateTime lastNight = endDate.toLocalDate().atStartOfDay();
            if (endDate.getHour() >= 12) {
                lastNight = lastNight.plusDays(1);
            }

            // Считаем количество ночей в периоде
            long totalNights = Period.between(firstNight.toLocalDate(), lastNight.toLocalDate()).getDays();

            // Считаем ночи со сном
            long nightsWithSleep = sessions.stream()
                .filter(this::isNightSleep)
                .map(session -> {
                    LocalDateTime nightStart = session.getSleepStart().toLocalDate().atStartOfDay();
                    // Если засыпание после 12, то это следующая ночь
                    if (session.getSleepStart().getHour() >= 12) {
                        nightStart = nightStart.plusDays(1);
                    } else {
                        nightStart = nightStart.minusDays(1);
                    }
                    return nightStart;
                })
                .distinct()
                .count();

            // Бессонные ночи = общее количество ночей - ночи со сном
            long sleeplessNights = totalNights - nightsWithSleep;

            return new SleepAnalysisResult<>("Количество бессонных ночей", sleeplessNights);
        }

        private boolean isNightSleep(SleepingSession session) {
            LocalTime sleepStart = session.getSleepStart().toLocalTime();
            LocalTime sleepEnd = session.getSleepEnd().toLocalTime();

            // Проверяем, пересекает ли сессия сна ночной интервал (00:00-06:00)
            return (sleepStart.isBefore(LocalTime.of(6, 0)) && sleepEnd.isAfter(LocalTime.MIDNIGHT)) ||
                   (sleepStart.isBefore(LocalTime.MIDNIGHT) && sleepEnd.isAfter(LocalTime.MIDNIGHT)) ||
                   (sleepStart.isBefore(LocalTime.of(6, 0)) && sleepEnd.isAfter(LocalTime.of(6, 0)));
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
                chronotype = "Сова";
            } else if (larkCount > owlCount && larkCount > pigeonCount) {
                chronotype = "Жаворонок";
            } else {
                chronotype = "Голубь";
            }

            return new SleepAnalysisResult<>("Хронотип пользователя", chronotype);
        }

        private boolean isNightSession(SleepingSession session) {
            // Игнорируем дневные сессии (короче 4 часов и не пересекающие ночь)
            return session.getDurationInMinutes() >= 240 ||
                   (session.getSleepStart().getHour() < 6 || session.getSleepEnd().getHour() > 22);
        }

        private boolean isOwl(SleepingSession session) {
            LocalTime sleepStart = session.getSleepStart().toLocalTime();
            LocalTime sleepEnd = session.getSleepEnd().toLocalTime();
            return sleepStart.isAfter(LocalTime.of(23, 0)) &&
                   sleepEnd.isAfter(LocalTime.of(9, 0));
        }

        private boolean isLark(SleepingSession session) {
            LocalTime sleepStart = session.getSleepStart().toLocalTime();
            LocalTime sleepEnd = session.getSleepEnd().toLocalTime();
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
                        SleepQuality quality = SleepQuality.valueOf(parts[2]);
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
