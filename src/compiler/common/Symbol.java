package compiler.common;

/**
 * Title：符号表
 * Description：符号类型名字
 * Created by Myth on 4/8/2017.
 */
public enum Symbol {
    nul, //不能识别的符号
    ident, //标识符
    number,
    plus, minus,multiply,divide, equal, //加减乘除 等于
    lbracket,rbracket, //圆括号
    lbrace,rbrace,  //大括号
    comma, //逗号
    semicolon, //分号
    intsym,  //int
    voidsym, //void
    returnsym,
    mainsym,
    ifsym,
    elsesym,
    whilesym,
    scanfsym,
    printfsym,
}
