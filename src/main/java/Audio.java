import java.io.*;
import java.util.Random;
import javax.sound.sampled.*;

/**
* Audio class that stores audio data as raw bytes and performs modifying functions.
* @author Jan Tryka 
*/
public class Audio implements Cloneable {
    private byte[] Right,Left;
    private AudioFormat format;

    /**
    * constructor from bytes
    * @param R,L  Right and Left channel as bytearrays 
    * @param f    format as AudioFormat object
    */

    public Audio(byte[] R, byte[] L,AudioFormat f){
        Right=R;
        Left=L;
        format=f;
    }

    /**
    * alternate constructor from file
    * @param filename                         path to the audio file
    * @throws UnsupportedAudioFileException   if incompatible audio format 
    * @throws IOException                     if problems with the file/path occur 
    */
    public Audio(String filename) throws UnsupportedAudioFileException, IOException{
        AudioInputStream inputStream=AudioSystem.getAudioInputStream(new File(filename));
        format=inputStream.getFormat();
        if(format.getChannels()!=2 || !(format.getSampleSizeInBits()==16||format.getSampleSizeInBits()==8))throw new UnsupportedAudioFileException();
        int bytecount= (int) (inputStream.getFrameLength()*format.getFrameSize());
        Left=new byte[bytecount/2];
        Right=new byte[bytecount/2];
        byte[] AudioData=new byte[bytecount];
        inputStream.read(AudioData);
        if(format.getSampleSizeInBits()==16) {
            for (int i = 0; i < bytecount / 2; i += 2) {
                Left[i] = AudioData[2 * i];
                Left[i + 1] = AudioData[2 * i + 1];
                Right[i] = AudioData[2 * i + 2];
                Right[i + 1] = AudioData[2 * i + 3];
            }
        }else{
            for (int i = 0; i < bytecount; i += 2) {
                Left[i] = AudioData[i];
                Right[i] = AudioData[i+1];
            }
        }
    }

    public byte[] getRight() {
        return Right;
    }

    public byte[] getLeft() {
        return Left;
    }

    public AudioFormat getFormat() {
        return format;
    }

    /**
    * @return  itself but converted to 2 bytes per sample format
    */
    public Audio to16(){
        if(format.getSampleSizeInBits()==16)return this;
        AudioFormat f=new AudioFormat(format.getEncoding(),format.getSampleRate(), 16, 2, 4, format.getFrameRate(), format.isBigEndian());
        byte [] nLeft=new byte[Left.length*2];
        byte [] nRight=new byte[Right.length*2];
        for(int i=0;i<Left.length;i++){
            nLeft[2*i+1]=Left[i];
            nLeft[2*i]=0;
            nRight[2*i+1]=Right[i];
            nRight[2*i]=0;
        }
        return (new Audio(nLeft,nRight,f)).changeSpeed(2);
    }
    /**
    * @return  itself but converted to 1 byte per sample format
    */
    public Audio to8(){
        if(format.getSampleSizeInBits()==8)return this;
        AudioFormat f = new AudioFormat(format.getEncoding(),format.getSampleRate(), 8, 2, 2, format.getFrameRate(), format.isBigEndian());
        byte [] nLeft=new byte[Left.length/2];
        byte [] nRight=new byte[Right.length/2];
        for(int i=0;i<nLeft.length;i++){
            nLeft[i]=Left[2*i+1];
            nRight[i]=Right[2*i+1];
        }
         return (new Audio(nLeft,nRight,f)).changeSpeed(0.5);
    }
    /**
    * plays itself as a physical sound
    */
    public void play(){
        byte[] AudioData=new byte[Right.length*2];
        if(format.getSampleSizeInBits()==16) {
            for (int i = 0; i < Right.length; i += 2) {
                AudioData[2*i] = Left[i];
                AudioData[2*i + 1] = Left[i + 1];
                AudioData[2*i + 2] = Right[i];
                AudioData[2*i + 3] = Right[i + 1];
            }
        }else{
            for (int i = 0; i < Right.length; i+=2) {
                AudioData[i] = Left[i];
                AudioData[i + 1] = Right[i];
            }
        }
        Thread playThread = new Thread() {
            private SourceDataLine line=null;
            @Override
            public void run() {
                try {
                    line = AudioSystem.getSourceDataLine(format);
                    System.gc();
                    line.open(format);
                    line.start();
                    line.write(AudioData, 0, AudioData.length);
                    synchronized (this) {
                        line.drain();
                        line.close();
                    }
                } catch (LineUnavailableException e) {
                    System.out.println("Exception: " + e);
                }
            }
            @Override
            public void interrupt() {
                if (line != null) {
                    synchronized (this) {
                        line.stop();
                        line.flush();
                        line.close();
                    }
                }
            }
        };
        playThread.setPriority(Thread.NORM_PRIORITY);
        playThread.start();
    }
    /**
    * saves itself to a file
    * @param filename        path to the audio file
    * @throws IOException    if problems with the file/path occur 
    */
    public void save(String filename) throws IOException{
        File file = new File(filename);
        byte[] AudioData=new byte[Right.length*2];
        if(format.getSampleSizeInBits()==16) {
            for (int i = 0; i < Right.length; i += 2) {
                AudioData[2*i] = Left[i];
                AudioData[2*i + 1] = Left[i + 1];
                AudioData[2*i + 2] = Right[i];
                AudioData[2*i + 3] = Right[i + 1];
            }
        }else{
            for (int i = 0; i < Right.length; i+=2) {
                AudioData[i] = Left[i];
                AudioData[i + 1] = Right[i];
            }
        }
        ByteArrayInputStream byteStream = new ByteArrayInputStream(AudioData);
        AudioInputStream stream = new AudioInputStream(byteStream, format,AudioData.length / 4);
        AudioSystem.write(stream,AudioFileFormat.Type.WAVE, file);

    }
    /**
    * @param Bytes is converted to an array of short ints
    * @throws UnsupportedAudioFileException   if incompatible audio format 
    * @return an array of short ints
    */
    private short[] byteToShortArr(byte[] Bytes) throws UnsupportedAudioFileException{
        if(format.getEncoding()!=AudioFormat.Encoding.PCM_SIGNED) throw new UnsupportedAudioFileException();
        short[] result=new short[Bytes.length/2];
        for(int i=0;i<Bytes.length;i+=2){
            byte byte1=Bytes[i];
            byte byte2=Bytes[i+1];
            if(format.isBigEndian()){
                result[i/2]= (short) ((short) (byte1 << 8) | (byte2 & 0xff));
            }else{
                result[i/2]= (short) ((short) (byte2 << 8) | (byte1 & 0xff));
            }
        }
        return result;
    }
    /**
    * @param Data is converted to an array of bytes
    * @return an array of bytes
    */
    private byte[] shortToByteArr(short[] Data){
        byte[] result=new byte[Data.length*2];
        for(int i=0;i<Data.length*2;i+=2){
            short s=Data[i/2];
            if(format.isBigEndian()){
                result[i]=(byte)(s>>8);
                result[i+1]=(byte)(s&0xff);
            }else{
                result[i+1]=(byte)(s>>8);
                result[i]=(byte)(s&0xff);
            }
        }
        return result;
    }
    /**
    * multiplies 2 values, if an overflow occurs, border value is returned
    */
    private short safeMul(short s, double d){
        int result=(int)(s*d);
        if(result>32767) return 32767;
        if(result<-32768) return -32768;
        return (short)result;
    }
    /**
    * multiplies 2 values, if an overflow occurs, border value is returned
    */
    private byte safeMul(byte b, double d){
        int result=(int)((int)b*d);
        if(result>127) return 127;
        if(result<-128) return -128;
        return (byte)result;
    }

    /**
    * changes volume of the audio
    * @param multiplier 
    * @return  itself but louder/quieter
    */
    public Audio changeVolume(double multiplier)throws UnsupportedAudioFileException {
        if (format.getSampleSizeInBits() == 16) {
            short[] WaveL = byteToShortArr(Left);
            for (int i = 0; i < WaveL.length; i++) WaveL[i] = safeMul(WaveL[i], multiplier);
            byte[] nLeft = shortToByteArr(WaveL);

            short[] WaveR = byteToShortArr(Right);
            for (int i = 0; i < WaveR.length; i++) WaveR[i] = safeMul(WaveR[i], multiplier);
            byte[] nRight= shortToByteArr(WaveR);
            return new Audio(nLeft,nRight,format);
        }else{
            byte[] nLeft=new byte[Left.length];
            byte[] nRight=new byte[Right.length];
            for(int i = 0; i < Left.length; i++)nLeft[i]=safeMul(Left[i],multiplier);
            for(int i = 0; i < Right.length; i++)nRight[i]=safeMul(Right[i],multiplier);
            return new Audio(nLeft,nRight,format);
        }
    }
    /**
    * distorts the audio unpleasantly
    * @return  itself but humongously rectangular
    */
    public Audio gwozdziuj()throws UnsupportedAudioFileException{
        if (format.getSampleSizeInBits() == 16) {
            short[] WaveL = byteToShortArr(Left);
            for (int i = 0; i < WaveL.length; i++) {
                if (WaveL[i] < 0) WaveL[i] = -32768;
                else WaveL[i] = 32767;
            }
            byte[] nLeft = shortToByteArr(WaveL);

            short[] WaveR = byteToShortArr(Right);
            for (int i = 0; i < WaveR.length; i++) {
                if (WaveR[i] < 0) WaveR[i] = -32768;
                else WaveR[i] = 32767;
            }
            byte[] nRight = shortToByteArr(WaveR);
            return new Audio(nLeft,nRight,format);
        }else{
            byte[] nLeft=new byte[Left.length];
            byte[] nRight=new byte[Right.length];
            for(int i = 0; i < Left.length; i++)if(Left[i]>0)nLeft[i]=127;else Left[i]=-128;
            for(int i = 0; i < Right.length; i++)if(Right[i]>0)nRight[i]=127;else Right[i]=-128;
            return new Audio(nLeft,nRight,format);
        }
    }
    /**
    * adds random glitches
    * @param prob  probability of a randomly generated glitched sample 
    * @return      itself but glitched
    */
    public Audio mlotuj(float prob)throws UnsupportedAudioFileException,IllegalArgumentException{
        Random r=new Random();
        if (prob > 1 || prob < 0) throw new IllegalArgumentException();
        if (format.getSampleSizeInBits() == 16) {
            short[] WaveL = byteToShortArr(Left);
            for (int i = 0; i < WaveL.length; i++) {
                if (r.nextFloat() < prob) {
                    WaveL[i] = (short) r.nextInt();
                }
            }
            byte[] nLeft = shortToByteArr(WaveL);

            short[] WaveR = byteToShortArr(Left);
            for (int i = 0; i < WaveR.length; i++) {
                if (r.nextFloat() < prob) {
                    WaveR[i] = (short) r.nextInt();
                }
            }
            byte[] nRight = shortToByteArr(WaveR);
            return new Audio(nLeft,nRight,format);
        }else{
            byte[] nLeft=new byte[Left.length];
            byte[] nRight=new byte[Right.length];
            for (int i = 0; i < Left.length; i++) {
                if (r.nextFloat() < prob) nLeft[i] = (byte) r.nextInt();
                else nLeft[i]=Left[i];
            }
            for (int i = 0; i < Right.length; i++) {
                if (r.nextFloat() < prob)nRight[i] = (byte) r.nextInt();
                else nRight[i]=Right[i];
            }
            return new Audio(nLeft,nRight,format);
        }
    }
    /**
    * changes speed of the audio
    * @param speed                    the audio gets reversed if speed is negative 
    @throws IllegalArgumentException  if speed==0
    * @return                         itself but faster/slower/reversed
    */
    public Audio changeSpeed(double speed){
        if(speed==0)throw new IllegalArgumentException();
        if(format.getSampleSizeInBits()==16){
            byte[] nLeft=new byte[2*(int)Math.floor(Left.length/Math.abs(speed)/2)];
            byte[] nRight=new byte[2*(int)Math.floor(Right.length/Math.abs(speed)/2)];
            for(int i=0;i<nLeft.length;i+=2){
                nLeft[i]=Left[2*(int)Math.floor(i*Math.abs(speed)/2)];
                nLeft[i+1]=Left[2*(int)Math.floor(i*Math.abs(speed)/2)+1];
                nRight[i]=Right[2*(int)Math.floor(i*Math.abs(speed)/2)];
                nRight[i+1]=Right[2*(int)Math.floor(i*Math.abs(speed)/2)+1];
            }
            if(speed<0){
                byte[] nnLeft=new byte[nLeft.length];
                byte[] nnRight=new byte[nRight.length];
                int l=nLeft.length;
                for(int i=0;i<l;i+=2){
                    nnLeft[i]=nLeft[l-i-2];
                    nnLeft[i+1]=nLeft[l-i-1];
                    nnRight[i]=nRight[l-i-2];
                    nnRight[i+1]=nRight[l-i-1];
                }
                return new Audio(nnLeft,nnRight,format);
            }
            return new Audio(nLeft,nRight,format);
        }else{
            byte[] nLeft=new byte[(int)Math.floor(Left.length/Math.abs(speed))];
            byte[] nRight=new byte[(int)Math.floor(Right.length/Math.abs(speed))];
            for(int i=0;i<nLeft.length;i++){
                nLeft[i]=Left[(int)Math.floor(i*Math.abs(speed))];
                nRight[i]=Right[(int)Math.floor(i*Math.abs(speed))];
            }
            if(speed<0){
                byte[] nnLeft=new byte[nLeft.length];
                byte[] nnRight=new byte[nRight.length];
                int l=nLeft.length;
                for(int i=0;i<l;i++){
                    nnLeft[i]=nLeft[l-i-1];
                    nnRight[i]=nRight[l-i-1];
                }
                return new Audio(nnLeft,nnRight,format);
            }

            return new Audio(nLeft,nRight,format);
        }
    }
    /**
    * crops itself according to provided timestamps
    * @param start,end                   bounding timestamps
    * @throws IndexOutOfBoundsException  if timestamps are out of audio length range
    * @return                            itself but cropped
    */
    public Audio crop(double start, double end){
        int s= (int)(start*format.getFrameRate())*2;
        int e= (int)(end*format.getFrameRate())*2;
        if(s<0||e<0||s>=Left.length)throw new IndexOutOfBoundsException();
        if(e>=Left.length)e=Left.length;
        if(format.getSampleSizeInBits()==16&&s%2==1)s++;
        int l=e-s;
        byte[] nLeft=new byte[l];
        byte[] nRight=new byte[l];
        for(int i=0;i<l;i++){
            nLeft[i]=Left[s+i];
            nRight[i]=Right[s+i];
        }
        return new Audio(nLeft,nRight,format);
    }
    /**
    * joins two Audio objects together
    * @param a  the latter audio which is joined to "this"
    * @return   itself but with other audio data appended
    */
    public Audio join(Audio a){
        if(this.format.getSampleSizeInBits()!=a.format.getSampleSizeInBits()||this.format.getSampleRate()!=a.format.getSampleRate())throw new IllegalArgumentException("niekompatybilne formaty "+this.format+" "+a.format);
        int l=this.Left.length+a.Left.length;
        byte[] nLeft=new byte[l];
        byte[] nRight=new byte[l];
        for(int i=0;i<this.Left.length;i++){
            nLeft[i]=this.Left[i];
            nRight[i]=this.Right[i];
        }
        for(int i=this.Left.length;i<l;i++){
            nLeft[i]=a.Left[i-this.Left.length];
            nRight[i]=a.Right[i-this.Right.length];
        }
        return new Audio(nLeft,nRight,format);
    }
}
