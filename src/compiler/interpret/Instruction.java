package compiler.interpret;

/**
 * Title：指令（目标代码）格式
 * Description：
 * Created by Myth on 4/8/2017.
 */
public class Instruction {
    private InstructionType name;  //指令名字
    private int layerDiff; //层差
    private int third; //指令的第三个数

    public InstructionType getName() {
        return name;
    }

    public void setName(InstructionType name) {
        this.name = name;
    }

    public int getLayerDiff() {
        return layerDiff;
    }

    public void setLayerDiff(int layerDiff) {
        this.layerDiff = layerDiff;
    }

    public int getThird() {
        return third;
    }

    public void setThird(int third) {
        this.third = third;
    }

    @Override
    public String toString() {
        return name + " " + layerDiff + " " +third;
    }
}
