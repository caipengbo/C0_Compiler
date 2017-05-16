package compiler.parser;

import compiler.common.Symbol;
import compiler.common.Word;
import compiler.common.Wrong;
import compiler.lexer.Lexer;

import java.util.*;

/**
 * Title：程序处理
 * Description： <程序>->[<全局变量定义部分>] {<自定义函数定义部分>} <主函数>
 * Created by Myth on 5/4/2017.
 */
public class ProgramParser {
    private Lexer lexer;
    private Word word; //每次去到的单词
    private List<Wrong> wrongList;
    //为方便定位变量与函数(名字作为Key)，使用Map表示 变量表与函数表
    private Map<String,Variable> variableMap;
    private Map<String,Function> functionMap;

    public List<Wrong> getWrongList() {
        return wrongList;
    }

    public Map<String, Variable> getVariableMap() {
        return variableMap;
    }

    public Map<String, Function> getFunctionMap() {
        return functionMap;
    }

    public ProgramParser(String pathname) {
        wrongList = new ArrayList<>();
        variableMap = new HashMap<>();
        functionMap = new HashMap<>();
        lexer = new Lexer();
        try {
            lexer.openFile(pathname);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	//子程序处理
	private void parseSubProgram(String functionName) throws Exception {
        word = lexer.getWord();
        if (word.getType() != Symbol.lbrace) {
            wrongList.add(new Wrong(lexer.getPosition(), 5, "缺少'{'"));
        } else {
            word = lexer.getWord();
            if (word.getType() == Symbol.intsym) {  //局部变量声明
                word = lexer.getWord();
                if (word.getType() != Symbol.ident) {
                    wrongList.add(new Wrong(lexer.getPosition(), 1, "缺少标识符"));
                } else {
                    int localCount = 0;
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
                    } else {
                        Function function = functionMap.get(functionName);
                        function.updateSize(localCount);
                    }
                }
            }
            //语句
            word = lexer.getWord();
            //
            if (word.getType() != Symbol.rbrace) {
                wrongList.add(new Wrong(lexer.getPosition(), 6, "缺少'}'"));
            } else {
                word = lexer.getWord();
            }
        }
    }

    //处理
    public void parse() throws Exception {
        //TODO 全局变量位置待修改
        int globalCount = 1;
        word = lexer.getWord();
        while (word.getType() == Symbol.intsym || word.getType() == Symbol.voidsym) {
            if (word.getType() == Symbol.intsym) {  //变量或者函数
                word = lexer.getWord();
                Variable variable;
                if (word.getType() == Symbol.ident) {
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
                            //加入函数声明列表
                            //TODO 子程序处理
                            parseSubProgram(val);
                        }
                    } else if (word.getType() == Symbol.semicolon) { //仅有一个变量声明
                        variable = new Variable(val, "global", globalCount + 3);
                        variableMap.put(word.getValue(),variable);
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
