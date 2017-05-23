package compiler.test;

import compiler.common.Symbol;
import compiler.parser.Function;
import org.junit.Test;

import java.io.*;
import java.util.*;

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

    @Test
    public void test3() {
        Map<Integer,Function> testMap = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            Function function = new Function("name"+i,true);
            testMap.put(i,function);
        }
        Function function = testMap.get(1);
        function.updateSize(100);
        //testMap.put(1,newString);
    }
    @Test
    public void test4() {
        //初始是false
        boolean[] a = new boolean[20];
        a[2] = true;
        for (int i = 0; i < 20; i++) {
            System.out.println(a[i]);
        }
    }
    @Test
    public void test5() {
        System.out.println(Symbol.values().length);
    }
    @Test
    public void test6() {
        //Junit无法实现控制台的输入
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        System.out.println(a);
        scanner.close();
    }
    @Test
    public void test7() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String s = bufferedReader.readLine();
        System.out.println(s);
    }
}
