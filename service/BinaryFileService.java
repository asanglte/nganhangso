package Asm04.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BinaryFileService implements Serializable {
    public static boolean IS_DEBUG = false;

    public static <T> List<T> readFile(String fileName) {
        List<T> objects = new ArrayList<>();
        try (ObjectInputStream file = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {
            boolean eof = false;

            // Đọc từng đối tượng từ tệp
            while (!eof) {
                try {
                    // Đọc một đối tượng từ tệp
                    T object = (T) file.readObject();
                    objects.add(object);
                } catch (EOFException e) {
                    // Khi đã đọc hết tệp, EOFException sẽ xảy ra, và ta sẽ thoát vòng lặp
                    eof = true;
                }
            }
        } catch (EOFException e) {
            // Nếu không có đối tượng nào trong tệp, trả về danh sách rỗng
            return new ArrayList<>();
        } catch (IOException io) {
            // Xử lý nếu có lỗi trong quá trình đọc tệp
            System.out.println("IO Exception" + io.getMessage());
        } catch (ClassNotFoundException e) {
            // Xử lý nếu không tìm thấy lớp đối tượng trong tệp
            System.out.println("ClassNotFoundException :" + e.getMessage());
        }
        return objects; // Trả về danh sách các đối tượng đã đọc từ tệp
    }

    public static <T> void writeFile(String fileName, List<T> objects) {
        try (ObjectOutputStream file = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))) {

            // Ghi từng đối tượng trong danh sách vào tệp
            for (T object : objects) {
                file.writeObject(object);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
