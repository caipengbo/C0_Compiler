package compiler.interpret;

/**
 * Title：指令（目标代码）格式
 * Description：
 * Created by Myth on 4/8/2017.
 */
public class Instruction {
    private InstructionType name;  //指令名字
    private int layer; //层数（0全局变量、1当前层的变量）
    private int third; //指令的第三个数

    public InstructionType getName() {
        return name;
    }

    public void setName(InstructionType name) {
        this.name = name;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getThird() {
        return third;
    }

    public void setThird(int third) {
        this.third = third;
    }

    @Override
    public String toString() {
        return name + " " + layer + " " +third;
    }
}
