import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
//import org.json.JSONObject;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileMenager{



    public FileMenager(){

    }

    /**
     * Creates new project and if needed new SoundEditor folder
     * @param name
     * @param originalAudio
     * @return
     * @throws IOException
     * @throws JavaLayerException
     */
    public ArrayList<Path> createNewProject(String name , Path originalAudio) throws IOException, JavaLayerException {

        ArrayList<Path> pathList = getPaths(name);

        File projectFolder = new File(String.valueOf(pathList.get(0))) ;
        File conf = new File(String.valueOf(pathList.get(2)));
        projectFolder.mkdirs();
        PrintWriter out = new PrintWriter(conf);
        //JSONObject jo = new JSONObject();

         try {
             fileCopier(originalAudio, pathList.get(1), null);
             //out.print(jo);
             out.close();

         }catch (FileAlreadyExistsException e){
             System.out.println("File already exists. Do you want to overwrite? [Y/N]");
             Scanner sc = new Scanner(System.in);
             if(sc.next().toUpperCase().equals("Y")) {
                 fileCopier(originalAudio, pathList.get(1),  REPLACE_EXISTING);
                 //out.print(jo);
                 out.close();
             }
             sc.close();
         }
         return pathList;

    }

    /**
     * Copies sound files to project folder and if needed converts them to WAV
     * @param file
     * @param dest
     * @param opt
     * @throws JavaLayerException
     * @throws IOException
     */
    public void fileCopier(Path file, Path dest, CopyOption opt) throws JavaLayerException, IOException {

        try {
            if(FilenameUtils.getExtension(String.valueOf(file)).equals("mp3")){
                new Converter().convert(String.valueOf(file), String.valueOf(dest));
            }
            else {
                if(opt == REPLACE_EXISTING) {
                    Files.copy(file, dest, REPLACE_EXISTING);
                }else  Files.copy(file, dest);
            }
        }catch (JavaLayerException e){
            e.printStackTrace();
        }

    }

    /**
     * Returns path list to project with given name
     * @param name
     * @return
     */
    public static ArrayList<Path> getPaths(String name){
        Path path = null;
        Path pathOri = null;
        Path pathConf = null;
        Path copy = null;
        Path edited = null;
        if(SystemUtils.IS_OS_WINDOWS) {
            path = Path.of(FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath() + "\\EdytorAudio\\" + name + ".umc");
            pathOri = Path.of(FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath() + "\\EdytorAudio\\" + name + ".umc" + "\\original.wav");
            pathConf = Path.of(FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath() + "\\EdytorAudio\\" + name + ".umc" + "\\config.json");
            copy = Path.of(FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath() + "\\EdytorAudio\\" + name + ".umc" + "\\copy.wav");
            edited = Path.of(FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath() + "\\EdytorAudio\\" + name + ".umc" + "\\edited.wav");
        }
        else{
            path = Path.of(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/EdytorAudio/" + name + ".umc");
            pathOri = Path.of(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/EdytorAudio/" + name + ".umc" + "/original.wav");
            pathConf = Path.of(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/EdytorAudio/" + name + ".umc" + "/config.json");
            copy = Path.of(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/EdytorAudio/" + name + ".umc" + "/copy.wav");
            edited = Path.of(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/EdytorAudio/" + name + ".umc" + "/edited.wav");
        }
        ArrayList<Path> pathList = new ArrayList<>(Arrays.asList(path, pathOri, pathConf,copy,edited));
        return pathList;
    }


    /**
     * Loads audio from given project
     * @param path
     * @return
     * @throws UnsupportedAudioFileException
     * @throws IOException
     */
    public Audio loadFromProject(String path) throws UnsupportedAudioFileException, IOException {
        return new Audio(path);
    }

}
