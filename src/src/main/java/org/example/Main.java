package org.example;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        DiskIO diskIO = null; boolean[] variables = new boolean[2]; variables[0] = false; variables[1] = true;

        System.out.println("What can I do for you? (Merging with large buffers)");

        boolean loop = true;
        while(loop) {
            System.out.println("1. Read from the file \n2. Create random dataset \n3. Read dataset from keyboard \n4. Change options \n5. Sorting \n6. Exit");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": {
                    System.out.println("Specify the file to read:");
                    choice = scanner.nextLine();
                    diskIO = new DiskIO(choice);
                    break;
                }

                case "2": {
                    System.out.println("Creating random dataset:");
                    System.out.println("Specify the number of records:");
                    choice = scanner.nextLine(); int temp = Integer.parseInt(choice);
                    diskIO = new DiskIO("ter.txt");
                    diskIO.setRecToGenerate(temp);
                    diskIO.createDataset();
                    break;
                }

                case "3": {
                    System.out.println("Specify the number of records:");
                    choice = scanner.nextLine(); int temp = Integer.parseInt(choice);
                    diskIO = new DiskIO("ter.txt");
                    diskIO.deleteFile();
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
                    System.out.println("1. For all results 2. Only start and end 3.Quit");
                    System.out.println("Show all: " + Boolean.toString(!variables[0]) + " Show nothing: " + Boolean.toString(!variables[1]));
                    choice = scanner.nextLine(); int ch = Integer.parseInt(choice);
                    if(ch == 1)
                        variables[0] = !variables[0];
                    else if(ch == 2)
                        variables[1] = !variables[1];
                    System.out.println();
                    break;
                }

                case "5": {
                    System.out.println("Give number of buffers and bufferSize:");
                    choice = scanner.nextLine();
                    String[] stringArray = choice.split(" ");
                    assert diskIO != null;
                    BigBuffers sort = new BigBuffers(Integer.parseInt(stringArray[1]), Integer.parseInt(stringArray[0]), diskIO,diskIO.getRecToGenerate() * 4);
                    sort.setShowMidResults(variables[1]); sort.setShowResults(variables[0]);
                    sort.start();
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

        scanner.close();
    }
}
