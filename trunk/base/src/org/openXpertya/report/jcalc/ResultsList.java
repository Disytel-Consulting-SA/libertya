package org.openXpertya.report.jcalc;


import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

/*TODO
 *  align equations during that transtion from no vertical scroll bar to vertical scroll bar
 *  allow focus to come back down into the command line with down arrow
 *  fix hitting enter and then replacing the entire command line
 *
 *  show (dec|octal|hex), rpn
 *
 */

public class ResultsList extends javax.swing.JList implements java.util.Observer,KeyListener,  FocusListener {
    Calculator results_lists_calc;
    GuiCommandLine results_lists_cmd_line;

    Vector list = new Vector();
    
    
    public void setCommandLine(GuiCommandLine cl){
        this.results_lists_cmd_line = cl;
    }
    
    public void setCommandLineText(String s){
        results_lists_cmd_line.setText(s);
        results_lists_cmd_line.grabFocus();
    }
    
    private void fixList(){

        Font f = this.getFont();
        FontMetrics fm = this.getFontMetrics(f);
        
        int list_width = this.getParent().getWidth();

        
        int tab = list_width/3;
        String tab_string = new String("  ");
        while(fm.stringWidth(tab_string+" ")<tab){
            tab_string+=" ";
        }


        
        for(int i=0; i<list.size(); i++){
            if(i%2==1){
                String equation = (String)list.elementAt(i);
                String s = new String(tab_string+equation);
                
                while(fm.stringWidth(s+"  ")<list_width){
                    s = " " + s;
                }
                
                list.setElementAt(s,i);
            }
        }
    }
    
    public void update(Observable o, Object arg){
        list = results_lists_calc.entries.getAllEntries();
        fixList();
        this.setListData(list);
        if(list.size()>0){
            //with out this clearing the list causes problems
            this.ensureIndexIsVisible(this.getModel().getSize()-1);
            //this.ensureIndexIsVisible(this.getModel().getSize());
            
        }
    }

    
    public ResultsList(Calculator c){
        this.results_lists_calc = c;
        this.results_lists_calc.tellMeAboutEntries(this);
        this.addMouseListener(new ResultList_MouseListener(this));
        this.addKeyListener(this);
    }
    
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        //System.out.println(keyCode + " => " + e);
        
        //del||delet => 127
        //backspace  => 8
        //arrow down => 40
        
        if(keyCode==10){
            //return
            this.setCommandLineText(getSelectedValue().toString().trim());
            //results_lists_cmd_line.setText(this.getSelectedValue());
            //results_lists_cmd_line.grabFocus();

        }else if(keyCode==38){
            //up
            
        }else if(keyCode==40){
            int last_element = results_lists_calc.entries.getNumEntries() * 2 -1;
            if(this.getSelectedIndex()==last_element){
                System.out.println("leave");
                //this.setSelectedIndex(-1);
                this.transferFocus();
                return;
                //this.transferFocus();
            }
            //down
        }else if(keyCode==127 || keyCode==8){
            int selectedIndex = this.getSelectedIndex();
            if(selectedIndex>-1){
                int delete_index = (int)Math.ceil( ((long)selectedIndex) /2);
                int size = results_lists_calc.entries.getNumEntries();
                try{
                    results_lists_calc.entries.delete(size-delete_index);
                }catch(Exception excep){
                    //don't do anything
                }

                if(size-1==0){
                    //get rid of focus, as there are no more elements left
                    this.transferFocus();
                    return;
                }
                
                if(selectedIndex>=((size-1)*2)){
                    if(selectedIndex%2==1){
                        this.setSelectedIndex(( (size-1)*2)-1 );
                    }else{
                        this.setSelectedIndex(( size-1)*2-2 );
                    }
                }else{
                    this.setSelectedIndex(selectedIndex);
                }
                
            }
                            
            
        }
    }//end - public void keyPressed(KeyEvent e)

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}    
    
    
    
    //this shouldn't be in here, but i got sick
    //of accidently hitting RUN while editing this file
    //and getthing an error, then having to shut the run window, then...
    public static void main(String[] args){
        jcalc console = new jcalc();
        console.setSize(400,400);
        console.setLocation(300,200);
        console.setVisible(true);            
    }    
    
    public void focusGained(java.awt.event.FocusEvent focusEvent) {
        System.out.println("gained the focus");
    }
    
    public void focusLost(java.awt.event.FocusEvent focusEvent) {
        System.out.println("lost the focus");
    }

    
    
}//end - class ResultsList extends javax.swing.JList


class ResultList_MouseListener implements MouseListener {
    ResultsList mouselisteners_results_list;
    
    public ResultList_MouseListener(ResultsList r){
        this.mouselisteners_results_list = r;
    }
    
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount()==2){
            if(mouselisteners_results_list.getSelectedIndex()>-1){
                String value = (String)mouselisteners_results_list.getSelectedValue();
                mouselisteners_results_list.setCommandLineText(value.trim());
            }
        }
    }
    public void mouseEntered(MouseEvent e){} 
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e)  {}
}//end - class ResultList_MouseListener implements MouseListener {

