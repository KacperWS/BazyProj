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
    private List<Record> records;
    private final int bufferNumber = 10;
    private final int bufferSize = 10;
    private List<List<Record>> buffers;
    private int fileNumber = 0;
    private int fileNumberMerge = 0;
    private int counterFile = 0;
    private int testNum = 0;

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
        this.buffers.clear();// = new ArrayList<>();
    }

    private void allocateBuffers(){
       // buffers = new ArrayList<>(bufferNumber);
        List<Record> temp;
        for(int i = bufferNumber; i > 0; i--){
            temp = new ArrayList<>();
            buffers.add(temp);
        }
    }

    public void merge() throws IOException {
        DiskIO temp = new DiskIO("ter");
        allocateBuffers();
        String path = "img"; int count = fileNumber;
        while(count > 1) {
            for (int i = bufferNumber; i > 0; i--) {
                //buffers.add(i - 1, new ArrayList<>());
                mergeBuffer(i - 1, bufferNumber - 1);
                if (fileNumber < 1) {
                    //fileNumber = counterFile;
                    break;
                }
            }
            //count = temp.checkEnd(path);
            count = fileNumber;
            if (testNum == 0 && count < 1) {
                path = "img2";
                //count = temp.checkEnd(path);
                testNum = -1;
                fileNumber = counterFile;
                count = fileNumber;
                counterFile = 0;
                fileNumberMerge = 0;
            } else if (testNum == -1 && count < 1){
                path = "img";
                //count = temp.checkEnd(path);
                testNum = 0;
                fileNumber = counterFile;
                count = fileNumber;
                counterFile = 0;
                fileNumberMerge = 0;
            }else{
                path = "img";
            }
        }
        System.out.println("Soting ended");
    }

    private void mergeBuffer(int xd, int n) throws IOException {
        DiskIO save = new DiskIO("ter");
        List<DiskIO> files = new ArrayList<>();
        List<Record> buffer = buffers.get(xd);
        List<Record> result = new ArrayList<>();
        int bytesSaved = 0; String fileName;
        if(testNum == 0) {
            fileName = "img/ter";// + fileNumberMerge;// + ".txt";
        }else{
            fileName = "img2/ter";// + fileNumberMerge;// + ".txt";
        }
        for(int i = 0; i < n; i++){
            File file = new File(fileName + fileNumberMerge + ".txt");

            // Check if the file exists
            if (file.exists()) {
                files.add(new DiskIO(fileName + fileNumberMerge));
                fileNumberMerge++;
                files.get(i).openIN();
            }
            else {
                n = i;
                break;
            }
        }
        if(testNum == 0) {
            fileName = "img2/ter" + counterFile + ".txt";
        }else{
            fileName = "img/ter" + counterFile + ".txt";
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
                files.get(temp).deleteFile();
                files.remove(temp);
                //n--;
            }else
                result.add(tempRecord);
            if(buffer.size() == bufferSize){
                save.saveBuffer(buffer, fileName);
                bytesSaved += buffer.size() * 6 * Integer.BYTES;
                buffer.clear();// = new ArrayList<>();
                //buffer = te;
            }
        }
        save.saveBuffer(buffer, fileName);
        buffer.clear();
        counterFile++;
        fileNumber-=n;
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
