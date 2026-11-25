CREATE DATABASE db_library_management;
USE db_library_management;

CREATE TABLE borrow_cards (
card_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
book_title VARCHAR(150) NOT NULL,
borrower_name VARCHAR(100) NOT NULL,
borrow_date TIMESTAMP NOT NULL,
return_deadline TIMESTAMP NOT NULL,
quantity INT NOT NULL,
status VARCHAR(30) NOT NULL);

-- Lấy danh sách phiếu mượn
DELIMITER //
CREATE PROCEDURE get_borrow_cards_list()
BEGIN
	SELECT * FROM borrow_cards ORDER BY card_id;
END //
DELIMITER ;

 -- Thêm mới phiếu mượn 
DELIMITER //
CREATE PROCEDURE insert_borrow_card(
IN p_book_title VARCHAR(150),
IN p_borrower_name VARCHAR(100),
IN p_borrow_date TIMESTAMP,
IN p_return_deadline TIMESTAMP,
IN p_quantity INT,
IN p_status VARCHAR(30)
)
BEGIN
	INSERT INTO borrow_cards (book_title,borrower_name,borrow_date,return_deadline,quantity,status)
	VALUES (p_book_title,p_borrower_name,p_borrow_date,p_return_deadline,p_quantity,p_status);
END //
DELIMITER ;

-- Lấy danh sách theo tên độc giả
 DELIMITER //
 CREATE PROCEDURE get_borrow_card_by_borrower_name(IN p_borrower_name VARCHAR(100))
 BEGIN
	SELECT * FROM borrow_cards
	WHERE LOWER(borrower_name) LIKE CONCAT('%', LOWER(p_borrower_name), '%')
	ORDER BY card_id;
 END //
 DELIMITER ;
 
 -- Cập nhật thông tin phiếu mượn theo card_id
 DELIMITER //
 CREATE PROCEDURE update_borrow_card(
IN p_card_id BIGINT,
IN p_book_title VARCHAR(150),
IN p_borrower_name VARCHAR(100),
IN p_borrow_date TIMESTAMP,
IN p_return_deadline TIMESTAMP,
IN p_quantity INT,
IN p_status VARCHAR(30),
OUT p_affected_rows INT
)
BEGIN
UPDATE borrow_cards
	SET book_title = p_book_title,
	borrower_name = p_borrower_name,
    borrow_date = p_borrow_date,
    return_deadline = p_return_deadline,
    quantity = p_quantity,
    status = p_status
	WHERE card_id = p_card_id;
	SET p_affected_rows = ROW_COUNT();
END //
DELIMITER ;

-- Xóa phiếu mượn theo card_id
DELIMITER //
CREATE PROCEDURE delete_borrow_card_by_card_id(
IN p_card_id BIGINT,
OUT p_affected_rows INT
)
BEGIN
	DELETE FROM borrow_cards WHERE card_id = p_card_id;
	SET p_affected_rows = ROW_COUNT(); 
END //
DELIMITER ;

-- Tìm kiếm phiếu mượn theo tên sách
DELIMITER //
CREATE PROCEDURE search_by_book_title (IN p_book_title VARCHAR(150))
BEGIN
	SELECT * FROM borrow_cards
    WHERE LOWER(book_title) LIKE CONCAT('%', LOWER(p_book_title), '%')
	ORDER BY card_id;
END //
DELIMITER ;

-- Dữ liệu mẫu
 INSERT INTO borrow_cards (book_title, borrower_name, borrow_date, return_deadline, quantity, status)
	VALUES ('Clean Code', 'Nguyen Van An', '2026-11-01', '2026-11-15', 1, 'Borrowing'),
			('Dac Nhan Tam', 'Tran Thi Binh', '2026-11-03', '2026-11-17', 2, 'Returned'),
			('Lap Trinh Java Co Ban', 'Le Minh Cuong', '2026-11-05', '2026-11-19', 1, 'Borrowing'),
			('Nha Gia Kim', 'Pham Thu Ha', '2026-10-20', '2026-11-03', 1, 'Overdue'),
			('Co So Du Lieu MySQL', 'Hoang Gia Huy', '2026-11-10', '2026-11-24', 3, 'Borrowing');
-- Thử
CALL  get_borrow_cards_list();
CALL insert_borrow_card('Design Patterns', 'Do Quang Minh', '2026-11-12', '2026-11-26', 1, 'Borrowing');
CALL get_borrow_card_by_borrower_name('gia');
CALL update_borrow_card(1, 'Clean Code', 'Nguyen Van An', '2026-11-01', '2026-11-15', 1, 'Returned',@rows); SELECT @rows;
CALL delete_borrow_card_by_card_id(1, @rows); SELECT @rows;
CALL search_by_book_title('trinh');