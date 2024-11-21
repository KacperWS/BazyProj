package org.example;

public class Record implements Cloneable{

    private int id;

    private int[] data;

    public void setData(int[] data) {
        this.data = data;
        this.id = calcValue();
    }

    public Record(int[] dataSet) {
        this.data = dataSet;
        this.id = calcValue();
    }

    public Record(int[] dataSet, int id) {
        this.data = dataSet;
        this.id = id;
    }

    @Override
    public Record clone() {
        try {
            return (Record) super.clone();  // Shallow copy
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    public boolean isEmpty(){
        int check = 0;
        for(int i = 0; i < 6; i++){
            if(data[i] == 0)
                check++;
        }
        return check == 6;
    }

    public int[] getData() {
        return data;
    }

    private int calcValue(){
        int[] temp = this.data;
        int x = temp[5];
        int suma = 0, xpower = x;
        suma += temp[0] + temp[1] * xpower;
        suma+= temp[2] * (xpower *= x);
        suma+= temp[3] * (xpower *= x);
        suma+= temp[4] * (xpower * x);
        return suma;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}

