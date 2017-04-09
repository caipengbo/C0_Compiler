package compiler;

import compiler.nameenum.InstructionName;
import compiler.struct.Instruction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Title：解释器
 * Description：解释目标程序
 * Created by Myth on 4/6/2017
 */
public class Interpreter {
    /**
     * 程序栈大小
     */
    private static final int MAX_SIZE = 500;

    /**
     * 将要解释的指令列表
     */
    private List<Instruction> instructionList;

    /**
     * 运行栈
     */
    private int[] runtimeStack;

    /**
     * 栈
     */
    private int position;
    public Interpreter() {
        runtimeStack = new int[MAX_SIZE];
    }

    public void setInstructionList(List<Instruction> instructionList) {
        this.instructionList = instructionList;
    }

    /**
     * 打开解释程序文件,为指令列表赋值
     * @param pathname  解释程序文件(.itp)的路径
     * @return 失败返回false
     */
    public boolean open(String pathname ) {
        File file = new File(pathname);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        String line;
        instructionList = new ArrayList<>();
        try {
            while((line = bufferedReader.readLine())!=null) {
                String[] str = line.split(" ");
                Instruction instruction = new Instruction();
                instruction.setName(InstructionName.valueOf(str[0]));
                instruction.setLayerDiff(Integer.parseInt(str[1]));
                instruction.setThird(Integer.parseInt(str[2]));
                instructionList.add(instruction);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 打印解释程序列表,行号从1开始
     */
    public void printInstructionList() {
        int i = 1;
        for (Instruction instruction : instructionList) {
            System.out.println(i + ": " + instruction.toString());
            i++;
        }
    }

    /**
     * 将解释程序写入文件
     * @param file 指定文件
     * @return 失败返回false
     */
    public boolean writeToFile(File file) {
        BufferedWriter bufferedWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            int i = 1;
            for (Instruction instruction : instructionList) {
                bufferedWriter.write(i + ": " +instruction.toString() + "\n");
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
