package org.example;

import java.io.*;
import java.nio.IntBuffer;
import java.util.*;
import java.nio.ByteBuffer;

public class DiskIO {
    private String filename;
    private int readCounter;
    private int writeCounter;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private final int recordSize = 6;

    public DiskIO(String filename) throws IOException {
        this.filename = filename;
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

    public void openIN() throws IOException {
        this.bis = new BufferedInputStream(new FileInputStream(filename));
    }

    public void openOUT() throws IOException {
        this.bos = new BufferedOutputStream(new FileOutputStream(filename));
    }

    public static void main(String args) {
        String fileName = "ter.txt"; // Change this to your file's path

        // Example binary data (byte array)
        byte[] binaryData = new byte[24];  // Simulate a large binary data array
        Random rand = new Random();
        // Fill the array with some data for demonstration
        for (int i = 0; i < binaryData.length; i++) {
            binaryData[i] = (byte) ((rand.nextInt() + i * rand.nextInt()));  // Fill with some values (0-255)
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
            // Write the data in chunks
            int chunkSize = 1024; // Chunk size (1 KB)
            for (int i = 0; i < binaryData.length; i += chunkSize) {
                int bytesToWrite = Math.min(chunkSize, binaryData.length - i);
                bos.write(binaryData, i, bytesToWrite);
            }

            System.out.println("Binary data saved successfully in chunks to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] buffer = new byte[1024]; // Buffer size (1 KB)
            int bytesRead;
            ArrayList <Record> lista = new ArrayList<>();
            // Read the file in chunks
            while ((bytesRead = bis.read(buffer)) != -1) {
                // Process the chunk of data (here we just print it in hex)
                for (int i = 0; i < bytesRead; i++) {
                    //System.out.format("0x%02X ", buffer[i]);
                }
                int buffSize = 10;
                ByteBuffer bufferme;// = ByteBuffer.allocate(Integer.BYTES * 5 * buffSize);
                bufferme = ByteBuffer.wrap(buffer);
                IntBuffer test = bufferme.asIntBuffer();
                int i = 0; int hmm;
                int[] array = new int [6];
                int[] array1 = new int [6];
                while((hmm = test.get(i)) != 0){
                    array[i%6] = hmm;
                    System.out.println(hmm);
                    i++;
                    if(i > 0 && i%6 == 0){
                        Record nowy = new Record(array);
                        nowy.setData(array);
                        array = array1;
                        lista.add(nowy);
                    }
                }
                /*int j = 0;
                byte something[] = new byte[4];
                while(j < 4){
                    something[j] = (buffer[j]);
                    j++;
                }
                int test = Integer.BYTES; BitSet a;
                System.out.println();
                int number = Integer.MAX_VALUE;
                something[0] = (byte) (number >> 24);  // Most significant byte
                something[1] = (byte) (number >> 16);
                something[2] = (byte) (number >> 8);
                something[3] = (byte) (number);       // Least significant byte
                ByteBuffer buffer1 = ByteBuffer.wrap(something);
                System.out.println(buffer1.getInt());
                int number1 = 123456789;  // Example integer

                // Create a ByteBuffer and put the int into it
                ByteBuffer buffer2 = ByteBuffer.allocate(4);  // 4 bytes for an int
                buffer2.putInt(number1);

                // Convert ByteBuffer to byte array
                byte[] byteArray = buffer2.array();

                // Print the byte array
                System.out.println("Byte array:");
                for (byte b : byteArray) {
                    System.out.printf("0x%02X ", b);  // Print each byte in hexadecimal format
                }*/
                lista.clear();
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Record> read(int bufferSize) throws IOException{
        this.openIN();

        byte[] buffer = new byte[bufferSize * Integer.BYTES]; // Buffer size contains ints so
        List<Record> lista = new ArrayList<>();
        // Read the file in chunks
        if (bis.read(buffer) != -1) {
            ByteBuffer bufferme;// = ByteBuffer.allocate(Integer.BYTES * 5 * buffSize);
            bufferme = ByteBuffer.wrap(buffer);
            IntBuffer test = bufferme.asIntBuffer();
            int i = 0; int hmm = this.recordSize;
            int[] array = new int [hmm];
            //int[] array1 = new int [6];
            while(i < test.capacity()){
                array[i%hmm] = test.get(i);
                i++;
                if(i%hmm == 0){
                    Record news = new Record(array);
                    //array = array1;
                    lista.add(news);
                }
            }
        }else {
            closeIN(); //File ended
            return null; //Indicates that file ended
        }
        return lista;
    }

    public void saveBuffers(List<Record> buffer, int bufferSize) throws IOException{
        openOUT();
        int bytesNum = bufferSize * Integer.BYTES;
        byte[] binaryData = new byte[bytesNum];
        ByteBuffer temp = ByteBuffer.allocate(bytesNum);
        for (Record record : buffer) {
            int[] data = record.getData();
            for (int j = 0; j < recordSize; j++) {
                temp.putInt(data[j]);
            }
        }
        temp.get(binaryData);
        bos.write(binaryData);
        System.out.println("Binary data saved successfully in chunks to ");
        closeOUT();
    }
}

