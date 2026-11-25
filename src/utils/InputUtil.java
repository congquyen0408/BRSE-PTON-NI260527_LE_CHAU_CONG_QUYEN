package utils;

import model.BorrowCard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputUtil {
    private static final Scanner SCANNER = new Scanner(System.in);
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN);

    private InputUtil() {
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = SCANNER.nextLine();
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập vào một số nguyên hợp lệ!");
            }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Lỗi: Giá trị phải nằm trong khoảng từ " + min + " đến " + max + "!");
        }
    }

    public static int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Lỗi: Giá trị phải là số nguyên lớn hơn 0!");
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = SCANNER.nextLine();
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập vào một số thực hợp lệ!");
            }
        }
    }

    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input.trim();
            }
            System.out.println("Lỗi: Nội dung không được để trống!");
        }
    }

    public static String readString(String prompt, int maxLength) {
        while (true) {
            String input = readString(prompt);
            if (input.length() <= maxLength) {
                return input;
            }
            System.out.println("Lỗi: Độ dài tối đa là " + maxLength + " ký tự (hiện tại: " + input.length() + ")!");
        }
    }

    public static String readString(String prompt, String regex, String errorMessage) {
        while (true) {
            String input = readString(prompt);
            if (input.matches(regex)) {
                return input;
            }
            System.out.println("Lỗi: " + errorMessage);
        }
    }

    public static LocalDate readLocalDate(String prompt) {
        return readLocalDate(prompt, DEFAULT_DATE_PATTERN);
    }

    public static LocalDate readLocalDate(String prompt, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        while (true) {
            String input = readString(prompt);
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Lỗi: Sai định dạng ngày tháng! (Yêu cầu định dạng: " + pattern + ", ví dụ: 2026-11-25)");
            }
        }
    }

    public static LocalDateTime readBorrowDate(String prompt) {
        return readLocalDate(prompt).atStartOfDay();
    }

    public static LocalDateTime readReturnDeadline(String prompt, LocalDateTime borrowDate) {
        while (true) {
            LocalDateTime deadline = readLocalDate(prompt).atTime(LocalTime.of(23, 59, 59));
            if (deadline.isAfter(borrowDate)) {
                return deadline;
            }
            System.out.println("Lỗi: Hạn trả sách phải sau ngày mượn (" + borrowDate.toLocalDate().format(DATE_FORMATTER) + ")!");
        }
    }

    public static LocalDateTime readLocalDateTime(String prompt) {
        return readLocalDateTime(prompt, DEFAULT_DATE_TIME_PATTERN);
    }

    public static LocalDateTime readLocalDateTime(String prompt, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        while (true) {
            String input = readString(prompt);
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Lỗi: Sai định dạng ngày giờ! (Yêu cầu định dạng: " + pattern + ", ví dụ: 2026-11-25 20:00:00)");
            }
        }
    }

    public static String readBorrowStatus(String prompt) {
        while (true) {
            String raw = readString(prompt);
            if ("Borrowing".equalsIgnoreCase(raw)) {
                return "Borrowing";
            }
            if ("Returned".equalsIgnoreCase(raw)) {
                return "Returned";
            }
            if ("Overdue".equalsIgnoreCase(raw)) {
                return "Overdue";
            }
            System.out.println("Lỗi: Trạng thái chỉ được là Borrowing, Returned hoặc Overdue!");
        }
    }

    public static String readBookTitle(String prompt) {
        return readString(prompt, 150);
    }

    public static String readBorrowerName(String prompt) {
        return readString(prompt, 100);
    }

    public static BorrowCard readBorrowCard() {
        String bookTitle = readBookTitle("Nhập tên sách mượn: ");
        String borrowerName = readBorrowerName("Nhập tên độc giả mượn: ");
        LocalDateTime borrowDate = readBorrowDate("Nhập ngày mượn (yyyy-MM-dd): ");
        LocalDateTime returnDeadline = readReturnDeadline("Nhập hạn trả (yyyy-MM-dd): ", borrowDate);
        int quantity = readPositiveInt("Nhập số lượng mượn: ");
        String status = readBorrowStatus("Nhập trạng thái (Borrowing / Returned / Overdue): ");

        BorrowCard card = new BorrowCard();
        card.setBook_title(bookTitle);
        card.setBorrower_name(borrowerName);
        card.setBorrow_date(borrowDate);
        card.setReturn_deadline(returnDeadline);
        card.setQuantity(quantity);
        card.setStatus(status);
        return card;
    }

    public static void close() {
        SCANNER.close();
    }
}