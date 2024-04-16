package Asm04;

import Asm04.model.DigitalBank;

import java.util.Scanner;

public class Asm04 {
    private static final int EXIT_COMMAND_CODE = 0;
    private static final int EXIT_ERRO_CODE = -1;
    private static final Scanner scanner = new Scanner(System.in);
    private static final DigitalBank activeBank = new DigitalBank();
    public static final String AUTHOR = "FX21645";
    public static final String VERSION_4 = "4.0.0";

    public static String getInputString(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    public static void main(String[] args) {
        int chucNang = -1;
        do {
            display(AUTHOR, VERSION_4);
            System.out.print("Chuc nang: ");
            try {
                chucNang = scanner.nextInt();
                scanner.nextLine();
                System.out.println("+----------+----------+----------+----------+----------+");
            } catch (Exception e) {
                scanner.nextLine();
            }
            String customerId = null;
            String[] functionMessages = {
                    "",
                    "",
                    "",
                    "Thêm tài khoản ATM",
                    "Chuyển tiền",
                    "Rút tiền",
                    "Xem lịch sử giao dịch"
            };
            switch (chucNang) {
                case 1 -> {
                    activeBank.showCustomer();
                    chucNang = EXIT_ERRO_CODE;
                }
                case 2 -> {
                    try {
                        System.out.println("Nhập đường dẫn đến File");
                        System.out.print("src: ");
                        String fileName = scanner.nextLine();
                        activeBank.addCustomerFromtxtFile(fileName);
                    } catch (RuntimeException e) {
                        System.out.println("Đường dẫn không đúng");
                    }
                    chucNang = EXIT_ERRO_CODE;
                }
                case 3, 4, 5, 6 -> {
                    System.out.println("Bạn đang thực hiện chức năng: " + functionMessages[chucNang]);
                    customerId = getInputString("Vui lòng nhập CCCD: ");
                    switch (chucNang) {
                        case 3 -> activeBank.addSavingsAccount(scanner, customerId);
                        case 4 -> activeBank.transfer(scanner, customerId);
                        case 5 -> activeBank.withdraw(scanner, customerId);
                        case 6 -> activeBank.Transactions(customerId);
                    }
                    chucNang = EXIT_ERRO_CODE;
                    break;
                }
                case EXIT_COMMAND_CODE -> {
                    return;
                }
                default -> System.out.println("Chức năng không đúng vui lòng nhập lại.");
            }
        } while (true);
    }

    private static void display(String author, String version) {
        System.out.println("+----------+----------+----------+----------+----------+\n" +
                "| NGAN HANG DIEN TU | " + author + "@v" + version + "                   |\n" +
                "+----------+----------+----------+----------+----------+\n" +
                " 1. Xem danh sách khách hàng\n" +
                " 2. Nhập danh sách khách hàng\n" +
                " 3. Thêm tài khoản ATM\n" +
                " 4. Chuyển tiền\n" +
                " 5. Rút tiền\n" +
                " 6. Tra cứu lịch sử giao dịch\n" +
                " 0. Thoát\n" +
                "+----------+----------+----------+----------+----------+");
    }
}
