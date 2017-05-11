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
    public ProgramParser(String pathname) {
        wrongList = new ArrayList<>();
        lexer = new Lexer();
        try {
            lexer.openFile(pathname);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //全局变量声明处理
	private void varDeclaration(String varName) {

	}
    //自定义函数
    //主函数
    //处理
    public void parse() throws Exception {
        word = lexer.getWord();
        if (word.getType() == Symbol.intsym) {  //变量或者函数
            word = lexer.getWord();
            if (word.getType() == Symbol.ident) {
                String val = word.getValue(); //保留该值 以防是变量定义
                word = lexer.getWord();
                if (word.getType() == Symbol.comma) { //变量
                    varDeclaration(val);
                } else if (word.getType() == Symbol.lbracket) {  //函数

                } else {

                }

            } else {
                wrongList.add(new Wrong(lexer.getPosition(),1,"缺少标识符"));

            }
        }
    }
}
