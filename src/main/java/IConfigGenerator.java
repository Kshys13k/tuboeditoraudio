import java.util.List;


/***
 * Reads and writes instructions to conf.json
 */
public interface IConfigGenerator {
    //adding new instruction to config file
    public void addInstruction(Instruction instruction);

    //reading one instruction from config file
    public List<Instruction> readInstructions();

    //deleting given element
    public void deleteInstruction(String instruction);

    //deleting all instructions from config file
    public void clearConf();
}
