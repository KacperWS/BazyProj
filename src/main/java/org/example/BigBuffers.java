package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BigBuffers {
    private List<Record> records;
    private final int bufferNumber = 10;
    private final int bufferSize = 10;
    private List<List<Record>> buffers;
    private int fileNumber = 0;

    public BigBuffers(){
        this.buffers = new ArrayList<>();
    }

    public void start() throws IOException {
        DiskIO temp = new DiskIO("ter");
        int i = 0;
        List<Record> list = new ArrayList<>();
        temp.openIN();
        while(list  != null) {
            while (i < bufferNumber) {
                if((list = temp.read(this.bufferSize)) != null) {
                    this.buffers.add(list);
                    i++;
                }else{
                    break;
                }
            }
            i = 0;
            this.sort();
            this.save(temp);
        }
    }

    private void sort(){
        for (List<Record> buffer : buffers) {
            if (buffer.size() > 1) {
                buffer.sort(Comparator.comparingInt(Record::getId));
            } else {
                break;
            }
        }
    }

    private void save(DiskIO temp) throws IOException {
        for(List<Record> buffer : buffers){
            temp.saveBuffers(buffer, bufferSize, fileNumber);
            fileNumber++;
        }
        this.buffers = new ArrayList<>();
    }

    private void merge(){

    }
}
