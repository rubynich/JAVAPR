package ru.edu.pr05;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Заявка на командировку. Объект самостоятельно защищает допустимость своих состояний. */
public class BusinessTrip {
    /** Индивидуальный вариант 6: завершить командировку можно не позднее чем через 35 дней после даты окончания. */
    public static final long COMPLETION_GRACE_DAYS = 35;

    private final String id;
    private final LocalDate start;
    private final LocalDate end;
    private TripStatus status = TripStatus.DRAFT;
    private String rejectionReason;

    public BusinessTrip(String id, LocalDate start, LocalDate end) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Идентификатор заявки не должен быть пустым");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("Даты начала и окончания обязательны");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }
        this.id = id.trim();
        this.start = start;
        this.end = end;
    }

    public void submit() {
        requireStatus(TripStatus.DRAFT, "Отправить можно только черновик");
        // Обязательные поля проверены конструктором и неизменяемы (final).
        status = TripStatus.SUBMITTED;
    }

    public void approve() {
        requireStatus(TripStatus.SUBMITTED, "Согласовать можно только отправленную заявку");
        status = TripStatus.APPROVED;
    }

    public void reject(String reason) {
        requireStatus(TripStatus.SUBMITTED, "Отклонить можно только отправленную заявку");
        if (reason == null || reason.trim().length() < 10) {
            throw new IllegalArgumentException("Причина отклонения должна содержать минимум 10 символов");
        }
        rejectionReason = reason.trim();
        status = TripStatus.REJECTED;
    }

    public void cancel() {
        if (status != TripStatus.DRAFT && status != TripStatus.SUBMITTED && status != TripStatus.APPROVED) {
            throw new IllegalStateException("Отменить заявку в состоянии " + status + " нельзя");
        }
        status = TripStatus.CANCELLED;
    }

    public void complete(LocalDate today) {
        requireStatus(TripStatus.APPROVED, "Завершить можно только согласованную заявку");
        if (today == null) {
            throw new IllegalArgumentException("Текущая дата не должна быть null");
        }
        if (!today.isAfter(end)) {
            throw new IllegalStateException("Завершить командировку можно только после даты окончания");
        }
        long daysAfterEnd = ChronoUnit.DAYS.between(end, today);
        if (daysAfterEnd > COMPLETION_GRACE_DAYS) {
            throw new IllegalStateException("Завершение допускается не позднее 35 дней после даты окончания");
        }
        status = TripStatus.COMPLETED;
    }

    private void requireStatus(TripStatus expected, String message) {
        if (status != expected) {
            throw new IllegalStateException(message + ". Текущий статус: " + status);
        }
    }

    public String getId() { return id; }
    public LocalDate getStart() { return start; }
    public LocalDate getEnd() { return end; }
    public TripStatus getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
}
