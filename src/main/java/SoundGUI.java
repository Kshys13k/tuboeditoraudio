import javazoom.jl.decoder.JavaLayerException;
import org.apache.commons.lang3.SystemUtils;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;


public class SoundGUI extends JFrame implements ActionListener, ChangeListener, WindowListener {

    private final JMenuItem mOpen, mSave, mExit;
    FileMenager fm = new FileMenager();
    ArrayList<Path> pathList;
    ConfigGenerator cg;
    SoundEditor soundEditor;
    private JTextField cropFrom, cropTo;
    private final JCheckBox gwozdziuj;
    private final JSlider soundVolume, mlotowanie, changeSpeed;
    private JPanel loadPanel, wavPanel, soundEditorPanel, WavePanel;
    public JPanel ACPanel1, ACPanel2;
    public JLabel pic, nowPlaying, VolumeLabel, mlotujLabel, speedLabel, cropLabel, folderLabel,loadLabel;
    public AudioControlPanel audioControlPanel;
    private final JButton loadButton, apply3Button,
            back4Button, restartButton, NFButton, cropButton, orginalButton, editedButton, concatLeft, concatRight;
    private String editedPath;

    int folders = 0;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    float frameWidth;
    float frameHeight;

    public SoundGUI(float a) throws UnsupportedAudioFileException, MidiUnavailableException, InvalidMidiDataException, LineUnavailableException, IOException {

        addWindowListener(this);
        // FRAME CONFIG
        this.frameWidth = (float) 0.8*screenSize.width*a;
        this.frameHeight = (float) 0.9*screenSize.height*a;

        setSize((int)frameWidth, (int)frameHeight);

        System.out.println(screenSize.width);
        System.out.println(frameWidth);
        System.out.println(frameHeight);
        System.out.println(screenSize.height);

        setTitle("TUBOEDYTORAUDIO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);


        /*

        MENU

         */

        JMenuBar menuBar = new JMenuBar();

        // FILE

        JMenu menuFile = new JMenu("File");
        mOpen = new JMenuItem("Open");
        mOpen.addActionListener(this);
        mOpen.setEnabled(false);
        mSave = new JMenuItem("Save");
        mSave.addActionListener(this);

        mExit = new JMenuItem("Exit");
        mExit.addActionListener(this);

        menuFile.add(mOpen);
        menuFile.addSeparator();
        menuFile.add(mSave);
        menuFile.addSeparator();
        menuFile.add(mExit);

        // HELP

        JMenu menuHelp = new JMenu("Help");
        JMenuItem mHelp = new JMenuItem("Help");

        menuHelp.add(mHelp);

        // TOOLS

        JMenu menuTools = new JMenu("Tools");
        menuTools.setEnabled(false);

        setJMenuBar(menuBar);
        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        menuBar.add(menuTools);


         /*

        PANELS

         */


        //LOAD PANEL----------------------------------------------------------------------------------------------------

        loadPanel = new JPanel();
        loadPanel.setBounds(20, 20, (int)(0.3*frameWidth), (int)(0.315*frameHeight));
        loadPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        loadPanel.setLayout(null);

        loadLabel = new JLabel("Project Folder");
        loadLabel.setBounds((int)(0.3*0.25*frameWidth), 5, (int)(0.6666*0.3*frameWidth), (int)(0.1905*0.315*frameHeight));

        loadLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(33*0.3*frameWidth/450)));
        loadPanel.add(loadLabel);

        NFButton = new JButton("New Project");
        NFButton.setBounds((int)(0.53*0.3*frameWidth), (int)(0.44444*0.315*frameHeight), (int)(0.44444*0.3*frameWidth), (int)(0.35*0.315*frameHeight));
        NFButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(15*0.3*frameWidth/450)));
        loadPanel.add(NFButton);
        NFButton.addActionListener(this);

        loadButton = new JButton("Load Project");
        loadButton.setBounds((int)(0.033333*0.3*frameWidth), (int)(0.44444*0.315*frameHeight), (int)(0.44444*0.3*frameWidth), (int)(0.35*0.315*frameHeight));
        loadButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(15*0.3*frameWidth/450)));
        loadPanel.add(loadButton);
        loadButton.addActionListener(this);
        this.add(loadPanel);

        folderLabel = new JLabel();
        folderLabel.setBounds((int)(0.17777*0.3*frameWidth), (int)(0.873*0.315*frameHeight), (int)(0.66666*0.3*frameWidth), (int)(0.095*0.315*frameHeight));
        folderLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(11*0.3*frameWidth/450)));
        loadPanel.add(folderLabel);


        //SOUND EDITOR PANEL--------------------------------------------------------------------------------------------

        soundEditorPanel = new JPanel();
        soundEditorPanel.setBounds(20, (int)(0.35*frameHeight), (int)(0.3215*frameWidth), (int)(0.525*frameHeight));
        soundEditorPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        soundEditorPanel.setLayout(null);


        JLabel SELabel = new JLabel("Audio modifiers");
        SELabel.setBounds((int)(0.255*0.3215*frameWidth), 5, (int)(0.65*0.35*frameWidth), (int)(0.1143*0.525*frameHeight));
        SELabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(31*0.3215*frameWidth/510)));
        soundEditorPanel.add(SELabel);

        VolumeLabel = new JLabel("Volume level");
        VolumeLabel.setBounds((int)(0.0196*0.3215*frameWidth), (int)(0.525*frameHeight*0.13333), (int)(0.294*0.3215*frameWidth), (int)(0.1429*0.525*frameHeight));
        VolumeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(16*0.3215*frameWidth/510)));
        soundEditorPanel.add(VolumeLabel);

        mlotujLabel = new JLabel("Młotowanie");
        mlotujLabel.setBounds((int)(0.0196*0.3215*frameWidth), (int)(0.525*frameHeight*0.2762), (int)(0.294*0.3215*frameWidth), (int)(0.1429*0.525*frameHeight));
        mlotujLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(14*0.3215*frameWidth/510)));
        soundEditorPanel.add(mlotujLabel);

        speedLabel = new JLabel("Speed");
        speedLabel.setBounds((int)(0.0196*0.3215*frameWidth), (int)(0.525*frameHeight*0.419), (int)(0.294*0.3215*frameWidth), (int)(0.1429*0.525*frameHeight));
        speedLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(16*0.3215*frameWidth/510)));
        soundEditorPanel.add(speedLabel);

        cropLabel = new JLabel("Cropping");
        cropLabel.setBounds((int)(0.0196*0.3215*frameWidth), (int)(0.525*frameHeight*0.5714), (int)(0.294*0.2157*frameWidth), (int)(0.0952*0.525*frameHeight));
        cropLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(16*0.3215*frameWidth/510)));
        soundEditorPanel.add(cropLabel);


        cropFrom = new JTextField("From");
        cropFrom.setBounds((int)(0.2941*0.3215*frameWidth), (int)(0.525*frameHeight*0.581), (int)(0.147*0.3215*frameWidth), (int)(0.0857*0.525*frameHeight));
        cropFrom.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(13*0.3215*frameWidth/510)));
        soundEditorPanel.add(cropFrom);

        cropTo = new JTextField("To");
        cropTo.setBounds((int)(0.5294*0.3215*frameWidth), (int)(0.525*frameHeight*0.581), (int)(0.147*0.3215*frameWidth), (int)(0.0857*0.525*frameHeight));
        cropTo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(13*0.3215*frameWidth/510)));
        soundEditorPanel.add(cropTo);


        cropButton = new JButton("Crop");
        cropButton.setBounds((int)(0.7647*0.3215*frameWidth), (int)(0.525*frameHeight*0.581), (int)(0.176*0.3215*frameWidth), (int)(0.0857*0.525*frameHeight));
        cropButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(11*0.3215*frameWidth/510)));
        soundEditorPanel.add(cropButton);
        cropButton.addActionListener(this);

        soundVolume = new JSlider(0, 200, 100);
        soundVolume.setBounds((int)(0.2549*0.3215*frameWidth), (int)(0.525*frameHeight*0.1429), (int)(0.65*0.3215*frameWidth), (int)(0.1429*0.525*frameHeight));
        soundVolume.setMajorTickSpacing(50);
        soundVolume.setPaintTicks(true);
        soundVolume.setPaintLabels(true);
        soundEditorPanel.add(soundVolume);
        soundVolume.addChangeListener(this);

        mlotowanie = new JSlider(0, 100, 0);
        mlotowanie.setBounds((int)(0.2549*0.3215*frameWidth), (int)(0.525*frameHeight*0.2952), (int)(0.65*0.3215*frameWidth), (int)(0.1429*0.525*frameHeight));
        mlotowanie.setMajorTickSpacing(25);
        mlotowanie.setPaintTicks(true);
        mlotowanie.setPaintLabels(true);
        soundEditorPanel.add(mlotowanie);
        mlotowanie.addChangeListener(this);

        changeSpeed = new JSlider(0, 200, 100);
        changeSpeed.setBounds((int)(0.2549*0.3215*frameWidth), (int)(0.525*frameHeight*0.438), (int)(0.65*0.3215*frameWidth), (int)(0.1429*0.525*frameHeight));
        changeSpeed.setMajorTickSpacing(50);
        changeSpeed.setPaintTicks(true);
        changeSpeed.setPaintLabels(true);
        soundEditorPanel.add(changeSpeed);
        changeSpeed.addChangeListener(this);


        Icon icon = new ImageIcon("apply.png");
        Icon icon2 = new ImageIcon("cofnij.png");
        Icon icon3 = new ImageIcon("restart.png");


        back4Button = new JButton(icon2);
        back4Button.setBounds((int)(0.8666*0.3215*frameWidth), (int)(0.842*0.525*frameHeight), 35, 35);
        back4Button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(17*0.3215*frameWidth/510)));
        back4Button.setBorder(null);
        soundEditorPanel.add(back4Button);
        back4Button.addActionListener(this);

        restartButton = new JButton(icon3);
        restartButton.setBounds((int)(0.06*0.3215*frameWidth), (int)(0.842*0.525*frameHeight), 35, 35);
        restartButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(9*0.3215*frameWidth/510)));
        restartButton.setBorder(null);
        soundEditorPanel.add(restartButton);
        restartButton.addActionListener(this);

        apply3Button = new JButton(icon);
        apply3Button.setBounds((int)(0.4549*0.3215*frameWidth), (int)(0.842*0.525*frameHeight), 35, 35);
        apply3Button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(9*0.3215*frameWidth/510)));
        apply3Button.setBorder(null);
        soundEditorPanel.add(apply3Button);
        apply3Button.addActionListener(this);

        JLabel resLabel = new JLabel("Restart");
        resLabel.setBounds((int)(0.06*0.3215*frameWidth), (int)(0.89*0.525*frameHeight), (int)(0.1*0.3215*frameWidth), (int)(0.0762*0.525*frameHeight));
        resLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(12*0.3215*frameWidth/510)));
        soundEditorPanel.add(resLabel);

        JLabel applyLabel = new JLabel("Apply");
        applyLabel.setBounds((int)(0.46*0.3215*frameWidth), (int)(0.89*0.525*frameHeight), (int)(0.1*0.3215*frameWidth), (int)(0.0762*0.525*frameHeight));
        applyLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(12*0.3215*frameWidth/510)));
        soundEditorPanel.add(applyLabel);

        JLabel backLabel = new JLabel("Back");
        backLabel.setBounds((int)(0.875*0.3215*frameWidth), (int)(0.89*0.525*frameHeight), (int)(0.1*0.3215*frameWidth), (int)(0.0762*0.525*frameHeight));
        backLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(12*0.3215*frameWidth/510)));
        soundEditorPanel.add(backLabel);

        this.add(soundEditorPanel);


        concatLeft = new JButton("Concat. Left");
        concatLeft.setBounds((int)(0.0196*0.3215*frameWidth), (int)(0.7*0.525*frameHeight), (int)(0.2*0.3215*frameWidth), (int)(0.0857*0.525*frameHeight));
        concatLeft.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(11*0.3215*frameWidth/510)));
        soundEditorPanel.add(concatLeft);
        concatLeft.addActionListener(this);
        concatLeft.setEnabled(false);

        concatRight = new JButton("Concat. Right");
        concatRight.setBounds((int)(0.7647*0.3215*frameWidth), (int)(0.7*0.525*frameHeight), (int)(0.2*0.3215*frameWidth), (int)(0.0857*0.525*frameHeight));
        concatRight.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(11*0.3215*frameWidth/510)));
        soundEditorPanel.add(concatRight);
        concatRight.addActionListener(this);
        concatRight.setEnabled(false);


        gwozdziuj = new JCheckBox("Gwozdziuj");
        gwozdziuj.setBounds((int)(0.3824*0.3215*frameWidth), (int)(0.6863*0.525*frameHeight), (int)(0.4412*0.3215*frameWidth), (int)(0.1143*0.525*frameHeight));
        gwozdziuj.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(25*0.3215*frameWidth/510)));
        soundEditorPanel.add(gwozdziuj);
        gwozdziuj.addActionListener(this);


        //AUDIO CONTROL PANEL-------------------------------------------------------------------------------------------


        ACPanel1 = new JPanel();
        ACPanel1.setBounds((int)(0.36*frameWidth), (int)(0.02*frameHeight), (int)(0.61*frameWidth), (int)(0.25*frameHeight));
        ACPanel1.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        ACPanel1.setLayout(new BorderLayout());
        this.add(ACPanel1);

        ACPanel2 = new JPanel();
        ACPanel2.setBounds((int)(0.36*frameWidth), (int)(0.265*frameHeight), (int)(0.61*frameWidth), (int)(0.07*frameHeight));
        ACPanel2.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        ACPanel2.setLayout(null);

        nowPlaying = new JLabel("");
        nowPlaying.setBounds((int)(0.2*frameWidth), (int)(0.017*frameHeight), (int)(0.3*frameWidth), (int)(0.045*frameHeight));
        nowPlaying.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(12*0.3*frameWidth/450)));
        ACPanel2.add(nowPlaying);

        orginalButton = new JButton("Orginal audio");
        orginalButton.setBounds((int)(0.01666*frameWidth), (int)(0.01*frameHeight), (int)(0.1*frameWidth), (int)(0.05*frameHeight));
        orginalButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(14*0.3*frameWidth/450)));
        ACPanel2.add(orginalButton);
        orginalButton.addActionListener(this);

        editedButton = new JButton("Edited audio");
        editedButton.setBounds((int)(0.49*frameWidth), (int)(0.01*frameHeight), (int)(0.1*frameWidth), (int)(0.05*frameHeight));
        editedButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int)(14*0.3*frameWidth/450)));
        ACPanel2.add(editedButton);
        editedButton.addActionListener(this);


        this.add(ACPanel2);




        //WVE VISUALISATION PANEL---------------------------------------------------------------------------------------
        WavePanel = new JPanel();
        WavePanel.setBounds((int)(0.36*frameWidth), (int)(0.35*frameHeight), (int)(0.61*frameWidth), (int)(0.45*frameHeight));
        WavePanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        this.add(WavePanel);

        wavPanel = new JPanel();

        editedButton.setEnabled(false);
        restartButton.setEnabled(false);
        apply3Button.setEnabled(false);
        back4Button.setEnabled(false);
        cropButton.setEnabled(false);
        orginalButton.setEnabled(false);
        gwozdziuj.setEnabled(false);
        soundVolume.setEnabled(false);
        mlotowanie.setEnabled(false);
        changeSpeed.setEnabled(false);
        cropTo.setEnabled(false);
        cropFrom.setEnabled(false);

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        JFileChooser fileChooser;
        File file = null;
        if (source == mExit) {
            if(folders>0) {
                try {
                    Files.delete(getPathTo("wavVis.png"));

                    

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }



                dispose();
            }
        } else if (source == mSave) {
            if (folders > 0) {
                String editedName = JOptionPane.showInputDialog("Enter edited file name");
                editedName = editedName.replaceAll(".wav", "");
                while (editedName.equals("")) {
                    editedName = JOptionPane.showInputDialog("Enter  edited file name, file name cannot be empty!");
                }

                fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select folder to save in");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    try {
                        if(SystemUtils.IS_OS_WINDOWS) {
                            soundEditor.getAudio().save(file.getPath() + "\\" + editedName + ".wav");
                            System.out.println(file.getPath() + "\\" + editedName + ".wav");
                        }else {
                            soundEditor.getAudio().save(file.getPath() + "/" + editedName + ".wav");
                            System.out.println(file.getPath() + "/" + editedName + ".wav");
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    try {
                        Files.delete(getPathTo("wavVis.png"));
                        

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                }

            }
        }



        if (source == loadButton) {

            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }
            pathList = FileMenager.getPaths(file.getName().replaceAll(".umc", ""));
            cg = new ConfigGenerator(pathList.get(2));
            try {
                soundEditor = new SoundEditor(cg, fm.loadFromProject(String.valueOf(pathList.get(1))), pathList);
                try {
                    soundEditor.apply();
                }catch (NullPointerException npe){
                    System.err.println("No instructions to load");
                } catch (JavaLayerException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (UnsupportedAudioFileException | IOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println(pathList.toString());

            folderLabel.setText(String.valueOf(pathList.get(0)));

            try {
                audioControlPanel = new AudioControlPanel(new File(String.valueOf(pathList.get(1))), false);
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | MidiUnavailableException | InvalidMidiDataException ioException) {
                ioException.printStackTrace();
            }

            editedButton.setEnabled(true);
            restartButton.setEnabled(true);
            if(!cg.getEditorQueue().isEmpty())
                back4Button.setEnabled(true);

            cropButton.setEnabled(true);
            orginalButton.setEnabled(true);
            gwozdziuj.setEnabled(true);
            soundVolume.setEnabled(true);
            mlotowanie.setEnabled(true);
            changeSpeed.setEnabled(true);
            cropTo.setEnabled(true);
            cropFrom.setEnabled(true);
            concatLeft.setEnabled(true);
            concatRight.setEnabled(true);

            if(folders>0){
                try {
                    Files.delete(getPathTo("wavVis.png"));
                    

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            folders++;

            orginalButton.doClick();

        } else if (source == editedButton) {

            try {
                soundEditor.getAudio().save(String.valueOf(getPathTo("edited.wav")));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            editedPath = String.valueOf(getPathTo("edited.wav"));

            try {
                WavCreate wc = new WavCreate(editedPath, String.valueOf(getPathTo("wavVis.png")),(int)(0.61*frameWidth),(int)(0.45*frameHeight));
                System.out.println(frameHeight);
                System.out.println(frameWidth);
                wc.createAudioInputStream();
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }

            WavePanel.remove(wavPanel);

            wavPanel = new JPanel();
            wavPanel.setBounds(0, 0, (int)(0.61*frameWidth), (int)(0.45*frameHeight));

            BufferedImage img = null;
            try {
                img = ImageIO.read(new File( String.valueOf(getPathTo("wavVis.png"))));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            pic = new JLabel(new ImageIcon(img));
            wavPanel.add(pic);
            wavPanel.repaint();
            this.repaint();
            WavePanel.add(wavPanel);
            this.repaint();

            if (audioControlPanel.clip.getMicrosecondPosition() == 0)
                audioControlPanel.clip.setMicrosecondPosition(1000);
            audioControlPanel.reset();

            ACPanel1.remove(audioControlPanel);
            ACPanel1.repaint();

            try {
                audioControlPanel = new AudioControlPanel(new File(editedPath), false);
                audioControlPanel.play();
                audioControlPanel.clip.stop();

                audioControlPanel.repaint();

                nowPlaying.setText(String.valueOf(pathList.get(1)));


            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | MidiUnavailableException | InvalidMidiDataException ioException) {
                ioException.printStackTrace();
            }
            ACPanel1.add(audioControlPanel);
            audioControlPanel.repaint();
            ACPanel1.repaint();
            nowPlaying.setText(editedPath);
        } else if (source == apply3Button) {
            System.out.println("test");
            try {
                soundEditor.setAudio(pathList.get(1));
            } catch (UnsupportedAudioFileException | IOException unsupportedAudioFileException) {
                unsupportedAudioFileException.printStackTrace();
            }
            try {
                soundEditor.apply();
            } catch (UnsupportedAudioFileException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (JavaLayerException ex) {
                throw new RuntimeException(ex);
            }
            editedButton.doClick();

            apply3Button.setEnabled(false);

        } else if (source == restartButton) {

            cg.clearConf();
            try {
                soundEditor.setAudio(pathList.get(1));
            } catch (UnsupportedAudioFileException | IOException unsupportedAudioFileException) {
                unsupportedAudioFileException.printStackTrace();
            }
            editedButton.doClick();
            audioControlPanel.clip.stop();
            apply3Button.setEnabled(false);


        }
        else if (source == back4Button) {

            cg.deleteInstruction(cg.getEditorQueue().peek());
            if(cg.getEditorQueue().isEmpty())
            back4Button.setEnabled(false);
            try {
                soundEditor.setAudio(pathList.get(1));
                soundEditor.apply();
            }catch (NullPointerException | UnsupportedAudioFileException npe){
                System.err.println("No instructions to load");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (JavaLayerException ex) {
                throw new RuntimeException(ex);
            }

            apply3Button.setEnabled(true);
            editedButton.doClick();

        }

        else if (source == NFButton) {

            String newProjName = JOptionPane.showInputDialog("Enter project folder name");
            while (newProjName.equals("")) {
                newProjName = JOptionPane.showInputDialog("Enter project folder name, folder name cannot be empty!");
            }
            System.out.println(newProjName);
            fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select .wav or .mp3 file");
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }

//            try {
//                pathList = fm.createNewProject(newProjName, file.toPath());
//                cg = new ConfigGenerator(pathList.get(2));
//                soundEditor = new SoundEditor(cg, fm.loadFromProject(String.valueOf(pathList.get(1))),pathList);
//            } catch (IOException | JavaLayerException | UnsupportedAudioFileException ex) {
//                throw new RuntimeException(ex);
//            }


            try {
                pathList = fm.createNewProject(newProjName, file.toPath());
                cg = new ConfigGenerator(pathList.get(2));
                soundEditor = new SoundEditor(cg, fm.loadFromProject(String.valueOf(pathList.get(1))), pathList);
                try {
                    soundEditor.apply();
                }catch (NullPointerException npe){
                    System.err.println("No instructions to load");
                } catch (JavaLayerException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (UnsupportedAudioFileException | IOException ex) {
                throw new RuntimeException(ex);
            }catch (JavaLayerException ex) {
                throw new RuntimeException(ex);
            }

            folderLabel.setText(String.valueOf(pathList.get(0)));
            try {
                audioControlPanel = new AudioControlPanel(new File(String.valueOf(pathList.get(1))), false);
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | MidiUnavailableException | InvalidMidiDataException ioException) {
                ioException.printStackTrace();
            }

            editedButton.setEnabled(true);
            restartButton.setEnabled(true);
            apply3Button.setEnabled(true);
            if(cg.getEditorQueue().isEmpty())
                back4Button.setEnabled(false);

            cropButton.setEnabled(true);
            orginalButton.setEnabled(true);
            gwozdziuj.setEnabled(true);
            soundVolume.setEnabled(true);
            mlotowanie.setEnabled(true);
            changeSpeed.setEnabled(true);
            cropTo.setEnabled(true);
            cropFrom.setEnabled(true);
            concatLeft.setEnabled(true);
            concatRight.setEnabled(true);

            if(folders>0){
                try {
                    Files.delete(getPathTo("wavVis.png"));

                    

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
            folders++;
            orginalButton.doClick();

        }

        else if(source == concatLeft){
            apply3Button.setEnabled(true);
            fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select audio file to join");
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }

            Instruction i1 = new Instruction("JOIN", Arrays.asList(file.getAbsolutePath(),"0"));
            cg.addInstruction(i1);

            apply3Button.doClick();

        }

        else if(source == concatRight){
            apply3Button.setEnabled(true);
            fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select audio file to join");
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }

            Instruction i1 = new Instruction("JOIN", Arrays.asList(file.getAbsolutePath(),"1"));

            cg.addInstruction(i1);

            apply3Button.doClick();


        }

                else if (source == orginalButton) {

            try {
                WavCreate wc = new WavCreate(String.valueOf(pathList.get(1)),  String.valueOf(getPathTo("wavVis.png")),(int)(0.61*frameWidth),(int)(0.45*frameHeight));
                wc.createAudioInputStream();
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }

            WavePanel.remove(wavPanel);

            wavPanel = new JPanel();
            wavPanel.setBounds(0, 0, (int)(0.61*frameWidth), (int)(0.45*frameHeight));

            BufferedImage img = null;
            try {
                img = ImageIO.read(new File(String.valueOf(getPathTo("wavVis.png"))));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            pic = new JLabel(new ImageIcon(img));
            wavPanel.add(pic);
            wavPanel.repaint();
            this.repaint();
            WavePanel.add(wavPanel);
            this.repaint();

            if (audioControlPanel.clip.getMicrosecondPosition() == 0)
                audioControlPanel.clip.setMicrosecondPosition(1000);

            audioControlPanel.reset();
            ACPanel1.remove(audioControlPanel);
            ACPanel1.repaint();

            try {
                audioControlPanel = new AudioControlPanel(new File(String.valueOf(pathList.get(1))), false);
                audioControlPanel.play();
                audioControlPanel.clip.stop();


                audioControlPanel.repaint();

                nowPlaying.setText(String.valueOf(pathList.get(1)));


            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | MidiUnavailableException | InvalidMidiDataException ioException) {
                ioException.printStackTrace();
            }
            ACPanel1.add(audioControlPanel);
            audioControlPanel.repaint();
            ACPanel1.repaint();
            nowPlaying.setText(String.valueOf(pathList.get(1)));


        } else if (source == gwozdziuj) {
            if (gwozdziuj.isSelected()) {
                Instruction i1 = new Instruction("gwozdziuj", Arrays.asList(""));
                cg.addInstruction(i1);
                System.out.println("gwozdziuje sie");
                apply3Button.setEnabled(true);
                back4Button.setEnabled(true);

            }
        } else if (source == cropButton) {
            cropFrom.setText(cropFrom.getText().replaceAll("\\D+", ""));
            cropTo.setText(cropTo.getText().replaceAll("\\D+", ""));
            if (!cropFrom.getText().equals("") || !cropTo.getText().equals("")) {
                Instruction i2 = new Instruction("crop", Arrays.asList(cropFrom.getText().replaceAll("\\D+", ""), cropTo.getText().replaceAll("\\D+", "")));
                cropFrom.setText(cropFrom.getText().replaceAll("\\D+", ""));
                cropTo.setText(cropTo.getText().replaceAll("\\D+", ""));
                System.out.println(i2);
                cg.addInstruction(i2);
                apply3Button.setEnabled(true);
                back4Button.setEnabled(true);
            }
        }


    }

    @Override
    public void stateChanged(ChangeEvent e) {

        JSlider source = (JSlider) e.getSource();
        float voiceLevel = source.getValue();
        float mlotowanieVal = source.getValue();
        float speedVal = source.getValue();

        if (!source.getValueIsAdjusting()) {
            int temp2 = ((JSlider) e.getSource()).getValue();

            Object source1 = e.getSource();
            if (source1 == soundVolume) {

                Instruction i3 = new Instruction("changeVolume", Arrays.asList(String.valueOf(voiceLevel / 100)));

                cg.addInstruction(i3);
                back4Button.setEnabled(true);
                apply3Button.setEnabled(true);

            } else if (source1 == mlotowanie) {


                Instruction i4 = new Instruction("mlotuj", Arrays.asList(String.valueOf(mlotowanieVal / 1000)));
                System.out.println("test");
                System.out.println(mlotowanieVal / 1000);
                cg.addInstruction(i4);
                apply3Button.setEnabled(true);
                back4Button.setEnabled(true);


            } else if (source1 == changeSpeed) {


                Instruction i5 = new Instruction("changeSpeed", Arrays.asList(String.valueOf(speedVal / 100)));
                System.out.println("test");
                System.out.println(speedVal);
                cg.addInstruction(i5);
                apply3Button.setEnabled(true);
                back4Button.setEnabled(true);


            }
        }

        if(source==soundVolume){
            VolumeLabel.setText("Volume: " + voiceLevel + "%");
        }

        else if (source == mlotowanie){
            mlotujLabel.setText("Mlotowanie: " + mlotowanieVal + "%");
        }

        else if (source == changeSpeed){
            speedLabel.setText("Speed: " + speedVal + "%");
        }
    }

    private Path getPathTo(String file){
        if(SystemUtils.IS_OS_WINDOWS) {
          return Path.of(pathList.get(0) + "\\" + file);
        }
        else{
           return Path.of(pathList.get(0) + "/" + file);
        }
        }

    public static void main(String[] args) throws UnsupportedAudioFileException, MidiUnavailableException, InvalidMidiDataException, LineUnavailableException, IOException {

        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        SoundGUI sg = new SoundGUI(1f);


        sg.setVisible(true);
    }


    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(folders>0) {

            try {
                Files.delete(getPathTo("wavVis.png"));

                

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            
        }
    }


    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
