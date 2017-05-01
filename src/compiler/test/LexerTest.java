package compiler.test;

import compiler.lexer.CharPosition;
import compiler.lexer.Lexer;
import org.junit.Test;

import java.util.List;

/**
 * Title：
 * Description：
 * Created by Myth on 5/1/2017.
 */
public class LexerTest {
    private static final CharPosition CHAR_END_POSITION = new CharPosition(-1,-1);
    @Test
    public void openFile() throws Exception {
        Lexer lexer = new Lexer();
        lexer.openFile("src/compiler/test/test.c0"); //注意路径
        List<String> list = lexer.getSourceCodeLineList();
        for (String s: list) {
            System.out.println(s);
        }
    }

    @Test
    public void convertToSymbol() throws Exception {
        Lexer lexer = new Lexer();
        lexer.openFile("src/compiler/test/test.c0"); //注意路径
        CharPosition charPosition = new CharPosition(0,0);
        while (!charPosition.equals(CHAR_END_POSITION)) {
            charPosition = lexer.convertToSymbol(charPosition);
        }


    }

}
