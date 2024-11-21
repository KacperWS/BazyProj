package org.example;

import java.io.*;
import java.nio.IntBuffer;
import java.util.*;
import java.nio.ByteBuffer;

public class DiskIO {
    private String filename;
    private int readCounter;
    private int writeCounter;

    public DiskIO(String filename) {
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
                records.add(new Record(Integer.parseInt(line)));
            }
        }
        return records;
    }

    public void clearFile() throws IOException {
        new FileWriter(filename).close(); // Opróżnia plik
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
                int[] array = new int [5];
                int[] array1 = new int [5];
                while((hmm = test.get(i)) != 0){
                    array[i%5] = hmm;
                    System.out.println(hmm);
                    i++;
                    if(i > 0 && i%5 == 0){
                        Record nowy = new Record(1);
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

    public IntBuffer read(){
        IntBuffer results = null;

        return results;
    }
}

