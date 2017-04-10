import compiler.interpret.Interpreter;

public class Main {

    public static void main(String[] args) {

        Interpreter interpreter = new Interpreter();
        interpreter.open("src/code.txt");
        //interpreter.printInstructionList();
        //File file  = new File("src/out.txt");
        //interpreter.writeToFile(file);
        interpreter.interpret();
    }
}
