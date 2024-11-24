package org.example;

import java.util.ArrayList;
import java.util.List;

public class Buffer {
    private int jump;
    private int bytesRead;
    private int bufferSize = 100; //How many records can fit in
    private List<Record> buffer;

    public Buffer(int bufferSize){
        this.bufferSize = bufferSize;
        this.buffer = new ArrayList<>();
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public int getBytesRead() {
        return bytesRead;
    }

    public int getJump() {
        return jump;
    }

    public List<Record> getBuffer() {
        return buffer;
    }

    public void setBytesRead(int bytesRead) {
        this.bytesRead = bytesRead;
    }

    public void setJump(int jump) {
        this.jump = jump;
    }

    public void setBuffer(List<Record> buffer) {
        this.buffer = buffer;
    }

    public void setNewJump(int n, int jumps){
        jump += n * jumps;
    }

}
