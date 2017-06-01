package compiler.parser;

/**
 * Title：函数调用类
 * Description： 描述函数调用，实现先使用后定义
 * Created by Myth on 5/18/2017.
 */
public class FunctionCall {
    private String functionName;
    private int callInstructionPosition = -1; //CAL 指令的位置，便于回填 函数入口
    private int callSourcePosition = -1;   //调用语句在源代码中的位置，用来指示错误位置
    private boolean wantReturnValue = false;

    public FunctionCall(String functionName, int callInstructionPosition, boolean wantReturnValue, int callSourcePosition) {
        this.functionName = functionName;
        this.callInstructionPosition = callInstructionPosition;
        this.wantReturnValue = wantReturnValue;
        this.callSourcePosition = callSourcePosition;
    }

    public String getFunctionName() {
        return functionName;
    }

    public int getCallInstructionPosition() {
        return callInstructionPosition;
    }

    public int getCallSourcePosition() {
        return callSourcePosition;
    }

    public boolean isWantReturnValue() {
        return wantReturnValue;
    }

    @Override
    public String toString() {
        return "FunctionCall{" +
                "functionName='" + functionName + '\'' +
                ", callInstructionPosition=" + callInstructionPosition +
                ", callSourcePosition=" + callSourcePosition +
                ", wantReturnValue=" + wantReturnValue +
                '}';
    }
}
