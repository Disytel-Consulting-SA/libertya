package org.openXpertya.report.jcalc;


import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class GuiCommandLine extends javax.swing.JTextField implements KeyListener,FocusListener  {
    Calculator command_lines_calc;
    ResultsList results_list;
    
    int error_msg = 0;
        //0 - show and print
        //1 - print
        //2 - nothing

    
    boolean highLightAll = true;
        //determines if the command line should be highlighted
        //  after hitting enter
        //this'll be set to false only when there is a syntax error
    
    
    public GuiCommandLine(Calculator c, ResultsList r){
        //GuiCommandLine gkl = new GuiCommandLine();
	//CommandLine_KeyListener ckl = new CommandLine_KeyListener();
        this.addKeyListener(this);
        //this.requestFocus();
        
        this.setText("Enter Equations Here");
        this.selectAll();
        
        command_lines_calc = c;
        results_list = r;
    }//end - CommandLine

    
    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if(keyCode==10){
            //return
            String equation = this.getText();
            
            try{
                String result = command_lines_calc.evaluate_equation_and_add(equation);
                //System.out.println(result);
            } 
            catch(CalculatorException exception){
                
                if(error_msg<2){
                    System.out.print("error:"+equation + "=>" + exception.getMessage());
                    if(exception.location!=-1){
                        System.out.print("=>" + exception.location);
                    }
                    System.out.println();
                }
                
                if(error_msg==0){
                    Object[] options = {"OK", "No More Dialog", "Don't Warn Me" };
                    error_msg = JOptionPane.showOptionDialog(null, exception.getMessage(), "Error", 0, 0, null, options, options[0]);
                }
                
                if(exception.location!=-1){
                    this.setCaretPosition(exception.location);
                    highLightAll = false;
                }
                
            }
            catch(Exception exception){
                String msg = "PROGRAM ERROR: " + exception;
                
                if(error_msg<2){
                    System.out.println(msg);
                    exception.printStackTrace();    
                }
                if(error_msg==0){
                    Object[] opt = {"OK"};
                    error_msg = JOptionPane.showOptionDialog(null, msg, "Program Error",  JOptionPane.DEFAULT_OPTION, 0, null, opt, opt[0]);
                }
            }
            catch(java.lang.OutOfMemoryError err){
                String msg = "not enough memory to perform this operation";
                if(error_msg<2){
                    System.out.println(msg);
                }
                if(error_msg==0){
                    JOptionPane.showMessageDialog(null, "WARNING", msg, JOptionPane.ERROR_MESSAGE); 
                }
            }
            catch(java.lang.Error err){
                String msg = "PROGRAM ERROR: " + err + "\nPlease contact program author.";
                
                if(error_msg<2){
                    System.out.println(msg);
                    err.printStackTrace();    
                }
                if(error_msg==0){
                    Object[] opt = {"OK"};
                    error_msg = JOptionPane.showOptionDialog(null, msg, "Program Error",  JOptionPane.DEFAULT_OPTION, 0, null, opt, opt[0]);
                }                
            }
            finally{
                //be able to turn this on and off
                if(highLightAll){
                    this.selectAll();
                }
                highLightAll=true;
            }
            
            
        }    
        else if(keyCode==38){
            //up
            if(results_list.getLastVisibleIndex()>-1){
                results_list.setSelectedIndex(results_list.getLastVisibleIndex());
                results_list.grabFocus();
            }
        }else if(keyCode==40){
            //down
        }
    }//end - public void keyPressed(KeyEvent e)

    public void keyReleased(KeyEvent e) {  }
    public void keyTyped(KeyEvent e) { }

    public void focusGained(java.awt.event.FocusEvent focusEvent) {
        System.out.println("cmd lost focus");
    }
    
    public void focusLost(java.awt.event.FocusEvent focusEvent) {
        System.out.println("cmd gained focus");
    }
    
    public static void main(String[] args) {
        jcalc console = new jcalc();
        console.setSize(400,400);
        console.setLocation(300,200);
        console.setVisible(true);
    }     
    
}//end - class CommandLine extends javax.swing.JTextField implements KeyListener{
