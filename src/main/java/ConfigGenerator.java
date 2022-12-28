import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/***
 * Reads and writes instructions to conf.json
 */
public class ConfigGenerator implements IConfigGenerator{
    private LinkedHashMap<String, Instruction> instructionMap= new LinkedHashMap<>();
    final private String filePath;

    public ConfigGenerator(Path path){
        this.filePath= String.valueOf(path);
    }

    @Override
    public void addInstruction(Instruction instruction) {
        if(instructionMap.containsKey(instruction.getInstruction())) instructionMap.remove(instruction.getInstruction());
        instructionMap.put(instruction.getInstruction(), instruction);
        writeToFile();
    }

    @Override
    public List <Instruction> readInstructions() {
        readFromFile();
        ArrayList<Instruction> temp = new ArrayList<>();
        for(String inst : instructionMap.keySet())
            temp.add(instructionMap.get(inst));
        return temp;
    }

    @Override
    public void deleteInstruction(String instruction) {
        readFromFile();
        instructionMap.remove(instruction);
        writeToFile();
    }

    @Override
    public void clearConf() {
        instructionMap.clear();
        writeToFile();
    }

    public Stack<String> getEditorQueue(){
        Stack<String> stack = new Stack<>();
        for(String inst : instructionMap.keySet())
            stack.add(inst);
        return stack;
    }

    private void writeToFile(){
        Gson gson= new Gson();
        Path path = Paths.get(filePath);
        String json="[";

        if(!instructionMap.isEmpty()){
            for(String inst : instructionMap.keySet()){
                json=json+gson.toJson(instructionMap.get(inst))+",\n";
            }
            json=json.substring(0,json.length()-2);
        }

        json=json+"]";
        try {
            Files.writeString(path, json);
        } catch (IOException ex) {
            System.out.println("Error. Cannot write to config file.");
        }
    }
    private void readFromFile(){
        String line;
        Gson gson=new Gson();
        instructionMap.clear();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null){
                if(line.startsWith("[")) line=line.substring(1,line.length());
                if(line.endsWith("]")||line.endsWith(",")) line=line.substring(0,line.length()-1);
                Instruction instruction= gson.fromJson(line,Instruction.class);
                instructionMap.put(instruction.getInstruction(), instruction);
            }
        } catch (IOException e) {
            System.out.println("Cannot load config file");
            e.printStackTrace();
        }
    }
}