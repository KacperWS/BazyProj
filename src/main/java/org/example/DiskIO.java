package org.example;

import java.io.*;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.nio.ByteBuffer;

public class DiskIO {
    private String filename;
    private String filenameNoext;
    private int readCounter;
    private int writeCounter;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private RandomAccessFile raf;
    private final int recordSize = 6;
    private final int recToGenerate = 500000 * recordSize;

    public DiskIO(String filename) throws IOException {
        this.filename = filename + ".txt";
        this.filenameNoext = filename;
    }

    public void writeRecord(int[] data) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename))) {
            ByteBuffer bytes = ByteBuffer.allocate(recordSize * Integer.BYTES);
            byte[] table = new byte[recordSize * Integer.BYTES];
            for(int i = 0; i < recordSize; i++)
                bytes.putInt(data[i]);
            bytes.flip();
            bytes.get(table);
            bos.write(table);
        }
    }

    public List<Record> readRecords() throws IOException {
        List<Record> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //records.add(new Record(Integer.parseInt(line)));
            }
        }
        return records;
    }

    public void setFilename(String filename){
        this.filename = filename + ".txt";
        this.filenameNoext = filename;
    }

    public void closeIN() throws IOException {
        if (bis != null) {
            bis.close();
        }
    }

    public void closeOUT() throws IOException {
        if (bos != null) {
            bos.close();
        }
    }

    public void closeRAF() throws IOException {
        if (raf != null) {
            raf.close();
        }
    }

    public void closeALL() throws IOException {
        closeIN();
        closeOUT();
        closeRAF();
    }

    public void openIN() throws IOException {
        this.bis = new BufferedInputStream(new FileInputStream(filename));
    }

    public void openOUT(String Filename, boolean mode) throws IOException {
        this.bos = new BufferedOutputStream(new FileOutputStream(Filename, mode));
    }

    public void openRAF() throws FileNotFoundException {
        this.raf = new RandomAccessFile(filename, "r");
    }

    public int main(String args) {
        String fileName = "ter.txt";
        byte[] binaryData = new byte[4 * this.recToGenerate];
        Random rand = new Random();

        ByteBuffer test = ByteBuffer.allocate(4 * this.recToGenerate);
        for (int i = 0; i < binaryData.length / 4; i++) {
            rand.nextInt();
            int value = rand.nextInt(8) + 1;

            test.putInt(value);
        }
        test.flip(); test.get(binaryData);

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
            int chunkSize = 1024;
            for (int i = 0; i < binaryData.length; i += chunkSize) {
                int bytesToWrite = Math.min(chunkSize, binaryData.length - i);
                bos.write(binaryData, i, bytesToWrite);
            }

            System.out.println("Binary data saved successfully in chunks to " + (4 * this.recToGenerate));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 4 * this.recToGenerate;
    }

    public List<Record> read(int bufferSize) throws IOException{

        byte[] buffer = new byte[bufferSize * Integer.BYTES * recordSize];
        List<Record> lista = new ArrayList<>();
        if (bis.read(buffer) != -1) {
            ByteBuffer bufferme;
            bufferme = ByteBuffer.wrap(buffer);
            IntBuffer test = bufferme.asIntBuffer();
            int i = 0; int hmm = this.recordSize;
            int[] array = new int [hmm];
            while(i < test.capacity()){
                array[i%hmm] = test.get(i);
                i++;
                if(i%hmm == 0){
                    Record newRecord = new Record(Arrays.copyOf(array, array.length));
                    //array = array1;
                    if (newRecord.isEmpty())
                        break;
                    lista.add(newRecord);
                }
            }
        }else {
            closeIN(); //File ended
            return null; //Indicates that file ended
        }
        readCounter++;
        return lista;
    }

    public void saveBuffer(Buffer buffer, String addon, int bufferSize) throws IOException{
        openOUT(addon, true);
        byte[] binaryData = new byte[bufferSize];
        ByteBuffer temp = ByteBuffer.allocate(bufferSize);
        List<Record> list = buffer.getBuffer();
        for (Record record : list) {
            int[] data = record.getData();
            for (int j = 0; j < recordSize; j++) {
                temp.putInt(data[j]);
            }
        }
        temp.flip();
        temp.get(binaryData, 0, temp.limit());
        bos.write(binaryData); writeCounter++;
        closeOUT();
    }

    public boolean sortHere(int n, Buffer buffer, int bufferSize) throws IOException {
        openRAF();
        byte[] bufferByte = new byte[bufferSize];
        List<Record> lista = new ArrayList<>();
        raf.seek(buffer.getJump() + buffer.getBytesRead());
        if (raf.read(bufferByte) != -1) {
            ByteBuffer bufferme;
            bufferme = ByteBuffer.wrap(bufferByte);
            IntBuffer test = bufferme.asIntBuffer();
            int i = 0; int hmm = this.recordSize;
            int[] array = new int [hmm];
            while(i < test.capacity()){
                array[i%hmm] = test.get(i);
                i++;
                if(i%hmm == 0){
                    Record newRecord = new Record(Arrays.copyOf(array, array.length));
                    if (newRecord.isEmpty())
                        break;
                    lista.add(newRecord);
                }
            }
        }else {
            closeRAF(); //File ended
            return true; //Indicates that file ended
        }
        buffer.updateBytesRead(bufferSize);
        buffer.setBuffer(lista); readCounter++;
        closeRAF();
        return false;
    }

    public void deleteFile(){
        Path path = Paths.get(filename);
        try {
            Files.delete(path);
        } catch (IOException e) {
            System.out.println("Failed to delete the file. Error: " + e.getMessage());
        }
    }

    public void showFile(){
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] byteBuffer = new byte[4];
            int i = 0;
            int[] temp = new int[6];
            while (fis.read(byteBuffer) != -1) {
                ByteBuffer byteBufferWrapper = ByteBuffer.wrap(byteBuffer);
                int number = byteBufferWrapper.getInt();
                if(i%6 == 0 && i > 0){
                    int x = temp[5];
                    int suma = 0, xpower = x;
                    suma += temp[0] + temp[1] * xpower;
                    suma+= temp[2] * (xpower *= x);
                    suma+= temp[3] * (xpower *= x);
                    suma+= temp[4] * (xpower * x);
                    System.out.println("Rekord = " + suma);
                }
                temp[i%6] = number;
                System.out.print(number + " ");
                i++;
            }
            int x = temp[5];
            int suma = 0, xpower = x;
            suma += temp[0] + temp[1] * xpower;
            suma+= temp[2] * (xpower *= x);
            suma+= temp[3] * (xpower *= x);
            suma+= temp[4] * (xpower * x);
            System.out.println("Rekord = " + suma);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showResults(){
        System.out.println("Reads: " + readCounter + " Writes: " + writeCounter);
    }

    public void check(){
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] byteBuffer = new byte[4];
            int i = 0;
            int[] temp = new int[6];
            int suma = 0; int next = -1; int check = 0;
            while (fis.read(byteBuffer) != -1) {
                ByteBuffer byteBufferWrapper = ByteBuffer.wrap(byteBuffer);
                int number = byteBufferWrapper.getInt();
                if(i%6 == 0 && i > 0){
                    int x = temp[5];
                    suma = 0; int xpower = x;
                    suma += temp[0] + temp[1] * xpower;
                    suma+= temp[2] * (xpower *= x);
                    suma+= temp[3] * (xpower *= x);
                    suma+= temp[4] * (xpower * x);
                    if(next <= suma)
                        check++;
                    //System.out.println("Rekord = " + suma + " next " + next);
                    next = suma;
                }
                temp[i%6] = number;
                //System.out.print(number + " ");
                i++;
            }
            int x = temp[5];
            suma = 0;int xpower = x;
            suma += temp[0] + temp[1] * xpower;
            suma+= temp[2] * (xpower *= x);
            suma+= temp[3] * (xpower *= x);
            suma+= temp[4] * (xpower * x);
            if(next <= suma)
                check++;
            //next = suma;
            System.out.println("Rekord = " + check);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

