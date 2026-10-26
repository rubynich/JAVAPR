package ru.edu.pr02;

/**
 * Единственное место, где хранятся числовые правила табеля.
 * Изменение правила (в том числе параметр варианта) делается только здесь.
 */
public final class TimesheetRules {
    /** Сколько значений (рабочих дней) должно быть в строке. */
    public static final int DAYS_COUNT = 5;
    /** Минимально допустимое число часов за день (включительно). */
    public static final int MIN_HOURS = 0;
    /** Максимально допустимое число часов за день (включительно). */
    public static final int MAX_HOURS = 16;
    /** День считается переработкой, если часов БОЛЬШЕ этого значения. */
    public static final int DAILY_NORM = 8;
    public static final int WEEKLY_NORM = 35;
    /** Разделитель значений в строке. */
    public static final String SEPARATOR = ";";

    private TimesheetRules() {
    }
}
