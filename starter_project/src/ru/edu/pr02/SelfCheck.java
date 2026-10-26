package ru.edu.pr02;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class SelfCheck {
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private static int failed = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        // Исходная проверка из стартового проекта (сохранена без изменений по смыслу)
        boolean ok = false;
        try { ok = (TimesheetAnalyzer.total(new int[]{8,8,8,8,8}) == 40 && TimesheetAnalyzer.overtimeDays(new int[]{9,8,7,10,8}) == 2); } catch (Exception e) { OUT.println("FAIL exception " + e.getMessage()); }
        OUT.println(ok ? "PASS" : "FAIL  complete TODO methods");
        if (!ok) {
            failed++;
        }

        // 1. Корректное типовое значение
        int[] typical = TimesheetAnalyzer.parseHours("9;8;7;10;8");
        check("1. типовой: сумма 42", TimesheetAnalyzer.total(typical) == 42);
        check("1. типовой: среднее 8.4", Math.abs(TimesheetAnalyzer.average(typical) - 8.4) < 1e-9);
        check("1. типовой: дней с переработкой 2", TimesheetAnalyzer.overtimeDays(typical) == 2);
        check("1. типовой: недельная переработка 7 (42 - 35)", TimesheetAnalyzer.weeklyOvertime(typical) == 7);

        // 2. Нижняя граница (0 допустимо)
        int[] zeros = TimesheetAnalyzer.parseHours("0;0;0;0;0");
        check("2. нижняя граница 0: разбор успешен, сумма 0", TimesheetAnalyzer.total(zeros) == 0);
        check("2. нижняя граница 0: переработки нет", TimesheetAnalyzer.weeklyOvertime(zeros) == 0
                && TimesheetAnalyzer.overtimeDays(zeros) == 0);
        expectError("2. значение -1 отклоняется", "8;-1;8;8;8", "позиция 2");

        // 3. Верхняя граница (16 допустимо, 17 нет)
        int[] max = TimesheetAnalyzer.parseHours("16;16;16;16;16");
        check("3. верхняя граница 16: сумма 80, переработка 45, дней 5",
                TimesheetAnalyzer.total(max) == 80 && TimesheetAnalyzer.weeklyOvertime(max) == 45
                        && TimesheetAnalyzer.overtimeDays(max) == 5);
        expectError("3. значение 17 отклоняется с позицией 3", "8;8;17;8;8", "позиция 3");

        // 4. Пустая / отсутствующая строка и пустые значения
        expectError("4. пустая строка", "", "пустая строка");
        expectError("4. null вместо строки", null, "пустая строка");
        expectError("4. пустое значение в середине", "8;;8;8;8", "позиция 2");

        // 5. Неверный формат
        expectError("5. буквы вместо числа", "8;abc;8;8;8", "позиция 2");
        expectError("5. дробное число", "8;8;8;8.5;8", "позиция 4");

        // 6. Повтор / конфликт структуры
        expectError("6. четыре значения вместо пяти", "8;8;8;8", "получено 4");
        expectError("6. шесть значений (лишний ';' в конце)", "8;8;8;8;8;", "получено 6");

        // 7. Граница дневной нормы (ровно 8 - не переработка, 9 - переработка)
        check("7. 8 часов не переработка", TimesheetAnalyzer.overtimeDays(new int[]{8, 8, 8, 8, 8}) == 0);
        check("7. 9 часов переработка", TimesheetAnalyzer.overtimeDays(new int[]{9, 8, 8, 8, 8}) == 1);

        // 8. Вариант 6: граница недельной нормы 35
        check("8. сумма 35 -> переработка 0", TimesheetAnalyzer.weeklyOvertime(new int[]{7, 7, 7, 7, 7}) == 0);
        check("8. сумма 36 -> переработка 1", TimesheetAnalyzer.weeklyOvertime(new int[]{8, 7, 7, 7, 7}) == 1);
        check("8. сумма 40 -> переработка 5 (а не 0, как при норме 40)",
                TimesheetAnalyzer.weeklyOvertime(new int[]{8, 8, 8, 8, 8}) == 5);
        check("8. сумма 30 -> переработка 0 (не отрицательная)",
                TimesheetAnalyzer.weeklyOvertime(new int[]{6, 6, 6, 6, 6}) == 0);

        // Отчёт целиком
        check("9. отчёт содержит позицию ошибки", TimesheetAnalyzer.buildReport("8;8;17;8;8").contains("позиция 3"));
        check("9. отчёт корректного табеля начинается с OK", TimesheetAnalyzer.buildReport("8;8;8;8;8").startsWith("OK"));

        OUT.println("Итого: пройдено " + passed + ", провалено " + failed);
        OUT.println(failed == 0 ? "PASS ALL" : "FAIL");
        if (failed != 0) {
            System.exit(1);
        }
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            OUT.println("  ok    " + name);
        } else {
            failed++;
            OUT.println("  FAIL  " + name);
        }
    }

    /** Ожидает InvalidTimesheetException, сообщение которой содержит expectedFragment. */
    private static void expectError(String name, String line, String expectedFragment) {
        try {
            TimesheetAnalyzer.parseHours(line);
            check(name + " (ошибка не возникла)", false);
        } catch (InvalidTimesheetException e) {
            check(name + " -> \"" + e.getMessage() + "\"", e.getMessage().contains(expectedFragment));
        } catch (RuntimeException e) {
            check(name + " (неконтролируемое исключение " + e.getClass().getSimpleName() + ")", false);
        }
    }
}
