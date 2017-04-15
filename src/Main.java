import compiler.interpret.Interpreter;

public class Main {

    public static void main(String[] args) {

        Interpreter interpreter = new Interpreter();
<<<<<<< HEAD
        try {
            interpreter.openFile("src/test.txt");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //interpreter.printInstructionList();
        //File file  = new File("src/out.txt");
        //interpreter.writeToFile(file);
        try {
            interpreter.interpret();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("目标代码有问题");
            return;
        }
=======
        interpreter.open("src/out.txt");
        interpreter.printInstructionList();
        //File file  = new File("src/out.txt");
        //interpreter.writeToFile(file);
//        try {
//            interpreter.interpret();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("目标代码有问题");
//        }
>>>>>>> fbd6473bab90808cb056c6d7f858f19bfbf04442
    }
}
