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
    private final int recToGenerate = 20 * recordSize;

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
            int value = rand.nextInt(50);  // Small integer between 0 and 255

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
        return lista;
    }

    public void saveBuffer(Buffer buffer, String addon, int bufferSize) throws IOException{
        String file = filenameNoext + addon + ".txt";//"img/"+this.filenameNoext + buffNum + ".txt";
        openOUT(file, true);
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
        temp.get(binaryData);
        bos.write(binaryData);
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
        buffer.setBytesRead(bufferSize);
        buffer.setBuffer(lista);
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
}

