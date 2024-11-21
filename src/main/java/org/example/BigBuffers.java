package org.example;

import java.io.BufferedOutputStream;
import java.io.File;
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
    private int fileNumberMerge = 0;

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

    public void merge() throws IOException {
        DiskIO temp = new DiskIO("ter");
        //temp.sortHere(fileNumber);
        buffers = new ArrayList<>(bufferNumber);
        for(int i = bufferNumber; i > 0; i--){
            //buffers.add(i - 1, new ArrayList<>());
            mergeBuffer(0,i - 1);
        }
    }

    private void mergeBuffer(int xd, int n) throws IOException {
        DiskIO save = new DiskIO("ter");
        List<DiskIO> files = new ArrayList<>(); List<Record> te = new ArrayList<>();
        List<Record> buffer = te;//buffers.get(xd);
        List<Record> result = new ArrayList<>();
        int bytesSaved = 0;
        for(int i = 0; i < n; i++){
            File file = new File("img/ter" + i + ".txt");

            // Check if the file exists
            if (file.exists()) {
                files.add(new DiskIO("img/ter" + i));
                files.get(i).openIN();
            }
            else {
                n = i;
                break;
            }
        }

        int temp = 0;
        Record temp1;
        for(int i = 0; i < n; i++) {
            result.add(files.get(i).sortHere(n));
        }
        while(!files.isEmpty()){
            temp1 = chooseMin(result);
            temp = result.indexOf(temp1);

            buffer.add(temp1);
            result.remove(temp1);
            Record tempRecord; //to avoid losing data
            if((tempRecord = (files.get(temp).sortHere(temp))) == null) {
                files.remove(temp);
                n--;
            }else
                result.add(tempRecord);
            if(buffer.size() == bufferSize){
                save.saveBuffer(buffer,-1);
                bytesSaved += buffer.size() * 6 * Integer.BYTES;
                te = new ArrayList<>();
                buffer = te;
            }
        }
        save.saveBuffer(buffer,-1);
        int suma = 6 + 6;
    }

    private Record chooseMin(List <Record> lista){
        Record temp = lista.getFirst();
        for(int i = 1; i < lista.size(); i++){
            if(temp.getId() > lista.get(i).getId())
                temp = lista.get(i);
        }
        return temp;
    }
}
