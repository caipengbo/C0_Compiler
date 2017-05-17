package compiler.test;

import compiler.common.Wrong;
import compiler.interpret.Instruction;
import compiler.parser.Function;
import compiler.parser.ProgramParser;
import compiler.parser.Variable;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Title：单元测试程序
 * Description：
 * Created by Myth on 5/11/2017.
 */
public class ProgramParserTest {
    @Test
    public void parse() throws Exception {
        ProgramParser programParser = new ProgramParser("src/compiler/test/declare.txt");
        programParser.parse();
        List<Wrong> wrongList = programParser.getWrongList();
        Map<String,Variable> variableMap = programParser.getVariableMap();
        Map<String,Function> functionMap = programParser.getFunctionMap();
        List<Instruction> instructionList = programParser.getGeneratedInstructions();
        for (Wrong wrong : wrongList) {
            System.out.println(wrong);
        }
        for (Map.Entry<String,Variable> entry : variableMap.entrySet()) {
            System.out.println(entry.getValue());
        }
        for (Map.Entry<String,Function> entry : functionMap.entrySet()) {
            System.out.println(entry.getValue());
        }
        for (Instruction instruction : instructionList) {
            System.out.println(instruction);
        }

    }

}