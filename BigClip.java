import java.awt.Component;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.*;
import java.util.logging.*;
import java.util.Arrays;

public class BigClip implements Clip, LineListener {

    /** The DataLine used by this Clip. */
    private SourceDataLine dataLine;

    /** The raw bytes of the audio data. */
    private byte[] audioData;

    /** The stream wrapper for the audioData. */
    private ByteArrayInputStream inputStream;

    /** Loop count set by the calling code. */
    private int loopCount = 1;
    /** Internal count of how many loops to go. */
    private int countDown = 1;
    /** The start of a loop point.    Defaults to 0. */
    private int loopPointStart;
    /** The end of a loop point.    Defaults to the end of the Clip. */
    private int loopPointEnd;

    /** Stores the current frame position of the clip. */
    private int framePosition;

    /** Thread used to run() sound. */
    private Thread thread;
    /** Whether the sound is currently playing or active. */
    private boolean active;
    /** Stores the last time bytes were dumped to the audio stream. */
    private long timelastPositionSet;

    private int bufferUpdateFactor = 2;

    /** The parent Component for the loading progress dialog.    */
    Component parent = null;

    /** Used for reporting messages. */
    private Logger logger = Logger.getAnonymousLogger();

    /** Default constructor for a BigClip.    Does nothing.    Information from the
    AudioInputStream passed in open() will be used to get an appropriate SourceDataLine. */
    public BigClip() {}

    /** There are a number of AudioSystem methods that will return a configured Clip.    This
    convenience constructor allows us to obtain a SourceDataLine for the BigClip that uses
    the same AudioFormat as the original Clip.
    @param clip Clip The Clip used to configure the BigClip. */
    public BigClip(Clip clip) throws LineUnavailableException {
        dataLine = AudioSystem.getSourceDataLine( clip.getFormat() );
    }

    /** Provides the entire audio buffer of this clip.
    @return audioData byte[] The bytes of the audio data that is loaded in this Clip. */
    public byte[] getAudioData() {
        return audioData;
    }

    /** Sets a parent component to act as owner of a "Loading track.." progress dialog.
    If null, there will be no progress shown. */
    public void setParentComponent(Component parent) {
        this.parent = parent;
    }

    /** Converts a frame count to a duration in milliseconds. */
    private long convertFramesToMilliseconds(int frames) {
        return (frames/(long)dataLine.getFormat().getSampleRate())*1000;
    }

    /** Converts a duration in milliseconds to a frame count. */
    private int convertMillisecondsToFrames(long milliseconds) {
        return (int)(milliseconds/dataLine.getFormat().getSampleRate());
    }

    @Override
    public void update(LineEvent le) {
        logger.log(Level.FINEST, "update: " + le );
    }

    @Override
    public void loop(int count) {
        logger.log(Level.FINEST, "loop(" + count + ") - framePosition: " + framePosition);
        loopCount = count;
        countDown = count;
        active = true;
        inputStream.reset();

        start();
    }

    @Override
    public void setLoopPoints(int start, int end) {
        if (
            start<0 ||
            start>audioData.length-1 ||
            end<0 ||
            end>audioData.length
            ) {
            throw new IllegalArgumentException(
                "Loop points '" +
                start +
                "' and '" +
                end +
                "' cannot be set for buffer of size " +
                audioData.length);
        }
        if (start>end) {
            throw new IllegalArgumentException(
                "End position " +
                end +
                " preceeds start position " + start);
        }

        loopPointStart = start;
        framePosition = loopPointStart;
        loopPointEnd = end;
    }

    @Override
    public void setMicrosecondPosition(long milliseconds) {
        framePosition = convertMillisecondsToFrames(milliseconds);
    }

    @Override
    public long getMicrosecondPosition() {
        return convertFramesToMilliseconds(getFramePosition());
    }

    @Override
    public long getMicrosecondLength() {
        return convertFramesToMilliseconds(getFrameLength());
    }

    @Override
    public void setFramePosition(int frames) {
        framePosition = frames;
        int offset = framePosition*format.getFrameSize();
        try {
            inputStream.reset();
            inputStream.read(new byte[offset]);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getFramePosition() {
        long timeSinceLastPositionSet = System.currentTimeMillis() - timelastPositionSet;
        int size = dataLine.getBufferSize()*(format.getChannels()/2)/bufferUpdateFactor;
        int framesSinceLast = (int)((timeSinceLastPositionSet/1000f)*
            dataLine.getFormat().getFrameRate());
        int framesRemainingTillTime = size - framesSinceLast;
        return framePosition
            - framesRemainingTillTime;
    }

    @Override
    public int getFrameLength() {
        return audioData.length/format.getFrameSize();
    }

    AudioFormat format;

    @Override
    public void open(AudioInputStream stream) throws
        IOException,
        LineUnavailableException {

        AudioInputStream is1;
        format = stream.getFormat();

        if (format.getEncoding()!=AudioFormat.Encoding.PCM_SIGNED) {
            is1 = AudioSystem.getAudioInputStream(
                AudioFormat.Encoding.PCM_SIGNED, stream );
        } else {
            is1 = stream;
        }
        format = is1.getFormat();
        InputStream is2;
        if (parent!=null) {
            ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(
                parent,
                "Loading track..",
                is1);
            pmis.getProgressMonitor().setMillisToPopup(0);
            is2 = pmis;
        } else {
            is2 = is1;
        }

        byte[] buf = new byte[ 2^16 ];
        int totalRead = 0;
        int numRead = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        numRead = is2.read( buf );
        while (numRead>-1) {
            baos.write( buf, 0, numRead );
            numRead = is2.read( buf, 0, buf.length );
            totalRead += numRead;
        }
        is2.close();
        audioData = baos.toByteArray();
        AudioFormat afTemp;
        if (format.getChannels()<2) {
            afTemp = new AudioFormat(
                format.getEncoding(),
                format.getSampleRate(),
                format.getSampleSizeInBits(),
                2,
                format.getSampleSizeInBits()*2/8, // calculate frame size
                format.getFrameRate(),
                format.isBigEndian()
                );
        } else {
            afTemp = format;
        }

        setLoopPoints(0,audioData.length);
        dataLine = AudioSystem.getSourceDataLine(afTemp);
        dataLine.open();
        inputStream = new ByteArrayInputStream( audioData );
    }

    @Override
    public void open(AudioFormat format,
        byte[] data,
        int offset,
        int bufferSize)
        throws LineUnavailableException {
        byte[] input = new byte[bufferSize];
        for (int ii=0; ii<input.length; ii++) {
            input[ii] = data[offset+ii];
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
        try {
            AudioInputStream ais1 = AudioSystem.getAudioInputStream(inputStream);
            AudioInputStream ais2 = AudioSystem.getAudioInputStream(format, ais1);
            open(ais2);
        } catch( UnsupportedAudioFileException uafe ) {
            throw new IllegalArgumentException(uafe);
        } catch( IOException ioe ) {
            throw new IllegalArgumentException(ioe);
        }
        // TODO    -    throw IAE for invalid frame size, format.
    }

    @Override
    public float getLevel() {
        return dataLine.getLevel();
    }

    @Override
    public long getLongFramePosition() {
        return dataLine.getLongFramePosition()*2/format.getChannels();
    }

    @Override
    public int available() {
        return dataLine.available();
    }

    @Override
    public int getBufferSize() {
        return dataLine.getBufferSize();
    }

    @Override
    public AudioFormat getFormat() {
        return format;
    }

    @Override
    public boolean isActive() {
        return dataLine.isActive();
    }

    @Override
    public boolean isRunning() {
        return dataLine.isRunning();
    }

    @Override
    public boolean isOpen() {
        return dataLine.isOpen();
    }

    @Override
    public void stop() {
        logger.log(Level.FINEST, "BigClip.stop()");
        active = false;
        // why did I have this commented out?
        dataLine.stop();
        if (thread!=null) {
            try {
                active = false;
                thread.join();
            } catch(InterruptedException wakeAndContinue) {
            }
        }
    }

    public byte[] convertMonoToStereo(byte[] data, int bytesRead) {
        byte[] tempData = new byte[bytesRead*2];
        if (format.getSampleSizeInBits()==8) {
            for(int ii=0; ii<bytesRead; ii++) {
                byte b = data[ii];
                tempData[ii*2] = b;
                tempData[ii*2+1] = b;
            }
        } else {
            for(int ii=0; ii<bytesRead-1; ii+=2) {
                //byte b2 = is2.read();
                byte b1 = data[ii];
                byte b2 = data[ii+1];
                tempData[ii*2] = b1;
                tempData[ii*2+1] = b2;
                tempData[ii*2+2] = b1;
                tempData[ii*2+3] = b2;
            }
        }
        return tempData;
    }

    boolean fastForward;
    boolean fastRewind;

    public void setFastForward(boolean fastForward) {
        logger.log(Level.FINEST, "FastForward " + fastForward);
        this.fastForward = fastForward;
        fastRewind = false;
        flush();
    }

    public boolean getFastForward() {
        return fastForward;
    }

    public void setFastRewind(boolean fastRewind) {
        logger.log(Level.FINEST, "FastRewind " + fastRewind);
        this.fastRewind = fastRewind;
        fastForward = false;
        flush();
    }

    public boolean getFastRewind() {
        return fastRewind;
    }

    /** TODO - fix bug in LOOP_CONTINUOUSLY */
    @Override
    public void start() {
        Runnable r = new Runnable() {
            public void run() {
                try {
                    /* Should these open()/close() calls be here, or explicitly
                    called by user program?    The JavaDocs for line suggest that
                    Clip should throw an IllegalArgumentException, so we'll
                    stick with that and call it explicitly. */
                    dataLine.open();

                    dataLine.start();

                    active = true;

                    int bytesRead = 0;
                    int frameSize = dataLine.getFormat().getFrameSize();
                    int bufSize = dataLine.getBufferSize();
                    boolean startOrMove = true;
                    byte[] data = new byte[bufSize];
                    int offset = framePosition*frameSize;
                    int totalBytes = offset;
                    bytesRead = inputStream.read(new byte[offset], 0, offset);
                    logger.log(Level.FINE, "bytesRead " + bytesRead );
                    bytesRead = inputStream.read(data,0,data.length);

                    logger.log(Level.FINE, "loopCount " + loopCount );
                    logger.log(Level.FINE, "countDown " + countDown );
                    logger.log(Level.FINE, "bytesRead " + bytesRead );

                    while (bytesRead != -1 &&
                        (loopCount==Clip.LOOP_CONTINUOUSLY ||
                        countDown>0) &&
                        active ) {
                        logger.log(Level.FINEST,
                            "BigClip.start() loop " + framePosition );
                        totalBytes += bytesRead;
                        int framesRead;
                        byte[] tempData;
                        if (format.getChannels()<2) {
                            tempData = convertMonoToStereo(data, bytesRead);
                            framesRead = bytesRead/
                                format.getFrameSize();
                            bytesRead*=2;
                        } else {
                            framesRead = bytesRead/
                                dataLine.getFormat().getFrameSize();
                            tempData = Arrays.copyOfRange(data, 0, bytesRead);
                        }
                        framePosition += framesRead;
                        if (framePosition>=loopPointEnd) {
                            framePosition = loopPointStart;
                            inputStream.reset();
                            countDown--;
                            logger.log(Level.FINEST,
                                "Loop Count: " + countDown );
                        }
                        timelastPositionSet = System.currentTimeMillis();
                        byte[] newData;
                        if (fastForward) {
                            newData = getEveryNthFrame(tempData, 2);
                        } else if (fastRewind) {
                            byte[] temp = getEveryNthFrame(tempData, 2);
                            newData = reverseFrames(temp);
                            inputStream.reset();
                            totalBytes -= 2*bytesRead;
                        framePosition -= 2*framesRead;
                            if (totalBytes<0) {
                                setFastRewind(false);
                                totalBytes = 0;
                            }
                            inputStream.skip(totalBytes);
                            logger.log(Level.FINE, "totalBytes " + totalBytes);
                        } else {
                            newData = tempData;
                        }
                        dataLine.write(newData, 0, newData.length);
                        if (startOrMove) {
                            data = new byte[bufSize/
                                bufferUpdateFactor];
                            startOrMove = false;
                        }
                        bytesRead = inputStream.read(data,0,data.length);
                        if (bytesRead<0 && countDown-->1) {
                            inputStream.read(new byte[offset], 0, offset);
                            logger.log(Level.FINE, "loopCount " + loopCount );
                            logger.log(Level.FINE, "countDown " + countDown );
                            inputStream.reset();
                            bytesRead = inputStream.read(data,0,data.length);
                        }
                    }
                    logger.log(Level.FINEST,
                        "BigClip.start() loop ENDED" + framePosition );
                    active = false;
                    countDown = 1;
                    framePosition = 0;
                    inputStream.reset();
                    dataLine.drain();
                    dataLine.stop();
                    /* should these open()/close() be here, or explicitly
                    called by user program? */
                    dataLine.close();
                } catch (LineUnavailableException lue) {
                    logger.log( Level.SEVERE,
                        "No sound line available!", lue );
                    if (parent!=null) {
                        JOptionPane.showMessageDialog(
                            parent,
                            "Clear the sound lines to proceed",
                            "No audio lines available!",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        thread= new Thread(r);
        // makes thread behaviour compatible with JavaSound post 1.4
        thread.setDaemon(true);
        thread.start();
    }

    /** Assume the frame size is 4. */
    public byte[] reverseFrames(byte[] data) {
        byte[] reversed = new byte[data.length];
        byte[] frame = new byte[4];

        for (int ii=0; ii<data.length/4; ii++) {
            int first = (data.length)-((ii+1)*4)+0;
            int last = (data.length)-((ii+1)*4)+3;
            frame[0] = data[first];
            frame[1] = data[(data.length)-((ii+1)*4)+1];
            frame[2] = data[(data.length)-((ii+1)*4)+2];
            frame[3] = data[last];

            reversed[ii*4+0] = frame[0];
            reversed[ii*4+1] = frame[1];
            reversed[ii*4+2] = frame[2];
            reversed[ii*4+3] = frame[3];
            if (ii<5 || ii>(data.length/4)-5) {
                logger.log(Level.FINER, "From \t" + first + " \tlast " + last );
                logger.log(Level.FINER, "To \t" + ((ii*4)+0) + " \tlast " + ((ii*4)+3) );
            }
        }

/*
        for (int ii=0; ii<data.length; ii++) {
            reversed[ii] = data[data.length-1-ii];
        }
*/

        return reversed;
    }

    /** Assume the frame size is 4. */
    public byte[] getEveryNthFrame(byte[] data, int skip) {
        int length = data.length/skip;
        length = (length/4)*4;
        logger.log(Level.FINEST, "length " + data.length + " \t" + length);
        byte[] b = new byte[length];
        //byte[] frame = new byte[4];
        for (int ii=0; ii<b.length/4; ii++) {
            b[ii*4+0] = data[ii*skip*4+0];
            b[ii*4+1] = data[ii*skip*4+1];
            b[ii*4+2] = data[ii*skip*4+2];
            b[ii*4+3] = data[ii*skip*4+3];
        }
        return b;
    }

    @Override
    public void flush() {
        dataLine.flush();
    }

    @Override
    public void drain() {
        dataLine.drain();
    }

    @Override
    public void removeLineListener(LineListener listener) {
        dataLine.removeLineListener(listener);
    }

    @Override
    public void addLineListener(LineListener listener) {
        dataLine.addLineListener(listener);
    }

    @Override
    public Control getControl(Control.Type control) {
        return dataLine.getControl(control);
    }

    @Override
    public Control[] getControls() {
        if (dataLine==null) {
            return new Control[0];
        } else {
            return dataLine.getControls();
        }
    }

    @Override
    public boolean isControlSupported(Control.Type control) {
        return dataLine.isControlSupported(control);
    }

    @Override
    public void close() {
        dataLine.close();
    }

    @Override
    public void open() throws LineUnavailableException {
        throw new IllegalArgumentException("illegal call to open() in interface Clip");
    }

    @Override
    public Line.Info getLineInfo() {
        return dataLine.getLineInfo();
    }

    /** Determines the single largest sample size of all channels of the current clip.
    This can be handy for determining a fraction to scal visual representations.
    @return Double between 0 & 1 representing the maximum signal level of any channel. */
    public double getLargestSampleSize() {

        int largest = 0;
        int current;

        boolean signed = (format.getEncoding()==AudioFormat.Encoding.PCM_SIGNED);
        int bitDepth = format.getSampleSizeInBits();
        boolean bigEndian = format.isBigEndian();

        int samples = audioData.length*8/bitDepth;

        if (signed) {
            if (bitDepth/8==2) {
                if (bigEndian) {
                    for (int cc = 0; cc < samples; cc++) {
                        current = (audioData[cc*2]*256 + (audioData[cc*2+1] & 0xFF));
                        if (Math.abs(current)>largest) {
                            largest = Math.abs(current);
                        }
                    }
                } else {
                    for (int cc = 0; cc < samples; cc++) {
                        current = (audioData[cc*2+1]*256 + (audioData[cc*2] & 0xFF));
                        if (Math.abs(current)>largest) {
                            largest = Math.abs(current);
                        }
                    }
                }
            } else {
                for (int cc = 0; cc < samples; cc++) {
                    current = (audioData[cc] & 0xFF);
                    if (Math.abs(current)>largest) {
                        largest = Math.abs(current);
                    }
                }
            }
        } else {
            if (bitDepth/8==2) {
                if (bigEndian) {
                    for (int cc = 0; cc < samples; cc++) {
                        current = (audioData[cc*2]*256 + (audioData[cc*2+1] - 0x80));
                        if (Math.abs(current)>largest) {
                            largest = Math.abs(current);
                        }
                    }
                } else {
                    for (int cc = 0; cc < samples; cc++) {
                        current = (audioData[cc*2+1]*256 + (audioData[cc*2] - 0x80));
                        if (Math.abs(current)>largest) {
                            largest = Math.abs(current);
                        }
                    }
                }
            } else {
                for (int cc = 0; cc < samples; cc++) {
                    if ( audioData[cc]>0 ) {
                        current = (audioData[cc] - 0x80);
                        if (Math.abs(current)>largest) {
                            largest = Math.abs(current);
                        }
                    } else {
                        current = (audioData[cc] + 0x80);
                        if (Math.abs(current)>largest) {
                            largest = Math.abs(current);
                        }
                    }
                }
            }
        }

        // audioData
        logger.log(Level.FINEST, "Max signal level: " + (double)largest/(Math.pow(2, bitDepth-1)));
        return (double)largest/(Math.pow(2, bitDepth-1));
    }
}