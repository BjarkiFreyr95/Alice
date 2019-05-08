package allie;

import javazoom.jl.decoder.*;
import net.sourceforge.lame.mp3.*;
import net.sourceforge.lame.lowlevel.*;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultListModel;

import java.io.ByteArrayOutputStream;
import java.awt.Label;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;


public class Converter {
	
    Thread thread;
    static double audioStrength[];
    String destinationFolder;
    DefaultBoundedRangeModel currentSongModel;
    DefaultBoundedRangeModel totalProgressModel;
    final int count = 512 * 1024; // 512 KB
    File[] musicFiles;
    Label currentPercentage;
    Label totalPercentage;
    int lastPercentage;
    GetFiles gf;
    boolean lastSong;
    Label currentSongStatusLabel;
    Label estimatedTimeLabel;
    long startingTime;
    DefaultListModel<String> songList;
    double increasingVolumeDouble;
    double currIncreasingVolumeDouble;
    double maxSizeFound;
    Label convertLabel;
    boolean needsReferenceSignal = true;
    //static double sinusPositive[];
    //static double sinusNegative[];
    static double signalProcessArr[];
    
    Converter(String theDestinationPath, File[] theMusicFiles, DefaultBoundedRangeModel cProgressModel, DefaultBoundedRangeModel tProgressModel, 
    		Label cPercentage, Label tPercentage, GetFiles gFiles, Label cSongStatus, Label eTimeLabel, DefaultListModel<String> sList, int incVolumeInt, Label convLabel) {
    	destinationFolder = theDestinationPath;
    	musicFiles = theMusicFiles;
    	currentSongModel = cProgressModel;
    	totalProgressModel = tProgressModel;
    	currentPercentage = cPercentage;
    	totalPercentage = tPercentage;
    	lastPercentage = 0;
    	gf = gFiles;
    	lastSong = false;
    	currentSongStatusLabel = cSongStatus;
    	estimatedTimeLabel = eTimeLabel;
    	songList = sList;
    	increasingVolumeDouble = (100 + incVolumeInt) / 100;
    	convertLabel = convLabel;
    	
    	audioStrength = new double[1100];
    	signalProcessArr = new double[1100];
    	
        for (int i = 0; i < 1100; i++) {
        	if (i < 800) {
        		signalProcessArr[i] = (Math.sin(2.0 * Math.PI * 19.2 * (double) (i) / 44.1) * 1000) * 0.5 * (1.0 - Math.cos(2.0 * Math.PI * (double) (i) / 799.0));
        	}
        	else {
        		signalProcessArr[i] = 0;
        	}
        	audioStrength[i] = (Math.sin((((double) (i) / 1100) * 2 * Math.PI)) * 0.5 + 0.5);
        }
        
    }
    public void setVolume (int incVolumeInt) {
    	increasingVolumeDouble = incVolumeInt;
    }
    
    public void saveByteArrayAsFile(byte[] data, String fileName) {
        FileOutputStream fos;
        try {
            File dir = new File(destinationFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            fos = new FileOutputStream(destinationFolder + "/" + fileName.substring(0, fileName.length() - 4) + " (Converted).mp3");
        } catch (FileNotFoundException ex) {
            return;
        }
        try {
            fos.write(data);
        } catch (IOException ex) {
            return;
        } finally {
            try {
            	System.out.println("finished saving");
            	changePercentage(currentPercentage, 100);
            	changeProgress(currentSongModel, 100);
            	changePercentage(totalPercentage, lastPercentage + 1);
            	changeProgress(totalProgressModel, lastPercentage + 1);
            	if (lastSong) {
            		changePercentage(totalPercentage, 100);
                	changeProgress(totalProgressModel, 100);
            		gf.clearMusicFilesArray();
            		songList.clear();
            	}
                fos.flush();
                fos.close();
            } catch (IOException ex) {
                return;
            }
        }
    }

    public void convertFiles() {
        thread = new Thread() {
            @Override
            public void run() {
                String fileName = "";
                lastSong = false;
                startingTime = System.currentTimeMillis();
                for (int j = 0; j < musicFiles.length; j++) {
                	if (j == musicFiles.length - 1) {
                		lastSong = true;
                	}
                    fileName = musicFiles[j].getName();
                    convertLabel.setText("Converting " + fileName + "...");
                    byte[] byteData = null;

                    try {
                    	findMaximum(musicFiles[j], 0, getMpthreeDuration(j), j, increasingVolumeDouble);
                    }catch (IOException e) {
                    }
                    System.out.println("Max size: " + maxSizeFound);
                	try {
                		//double oldMaximum = maxSizeFound / increasingVolumeDouble; // 31110
                		double newIntVolume = (32000 / (maxSizeFound)); //
                		//double newIntVolume = (maxSizeFound / (double) 32767)/(increasingVolumeDouble);
                		if (newIntVolume > 2) {
                			newIntVolume = 2;
                		}
                		currentSongStatusLabel.setText("Volume set at: " + (int) (newIntVolume * 100) + "%");
                        byteData = decode(musicFiles[j], 0, getMpthreeDuration(j), j, newIntVolume);
                        System.out.println("Max size: " + maxSizeFound + " newIntVolume: " + newIntVolume);
                    }catch (IOException e) {
                    }
                    saveByteArrayAsFile(byteData, fileName);
                }
            }

        };
        thread.start();

    }


    public int getMpthreeDuration(int fileIndex) {
        int duration = 0;
        Header h = null;
        FileInputStream file = null;
        try {
            file = new FileInputStream(musicFiles[fileIndex]);
        } catch (Exception ex) {   }
        Bitstream bitstream = new  Bitstream(file);
        try {
            h = bitstream.readFrame();
        } catch (BitstreamException ex) {}
        try {
            duration = (int) h.total_ms((int) file.getChannel().size());
        }catch (IOException ex) {}
        if (duration == 0) {
            return 1000;
        }
        return duration;
    }
    
    public void changeProgress(DefaultBoundedRangeModel cmdl, int prog) {
    	//cmdl.setValue(60);
    	cmdl.setValue(prog);
    }
    
    public void changePercentage(Label cPer, int prog) {
    	cPer.setText(prog + "%");
    }
    public void changeTimeRemaining(int secs) {
    	int hours = (int) ((double) secs)/3600; 
    	int mins = (int) ((double) secs - hours*3600)/60;
    	int sec = secs - hours*3600 - mins * 60;
    	String toPrint = "";
    	if (hours > 99) {
    		toPrint += "99:";
    	}
    	else {
    		if (hours < 10) {
        		toPrint += "0";
        	}
        	toPrint += hours + ":";
    	}
    	if (mins < 10) {
    		toPrint += "0";
    	}
    	toPrint += mins + ":";
    	if (sec < 10) {
    		toPrint += "0";
    	}
    	toPrint += sec;
    	estimatedTimeLabel.setText("Est Time Remaining: " + toPrint);
    }
    
    public void findMaximum(File file, int startMs, int maxMs, int currFile, double incVolume) 
    		throws IOException {
        int arrayIndexCounter = 0;
        float totalMs = 0;
        boolean seeking = true;
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file), 8 * 1024);
        try {
            Bitstream bitstream = new Bitstream(inputStream);
            Decoder decoder = new Decoder();
            maxSizeFound = 0;
            boolean done = false;
            while (! done) {
                Header frameHeader = bitstream.readFrame();
                if (frameHeader == null) {
                    done = true;
                } else {
                    totalMs += frameHeader.ms_per_frame();

                    if (totalMs >= startMs) {
                        seeking = false;
                    }

                    if (! seeking) {
                        SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);

                        if (output.getSampleFrequency() != 44100
                                || output.getChannelCount() != 2) {
                        	
                        }

                        int intTemp;
                        int minCounter = 0;
                        byte[] currPart = new byte[4];
                        short[] pcm = output.getBuffer();

                        for (short s : pcm) {
                            if (minCounter == 0) {
                                currPart[0] = (byte) (s & 0xff);
                                currPart[1] = (byte) ((s >> 8 ) & 0xff);
                                minCounter++;
                            }
                            else if (minCounter == 1) {
                                currPart[2] = (byte) (s & 0xff);
                                currPart[3] = (byte) ((s >> 8 ) & 0xff);
                                intTemp = (int)((((currPart[0] + (currPart[1] << 8)) + (currPart[2] + (currPart[3] << 8)))/2));
                                if (Math.abs(intTemp) > maxSizeFound) {
                                	maxSizeFound = Math.abs(intTemp);
                                }
                                minCounter = 0;
                                arrayIndexCounter++;
                                if (arrayIndexCounter >= audioStrength.length) {
                                    arrayIndexCounter = 0;
                                }
                            }
                        }

                    }

                    if (totalMs >= (startMs + maxMs)) {
                        done = true;
                    }
                }
                bitstream.closeFrame();
            }
        } catch (BitstreamException e) {
            throw new IOException("Bitstream error: " + e);
        }
        catch (DecoderException e) {
        } finally {
            inputStream.close();
        }
        return;
    }
    
    
    public byte[] decode(File file, int startMs, int maxMs, int currFile, double incVolume)
            throws IOException {
    	if (needsReferenceSignal) {
    		incVolume *= 0.9;
    	}
        LameEncoder encoder = new LameEncoder(new javax.sound.sampled.AudioFormat(44100.0f, 16, 2, true, false),256, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);
        ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
        byte[] buffer = new byte[encoder.getPCMBufferSize()];
        int bytesToTransfer;
        int bytesWritten;
        int chunkCounter = 0;
        byte[] chunkOfBytes = new byte[count];
        int arrayIndexCounter = 0;
        float totalMs = 0;
        boolean seeking = true;
        
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file), 8 * 1024);
        long updateTimer = startingTime;

        try {
            Bitstream bitstream = new Bitstream(inputStream);
            Decoder decoder = new Decoder();
            boolean done = false;
            while (! done) {
                Header frameHeader = bitstream.readFrame();
                if (frameHeader == null) {
                    done = true;
                } else {
                    totalMs += frameHeader.ms_per_frame();

                    if (totalMs >= startMs) {
                        seeking = false;
                    }

                    if (! seeking) {
                        SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);

                        if (output.getSampleFrequency() != 44100
                                || output.getChannelCount() != 2) {
                        	
                        }

                        short left;
                        short right;
                        int intTemp;
                        int minCounter = 0;
                        byte[] currPart = new byte[4];
                        short[] pcm = output.getBuffer();

                        for (short s : pcm) {
                            if (minCounter == 0) {
                                currPart[0] = (byte) (s & 0xff);
                                currPart[1] = (byte) ((s >> 8 ) & 0xff);
                                minCounter++;
                            }
                            else if (minCounter == 1) {
                                currPart[2] = (byte) (s & 0xff);
                                currPart[3] = (byte) ((s >> 8 ) & 0xff);
                                intTemp = (int)((((currPart[0] + (currPart[1] << 8)) + (currPart[2] + (currPart[3] << 8)))/2) * incVolume);
                                
                                left = (short) (intTemp * audioStrength[arrayIndexCounter]);
                                if (arrayIndexCounter != 0) {
                                    right = (short) (intTemp * audioStrength[1100 - arrayIndexCounter]);
                                }
                                else {
                                    right = (short) (intTemp * audioStrength[arrayIndexCounter]);
                                }
                                
                                // if we wish to add reference signal we add the audio from the signalprocess array
                                if (needsReferenceSignal) {
                                	left = (short)((signalProcessArr[arrayIndexCounter]) + left);
                                    right = (short)((signalProcessArr[arrayIndexCounter]) + right);
                                }
                                
                                // constructing audio data again
                                chunkOfBytes[chunkCounter] = (byte) (left &0xff);
                                chunkOfBytes[chunkCounter + 1] = (byte) ((left >> 8) &0xff);
                                chunkOfBytes[chunkCounter + 2] = (byte) (right &0xff);
                                chunkOfBytes[chunkCounter + 3] = (byte) ((right >> 8) &0xff);
                                chunkCounter += 4;
                                
                                if (chunkCounter >= 32768) {
                                	int currSongStatus = (int) ((totalMs/maxMs) * 100);
                                	int totalStatus = (int) (currSongStatus / musicFiles.length) + (int) ((currFile * 100 / musicFiles.length));
                                	changeProgress(currentSongModel, currSongStatus);
                                	changeProgress(totalProgressModel, totalStatus);
                                	changePercentage(currentPercentage, currSongStatus);
                                	lastPercentage = totalStatus;
                                	changePercentage(totalPercentage, totalStatus);
                                	if (System.currentTimeMillis() - updateTimer > 1000) {
                                		updateTimer = System.currentTimeMillis();
                                		long timePassed = System.currentTimeMillis() - startingTime;
                                    	if (totalStatus > 0) {
                                    		changeTimeRemaining((int) ((timePassed * 100 / totalStatus) - timePassed)/1000);
                                    	}
                                	}                                	
                                    bytesToTransfer = Math.min(buffer.length, chunkCounter);
                                    bytesWritten = encoder.encodeBuffer(chunkOfBytes, 0, bytesToTransfer, buffer);
                                    mp3.write(buffer, 0, bytesWritten);
                                    chunkCounter = 0;
                                }
                                minCounter = 0;
                                arrayIndexCounter++;
                                if (arrayIndexCounter >= audioStrength.length) {
                                    arrayIndexCounter = 0;
                                }
                            }
                        }

                    }

                    if (totalMs >= (startMs + maxMs)) {
                        done = true;
                        System.out.println("finished converting current Song");
                    }
                }
                bitstream.closeFrame();

            }
            encoder.close();
            return mp3.toByteArray();
        } catch (BitstreamException e) {
            throw new IOException("Bitstream error: " + e);
        }
        catch (DecoderException e) {
        } finally {
            inputStream.close();
        }
        return null;
    }
    
}
