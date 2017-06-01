package compiler.test;

import compiler.interpreter.Interpreter;
import org.junit.Test;

/**
 * Title：
 * Description：
 * Created by Myth on 5/23/2017.
 */
public class InterpreterTest {
    @Test
    public void interpret() throws Exception {
        Interpreter interpreter = new Interpreter();

        interpreter.openFile("src/while.txt");
        //interpreter.printInstructionList();
        //File file  = new File("src/out.txt");
        //interpreter.writeToFile(file);
        interpreter.interpret();

    }

}