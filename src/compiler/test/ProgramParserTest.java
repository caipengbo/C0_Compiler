package compiler.test;

import compiler.common.Wrong;
import compiler.parser.Function;
import compiler.parser.ProgramParser;
import compiler.parser.Variable;
import org.junit.Test;

import java.util.List;

/**
 * Title：
 * Description：
 * Created by Myth on 5/11/2017.
 */
public class ProgramParserTest {
    @Test
    public void parse() throws Exception {
        ProgramParser programParser = new ProgramParser("src/compiler/test/declare.txt");
        programParser.parse();
        List<Wrong> wrongList = programParser.getWrongList();
        List<Variable> variableList = programParser.getVariableList();
        List<Function> functionList = programParser.getFunctionList();
        for (Wrong wrong : wrongList) {
            System.out.println(wrong);
        }
        for (Variable variable : variableList) {
            System.out.println(variable);
        }
        for (Function function : functionList) {
            System.out.println(function);
        }
    }

}