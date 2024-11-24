package org.example;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BigBuffers {
    private final int bufferNumber;
    private final int bufferSize;
    private final List<Buffer> buffers;
    private final DiskIO discIO;

    private int fileNumber = 0;
    private int fileNumberMerge = 0;
    private int counterFile = 0;
    private int testNum = 0;

    private int fileSize;
    private boolean stageEnd = false;
    private int  jumpToSet;
    private final int bytesWrite;
    private int buffersInUse;
    //private int buffersEmpty;

    public BigBuffers(int bufferSize, int bufferNumber, String filename, int fileSize) throws IOException {
        this.bufferNumber = bufferNumber;
        this.bufferSize = bufferSize;
        this.fileSize = fileSize;
        this.bytesWrite = bufferSize * 6 * Integer.BYTES;
        this.jumpToSet = bytesWrite * 2;
        this.buffers = new ArrayList<>();
        allocateBuffers();
        this.discIO = new DiskIO(filename);
    }

    public void setJumpToSet() {
        jumpToSet *= 2;
    }

    public void start() throws IOException {
        int i = 0;
        List<Record> list = new ArrayList<>();
        discIO.openIN();
        while(list  != null) {
            while (i < bufferNumber) {
                if((list = discIO.read(this.bufferSize)) != null) {
                    this.buffers.get(i).setBuffer(list);
                    i++;
                }else{
                    break;
                }
            }
            i = 0;
            this.sort();
            this.save();
        }
    }

    private void sort(){
        for (Buffer buffer : buffers) {
            List<Record> temp = buffer.getBuffer();
            if (temp.size() > 1) {
                temp.sort(Comparator.comparingInt(Record::getId));
            } else {
                break;
            }
        }
    }

    private void save() throws IOException {
        for(Buffer buffer : buffers){
            if (buffer.getBuffer().isEmpty())
                break;
            discIO.saveBuffer(buffer, "1", bytesWrite);
            //fileNumber++;
            buffer.getBuffer().clear();
        }
        //this.buffers.clear();// = new ArrayList<>();
    }

    private void allocateBuffers(){
        for(int i = 0; i < bufferNumber; i++){
            Buffer temp1 = new Buffer(bufferSize);
            temp1.setJump(i * bytesWrite);
            buffers.add(temp1);
        }
    }

    private void updateBuffersAfter(){
        for(int i = 0; i < bufferNumber - 1; i++){
            //buffers.get(i).setJump(0);
            buffers.get(i).setNewJump(i, jumpToSet);
            buffers.get(i).setBytesRead(0);
        }
        setJumpToSet();
        buffersInUse = 0;
    }

    private void updateBuffersBefore(){
        for(int i = 0; i < bufferNumber - 1; i++){
            buffers.get(i).setNewJump(bufferNumber - 1, bytesWrite);
            buffers.get(i).setBytesRead(0);
        }
        buffersInUse = 0;
    }

    public void merge() throws IOException {
        while(buffers.get(1).getJump() < fileSize) { //Second buffer will read nothing so sorted
            while(!stageEnd) {
                mergeBuffer();
                updateBuffersBefore();
            }
            updateBuffersAfter();
            stageEnd = false;

            if (testNum == 0) {
                testNum = -1;
            } else if (testNum == -1){
                testNum = 0;
            }
        }
        System.out.println("Soting ended");
    }

    private void mergeBuffer() throws IOException {

        int bytesSaved = 0; String fileName;
        if(testNum == 0) {
            fileName = "img2/ter" + counterFile + ".txt";
        }else{
            fileName = "img/ter" + counterFile + ".txt";
        }
        for(int i = 0; i < bufferNumber - 1; i++){
            stageEnd = discIO.sortHere(0, buffers.get(i), bytesWrite);
            if(stageEnd)
                break;
            buffersInUse++;
        }
        if(stageEnd && buffersInUse < 1){
            return;
        }else if(stageEnd && buffersInUse == 1){
            discIO.saveBuffer(buffers.getFirst(), "2", bytesWrite);
        }else{
            List<Buffer> copy = new ArrayList<>(buffers);
            copy.remove(buffers.getLast());
            Record temp1;
            List<Record> temp = buffers.getLast().getBuffer();

            while(!copy.isEmpty()) {
                temp1 = chooseMin(copy);

                temp.add(temp1);

                if(temp.size() == bufferSize){
                    discIO.saveBuffer(buffers.getLast(), "2", bytesWrite);
                    buffers.getLast().getBuffer().clear();
                }
            }
            if (!(buffers.getLast().getBuffer().isEmpty()))
                discIO.saveBuffer(buffers.getLast(), "2", 4 * Integer.BYTES * buffers.getLast().getBuffer().size());
            buffers.getLast().getBuffer().clear();
        }
    }

    private Record chooseMin(List<Buffer> copy){
        Record temp = copy.getFirst().getBuffer().getFirst();
        int j = 0;
        for(int i = 1; i < copy.size(); i++){
            if(temp.getId() > copy.get(i).getBuffer().getFirst().getId()) {
                temp = copy.get(i).getBuffer().getFirst();
                j = i;
            }
        }
        copy.get(j).getBuffer().removeFirst();
        if(copy.get(j).getBuffer().isEmpty()) {
            //buffersEmpty++;
            copy.remove(j);
        }
        return temp;
    }
}
