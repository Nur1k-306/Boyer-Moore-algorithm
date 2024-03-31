import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BoyerMoore {

    // Метод для построения таблицы плохих символов
    private int[] buildBadCharTable(String pattern) {
        int[] badChar = new int[256]; // Создание массива размером 256 для всех ASCII символов
        Arrays.fill(badChar, -1); // Заполнение массива значениями -1 (по умолчанию)

        // Заполнение массива значением индекса последнего вхождения каждого символа из шаблона
        for (int i = 0; i < pattern.length(); i++) {
            badChar[(int) pattern.charAt(i)] = i;
        }

        return badChar; // Возвращаем построенную таблицу плохих символов
    }

    // Метод для поиска всех вхождений подстроки в тексте с использованием алгоритма Бойера-Мура
    public List<Integer> search(String text, String pattern, int[] iterations) {
        List<Integer> positions = new ArrayList<>(); // Список для хранения позиций найденных подстрок
        int n = text.length(); // Длина текста
        int m = pattern.length(); // Длина подстроки
        int[] badChar = buildBadCharTable(pattern); // Таблица плохих символов

        int shift = 0; // Смещение, с которого начинается сравнение
        int iterationCount = 0; // Счетчик итераций

        // Пока смещение shift меньше или равно разности длин текста и подстроки
        while (shift <= n - m) {
            int j = m - 1; // Индекс последнего символа подстроки

            iterationCount++; // Увеличиваем счетчик итераций

            // Пока j >= 0 и символы подстроки совпадают с символами текста на позициях shift + j
            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--; // Уменьшаем j
                iterationCount++; // Увеличиваем счетчик итераций
            }

            // Если j < 0, значит все символы подстроки совпали с соответствующими символами текста
            if (j < 0) {
                positions.add(shift); // Добавляем позицию в список
                // Вычисляем новое смещение с помощью таблицы плохих символов
                shift += (shift + m < n) ? m - badChar[text.charAt(shift + m)] : 1;
            } else {
                // Иначе вычисляем новое смещение на основе максимума между 1 и разностью j и значения из таблицы плохих символов
                shift += Math.max(1, j - badChar[text.charAt(shift + j)]);
            }
        }

        iterations[0] = iterationCount; // Сохраняем количество итераций
        return positions; // Возвращаем список позиций найденных подстрок
    }

    // Метод для генерации тестовых данных
    public static void generateTestData(int numberOfSets) {
        int minLength = 100; // Минимальная длина случайной строки
        int maxLength = 10000; // Максимальная длина случайной строки

        try {
            // Генерируем и записываем в файлы случайные строки
            for (int i = 0; i < numberOfSets; i++) {
                int length = (int) (Math.random() * (maxLength - minLength + 1) + minLength); // Случайная длина строки
                String randomString = generateRandomString(length); // Генерация случайной строки
                String fileName = "input_" + i + ".txt"; // Имя файла
                FileWriter writer = new FileWriter(fileName); // Создание FileWriter для записи в файл
                writer.write(randomString); // Запись строки в файл
                writer.close(); // Закрытие FileWriter
            }
        } catch (IOException e) {
            e.printStackTrace(); // Обработка исключения ввода-вывода
        }
    }

    // Метод для генерации случайной строки заданной длины
    public static String generateRandomString(int length) {
        Random random = new Random(); // Создание объекта Random
        StringBuilder sb = new StringBuilder(length); // Создание StringBuilder для построения строки
        // Генерация случайных символов и добавление их в StringBuilder
        for (int i = 0; i < length; i++) {
            char randomChar = (char) (random.nextInt(26) + 'a'); // Генерация случайного символа в диапазоне 'a'-'z'
            sb.append(randomChar); // Добавление символа в StringBuilder
        }
        return sb.toString(); // Возвращаем сгенерированную строку
    }

    // Метод для измерения производительности алгоритма
    public static void measureAlgorithmPerformance() {
        try {
            int[] iterations = new int[1]; // Массив для хранения количества итераций

            // Для каждого из 100 файлов ввода
            for (int i = 0; i < 100; i++) {
                BufferedReader reader = new BufferedReader(new FileReader("input_" + i + ".txt")); // Создание BufferedReader для чтения файла
                String text = reader.readLine(); // Считывание строки из файла

                int patternLength = (int) (Math.random() * (text.length() - 1) + 1); // Случайная длина подстроки от 1 до длины текста
                int patternIndex = (int) (Math.random() * (text.length() - patternLength)); // Случайный индекс для начала подстроки

                String pattern = text.substring(patternIndex, patternIndex + patternLength); // Выделение случайной подстроки
                System.out.println("Ищется подстрока для input_" + i + ".txt: " + pattern); // Вывод сообщения о том, какая подстрока ищется
                BoyerMoore bm = new BoyerMoore(); // Создание объекта BoyerMoore
                double startTime = System.nanoTime(); // Засекаем начальное время выполнения
                List<Integer> positions = bm.search(text, pattern, iterations); // Выполняем поиск подстроки
                double endTime = System.nanoTime(); // Засекаем конечное время выполнения
                double duration = (endTime - startTime)/1000000; // Вычисляем продолжительность выполнения в миллисекундах

                if (!positions.isEmpty()) { // Если подстрока найдена
                    // Выводим информацию о времени выполнения, количестве итераций и найденных позициях
                    System.out.print("Время выполнения для input_" + i + ".txt (" + text.length() + " символов): " + duration + " мс. ");
                    System.out.println("Количество итераций: " + iterations[0] + ", Найденные позиции: " + positions+"\n");
                } else { // Если подстрока не найдена
                    // Выводим информацию о времени выполнения, количестве итераций и сообщение о том, что подстрока не найдена
                    System.out.print("Время выполнения для input_" + i + ".txt (" + text.length() + " символов): " + duration + " мс. ");
                    System.out.println("Количество итераций: " + iterations[0] + ", Подстрока не найдена.\n");
                }

                reader.close(); // Закрываем BufferedReader
            }
        } catch (IOException e) { // Обработка исключения ввода-вывода
            e.printStackTrace(); // Вывод стека вызовов исключения
        }
    }
}
