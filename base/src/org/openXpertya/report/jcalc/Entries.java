package org.openXpertya.report.jcalc;

import java.util.Vector;


public class Entries extends java.util.Observable  {

    public Vector entries_list = new Vector();

    class Entry {
        String equation, result;
        public Entry(String e, String r){
            this.equation=e;
            this.result=r;
        }
    }

    public void clear(){
        entries_list.clear();
        setChanged();
        this.notifyObservers();
    }

    public int getNumEntries(){
        return entries_list.size();
    }
    
    public void reverseDelete(int i){ // throws CalculatorException {
        //if(i>entries_list.size()||i<1){
        //    throw new CalculatorException("Domain error");
        //}
        entries_list.removeElementAt(i);
        setChanged();
        this.notifyObservers();
    }
    
    
    public void delete(int i) throws CalculatorException {
        if(i>entries_list.size()||i<1){
            throw new CalculatorException("Domain error");
        }
        entries_list.removeElementAt(entries_list.size()-i);
        setChanged();
        this.notifyObservers();
    }    
    
    
    public String getAns(int i) throws CalculatorException {
        if(i>entries_list.size()||i<1){
            System.out.println("size: " + entries_list.size());
            throw new CalculatorException("Domain error");
        }
        return ((Entry)entries_list.elementAt( entries_list.size()-i )).result;
    }

    
    public String getEntry(int i) throws CalculatorException {
        if(i>entries_list.size()||i<1){
            throw new CalculatorException("Domain error");
        }
        
        System.out.println("retrungin: " + ((Entry)entries_list.elementAt( entries_list.size()-i )).equation);
        
        return ((Entry)entries_list.elementAt( entries_list.size()-i )).equation;
    }        

    
    public Vector getAllEntries(){
        Vector return_vector = new Vector(entries_list.size()*2);
        for(int i=0; i<entries_list.size(); i++){
            Entry e = (Entry)entries_list.elementAt(i);
            return_vector.add(e.equation);
            return_vector.add(e.result);
        }
        return return_vector;
    }

    
    public void addEntry(String equation, String results){
        Entry e = new Entry(equation,results);
        entries_list.add(e);
        setChanged();
        this.notifyObservers();
    }
}//end 

