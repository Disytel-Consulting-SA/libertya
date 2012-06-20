package org.openXpertya.report.jcalc;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.event.*;


public class jcalc extends JFrame {
    
    Calculator calc = new Calculator();
    
    
    public jcalc(){
        this(false);
    }
    
    
    public jcalc(boolean this_is_an_app){
        this.setTitle("JCalc Console");
        
        //this needs to be changed for applets, maybe if statement?
        if(!this_is_an_app){
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }else {
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        
        ResultsList results      = new ResultsList(calc);
        JScrollPane scrollPane   = new JScrollPane(results);

        GuiCommandLine command_line = new GuiCommandLine(calc, results);  
        results.setCommandLine(command_line);
        
        scrollPane.setNextFocusableComponent(command_line);
        
        
        JSplitPane splitPane = new JSplitPane();
        boolean splitPane_on = false;
        
        if(splitPane_on){
            splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, command_line);
            //JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, command_line);
        }
        
        Container contentPane = getContentPane();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        contentPane.setLayout(gridbag);
        c.fill    = GridBagConstraints.BOTH;
        c.anchor  = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.weighty = 10;
        c.gridy   = 0;
        c.insets = new Insets(5,10,5,10);
            //top, left, bottom, right
            
        if(splitPane_on){
            this.getContentPane().add(splitPane,c);
        }else{
            this.getContentPane().add(scrollPane,c);
            c.insets = new Insets(5,10,5,10);
            c.weighty = 0;
            c.gridy   = 1;
            this.getContentPane().add(command_line,c);
        } 

        
        command_line.selectAll();
    }

    public static void main(String[] args) {
        jcalc console = new jcalc();
        console.setSize(400,400);
        console.setLocation(300,200);
        console.setVisible(true);
    }    
    
}//end - class Calc_console extends JFrame


