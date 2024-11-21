package org.example;

import java.util.*;

public class NaturalMergeSort {
    private List<Record> records;
    private int phaseCount = 0;
    private int readCount = 0;
    private int writeCount = 0;

    public NaturalMergeSort(List<Record> records) {
        this.records = records;
    }

    public List<Record> sort() {
        List<Record> sortedRecords = new ArrayList<>(records);
        boolean sorted = false;

        while (!sorted) {
            phaseCount++; // Increment phase count at the start of a new phase
            List<List<Record>> runs = new ArrayList<>();
            int start = 0;

            while (start < sortedRecords.size()) {
                List<Record> run = new ArrayList<>();
                run.add(sortedRecords.get(start));
                start++;
                while (start < sortedRecords.size() && sortedRecords.get(start - 1).getValue() <= sortedRecords.get(start).getValue()) {
                    run.add(sortedRecords.get(start));
                    start++;
                }
                runs.add(run);
            }

            if (runs.size() == 1) {
                sorted = true; // Already sorted
                break;
            }

            sortedRecords = mergeRuns1(runs);
            int a = 0;
            int b = sortedRecords.size();
            while(sortedRecords.get(a).getValue() <= sortedRecords.get(a + 1).getValue() && a < b-2){
                a++;
            }
            if(a == b - 2) {
                sorted = true;
                break;
            }
        }

        // Final counts output
        System.out.println("Number of phases: " + phaseCount);
        System.out.println("Number of reads: " + readCount);
        System.out.println("Number of writes: " + writeCount);

        return sortedRecords;
    }

    private List<Record> mergeRuns(List<List<Record>> runs) {
        List<Record> merged = new ArrayList<>();
        PriorityQueue<Record> pq = new PriorityQueue<>(Comparator.comparingInt(Record::getValue));

        // Add the first element of each run to the priority queue
        for (List<Record> run : runs) {
            if (!run.isEmpty()) {
                pq.add(run.removeFirst());
            }
        }

        // Merge runs
        while (!pq.isEmpty()) {
            Record min = pq.poll();
            merged.add(min);
            writeCount++; // Increment write count for each merged record

            // Add the next element of the run from which the minimum came
            for (List<Record> run : runs) {
                if (!run.isEmpty() && run.getFirst().getValue() >= min.getValue()) {
                    pq.add(run.removeFirst());
                    readCount++; // Increment read count for each record read from the run
                    break;
                }
            }
        }

        // Count reads for the initial merge input
        for (List<Record> run : runs) {
            readCount += run.size(); // Count all records that were part of the runs
        }

        return merged;
    }

    private List<Record> mergeRuns1(List<List<Record>> runs){
        List<Record> merged = new ArrayList<>();
        List<List<Record>> tape = new ArrayList<>();
        List<List<Record>> tape1 = new ArrayList<>();
        for(int i = 0; i < runs.size(); i++){
            if(i%2==0){
                tape.add(runs.get(i));
            }
            else{
                tape1.add(runs.get(i));
            }
        }
        int a = 0;
        int b = runs.size();
        if(a < b){
            int m = a + (b - 1) / 2;
            merged = mergeSortedLists(tape, tape1);
        }
        return merged;
    }

    /*private List<Record> merge(List<List<Record>> tape, List<List<Record>> tape1, int a, int m, int b){
        List<Record> merged = new ArrayList<>();
        int i = 0;
        int j = 0;
        int k = a;
        int n1 = m - a + 1;
        int n2 = b - m;
        while (i < n1 && j < n2){
            if()
        }
        return merged;
    }*/

    public static List<Record> mergeSortedLists(List<List<Record>> list1, List<List<Record>> list2) {
        List<Record> merged = new ArrayList<>();
        int i = 0, j = 0;

        // Flattening both lists
        List<Record> flatList1 = flatten(list1);
        List<Record> flatList2 = flatten(list2);

        // Merging the two flattened lists
        /*while (i < flatList1.size() && j < flatList2.size()) {
            if (flatList1.get(i).getValue() <= flatList2.get(j).getValue()) {
                merged.add(flatList1.get(i));
                i++;
            }/* else {
                merged.add(flatList2.get(j));
                j++;
            }*//*
        }

        // Add remaining elements from flatList1
        while (i < flatList1.size()) {
            merged.add(flatList1.get(i));
            i++;
        }

        // Add remaining elements from flatList2
        while (j < flatList2.size()) {
            merged.add(flatList2.get(j));
            j++;
        }*/
        merged = merge2(flatList1, flatList2);
        return merged;
    }

    private static List<Record> flatten(List<List<Record>> list) {
        List<Record> flatList = new ArrayList<>();
        for (List<Record> innerList : list) {
            flatList.addAll(innerList);
        }
        return flatList;
    }

    public static List<Record> merge2(List<Record> tape1,
                                      List<Record> tape2)
    {
        if (tape2.size() == 0) {
            return tape1;
        }

        // Merge the data from tape1 and tape2
        // into a temporary output tape
        List<Record> outputTape = new ArrayList<>();

        int i = 0, j = 0;

        // Merge all the tapes left
        while (i < tape1.size() && j < tape2.size()) {
            if (tape1.get(i).getValue() < tape2.get(j).getValue()) {
                outputTape.add(tape1.get(i));
                i++;
            }
            else {
                outputTape.add(tape2.get(j));
                j++;
            }
        }
        outputTape.addAll(tape1.subList(i, tape1.size()));
        outputTape.addAll(tape2.subList(j, tape2.size()));
        return outputTape;
    }
}