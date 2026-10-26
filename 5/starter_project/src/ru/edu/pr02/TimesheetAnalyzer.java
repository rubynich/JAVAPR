package ru.edu.pr02;

import java.util.Locale;

public class TimesheetAnalyzer {

    /**
     * Строгий разбор строки табеля вида "8;8;8;8;8".
     * Ровно 5 значений, каждое - целое число 0..16. Любая ошибка -> InvalidTimesheetException
     * с номером позиции (нумерация с 1).
     */
    public static int[] parseHours(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new InvalidTimesheetException("пустая строка табеля");
        }
        // limit = -1 сохраняет пустые элементы в конце ("8;8;8;8;8;" даёт 6 элементов)
        String[] parts = line.split(TimesheetRules.SEPARATOR, -1);
        if (parts.length != TimesheetRules.DAYS_COUNT) {
            throw new InvalidTimesheetException("ожидалось " + TimesheetRules.DAYS_COUNT
                    + " значений, получено " + parts.length);
        }
        int[] hours = new int[TimesheetRules.DAYS_COUNT];
        for (int i = 0; i < parts.length; i++) {          // цикл обработки массива при разборе
            hours[i] = parseOne(parts[i], i + 1);
        }
        return hours;
    }

    /** Преобразует одно значение и проверяет диапазон; position - номер позиции с 1. */
    private static int parseOne(String raw, int position) {
        String s = raw.trim();
        if (s.isEmpty()) {
            throw positionError(position, "пустое значение");
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
                throw positionError(position, "\"" + s + "\" не является целым неотрицательным числом");
            }
        }
        // 9 цифр гарантированно помещаются в int; более длинное число заведомо вне диапазона
        if (s.length() > 9) {
            throw positionError(position, "значение " + s + " вне диапазона "
                    + TimesheetRules.MIN_HOURS + ".." + TimesheetRules.MAX_HOURS);
        }
        int value = Integer.parseInt(s);
        if (value < TimesheetRules.MIN_HOURS || value > TimesheetRules.MAX_HOURS) {
            throw positionError(position, "значение " + value + " вне диапазона "
                    + TimesheetRules.MIN_HOURS + ".." + TimesheetRules.MAX_HOURS);
        }
        return value;
    }

    private static InvalidTimesheetException positionError(int position, String reason) {
        return new InvalidTimesheetException("позиция " + position + ": " + reason);
    }

    /** Суммарное количество часов за неделю. */
    public static int total(int[] hours) {
        int sum = 0;
        for (int i = 0; i < hours.length; i++) {
            sum += hours[i];
        }
        return sum;
    }

    /** Среднее число часов в день; для пустого массива - 0.0 (без деления на ноль). */
    public static double average(int[] hours) {
        if (hours.length == 0) {
            return 0.0;
        }
        return (double) total(hours) / hours.length;
    }

    /** Количество дней, в которые указано БОЛЬШЕ DAILY_NORM часов (ровно 8 - не переработка). */
    public static int overtimeDays(int[] hours) {
        int count = 0;
        for (int i = 0; i < hours.length; i++) {
            if (hours[i] > TimesheetRules.DAILY_NORM) {
                count++;
            }
        }
        return count;
    }

    /** Недельная переработка = max(0, сумма - WEEKLY_NORM). Вариант 6: норма 35 часов. */
    public static int weeklyOvertime(int[] hours) {
        return Math.max(0, total(hours) - TimesheetRules.WEEKLY_NORM);
    }

    /** Итоговый отчёт по строке табеля либо сообщение об ошибке (ветка "нет" на Activity-диаграмме). */
    public static String buildReport(String line) {
        int[] hours;
        try {
            hours = parseHours(line);
        } catch (InvalidTimesheetException e) {
            return "ОШИБКА: табель некорректен - " + e.getMessage();
        }
        return String.format(Locale.ROOT,
                "OK: сумма=%d, среднее=%.2f, дней с переработкой=%d, недельная переработка (норма %d)=%d",
                total(hours), average(hours), overtimeDays(hours),
                TimesheetRules.WEEKLY_NORM, weeklyOvertime(hours));
    }
}
