import databases.Db;
import services.BorrowCardServices;
import utils.InputUtil;

    public static void main(String[] args) {

        Db db = new Db();
        BorrowCardServices service = new BorrowCardServices();
        int choice;

        do {
            System.out.println("================= LIBRARY MANAGEMENT =================");
            System.out.println("1. Danh sách tất cả phiếu mượn");
            System.out.println("2. Thêm mới phiếu mượn");
            System.out.println("3. Cập nhật thông tin phiếu mượn");
            System.out.println("4. Xóa phiếu mượn");
            System.out.println("5. Tìm kiếm phiếu mượn theo tên độc giả");
            System.out.println("6. Tìm kiếm phiếu mượn theo tên sách");
            System.out.println("7. Thoát");

            choice = InputUtil.readInt("Nhập lựa chọn của bạn: ", 1, 7);

            switch (choice) {
                case 1:
                    service.getBorrowCard();
                    break;
                case 2:
                    service.insertBorrowCard();
                    break;
                case 3:
                    service.updateBorrowCard();
                    break;
                case 4:
                    service.deleteBorrowCardById();
                    break;
                case 5:
                    service.getBorrowCardByBorrowerName();
                    break;
                case 6:
                    service.searchByBookTitle();
                    break;
                case 7:
                    System.out.println("Bạn đã thoát chương trình");
                    break;
            }
        } while (choice != 7);

    }