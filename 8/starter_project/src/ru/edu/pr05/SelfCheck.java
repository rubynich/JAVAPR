package ru.edu.pr05;

import java.time.LocalDate;

/** Самопроверка обязательных сценариев и граничных условий без внешних библиотек. */
public class SelfCheck {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        LocalDate start = LocalDate.of(2026, 10, 10);
        LocalDate end = LocalDate.of(2026, 10, 12);

        check("валидный объект создаётся в DRAFT", () ->
            expectStatus(new BusinessTrip("T-01", start, end), TripStatus.DRAFT));
        check("пустой id отклоняется", () -> expectThrows(IllegalArgumentException.class,
            () -> new BusinessTrip("  ", start, end)));
        check("неверный формат даты отклоняется парсером", () ->
            expectThrows(java.time.format.DateTimeParseException.class,
                () -> LocalDate.parse("12-10-2026")));
        check("отсутствующая дата отклоняется", () -> expectThrows(IllegalArgumentException.class,
            () -> new BusinessTrip("T-02", null, end)));
        check("дата окончания раньше начала отклоняется", () -> expectThrows(IllegalArgumentException.class,
            () -> new BusinessTrip("T-03", end, start)));
        check("DRAFT -> SUBMITTED -> APPROVED", () -> {
            BusinessTrip t = trip("T-04", start, end); t.submit(); t.approve(); expectStatus(t, TripStatus.APPROVED);
        });
        check("повторная отправка запрещена", () -> {
            BusinessTrip t = trip("T-05", start, end); t.submit();
            expectThrows(IllegalStateException.class, t::submit);
        });
        check("повторный id в сервисе запрещён", () -> {
            BusinessTripService service = new BusinessTripService();
            service.create("DUP-01", start, end);
            expectThrows(IllegalArgumentException.class,
                () -> service.create("DUP-01", start, end));
        });
        check("причина из 9 символов отклоняется", () -> {
            BusinessTrip t = trip("T-06", start, end); t.submit();
            expectThrows(IllegalArgumentException.class, () -> t.reject("123456789"));
        });
        check("причина из 10 символов принимается", () -> {
            BusinessTrip t = trip("T-07", start, end); t.submit(); t.reject("Причина123");
            expectStatus(t, TripStatus.REJECTED);
        });
        check("отмена из DRAFT разрешена", () -> {
            BusinessTrip t = trip("T-08", start, end); t.cancel(); expectStatus(t, TripStatus.CANCELLED);
        });
        check("отмена после REJECTED запрещена", () -> {
            BusinessTrip t = trip("T-09", start, end); t.submit(); t.reject("Причина отказа");
            expectThrows(IllegalStateException.class, t::cancel);
        });
        check("завершение в день окончания запрещено", () -> {
            BusinessTrip t = approved("T-10", start, end);
            expectThrows(IllegalStateException.class, () -> t.complete(end));
        });
        check("отсутствующая текущая дата отклоняется", () -> {
            BusinessTrip t = approved("T-10B", start, end);
            expectThrows(IllegalArgumentException.class, () -> t.complete(null));
        });
        check("вариант 6: завершение на 35-й день разрешено", () -> {
            BusinessTrip t = approved("T-11", start, end); t.complete(end.plusDays(35));
            expectStatus(t, TripStatus.COMPLETED);
        });
        check("вариант 6: завершение на 36-й день запрещено", () -> {
            BusinessTrip t = approved("T-12", start, end);
            expectThrows(IllegalStateException.class, () -> t.complete(end.plusDays(36)));
        });
        check("после COMPLETED отмена запрещена", () -> {
            BusinessTrip t = approved("T-13", start, end); t.complete(end.plusDays(1));
            expectThrows(IllegalStateException.class, t::cancel);
        });

        System.out.println("ИТОГ: " + passed + " пройдено, " + failed + " ошибок.");
        if (failed > 0) System.exit(1);
    }

    private static BusinessTrip trip(String id, LocalDate start, LocalDate end) {
        return new BusinessTrip(id, start, end);
    }
    private static BusinessTrip approved(String id, LocalDate start, LocalDate end) {
        BusinessTrip t = trip(id, start, end); t.submit(); t.approve(); return t;
    }
    private static void expectStatus(BusinessTrip t, TripStatus expected) {
        if (t.getStatus() != expected) throw new AssertionError("Ожидался статус " + expected + ", получен " + t.getStatus());
    }
    private static void expectThrows(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); } catch (Throwable ex) {
            if (type.isInstance(ex)) return;
            throw new AssertionError("Ожидалось " + type.getSimpleName() + ", получено " + ex, ex);
        }
        throw new AssertionError("Ожидалось исключение " + type.getSimpleName());
    }
    private static void check(String name, Runnable test) {
        try { test.run(); passed++; System.out.println("PASS: " + name); }
        catch (Throwable ex) { failed++; System.out.println("FAIL: " + name + " — " + ex.getMessage()); }
    }
}
