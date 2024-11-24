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
    private final int recToGenerate = 100000 * recordSize;

    public DiskIO(String filename) throws IOException {
        this.filename = filename + ".txt";
        this.filenameNoext = filename;
    }

    public void writeRecord(Record record) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(record.toString());
            writer.newLine();
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

    public void clearFile() throws IOException {
        new FileWriter(filename).close(); // Opróżnia plik
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
        String fileName = "ter.txt"; // Change this to your file's path

        // Example binary data (byte array)
        byte[] binaryData = new byte[4 * this.recToGenerate];  // Simulate a large binary data array
        Random rand = new Random();
        // Fill the array with some data for demonstration
        ByteBuffer test = ByteBuffer.allocate(4 * this.recToGenerate);
        for (int i = 0; i < binaryData.length / 4; i++) {
            rand.nextInt();
            int value = rand.nextInt(8);  // Small integer between 0 and 255

            test.putInt(value);
            // Store this value across the 4 bytes
        }
        test.flip(); test.get(binaryData);

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
            // Write the data in chunks
            int chunkSize = 1024; // Chunk size (1 KB)
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

        byte[] buffer = new byte[bufferSize * Integer.BYTES * recordSize]; // Buffer size contains ints so
        List<Record> lista = new ArrayList<>();
        // Read the file in chunks
        if (bis.read(buffer) != -1) {
            ByteBuffer bufferme;// = ByteBuffer.allocate(Integer.BYTES * 5 * buffSize);
            bufferme = ByteBuffer.wrap(buffer);
            IntBuffer test = bufferme.asIntBuffer();
            int i = 0; int hmm = this.recordSize;
            int[] array = new int [hmm];
            //int[] array1 = new int [hmm];
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
        //String file = filenameNoext + addon + ".txt";//"img/"+this.filenameNoext + buffNum + ".txt";
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
        //System.out.println("Binary data saved successfully in chunks to ");
        closeOUT();
    }

    public boolean sortHere(int n, Buffer buffer, int bufferSize) throws IOException {
        openRAF();
        byte[] bufferByte = new byte[bufferSize]; // Buffer size contains ints so
        List<Record> lista = new ArrayList<>();
        // Read the file in chunks
        raf.seek(buffer.getJump() + buffer.getBytesRead());
        if (raf.read(bufferByte) != -1) {
            ByteBuffer bufferme;// = ByteBuffer.allocate(Integer.BYTES * 5 * buffSize);
            bufferme = ByteBuffer.wrap(bufferByte);
            IntBuffer test = bufferme.asIntBuffer();
            int i = 0; int hmm = this.recordSize;
            int[] array = new int [hmm];
            //int[] array1 = new int [hmm];
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
            // Attempt to delete the file
            Files.delete(path);
            //System.out.println("File deleted successfully.");
        } catch (IOException e) {
            System.out.println("Failed to delete the file. Error: " + e.getMessage());
        }
    }

    public void showFile(){
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] byteBuffer = new byte[4];  // To store 4 bytes (size of an int)
            int i = 0;
            // Read the file in chunks of 4 bytes (size of an int)
            int[] temp = new int[6];
            while (fis.read(byteBuffer) != -1) {
                // Convert the 4 bytes into an int
                ByteBuffer byteBufferWrapper = ByteBuffer.wrap(byteBuffer);
                int number = byteBufferWrapper.getInt();  // Get the int value from the byte array
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

    public int checkEnd(String path){
        // Specify the folder path
        Path folderPath = Paths.get(path);
        boolean test = false; long test1 = 0;
        try {
            // Check if the folder exists and is a directory
            if (Files.exists(folderPath) && Files.isDirectory(folderPath)) {
                // Count the number of files in the folder
                long fileCount = Files.list(folderPath)
                        .filter(Files::isRegularFile) // Only count regular files
                        .count();

                int requiredFileCount = 1; // Replace with your desired number
                test1 = fileCount;
                return (int) fileCount;
                // Check if the folder contains the required number of files
                //test = fileCount == requiredFileCount;
            } else {
                System.out.println("The specified folder does not exist or is not a directory.");
            }
        } catch (IOException e) {
            System.out.println("Error reading the folder: " + e.getMessage());
        }
        return (int) test1;
        //return test;
    }

    public void check(){
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] byteBuffer = new byte[4];  // To store 4 bytes (size of an int)
            int i = 0;
            // Read the file in chunks of 4 bytes (size of an int)
            int[] temp = new int[6];
            int suma = 0; int next = -1; int check = 0;
            while (fis.read(byteBuffer) != -1) {
                // Convert the 4 bytes into an int
                ByteBuffer byteBufferWrapper = ByteBuffer.wrap(byteBuffer);
                int number = byteBufferWrapper.getInt();  // Get the int value from the byte array
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

