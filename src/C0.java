import compiler.common.Wrong;
import compiler.interpret.Instruction;
import compiler.interpret.Interpreter;
import compiler.parser.ProgramParser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class C0 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入C0源程序文件名：");
        String sourceFileName = scanner.next();
        String currentPath = System.getProperty("user.dir"); // 获取当前路径
        ProgramParser programParser = null;
        try {
            //不能在IntelliJ上使用以下路径
            programParser = new ProgramParser(currentPath + File.separator + sourceFileName);
            //IntelliJ IDEA路径
            //programParser = new ProgramParser("src/" + sourceFileName);
        } catch (Exception e) {
            System.out.println("找不到 '"+ sourceFileName +"' 源文件");
            scanner.close();
            return;
        }

        try {
            programParser.parseProgram();
        } catch (Exception e) {
            System.out.println("编译错误！");
            scanner.close();
            return;
        }

        List<Wrong> wrongList = programParser.getWrongList();
        List<Instruction> instructionList = programParser.getGeneratedInstructionList();

        //编译有错误时，打印错误
        if (wrongList.size() != 0) {
            for (Wrong wrong : wrongList) {
                System.out.println(wrong);
            }
        } else {
            System.out.print("是否输出指令至文件(instruction.txt) Y/N?");
            String choice;
            Interpreter interpreter = new Interpreter();
            interpreter.setInstructionList(instructionList);
            do {
                choice = scanner.next();
            } while (!"Y".equals(choice) && !"y".equals(choice) && !"N".equals(choice) && !"n".equals(choice));

            try {
                //将指令写入instruction.txt文件
                if ("Y".equals(choice) || "y".equals(choice)) {
                    File instructionFile = new File(currentPath + File.separator + "instruction.txt");
                    //File instructionFile = new File( "src/instruction.txt");
                    interpreter.writeToFile(instructionFile);
                }
            }  catch (IOException e){
                System.out.println("输出指令至文件失败！");
                e.printStackTrace();
            }
            System.out.println("运行...");
            try {
                interpreter.interpret();
            } catch (Exception e) {
                System.out.println("解释指令失败！");
            }

        }

    }



}