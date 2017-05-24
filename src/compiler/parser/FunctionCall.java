package compiler.parser;

/**
 * Title：函数调用类
 * Description： 描述函数调用，实现先使用后定义
 * Created by Myth on 5/18/2017.
 */
public class FunctionCall {
    private String functionName;
    private int callInstructionPosition = -1; //CAL 指令的位置，便于回填 函数入口
    private boolean wantReturnValue = false;

    public FunctionCall(String functionName, int callInstructionPosition, boolean wantReturnValue) {
        this.functionName = functionName;
        this.callInstructionPosition = callInstructionPosition;
        this.wantReturnValue = wantReturnValue;
    }

    @Override
    public String toString() {
        return "FunctionCall{" +
                "functionName='" + functionName + '\'' +
                ", callInstructionPosition=" + callInstructionPosition +
                ", wantReturnValue=" + wantReturnValue +
                '}';
    }
}
