import javazoom.jl.decoder.JavaLayerException;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Applying instructions from conf.json file to project
 */
public interface ISoundEditor {
    void apply() throws UnsupportedAudioFileException, IOException, JavaLayerException;

}
