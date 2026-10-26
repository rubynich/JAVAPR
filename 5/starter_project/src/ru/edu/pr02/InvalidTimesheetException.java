package ru.edu.pr02;

/** Контролируемая ошибка некорректной строки табеля (сообщение содержит позицию, если она известна). */
public class InvalidTimesheetException extends IllegalArgumentException {
    public InvalidTimesheetException(String message) {
        super(message);
    }
}
