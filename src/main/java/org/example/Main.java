package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        //DiskIO diskIO = new DiskIO("1.txt"); diskIO.main("tets");
/*
        // Wczytaj rekordy z pliku lub z klawiatury
        System.out.println("Czy chcesz wczytać dane z pliku? (t/n)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("t")) {
            List<Record> records = diskIO.readRecords();
            System.out.println("Zawartość pliku przed sortowaniem: " + records);
            NaturalMergeSort sorter = new NaturalMergeSort(records);
            List<Record> sortedRecords = sorter.sort();
            System.out.println("Zawartość pliku po sortowaniu: " + sortedRecords);
        } else {
            diskIO.clearFile();
            System.out.println("Ile rekordów chcesz wprowadzić?");
            int n = scanner.nextInt();
            scanner.nextLine(); // Konsumuje nową linię
            for (int i = 0; i < n; i++) {
                System.out.println("Wprowadź rekord:");
                int value = scanner.nextInt();
                diskIO.writeRecord(new Record(value));
            }
            List<Record> records = diskIO.readRecords();
            System.out.println("Zawartość pliku przed sortowaniem: " + records);
            NaturalMergeSort sorter = new NaturalMergeSort(records);
            List<Record> sortedRecords = sorter.sort();
            System.out.println("Zawartość pliku po sortowaniu: " + sortedRecords);
        }
*/
        DiskIO temp = new DiskIO("ter");
        int teste = temp.main("no");
        BigBuffers test = new BigBuffers(10, 1001, "ter", teste);
        test.start();
        test.merge();
        test.showResults();
        //test.showResults1();
        test.check();
        //temp = new DiskIO("ter2");
        //temp.showFile(); temp.se
        //temp.setFilename("ter2"); temp.showResults();
        //temp.showFile();
        scanner.close();
    }
}
