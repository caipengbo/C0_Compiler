package compiler.parser;

import compiler.common.Symbol;
import compiler.common.Word;
import compiler.common.Wrong;
import compiler.interpret.Instruction;
import compiler.interpret.InstructionType;
import compiler.lexer.Lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static compiler.common.Symbol.ident;

/**
 * Title：程序处理
 * Description： <程序>->[<全局变量定义部分>] {<自定义函数定义部分>} <主函数>
 * Warning: 注意生成的指令是从位置1开始的
 * Created by Myth on 5/4/2017.
 */
public class ProgramParser {
    private Lexer lexer;
    private Word word; //每次去到的单词
    private List<Wrong> wrongList;
    //为方便定位变量与函数(名字作为Key)，使用Map表示 变量表与函数表
    private Map<String, Variable> variableMap;
    private Map<String, Function> functionMap;
   // private Map<String,>
    //TODO 考虑是否在开始加一条跳转到主函数指令
    /**
     * 生成的指令，指令从位置1开始！
     */
    private List<Instruction> generatedInstructions;
    private int globalCount = 0;
    // 用来判断是否属于某一语法单位
    private boolean[] statementBeginSymbolFlags;  // 语句开始符号集
    private boolean[] factorBeginSymbolFlags; //因子开始符号集

    public List<Wrong> getWrongList() {
        return wrongList;
    }

    public Map<String, Variable> getVariableMap() {
        return variableMap;
    }

    public Map<String, Function> getFunctionMap() {
        return functionMap;
    }

    public List<Instruction> getGeneratedInstructions() {
        return generatedInstructions;
    }
    public ProgramParser(String pathname) {
        //相关初始化
        lexer = new Lexer();
        wrongList = new ArrayList<>();
        variableMap = new HashMap<>();
        functionMap = new HashMap<>();
        generatedInstructions = new ArrayList<>();
        generatedInstructions.add(new Instruction()); //添加一个占位元素
        try {
            lexer.openFile(pathname);
        } catch (Exception e) {
            e.printStackTrace();
        }

        statementBeginSymbolFlags = new boolean[24];  // 语句开始符号集
        factorBeginSymbolFlags = new boolean[24];  //因子开始符号集

        statementBeginSymbolFlags[Symbol.ident.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.ifsym.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.whilesym.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.ident.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.returnsym.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.scanfsym.ordinal()]= true;
        statementBeginSymbolFlags[Symbol.printfsym.ordinal()]= true;

        factorBeginSymbolFlags[Symbol.ident.ordinal()] = true;
        factorBeginSymbolFlags[Symbol.number.ordinal()] = true;
        factorBeginSymbolFlags[Symbol.lbracket.ordinal()] = true;

    }



    //生成指令,返回新指令所在的位置
    private int generateInstruction(InstructionType type, int layer, int third) {
        Instruction instruction = new Instruction();
        instruction.setName(type);
        instruction.setLayer(layer);
        instruction.setThird(third);
        generatedInstructions.add(instruction);
        return generatedInstructions.indexOf(instruction);
    }





    /*处理语句
    <语句>-> <条件语句>｜<循环语句> | '{'<语句序列>'}' | <自定义函数调用语句> | <赋值语句> | <返回语句> | <读语句> | <写语句> | ;
    */
    private void parseStatement(String functionName) throws Exception {

        while(statementBeginSymbolFlags[word.getType().ordinal()] == true) {  //循环直到不是语句开始符号
            switch (word.getType()) {
                //条件语句
                case ifsym: {

                }
                //循环语句
                case whilesym: {

                }
                //函数调用
                //赋值语句
                case ident: {
                    String val = word.getValue(); //保存原ident的值
                    word = lexer.getWord();
                    if (word.getType() == Symbol.lbracket) { //void函数调用
                        word = lexer.getWord();
                        if (word.getType() != Symbol.rbracket) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'"));
                        } else {
                            //TODO 函数调用
                        }
                    } else if (word.getType() == Symbol.equal) { //赋值
                        // int函数调用
                        //TODO 表达式处理
                    } else {
                        wrongList.add(new Wrong(lexer.getPosition(), 8, "语句格式错误"));
                    }
                }
                //返回
                case returnsym: {
                    if (word.getType() != Symbol.lbracket ) {
                        wrongList.add(new Wrong(lexer.getPosition(),4, "缺少'('"));
                    } else {
                        //TODO 表达式处理

                        if (word.getType() != Symbol.rbracket) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'"));
                        } else {
                            generateInstruction(InstructionType.STO, 1,0);
                            generateInstruction(InstructionType.RET, 0,1); //带return是带返回值的函数
                        }

                    }
                    break;
                }
                //读
                case scanfsym: {
                    word = lexer.getWord();
                    if (word.getType() != Symbol.lbracket ) {
                        wrongList.add(new Wrong(lexer.getPosition(),4, "缺少'('"));
                    } else {
                        word = lexer.getWord();
                        if (word.getType() == Symbol.ident) {
                            // 查找要读的变量
                            Variable variable = variableMap.get(word.getValue());
                            if (variable != null && (variable.getScope().equals(functionName)||variable.getScope().equals("global"))) {
                                generateInstruction(InstructionType.RED,0,0); //生成 读值到栈顶 指令
                                //生成 储存到变量 指令
                                if (variable.getScope().equals(functionName)) {
                                    generateInstruction(InstructionType.STO, 0,variable.getAddress()); //当前层
                                } else {
                                    generateInstruction(InstructionType.STO, 1,variable.getAddress()); //全局变量，外层
                                }
                            } else {
                                wrongList.add(new Wrong(lexer.getPosition(),7, "变量未声明或不在作用域内"));
                            }
                        } else {
                            wrongList.add(new Wrong(lexer.getPosition(),1,"缺少标识符"));
                        };
                        word = lexer.getWord();
                        if (word.getType() != Symbol.rbracket) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'"));
                        }
                    }
                    break;
                }
                //写
                case printfsym: {
                    word = lexer.getWord();
                    if (word.getType() != Symbol.lbracket ) {
                        wrongList.add(new Wrong(lexer.getPosition(),4, "缺少'('"));
                    } else
                    {
                        word = lexer.getWord();
                        if (word.getType() == Symbol.ident) {
                            // 查找 写 要的变量
                            Variable variable = variableMap.get(word.getValue());
                            if (variable != null && (variable.getScope().equals(functionName)||variable.getScope().equals("global"))) {
                                //生成 将变量放置栈顶 指令
                                if (variable.getScope().equals(functionName)) {
                                    generateInstruction(InstructionType.LOD, 0,variable.getAddress()); //当前层
                                } else {
                                    generateInstruction(InstructionType.LOD, 1,variable.getAddress()); //全局变量，外层
                                }
                                generateInstruction(InstructionType.WRT,0,0); //生成 写 指令
                            } else {
                                wrongList.add(new Wrong(lexer.getPosition(),7, "变量未声明或不在作用域内"));
                            }
                        } else {
                            wrongList.add(new Wrong(lexer.getPosition(),1,"缺少标识符"));
                        };
                        word = lexer.getWord();
                        if (word.getType() != Symbol.rbracket) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'"));
                        }
                    }
                    break;
                }
            }
            word = lexer.getWord();
            if (word.getType() != Symbol.semicolon) {
                wrongList.add(new Wrong(lexer.getPosition(), 2, "缺少';'"));
            }
            word = lexer.getWord();
        }


    }

    //子程序处理
    private void parseSubProgram(String functionName) throws Exception {
        int localCount = 0;
        word = lexer.getWord();
        if (word.getType() != Symbol.lbrace) {
            wrongList.add(new Wrong(lexer.getPosition(), 5, "缺少'{'"));
        } else {
            word = lexer.getWord();
            while (word.getType() == Symbol.intsym) {  //局部变量声明
                word = lexer.getWord();
                if (word.getType() != ident) {
                    wrongList.add(new Wrong(lexer.getPosition(), 1, "缺少标识符"));
                } else {
                    Variable variable = new Variable(word.getValue(), functionName, localCount + 3);
                    //TODO 由于是用名字作为Key,所以变量不能重名
                    variableMap.put(word.getValue(),variable);
                    localCount++;
                    word = lexer.getWord();
                    while (word.getType() == Symbol.comma) {
                        word = lexer.getWord();
                        variable = new Variable(word.getValue(), functionName, localCount + 3);
                        variableMap.put(word.getValue(),variable);
                        localCount++;
                        word = lexer.getWord();
                    }
                    if (word.getType() != Symbol.semicolon) {
                        wrongList.add(new Wrong(lexer.getPosition(), 2, "缺少';'"));
                    }
                }
                word = lexer.getWord();
            }
            Function function = functionMap.get(functionName);
            function.updateSize(localCount);
            int size = function.getSize();;
            int entryAddress = 0;
            //声明结束，为函数分配内存，记录当前函数入口地址
            if (!functionName.equals("main")) { //当前函数不是主函数，分配函数位置即可
                entryAddress = generateInstruction(InstructionType.INT, 0, size);
            } else { //函数是主函数时，将全局变量分配至主函数中
                // TODO 填写主函数的函数表
                entryAddress = generateInstruction(InstructionType.INT, 0, size + globalCount);
            }
            function.setEntryAddress(entryAddress);
            //语句
            parseStatement(functionName);

            if (word.getType() != Symbol.rbrace) {
                wrongList.add(new Wrong(lexer.getPosition(), 6, "缺少'}'"));
            } else {
                word = lexer.getWord();
            }
            //TODO 函数完毕，填写函数位置
        }
    }

    //处理整个程序
    public void parse() throws Exception {
        word = lexer.getWord();
        while (word.getType() == Symbol.intsym || word.getType() == Symbol.voidsym) {
            if (word.getType() == Symbol.intsym) {  //变量或者函数
                word = lexer.getWord();
                Variable variable;
                if (word.getType() == ident) {
                    String val = word.getValue(); //保留该值
                    word = lexer.getWord();
                    if (word.getType() == Symbol.comma) { //变量
                        //declareGlobalVariable(val,count); //val是变量名
                        //加入变量声明列表
                        variable = new Variable(val, "global", globalCount + 3);
                        variableMap.put(val,variable);
                        globalCount++;
                        while (word.getType() == Symbol.comma) {
                            word = lexer.getWord();
                            //declareGlobalVariable(word.getValue(),count);
                            variable = new Variable(word.getValue(), "global", globalCount + 3);
                            variableMap.put(word.getValue(),variable);
                            globalCount++;
                            word = lexer.getWord();
                        }
                        if (word.getType() == Symbol.semicolon) { //分号  结束
                            word = lexer.getWord();
                        } else {
                            wrongList.add(new Wrong(lexer.getPosition(), 2, "缺少';'"));
                        }
                    } else if (word.getType() == Symbol.lbracket) {  //函数
                        word = lexer.getWord();
                        Function function = new Function(val, true);
                        functionMap.put(val,function);
                        if (word.getType() != Symbol.rbracket) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'"));
                        } else {
                            //TODO 子程序处理
                            parseSubProgram(val);
                        }
                    } else if (word.getType() == Symbol.semicolon) { //仅有一个变量声明
                        variable = new Variable(val, "global", globalCount + 3);
                        variableMap.put(word.getValue(),variable);
                        globalCount++;
                        word = lexer.getWord();
                    } else {
                        wrongList.add(new Wrong(lexer.getPosition(), 2, "缺少';'"));
                    }
                } else {
                    wrongList.add(new Wrong(lexer.getPosition(), 1, "缺少标识符"));
                }
            } else if (word.getType() == Symbol.voidsym) {
                word = lexer.getWord();
                String functionName = word.getValue();
                Function function = new Function(functionName, false);
                functionMap.put(word.getValue(),function);
                word = lexer.getWord();
                if (word.getType() != Symbol.lbracket) {
                    wrongList.add(new Wrong(lexer.getPosition(), 4, "缺少'('"));
                } else {
                    word = lexer.getWord();
                    if (word.getType() != Symbol.rbracket) {
                        wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'"));
                    } else {
                        //TODO 子程序处理
                        parseSubProgram(functionName);
                    }
                }
            }
        }
    }


}
