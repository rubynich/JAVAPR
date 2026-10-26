package ru.edu.pr02;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/** Обрабатывает файл с табелями (по умолчанию data/sample.csv): одна строка файла - один табель. */
public class Main {
    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String path = args.length > 0 ? args[0] : "data/sample.csv";
        List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            out.println((i + 1) + ") [" + lines.get(i) + "] -> " + TimesheetAnalyzer.buildReport(lines.get(i)));
        }
    }
}
