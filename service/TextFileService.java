package Asm04.service;

import Asm04.exception.CustomerIdNotValidException;
import Asm04.model.Customer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextFileService {
    public static boolean IS_DEBUG = false;
    public static final String COMMA_DELIMITER = ",";

    public synchronized static List<Customer> readFiletxt(String fileName) {
        List<Customer> customers = new ArrayList<>();
        try {
            // Mở tệp để đọc
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";

            // Đọc từng dòng trong tệp
            while (true) {
                line = bufferedReader.readLine();
                if (line == null) {
                    // Nếu không còn dòng nào, thoát khỏi vòng lặp
                    break;
                } else {
                    if (line.equals("")) {
                        // Nếu dòng trống, bỏ qua và đọc dòng tiếp theo
                        continue;
                    } else {
                        // Tách chuỗi thành mảng các phần tử dựa trên dấu phẩy (dấu phẩy là phân cách giữa customerId và customerName)
                        String[] str = line.split(COMMA_DELIMITER);
                        String customerId = str[0];
                        String customerName = str[1];

                        // Thử tạo một đối tượng Customer từ customerId và customerName, có thể ném ra ngoại lệ nếu customerId không hợp lệ
                        try {
                            customers.add(new Customer(customerName, customerId));
                        } catch (CustomerIdNotValidException exception) {
                            System.out.println(exception.getMessage());
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // Xử lý nếu tệp không tồn tại
            System.out.println("Đường dẫn chưa chính xác");
        } catch (IOException e) {
            // Xử lý nếu có lỗi trong quá trình đọc tệp
            throw new RuntimeException(e);
        }
        return customers; // Trả về danh sách các đối tượng Customer đã đọc từ tệp
    }

}
