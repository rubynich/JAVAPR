package ru.edu.pr03;


public class ApprovalRouter {
    public static final long FINANCE_LIMIT = 500_000L;

    public static final int URGENT_LIMIT_PERCENT = 35;

    private long urgentLimit() {
        return FINANCE_LIMIT * URGENT_LIMIT_PERCENT / 100;
    }

    public ApprovalRoute route(long amount, boolean urgent, RiskLevel risk, boolean containsPersonalData) {
        validate(amount, risk);

        // Приоритет: персональные данные -> высокий риск -> финансы -> срочная заявка -> обычный маршрут.
        if (containsPersonalData) {
            return ApprovalRoute.SECURITY_REVIEW;
        }
        if (risk == RiskLevel.HIGH) {
            return ApprovalRoute.DIRECTOR_REVIEW;
        }
        if (amount >= FINANCE_LIMIT) {
            return ApprovalRoute.FINANCE_REVIEW;
        }
        if (urgent && amount < urgentLimit()) {
            return ApprovalRoute.FAST_TRACK;
        }
        return ApprovalRoute.STANDARD;
    }

    private void validate(long amount, RiskLevel risk) {
        if (amount < 0) {
            throw new IllegalArgumentException("Сумма заявки не может быть отрицательной");
        }
        if (risk == null) {
            throw new IllegalArgumentException("Уровень риска не задан");
        }
    }
}
