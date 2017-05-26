package compiler.test;

import compiler.common.Symbol;
import compiler.common.Word;
import compiler.lexer.Lexer;
import org.junit.Test;

import java.util.List;

/**
 * Title：
 * Description：
 * Created by Myth on 5/1/2017.
 */
public class LexerTest {

    @Test
    public void openFile() throws Exception {
        Lexer lexer = new Lexer();
        lexer.openFile("src/compiler/test/factorial.txt"); //注意路径
        List<String> list = lexer.getSourceCodeLineList();
        for (String s: list) {
            System.out.println(s);
        }
    }

    @Test
    public void getSymbol() throws Exception {
        Lexer lexer = new Lexer();
        lexer.openFile("src/compiler/test/factorial.txt");
        Word word = new Word();
        while (word.getType()!= Symbol.endsym) {
            word = lexer.getWord();
            System.out.println(word);
        }
    }

}
