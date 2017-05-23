import compiler.interpret.Interpreter;

public class Main {

    public static void main(String[] args) throws Exception {
        Interpreter interpreter = new Interpreter();

        interpreter.openFile("src/factorial2.txt");
        //interpreter.printInstructionList();
        //File file  = new File("src/out.txt");
        //interpreter.writeToFile(file);
        interpreter.interpret();

    }
}