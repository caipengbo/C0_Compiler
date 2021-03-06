package compiler.parser;

import compiler.common.Symbol;
import compiler.common.Word;
import compiler.common.Wrong;
import compiler.interpreter.Instruction;
import compiler.interpreter.InstructionType;
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
    private List<FunctionCall> functionCallList; //记录函数调用
    //为方便定位变量与函数(名字作为Key),所以一定要注意函数、变量（也包括不同作用域）不能重名,使用Map表示 变量表与函数表
    private Map<String, Variable> variableMap;
    private Map<String, Function> functionMap;
   // private Map<String,>
    //TODO 考虑是否在开始加一条跳转到主函数指令
    /**
     * 生成的指令，指令从位置1开始
     */
    private List<Instruction> generatedInstructionList;
    private int globalCount = 0;
    // 用来判断是否属于某一语法单位
    private boolean[] statementBeginSymbolFlags;  // 语句开始符号集
    private boolean[] factorBeginSymbolFlags; //因子开始符号集

    public List<Wrong> getWrongList() {
        return wrongList;
    }

    public List<FunctionCall> getFunctionCallList() {
        return functionCallList;
    }

    public Map<String, Variable> getVariableMap() {
        return variableMap;
    }

    public Map<String, Function> getFunctionMap() {
        return functionMap;
    }

    public List<Instruction> getGeneratedInstructionList() {
        return generatedInstructionList;
    }

    public List<String> getSourceCodeLineList() {
        return lexer.getSourceCodeLineList();
    }

    public ProgramParser(String pathname) throws Exception {
        //相关初始化
        lexer = new Lexer();
        wrongList = new ArrayList<>();
        functionCallList = new ArrayList<>();
        variableMap = new HashMap<>();
        functionMap = new HashMap<>();
        generatedInstructionList = new ArrayList<>();
        //为了使产生的代码从下标1开始，添加一个占位元素占据下标0位置
        generatedInstructionList.add(new Instruction());
        generatedInstructionList.add(new Instruction(InstructionType.JMP, 0, -1)); //产生一个跳转到主函数的指令，最后回填main位置
        lexer.openFile(pathname);
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
        generatedInstructionList.add(instruction);
        return generatedInstructionList.indexOf(instruction);
    }

    // 因子处理
    private void parseFactor(String functionName) throws Exception {
        while (factorBeginSymbolFlags[word.getType().ordinal()]) // 循环直到不是因子开始符号
        {
            switch (word.getType()) {
                case ident:{   //因子为变量或者函数调用
                    String val = word.getValue(); //保存值，因为不知道是变量还是函数
                    word = lexer.getWord();
                    if (word.getType() == Symbol.lbracket) { //函数调用
                        word = lexer.getWord();
                        if (word.getType() != Symbol.rbracket) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'", 115));
                        } else {
                            int position = generateInstruction(InstructionType.CAL, 0, -1);
                            FunctionCall functionCall = new FunctionCall(val, position,true, lexer.getLineNumber()+1);
                            functionCallList.add(functionCall);
                        }
                        word = lexer.getWord();
                    } else { //变量
                        Variable variable = variableMap.get(val);
                        if (variable != null && (variable.getScope().equals(functionName)||variable.getScope().equals("global"))) {
                            if (variable.getScope().equals(functionName)) {
                                generateInstruction(InstructionType.LOD, 1,variable.getAddress()); //当前层
                            } else {
                                generateInstruction(InstructionType.LOD, 0,variable.getAddress()); //全局变量，外层
                            }
                        } else {
                            wrongList.add(new Wrong(lexer.getPosition(), 7, "变量未声明或不在作用域内",130));
                        }
                    }
                    break;
                }
                case number: {   //因子为数
                    generateInstruction(InstructionType.LIT, 0, Integer.parseInt(word.getValue()));
                    word = lexer.getWord();
                    break;
                }
                case lbracket: {    //因子为表达式
                    word = lexer.getWord();
                    parseExpression(functionName);
                    if (word.getType() != Symbol.rbracket) {
                        wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'", 132));
                    }
                    word = lexer.getWord();
                    break;
                }
            }
        }
    }

    // 项处理 <因子>｛(*｜/) <因子>｝
    private void parseTerm(String functionName) throws Exception {
        Symbol symbol;
        parseFactor(functionName);	// 处理因子
        while (word.getType() == Symbol.multiply || word.getType() == Symbol.divide) {
            symbol = word.getType();
            word = lexer.getWord();
            parseFactor(functionName);
            if (symbol == Symbol.multiply) { // 生成乘法指令
                generateInstruction(InstructionType.MUL, 0, 0);
            } else { // 生成除法指令
                generateInstruction(InstructionType.DIV, 0, 0);
            }
        }
    }

    //表达式处理  [+｜-] <项> { (+｜-) <项>}
    // TODO 第一个项 不支持 + - ：<项> { (+｜-) <项>}
    private void parseExpression(String functionName) throws Exception {
        Symbol symbol;
        parseTerm(functionName); //处理项
        while (word.getType() ==  Symbol.plus || word.getType() == Symbol.minus) {
            symbol = word.getType();
            word = lexer.getWord();
            parseTerm(functionName);
            if (symbol == Symbol.plus)
            {
                generateInstruction(InstructionType.ADD, 0, 0); // 生成加法指令
            } else {
                generateInstruction(InstructionType.SUB, 0, 0); // 生成减法指令
            }
        }
    }

    /*处理语句
    <语句>-> <条件语句>｜<循环语句> | '{'<语句序列>'}' | <自定义函数调用语句> | <赋值语句> | <返回语句> | <读语句> | <写语句> | ;
    返回 最后一次生成的指令地址（在generatedInstructionList中的位置）
    */
    private int parseStatement(String functionName) throws Exception {
        int lastInstructionPosition = -1;
        while(statementBeginSymbolFlags[word.getType().ordinal()]) {  //循环直到不是语句开始符号
            switch (word.getType()) {
                //条件语句
                case ifsym: {
                    int jpcInstructionPosition;
                    int jmpInstructionPosition;
                    int elseFinishPosition;
                    //判断 (
                    word = lexer.getWord();
                    if (word.getType() != Symbol.lbracket ) {
                        wrongList.add(new Wrong(lexer.getPosition(),4, "缺少'('", 202));
                    } else {
                        word = lexer.getWord();
                        parseExpression(functionName); // if判别表达式
                        if (word.getType() != Symbol.rbracket ) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'",208));
                        } else {
                            //生成JPC指令，记录下来此条指令的地址(用于回填)
                            jpcInstructionPosition = generateInstruction(InstructionType.JPC, 0, -1);
                            word = lexer.getWord();
                            if (word.getType() != Symbol.lbrace) {
                                wrongList.add(new Wrong(lexer.getPosition(), 5, "缺少'{'", 214));
                            } else {
                                word = lexer.getWord();
                                parseStatement(functionName); // if语句
                                if (word.getType() != Symbol.rbrace) {
                                    wrongList.add(new Wrong(lexer.getPosition(), 6, "缺少'}'",219));
                                } else {
                                    jmpInstructionPosition = generateInstruction(InstructionType.JMP, 0, -1);
                                    elseFinishPosition = jmpInstructionPosition; //如果没有else语句
                                    word = lexer.getWord();
                                    if (word.getType() == Symbol.elsesym) {
                                        word = lexer.getWord();
                                        if (word.getType() != Symbol.lbrace) {
                                            wrongList.add(new Wrong(lexer.getPosition(), 5, "缺少'{'", 227));
                                        } else {
                                            word = lexer.getWord();
                                            elseFinishPosition = parseStatement(functionName); // else语句
                                            if (word.getType() != Symbol.rbrace) {
                                                wrongList.add(new Wrong(lexer.getPosition(), 6, "缺少'}'", 232));
                                            }
                                        }
                                        word = lexer.getWord();
                                    }
                                    Instruction jpcInstruction = generatedInstructionList.get(jpcInstructionPosition);
                                    jpcInstruction.setThird(jmpInstructionPosition+1);
                                    Instruction jmpInstruction = generatedInstructionList.get(jmpInstructionPosition);
                                    jmpInstruction.setThird(elseFinishPosition+1);
                                }
                            }
                        }
                    }
                    break;
                }
                //循环语句
                case whilesym: {
                    int expInstructionPosition;//表达式指令开始的位置,jmp语句跳的地方
                    int jpcInstructionPosition;
                    int jmpInstructionPosition;
                    word = lexer.getWord();
                    if (word.getType() != Symbol.lbracket ) {
                        wrongList.add(new Wrong(lexer.getPosition(),4, "缺少'('", 251));
                    } else {
                        expInstructionPosition = generatedInstructionList.size();
                        word = lexer.getWord();
                        parseExpression(functionName); //while表达式
                        if (word.getType() != Symbol.rbracket ) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'",256));
                        } else {
                            //产生JPC指令 ，记录下来JPC指令的位置
                            jpcInstructionPosition = generateInstruction(InstructionType.JPC, 0, -1);
                            word = lexer.getWord();
                            if (word.getType() != Symbol.lbrace) {
                                wrongList.add(new Wrong(lexer.getPosition(), 5, "缺少'{'", 264));
                            } else {
                                word = lexer.getWord();
                                parseStatement(functionName); // while语句
                                if (word.getType() != Symbol.rbrace) {
                                    wrongList.add(new Wrong(lexer.getPosition(), 6, "缺少'}'",219));
                                } else {
                                    jmpInstructionPosition = generateInstruction(InstructionType.JMP, 0, expInstructionPosition);
                                    //回填
                                    Instruction jpcInstruction = generatedInstructionList.get(jpcInstructionPosition);
                                    jpcInstruction.setThird(jmpInstructionPosition+1);
                                }
                                word = lexer.getWord();
                            }
                        }
                    }
                    break;
                }
                //函数调用、赋值语句
                case ident: {
                    String val = word.getValue(); //保存原ident的值
                    word = lexer.getWord();
                    if (word.getType() == Symbol.lbracket) { //void函数调用
                        word = lexer.getWord();
                        if (word.getType() != Symbol.rbracket) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'"));
                        } else {
                            //-1代表未知，如果调用函数正确，最后会查看函数定义表，回填函数入口地址。
                            lastInstructionPosition = generateInstruction(InstructionType.CAL, 0, -1);
                            FunctionCall functionCall = new FunctionCall(val, lastInstructionPosition,false, lexer.getLineNumber());
                            functionCallList.add(functionCall);
                        }
                        word = lexer.getWord(); //表达式处理的时候会含有此语句，所以不能放到外部，只能放到里面
                    } else if (word.getType() == Symbol.equal) { //赋值
                        //先判断该变量是否定义，在处理后面的表达式
                        Variable variable = variableMap.get(val);
                        if (variable != null && (variable.getScope().equals(functionName)||variable.getScope().equals("global"))) {
                            word = lexer.getWord();
                            parseExpression(functionName);
                            //赋值
                            if (variable.getScope().equals(functionName)) {
                                lastInstructionPosition = generateInstruction(InstructionType.STO, 1,variable.getAddress()); //当前层
                            } else {
                                lastInstructionPosition = generateInstruction(InstructionType.STO, 0,variable.getAddress()); //全局变量，外层
                            }
                        } else {
                            wrongList.add(new Wrong(lexer.getPosition(),7, "变量未声明或不在作用域内",258));
                        }
                    } else {
                        wrongList.add(new Wrong(lexer.getPosition(), 8, "语句格式错误", 261));
                    }
                    if (word.getType() != Symbol.semicolon) {
                        wrongList.add(new Wrong(lexer.getPosition(), 2, "缺少';'",386));
                    }
                    word = lexer.getWord();
                    break;
                }
                //返回
                case returnsym: {
                    word = lexer.getWord();
                    if (word.getType() != Symbol.lbracket ) {
                        wrongList.add(new Wrong(lexer.getPosition(),4, "缺少'('"));
                    } else {
                        word = lexer.getWord();
                        parseExpression(functionName);
                        if (word.getType() != Symbol.rbracket) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'"));
                        } else {
                            generateInstruction(InstructionType.STO, 1,0);
                            lastInstructionPosition = generateInstruction(InstructionType.RET, 0,1); //带return是带返回值的函数
                        }
                    }
                    word = lexer.getWord();
                    if (word.getType() != Symbol.semicolon) {
                        wrongList.add(new Wrong(lexer.getPosition(), 2, "缺少';'",340));
                    }
                    word = lexer.getWord();
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
                                    lastInstructionPosition = generateInstruction(InstructionType.STO, 1,variable.getAddress()); //当前层
                                } else {
                                    lastInstructionPosition = generateInstruction(InstructionType.STO, 0,variable.getAddress()); //全局变量，外层
                                }
                            } else {
                                wrongList.add(new Wrong(lexer.getPosition(),7, "变量未声明或不在作用域内"));
                            }
                        } else {
                            wrongList.add(new Wrong(lexer.getPosition(),1,"缺少标识符(或标识符错误)", 367));
                        }
                        word = lexer.getWord();
                        if (word.getType() != Symbol.rbracket) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'"));
                        }
                    }
                    word = lexer.getWord();
                    if (word.getType() != Symbol.semicolon) {
                        wrongList.add(new Wrong(lexer.getPosition(), 2, "缺少';'",376));
                    }
                    word = lexer.getWord();
                    break;
                }
                //写
                case printfsym: {
                    word = lexer.getWord();
                    if (word.getType() != Symbol.lbracket ) {
                        wrongList.add(new Wrong(lexer.getPosition(),4, "缺少'('"));
                    } else {
                        word = lexer.getWord();
                        parseExpression(functionName);
                        if (word.getType() != Symbol.rbracket) {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少')'"));
                        } else {
                            lastInstructionPosition = generateInstruction(InstructionType.WRT,0,0); //生成 写 指令
                        }
                    }
                    word = lexer.getWord();
                    if (word.getType() != Symbol.semicolon) {
                        wrongList.add(new Wrong(lexer.getPosition(), 2, "缺少';'",393));
                    }
                    word = lexer.getWord();
                    break;
                }
            }
        }
        return lastInstructionPosition;
    }

    //子程序处理
    private void parseSubProgram(String functionName) throws Exception {
        int localCount = 0;
        int address;
        word = lexer.getWord();
        if (word.getType() != Symbol.lbrace) {
            wrongList.add(new Wrong(lexer.getPosition(), 5, "缺少'{'"));
        } else {
            word = lexer.getWord();
            while (word.getType() == Symbol.intsym) {  //局部变量声明
                word = lexer.getWord();
                if (word.getType() != ident) {
                    wrongList.add(new Wrong(lexer.getPosition(), 1, "缺少标识符(或标识符错误)", 416));
                } else {
                    // 确保全局变量在主函数栈的相对位置
                    if (functionName.equals("main")) {
                        address = globalCount + localCount + 3;
                    } else {
                        address = localCount + 3;
                    }
                    Variable variable = new Variable(word.getValue(), functionName, address);
                    //TODO 由于是用名字作为Key,所以变量不能重名
                    variableMap.put(word.getValue(),variable);
                    localCount++;
                    word = lexer.getWord();
                    while (word.getType() == Symbol.comma) {
                        word = lexer.getWord();
                        if (functionName.equals("main")) {
                            address = globalCount + localCount + 3;
                        } else {
                            address = localCount + 3;
                        }
                        variable = new Variable(word.getValue(), functionName, address);
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
            if (functionName.equals("main")) {
                function.updateSize(localCount + globalCount);
            } else {
                function.updateSize(localCount);
            }
            int size = function.getSize();
            //声明结束，为函数分配内存，记录当前函数入口地址
            int entryAddress = generateInstruction(InstructionType.INT, 0, size);
            function.setEntryAddress(entryAddress);
            //语句
            parseStatement(functionName);
            if (word.getType() != Symbol.rbrace) {
                wrongList.add(new Wrong(lexer.getPosition(), 6, "缺少'}'",465));
            } else {
                //如果没有返回值的函数，末尾添加RET 0 0 指令(没有返回值的函数返回)
                if (!function.isHasReturn()) {
                    generateInstruction(InstructionType.RET, 0,0);
                }
                word = lexer.getWord();
            }
        }
    }

    //回填函数调用语句
    private void rewriteFunctionCall() {
        for (FunctionCall functionCall : functionCallList) {
            Function function = functionMap.get(functionCall.getFunctionName());
            if (function != null) {
                // 调用函数的返回值是否正确
                boolean returnValueCorrect = (!functionCall.isWantReturnValue()) || (functionCall.isWantReturnValue() == function.isHasReturn() || function.isHasReturn());
                if (returnValueCorrect) {
                    //回填调用函数入口地址
                    Instruction callInstruction = generatedInstructionList.get(functionCall.getCallInstructionPosition());
                    callInstruction.setThird(function.getEntryAddress());
                } else {
                    wrongList.add(new Wrong("第" + functionCall.getCallSourcePosition() + "行 ", 10, functionCall.getFunctionName()+" 无返回值"));
                }
            } else {
                wrongList.add(new Wrong("第" + functionCall.getCallSourcePosition() + "行 ", 9, functionCall.getFunctionName()+" 函数未定义"));
            }
        }
    }

    private void rewriteMainPosition() {
        Function function = functionMap.get("main");
        if (function != null) {
            Instruction instruction = generatedInstructionList.get(1);
            instruction.setThird(function.getEntryAddress());
        } else {
            wrongList.add(new Wrong("本程序", 11, "缺少main函数"));
        }
    }

    //处理整个程序
    public void parseProgram() throws Exception {
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
                            parseSubProgram(val);
                        }
                    } else if (word.getType() == Symbol.semicolon) { //仅有一个变量声明
                        variable = new Variable(val, "global", globalCount + 3);
                        variableMap.put(val,variable);
                        globalCount++;
                        word = lexer.getWord();
                    } else {
                        wrongList.add(new Wrong(lexer.getPosition(), 2, "缺少';'"));
                    }
                } else {
                    wrongList.add(new Wrong(lexer.getPosition(), 1, "缺少标识符(或标识符错误)",515));
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
                        parseSubProgram(functionName);
                    }
                }
            }
        }
        //回填函数调用入口地址
        rewriteFunctionCall();
        //回填main函数入口地址
        rewriteMainPosition();
    }

}
