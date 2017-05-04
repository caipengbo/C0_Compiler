package compiler.test;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**其他测试
 * Created by Myth on 2017/5/4 0004.
 */
public class OtherTest {
    @Test
    public void test1() throws IOException {
        File file = new File("src/compiler/test/test2.txt");
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        List<String> list = new ArrayList<>();
        while((line = bufferedReader.readLine())!=null) {
            list.add(line);
        }
        for (String s : list) {
            for (int i = 0; i < s.length(); i++) {
                System.out.println((int)(s.charAt(i)));
            }
        }
    }
    @Test
    public void test2() {
        String s = ""; // 空串 , null 空对象
        if (s.isEmpty()) {
            System.out.println(s.length());
            System.out.println("空");
        } else {
            System.out.println("非空");
        }
    }
}
