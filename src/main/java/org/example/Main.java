package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        DiskIO diskIO;

        System.out.println("What can I do for you? (Merging with large buffers)");

        boolean loop = true;
        while(loop) {
            System.out.println("1. Read from the file \n2.Create random dataset \n3. Read dataset from keyboard \n4. Change options \n5.Sorting \n6.Exit \n");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": {
                    System.out.println("Specify the file to read: \n");
                    choice = scanner.nextLine();
                    diskIO = new DiskIO(choice);
                    break;
                }

                case "2": {
                    System.out.println("Creating random dataset: \n");
                    diskIO = new DiskIO("ter.txt");
                    diskIO.createDataset();
                    break;
                }

                case "3": {
                    System.out.println("Specify the number of records: \n");
                    choice = scanner.nextLine(); int temp = Integer.parseInt(choice);
                    diskIO = new DiskIO("ter.txt");
                    for(int i = 0; i < temp; i++){
                        choice = scanner.nextLine();
                        String[] stringArray = choice.split(" ");

                        int[] intArray = new int[stringArray.length];

                        for (int j = 0; j < stringArray.length; j++) {
                            intArray[j] = Integer.parseInt(stringArray[j]);
                        }
                        diskIO.writeRecord(intArray);
                    }


                    break;
                }

                case "4": {

                    break;
                }

                case "5": {

                    break;
                }

                case "6": {
                    loop = false;
                    break;
                }

                default: {
                    System.out.println("This is not a correct option");
                    break;
                }
            }
        }

        /*DiskIO temp = new DiskIO("ter");
        int teste = temp.main("no");
        BigBuffers test = new BigBuffers(50, 1001, "ter", teste);
        test.start();
        test.merge();
        test.showResults();
        //test.showResults1();
        test.check();
        //temp = new DiskIO("ter2");
        //temp.showFile(); temp.se
        //temp.setFilename("ter2"); temp.showResults();
        //temp.showFile();
        scanner.close();*/
    }
}
