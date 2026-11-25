package services;

import databases.Db;
import model.BorrowCard;
import utils.InputUtil;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

public class BorrowCardServices {

    // 1. Lấy danh sách tất cả các phiếu mượn
    public void getBorrowCard() {
        String querySql = "{CALL get_borrow_cards_list()}";
        try {
            CallableStatement stmt = Db.getConn().prepareCall(querySql);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                int id = rs.getInt("card_id");
                String title = rs.getString("book_title");
                String borrowerName = rs.getString("borrower_name");
                LocalDateTime borrowDate = rs.getObject("borrow_date", LocalDateTime.class);
                LocalDateTime returnDeadline = rs.getObject("return_deadline", LocalDateTime.class);
                int quantity = rs.getInt("quantity");
                String status = rs.getString("status");

                BorrowCard card = new BorrowCard(id, title, borrowerName, borrowDate, returnDeadline, quantity, status);
                System.out.println(card);
            }

            if (count == 0) {
                System.out.println("Danh sách phiếu mượn trống!");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // 2. Thêm mới một phiếu mượn
    public void insertBorrowCard() {
        BorrowCard newCard = new BorrowCard();
        newCard.setBook_title(InputUtil.readBookTitle("Nhập tên sách mượn: "));
        newCard.setBorrower_name(InputUtil.readBorrowerName("Nhập tên độc giả mượn: "));
        newCard.setBorrow_date(InputUtil.readBorrowDate("Nhập ngày mượn (yyyy-MM-dd): "));
        newCard.setReturn_deadline(InputUtil.readReturnDeadline("Nhập hạn trả (yyyy-MM-dd): ", newCard.getBorrow_date()));
        newCard.setQuantity(InputUtil.readPositiveInt("Nhập số lượng mượn: "));
        newCard.setStatus(InputUtil.readBorrowStatus("Nhập trạng thái (Borrowing/Returned/Overdue): "));

        String insertSql = "{CALL insert_borrow_card(?, ?, ?, ?, ?, ?)}";

        try {
            CallableStatement stmt = Db.getConn().prepareCall(insertSql);
            stmt.setString(1, newCard.getBook_title());
            stmt.setString(2, newCard.getBorrower_name());
            stmt.setTimestamp(3, Timestamp.valueOf(newCard.getBorrow_date()));
            stmt.setTimestamp(4, Timestamp.valueOf(newCard.getReturn_deadline()));
            stmt.setInt(5, newCard.getQuantity());
            stmt.setString(6, newCard.getStatus());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Thêm phiếu thành công!");
            } else {
                System.out.println("Thêm phiếu thất bại!");
            }

            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm phiếu mới: " + e.getMessage());
        }
    }

    // 3. Cập nhật thông tin phiếu mượn theo card_id
    public void updateBorrowCard() {
        int id = InputUtil.readInt("Nhập card_id cần cập nhật: ");

        try {
            // Kiểm tra phiếu có tồn tại không
            CallableStatement findStmt = Db.getConn().prepareCall("{CALL get_borrow_cards_list()}");
            ResultSet rs = findStmt.executeQuery();
            boolean found = false;
            while (rs.next()) {
                if (rs.getInt("card_id") == id) {
                    found = true;
                    BorrowCard current = new BorrowCard(
                            rs.getInt("card_id"),
                            rs.getString("book_title"),
                            rs.getString("borrower_name"),
                            rs.getObject("borrow_date", LocalDateTime.class),
                            rs.getObject("return_deadline", LocalDateTime.class),
                            rs.getInt("quantity"),
                            rs.getString("status")
                    );
                    System.out.println("Thông tin hiện tại: " + current);
                }
            }
            rs.close();
            findStmt.close();

            if (!found) {
                System.out.println("Không tìm thấy phiếu mượn có card_id = " + id);
                return;
            }

            System.out.println("Nhập thông tin mới:");
            BorrowCard bc = new BorrowCard();
            bc.setBook_title(InputUtil.readBookTitle("Nhập tên sách mượn: "));
            bc.setBorrower_name(InputUtil.readBorrowerName("Nhập tên độc giả mượn: "));
            bc.setBorrow_date(InputUtil.readBorrowDate("Nhập ngày mượn (yyyy-MM-dd): "));
            bc.setReturn_deadline(InputUtil.readReturnDeadline("Nhập hạn trả (yyyy-MM-dd): ", bc.getBorrow_date()));
            bc.setQuantity(InputUtil.readPositiveInt("Nhập số lượng mượn: "));
            bc.setStatus(InputUtil.readBorrowStatus("Nhập trạng thái (Borrowing/Returned/Overdue): "));

            CallableStatement stmt = Db.getConn().prepareCall("{CALL update_borrow_card(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setLong(1, id);
            stmt.setString(2, bc.getBook_title());
            stmt.setString(3, bc.getBorrower_name());
            stmt.setTimestamp(4, Timestamp.valueOf(bc.getBorrow_date()));
            stmt.setTimestamp(5, Timestamp.valueOf(bc.getReturn_deadline()));
            stmt.setInt(6, bc.getQuantity());
            stmt.setString(7, bc.getStatus());
            stmt.registerOutParameter(8, Types.INTEGER);
            stmt.execute();

            if (stmt.getInt(8) > 0) {
                System.out.println("Cập nhật thành công!");
            } else {
                System.out.println("Không có thay đổi nào.");
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // 4. Xóa phiếu mượn theo card_id
    public void deleteBorrowCardById() {
        int id = InputUtil.readInt("Nhập card_id cần xóa: ");

        try {
            // Kiểm tra phiếu có tồn tại không
            CallableStatement findStmt = Db.getConn().prepareCall("{CALL get_borrow_cards_list()}");
            ResultSet rs = findStmt.executeQuery();
            boolean found = false;
            while (rs.next()) {
                if (rs.getInt("card_id") == id) {
                    found = true;
                    BorrowCard current = new BorrowCard(
                            rs.getInt("card_id"),
                            rs.getString("book_title"),
                            rs.getString("borrower_name"),
                            rs.getObject("borrow_date", LocalDateTime.class),
                            rs.getObject("return_deadline", LocalDateTime.class),
                            rs.getInt("quantity"),
                            rs.getString("status")
                    );
                    System.out.println("Phiếu sẽ xóa: " + current);
                }
            }
            rs.close();
            findStmt.close();

            if (!found) {
                System.out.println("Không tìm thấy phiếu mượn có card_id = " + id);
                return;
            }

            String confirm = InputUtil.readString("Bạn có chắc chắn muốn xóa? (Y/N): ", "[YyNn]", "Chỉ nhập Y hoặc N!");
            if (confirm.equalsIgnoreCase("N")) {
                System.out.println("Đã hủy xóa.");
                return;
            }

            CallableStatement stmt = Db.getConn().prepareCall("{CALL delete_borrow_card_by_card_id(?, ?)}");
            stmt.setLong(1, id);
            stmt.registerOutParameter(2, Types.INTEGER);
            stmt.execute();

            if (stmt.getInt(2) > 0) {
                System.out.println("Xóa thành công!");
            } else {
                System.out.println("Xóa thất bại!");
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // 5. Tìm kiếm phiếu mượn theo tên độc giả
    public void getBorrowCardByBorrowerName() {
        String borrowerName = InputUtil.readBorrowerName("Nhập tên độc giả cần tìm: ");

        try {
            CallableStatement stmt = Db.getConn().prepareCall("{CALL get_borrow_card_by_borrower_name(?)}");
            stmt.setString(1, borrowerName);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                int id = rs.getInt("card_id");
                String title = rs.getString("book_title");
                String name = rs.getString("borrower_name");
                LocalDateTime borrowDate = rs.getObject("borrow_date", LocalDateTime.class);
                LocalDateTime returnDeadline = rs.getObject("return_deadline", LocalDateTime.class);
                int quantity = rs.getInt("quantity");
                String status = rs.getString("status");

                BorrowCard card = new BorrowCard(id, title, name, borrowDate, returnDeadline, quantity, status);
                System.out.println(card);
            }

            if (count == 0) {
                System.out.println("Không tìm thấy kết quả nào!");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // 6. Tìm kiếm phiếu mượn theo tên sách
    public void searchByBookTitle() {
        String bookTitle = InputUtil.readBookTitle("Nhập tên sách cần tìm: ");

        try {
            CallableStatement stmt = Db.getConn().prepareCall("{CALL search_by_book_title(?)}");
            stmt.setString(1, bookTitle);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                int id = rs.getInt("card_id");
                String title = rs.getString("book_title");
                String borrowerName = rs.getString("borrower_name");
                LocalDateTime borrowDate = rs.getObject("borrow_date", LocalDateTime.class);
                LocalDateTime returnDeadline = rs.getObject("return_deadline", LocalDateTime.class);
                int quantity = rs.getInt("quantity");
                String status = rs.getString("status");

                BorrowCard card = new BorrowCard(id, title, borrowerName, borrowDate, returnDeadline, quantity, status);
                System.out.println(card);
            }

            if (count == 0) {
                System.out.println("Không tìm thấy kết quả nào!");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }
}