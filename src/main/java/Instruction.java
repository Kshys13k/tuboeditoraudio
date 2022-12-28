import java.util.List;

/**
 * Class object stores instruction and parameters.
 */
public class Instruction {
    final private String instruction;
    final private List<String>parameters;

    public Instruction(String instruction, List<String> parameters) {
        this.instruction = instruction;
        this.parameters = parameters;
    }

    public String getInstruction() {
        return instruction;
    }

    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
       StringBuilder stringBuilder=new StringBuilder();
       stringBuilder.append("Instruction: ");
       stringBuilder.append(instruction+ " ");
       for(int i=0;i<parameters.size();i++){
           stringBuilder.append(parameters.get(i));
       }
       return stringBuilder.toString();
   }
}
