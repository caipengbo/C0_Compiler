import compiler.interpreter.Interpreter;

import java.io.File;
import java.util.Scanner;

/**
 * Title：主程序 —— 解释器
 * Description：
 * Created by Myth on 6/1/2017.
 */
public class Interpret {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入解释程序文件名：");
        String sourceFileName = scanner.next();
        String currentPath = System.getProperty("user.dir"); // 获取当前路径
        Interpreter interpreter = new Interpreter();
        try {
            interpreter.openFile(currentPath + File.separator + sourceFileName);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("找不到 '"+ sourceFileName +"' 解释文件");
        }
        try {
            System.out.println("运行中...");
            interpreter.interpret();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("解释出错！！！");
        }
        scanner.close();
    }
}
