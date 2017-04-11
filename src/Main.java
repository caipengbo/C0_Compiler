import compiler.interpret.Interpreter;

public class Main {

    public static void main(String[] args) {

        Interpreter interpreter = new Interpreter();
        interpreter.open("src/equal.txt");
        //interpreter.printInstructionList();
        //File file  = new File("src/out.txt");
        //interpreter.writeToFile(file);
        try {
            interpreter.interpret();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("目标代码有问题");
        }


    }
}
