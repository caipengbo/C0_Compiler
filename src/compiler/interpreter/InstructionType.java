package compiler.interpreter;

/**
 * Title：指令名称
 * Description：假想的栈式指令系统 各个指令的名称
 * Created by Myth on 4/8/2017.
 */
public enum InstructionType {
    LIT,
    LOD,
    STO,
    CAL,
    INT,
    JMP,
    JPC,
    ADD,
    SUB,
    MUL,
    DIV,
    RED,
    WRT,
    RET,
}
