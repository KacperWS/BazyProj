package org.example;

public class Record {

    private int id;

    private int[] data = new int[6];

    public void setData(int[] data) {
        this.data = data;
        this.id = calcValue();
    }

    public Record(int[] dataSet) {
        this.data = dataSet;
        this.id = calcValue();
    }

    public int getId() {
        return this.id;
    }

    public int[] getData() {
        return data;
    }

    private int calcValue(){
        int[] temp = this.data;
        int x = temp[5];
        return temp[0] + temp[1]*x + temp[2]*x^2 + temp[3]*x^3 + temp[4]*x^4;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}

