package compiler.interpreter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    private List<Instruction> instructionList; //下标从1开始

    /**
     * 运行栈（每个函数都有初始三个位置，0：保存基地址、也用来保存返回值；1：调用者的地址；2：返回地址）
     */
    private int[] runtimeStack;

    /**
     * 位置指针（注意是指令位置，还是栈中位置）
     */
    private int currentInstructionPosition; //当前正在执行的解释程序位置
    private int top; //栈顶指针
    private int basePosition;   //每个函数的在栈中的初始位置

    public Interpreter() {
        runtimeStack = new int[MAX_SIZE];
        runtimeStack[2] = MAX_SIZE;  //runtimeStack[2]是主函数的返回地址，设置为一个大数字，用来控制主函数的结束
        currentInstructionPosition = 0;
        top = 0;
        basePosition = 0;
    }
    public void setInstructionList(List<Instruction> instructionList) {
        this.instructionList = instructionList;
    }

    /**
     * 打开解释程序文件,为指令列表赋值
     * @param pathname  解释程序文件的路径
     * @throws Exception
     * @return 失败返回false
     */
    public void openFile(String pathname) throws Exception {
        File file = new File(pathname);
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        instructionList = new ArrayList<>();
        //为了使instructionList 的下标从1开始，添加一个占位元素
        instructionList.add(new Instruction());
        while((line = bufferedReader.readLine())!=null) {
            String[] str = line.split(" ");
            Instruction instruction = new Instruction();
            instruction.setName(InstructionType.valueOf(str[0].toUpperCase()));
            instruction.setLayer(Integer.parseInt(str[1]));
            instruction.setThird(Integer.parseInt(str[2]));
            instructionList.add(instruction);
        }
        if (instructionList.size() > MAX_SIZE) {
            throw new Exception("解释程序过长！");
        }
    }

    /**
     * 打印解释程序列表,行号从1开始
     */
    public void printInstructionList() {
        int i = 0;
        for (Instruction instruction : instructionList) {
            System.out.println(i + ": " + instruction.toString());
            i++;
        }
    }

    /**
     * 将解释程序写入文件
     * @param file 指定文件
     * @throws IOException
     */
    public void writeToFile(File file) throws IOException {
        BufferedWriter bufferedWriter = null;
        if (!file.exists()) {
            file.createNewFile();
        }
        bufferedWriter = new BufferedWriter(new FileWriter(file));
        //parse产生的指令程序初始是 nul 0 0
        for (int i = 1; i < instructionList.size(); i++) {
            bufferedWriter.write(instructionList.get(i).toString() + "\r\n");
        }
        bufferedWriter.close();
    }

    /**
     * 解释 目标程序
     * @throws Exception
     */
    public void interpret() throws Exception {
        //初始指针位置
        Scanner scanner = new Scanner(System.in); //供输入指令RED使用
        int size = instructionList.size();
        currentInstructionPosition = 1;
        basePosition = 0;
        top = 0;
        while (currentInstructionPosition <= size) {
            Instruction instruction = instructionList.get(currentInstructionPosition);
            switch (instruction.getName()) {
                case LIT: {  //LIT 0 a	将常数值取到栈顶，a为常数值
                    runtimeStack[top] = instruction.getThird();
                    top++;
                    currentInstructionPosition++;
                    break;
                }
                case LOD: { //LOD t a 将变量值取到栈顶,a为相对地址,t为层数
                    int absolutePosition = getAbsolutePosition(basePosition,instruction.getLayer(),instruction.getThird());
                    runtimeStack[top] = runtimeStack[absolutePosition];
                    top++;
                    currentInstructionPosition++;
                    break;
                }
                case STO: { //STO t a 将栈顶内容送入某变量单元中，a为相对地址，t为层数
                    int absolutePosition = getAbsolutePosition(basePosition,instruction.getLayer(),instruction.getThird());
                    runtimeStack[absolutePosition] = runtimeStack[top-1];
                    currentInstructionPosition++;
                    break;
                }
                case CAL: { //CAL 0 a 调用函数，a为函数地址
                    //每个函数块分配的区域，都保存着 1.基地址（分配的区域开始位置）2.调用者的基地址 3.返回地址
                    runtimeStack[top] = top;  //基地址也用来保存返回值
                    runtimeStack[top+1] = basePosition; //调用者的基地址
                    runtimeStack[top+2] = currentInstructionPosition +1; //返回地址
                    basePosition = top;  //基地址指针改变
                    currentInstructionPosition = instruction.getThird(); // 当前位置指针更新（注意下标）
                    break;
                }
                case INT: { //INT 0 a	在运行栈中为被调用的过程开辟a个单元的数据区
                    top +=instruction.getThird();
                    currentInstructionPosition++;
                    break;
                }
                case JMP: { //JMP 0 a	无条件跳转至a地址
                    currentInstructionPosition = instruction.getThird();
                    break;
                }
                case JPC: { //JPC 0 a	条件跳转，当栈顶值为0，则跳转至a地址，否则顺序执行
                    if (runtimeStack[top-1] == 0) {
                        currentInstructionPosition = instruction.getThird();
                    } else {
                        currentInstructionPosition++;
                    }
                    break;
                }
                case ADD: { //次栈顶与栈顶相加,退两个栈元素，结果值进栈
                    runtimeStack[top-2] = runtimeStack[top-2] + runtimeStack[top-1];
                    top--;
                    currentInstructionPosition++;
                    break;
                }
                case SUB: { //减法
                    runtimeStack[top-2] = runtimeStack[top-2] - runtimeStack[top-1];
                    top--;
                    currentInstructionPosition++;
                    break;
                }
                case MUL: { //乘法
                    runtimeStack[top-2] = runtimeStack[top-2] * runtimeStack[top-1];
                    top--;
                    currentInstructionPosition++;
                    break;
                }
                case DIV: { //除法
                    runtimeStack[top-2] = runtimeStack[top-2] / runtimeStack[top-1];
                    top--;
                    currentInstructionPosition++;
                    break;
                }
                case RED: { //RED 0 0	从命令行读入一个输入置于栈顶
                    int value = scanner.nextInt();
                    runtimeStack[top] = value;
                    top++;
                    currentInstructionPosition++;
                    break;
                }
                case WRT: { //WRT 0 0	栈顶值输出至屏幕并换行
                    int value = runtimeStack[top-1];
                    System.out.println(value);
                    currentInstructionPosition++;
                    break;
                }
                case RET: { //RET 0 0	函数调用结束后,返回调用点并退栈
                    if(instruction.getThird() == 0) {   //没有返回值的退栈
                        currentInstructionPosition = runtimeStack[basePosition+2];
                        top = basePosition;
                        basePosition = runtimeStack[basePosition+1];
                    } else { //TODO 扩展的指令 RET 0 1 有返回值的 退栈 ，少退一个(返回值)
                        currentInstructionPosition = runtimeStack[basePosition+2];
                        top = basePosition+1;  // basePosition位置 保存的是返回值
                        basePosition = runtimeStack[basePosition+1];
                    }
                    break;
                }
            }
            if(top >= MAX_SIZE) {
                throw new Exception("C0虚拟机运行栈溢出！！");
            }
        }
        scanner.close();
        return;
    }
    /**
     * 根据层数、相对位置获得绝对位置
     * @param layer 0最外层（全局变量）  1当前层
     * @param relativePosition
     * @return
     */
    private int getAbsolutePosition(int basePosition, int layer, int relativePosition) {
        if (layer == 0) {
            return relativePosition;
        } else {
            return basePosition + relativePosition;
        }
    }
}