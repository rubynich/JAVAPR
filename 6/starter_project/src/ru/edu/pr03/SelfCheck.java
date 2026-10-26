package ru.edu.pr03;


public class SelfCheck {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        ApprovalRouter router = new ApprovalRouter();
        check("обычная заявка", router.route(120_000, false, RiskLevel.LOW, false), ApprovalRoute.STANDARD);
        check("срочная ниже 175000", router.route(174_999, true, RiskLevel.LOW, false), ApprovalRoute.FAST_TRACK);
        check("срочная ровно 175000", router.route(175_000, true, RiskLevel.LOW, false), ApprovalRoute.STANDARD);
        check("сумма 499999", router.route(499_999, false, RiskLevel.LOW, false), ApprovalRoute.STANDARD);
        check("сумма 500000", router.route(500_000, false, RiskLevel.LOW, false), ApprovalRoute.FINANCE_REVIEW);
        check("сумма 500001", router.route(500_001, false, RiskLevel.LOW, false), ApprovalRoute.FINANCE_REVIEW);
        check("персональные данные важнее срочности", router.route(10_000, true, RiskLevel.LOW, true), ApprovalRoute.SECURITY_REVIEW);
        check("высокий риск", router.route(10_000, true, RiskLevel.HIGH, false), ApprovalRoute.DIRECTOR_REVIEW);
        check("персональные данные важнее высокого риска", router.route(600_000, false, RiskLevel.HIGH, true), ApprovalRoute.SECURITY_REVIEW);
        checkThrows("отрицательная сумма", () -> router.route(-1, false, RiskLevel.LOW, false));
        checkThrows("не задан риск", () -> router.route(100, false, null, false));
        System.out.println("Итог: " + passed + " пройдено, " + failed + " ошибок.");
        if (failed > 0) System.exit(1);
    }

    private static void check(String name, ApprovalRoute actual, ApprovalRoute expected) {
        if (actual == expected) {
            passed++; System.out.println("PASS: " + name + " -> " + actual);
        } else {
            failed++; System.out.println("FAIL: " + name + "; ожидалось " + expected + ", получено " + actual);
        }
    }

    private static void checkThrows(String name, Runnable action) {
        try { action.run(); failed++; System.out.println("FAIL: " + name + "; исключение не возникло"); }
        catch (IllegalArgumentException expected) { passed++; System.out.println("PASS: " + name + " -> контролируемая ошибка"); }
    }
}
