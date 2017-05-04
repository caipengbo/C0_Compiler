package compiler.parser;

import compiler.common.Word;
import compiler.lexer.Lexer;

/**
 * Title：程序处理
 * Description： 
 * Created by Myth on 5/4/2017.
 */
public class ProgramParser {
    //<程序>->[<变量定义部分>] {<自定义函数定义部分>} <主函数>
    //变量声明处理
	private void var() {

	}
    //自定义函数
    //主函数
    //处理
    public void parse() throws Exception {
        Lexer lexer = new Lexer();
        lexer.openFile("src/compiler/test/test.c0");
        Word word = lexer.getWord();


    }
}
