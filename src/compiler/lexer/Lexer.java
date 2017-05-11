package compiler.lexer;

import compiler.common.Symbol;
import compiler.common.Word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title：词法分析器
 * Description：分析源程序的每一个字符，组合成单词，将单词转化成符号
 * Created by Myth on 4/27/2017.
 */
public class Lexer {
    //每一行C0源代码
    private List<String> sourceCodeLineList;

    //当前源代码行数
    private int lineSize = 0;
    //源代码最大行数
    private static final int MAX_LINE_SIZE = 100;



    //指针  代表 行(-1代表终止) 、 行内位置(-1代表终止)
    private int lineNumber;
    private int position;

    private String currentLine; //当前代码行
    private Map<String,Symbol> keywords; //关键字
    private Map<String,Symbol> singleWords; //单字符
    public Lexer() {
        lineNumber = 0;
        position = 0;
        //关键字初始化
        keywords = new HashMap<>();
        keywords.put("int",Symbol.intsym);
        keywords.put("void",Symbol.voidsym);
        keywords.put("main",Symbol.mainsym);
        keywords.put("return",Symbol.returnsym);
        keywords.put("if",Symbol.ifsym);
        keywords.put("else",Symbol.elsesym);
        keywords.put("while",Symbol.whilesym);
        keywords.put("scanf",Symbol.scanfsym);
        keywords.put("printf",Symbol.printfsym);
        //单字符符号
        singleWords = new HashMap<>();
        singleWords.put("+",Symbol.plus);
        singleWords.put("-",Symbol.minus);
        singleWords.put("*",Symbol.multiply);
        singleWords.put("/",Symbol.divide);
        singleWords.put("=",Symbol.equal);
        singleWords.put("(",Symbol.lbracket);
        singleWords.put(")",Symbol.rbracket);
        singleWords.put("{",Symbol.lbrace);
        singleWords.put("}",Symbol.rbrace);
        singleWords.put(",",Symbol.comma);
        singleWords.put(";",Symbol.semicolon);
    }
    public List<String> getSourceCodeLineList() {
        return sourceCodeLineList;
    }
    /**
     * 打开原文件，读取每一行原文件存至
     * @param pathname
     * @throws Exception
     */
    public void openFile(String pathname) throws Exception{
        File file = new File(pathname);
        BufferedReader bufferedReader;
        bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        sourceCodeLineList = new ArrayList<>();
        while((line = bufferedReader.readLine())!=null) {
            sourceCodeLineList.add(line);
        }
        lineSize = sourceCodeLineList.size();
        if (lineSize > MAX_LINE_SIZE) {
            lineSize = 0;
            throw new Exception("源程序过长！");
        }
        if (lineSize == 0) {
            lineSize = 0;
            throw new Exception("源程序不能为空！");
        }
        currentLine = sourceCodeLineList.get(0);
    }

    // 前移位置
    private void forth() {
        if (position >= currentLine.length()-1) {
            lineNumber++;
            position = 0;
        } else {
            position++;
        }
        if (lineNumber >= lineSize) {
            lineNumber = -1;
            position = -1;
            return;
        }
        currentLine = sourceCodeLineList.get(lineNumber);
    }
    /**
     * 获得一个词
     * @return 获得的词
     * @throws Exception
     */
    public Word getWord() throws Exception {
        Word word = new Word();
        if (lineNumber ==-1 && position==-1) {
            word.setType(Symbol.endsym); //终止位置，终止
            return word;
        }
        String wordValue = "";
        char currentChar = currentLine.charAt(position);
        //过滤开头空格
        while (currentChar == ' ') {
            forth();
            currentChar = currentLine.charAt(position);
        }
        //System.out.println("("+ lineNumber + "," + position +")"+" 字符：" + currentLine.charAt(position));
        if (currentChar >= 'a'&& currentChar <= 'z')
        {
            // 名字或保留字以a..z开头
            do
            {
                wordValue += currentChar;
                forth();
                if (lineNumber ==-1 && position==-1) {
                    if (!("".equals(wordValue))) {
                        word.setType(Symbol.ident);
                        word.setValue(wordValue);
                    } else {
                        word.setType(Symbol.endsym); //终止位置，终止
                    }
                    return word;
                }
                currentChar = currentLine.charAt(position);
            } while (currentChar >= 'a'&&currentChar <= 'z' || currentChar >= '0'&&currentChar <= '9');
            //是否为关键字
            if (keywords.containsKey(wordValue)) {
                word.setType(keywords.get(wordValue));
            } else {
                word.setType(Symbol.ident);
            }
        } else {
            /* 检测是否为数字：以0..9开头 */
            if (currentChar >= '0'&&currentChar <= '9')
            {
                do
                {
                    wordValue += currentChar;
                    forth();
                    if (lineNumber ==-1 && position==-1) {
                        word.setType(Symbol.endsym); //终止位置，终止
                        return word;
                    }
                    currentChar = currentLine.charAt(position);
                } while (currentChar >= '0'&&currentChar <= '9'); /* 获取数字的值 */
                word.setType(Symbol.number);
            }
            else {
                //检测单字符
                wordValue += currentChar;
                if (singleWords.containsKey(wordValue)) {
                    word.setType(singleWords.get(wordValue));
                } else {
                    word.setType(Symbol.nul);
                }
                forth();
            }
        }
        word.setValue(wordValue);
        return word;
    }
    public String getPosition() {
        return "(Ln: " + lineNumber + "Col: " + position + ")";
    }
}
