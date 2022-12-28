import javazoom.jl.decoder.JavaLayerException;
import org.apache.commons.io.FilenameUtils;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


/**
 * Applying instructions from conf.json file to project
 */
public class SoundEditor implements ISoundEditor{


    private List<Instruction> instructionList = new ArrayList<>();
    ConfigGenerator configGenerator;
    ArrayList<Path> pathList;
    FileMenager fm = new FileMenager();
    Path ori;
    Audio a;


    public SoundEditor(ConfigGenerator gen, Audio audio, ArrayList pathList) throws UnsupportedAudioFileException, IOException {
        this.pathList = pathList;
        this.configGenerator = gen;
        this.a = audio;
    }

    public enum Operations {
        GWOZDZIUJ,
        CHANGEVOLUME,
        MLOTUJ,
        CHANGESPEED,
        CROP,
        JOIN;

    }

    public void setAudio(Path path) throws UnsupportedAudioFileException, IOException {
        this.a = fm.loadFromProject(String.valueOf(path));
    }

    @Override
    public void apply() throws UnsupportedAudioFileException, IOException, JavaLayerException {

        Instruction instruction;
       instructionList=configGenerator.readInstructions();
       while(!instructionList.isEmpty()){
           instruction=instructionList.get(0);
           instructionList.remove(0);
           switch (Operations.valueOf(instruction.getInstruction().toUpperCase())){
               case GWOZDZIUJ:
                   a=a.gwozdziuj();
                   break;
               case CHANGEVOLUME:
                   a=a.changeVolume(Double.parseDouble(instruction.getParameters().get(0)));
                   break;
               case MLOTUJ:
                   a=a.mlotuj(Float.parseFloat(instruction.getParameters().get(0)));
                   break;
               case CHANGESPEED:
                   a=a.changeSpeed(Double.parseDouble(instruction.getParameters().get(0)));
                    break;
               case CROP:
                   a=a.crop(Double.parseDouble(instruction.getParameters().get(0)), Double.parseDouble(instruction.getParameters().get(1)));
                   break;
               case JOIN:
                   fm.fileCopier(Path.of(instruction.getParameters().get(0)), pathList.get(3),REPLACE_EXISTING);
                   if(instruction.getParameters().get(1).equals("0")){
                       a=a.join(new Audio(String.valueOf(pathList.get(3))));
                   }else a=new Audio(String.valueOf(pathList.get(3))).join(new Audio(String.valueOf(pathList.get(4))));
           }
       }
    }

    public Audio getAudio(){
        return a;
    }


}
