package compiler.parser;

import compiler.common.Symbol;
import compiler.common.Word;
import compiler.common.Wrong;
import compiler.lexer.Lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * Title：程序处理
 * Description： <程序>->[<全局变量定义部分>] {<自定义函数定义部分>} <主函数>
 * Created by Myth on 5/4/2017.
 */
public class ProgramParser {
    private Lexer lexer;
    private Word word;
    private List<Wrong> wrongList;
    private List<Variable> variableList;
    private List<Function> functionList;

    public List<Wrong> getWrongList() {
        return wrongList;
    }

    public List<Variable> getVariableList() {
        return variableList;
    }

    public List<Function> getFunctionList() {
        return functionList;
    }

    public ProgramParser(String pathname) {
        wrongList = new ArrayList<>();
        variableList = new ArrayList<>();
        functionList = new ArrayList<>();
        lexer = new Lexer();
        try {
            lexer.openFile(pathname);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //全局变量声明处理
	private void varDeclaration(String varName,int count) {
        Variable variable = new Variable(varName,"global",count+3); //初始有三个位置
        variableList.add(variable);
	}

    //处理
    public void parse() throws Exception {
        word = lexer.getWord();
        int globalCount = 1; //全局变量计数
        while (word.getType() == Symbol.intsym || word.getType() == Symbol.voidsym) {
            if (word.getType() == Symbol.intsym) {  //变量或者函数
                word = lexer.getWord();
                if (word.getType() == Symbol.ident) {
                    String val = word.getValue(); //保留该值
                    word = lexer.getWord();
                    if (word.getType() == Symbol.comma) { //变量
                        //varDeclaration(val,count); //val是变量名
                        //加入变量声明列表
                        Variable variable = new Variable(val, "global", globalCount + 3);
                        variableList.add(variable);
                        globalCount++;
                        while (word.getType() == Symbol.comma) {
                            word = lexer.getWord();
                            //varDeclaration(word.getValue(),count);
                            variable = new Variable(word.getValue(), "global", globalCount + 3);
                            variableList.add(variable);
                            globalCount++;
                            word = lexer.getWord();
                        }
                        if (word.getType() == Symbol.semicolon) { //分号  结束
                            word = lexer.getWord();
                        } else {
                            wrongList.add(new Wrong(lexer.getPosition(), 2, "缺少分号"));
                        }

                    } else if (word.getType() == Symbol.lbracket) {  //函数
                        word = lexer.getWord();
                        if (word.getType() == Symbol.rbracket) {
                            word = lexer.getWord();
                        } else {
                            wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少右括号"));
                        }
                        //加入函数声明列表
                        //funDeclaration(val,true);//val是函数名
                        Function function = new Function(val, true);
                        functionList.add(function);

                        //TODO 子程序处理
                        word = lexer.getWord();

                    } else if (word.getType() == Symbol.semicolon) {
                        word = lexer.getWord();
                    } else {
                        wrongList.add(new Wrong(lexer.getPosition(), 2, "缺少分号"));
                    }
                } else {
                    wrongList.add(new Wrong(lexer.getPosition(), 1, "缺少标识符"));
                }
            } else if (word.getType() == Symbol.voidsym) {
                word = lexer.getWord();
                Function function = new Function(word.getValue(), false);
                functionList.add(function);
                word = lexer.getWord();
                if (word.getType() == Symbol.lbracket) {
                    word = lexer.getWord();
                    if (word.getType() == Symbol.rbracket) {
                        word = lexer.getWord();
                    } else {
                        wrongList.add(new Wrong(lexer.getPosition(), 3, "缺少右括号"));
                    }
                } else {
                    wrongList.add(new Wrong(lexer.getPosition(), 4, "缺少左括号"));
                }
                //TODO 子程序处理

            }

        }
    }
}
