package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jdom.Element;
import org.openXpertya.util.DB;

public class ExecuteExportXML extends SvrProcess {

	private String Menuto="";
	private int Menuof;
	
    private String[][]  Principal=Principal();
    private String[][]  Window=Load("Window");
    private String[][]  Workflow=Load("Workflow");
    private String[][]  Task=Load("Task");
    private String[][]  Process=Load("Process");
    private String[][]  Form=Load("Form");
    private String[][]  Role=Load("Role");
    
    private String[]  Order=Order();
    
    private int MaxRow=MaxRows();

    
    /**
     * Descripcin de Mtodo: Prepare
     *
     *
     * @param 
     * @return void  
     */
	protected void prepare() {
		log.info("Loading variables...");
		
        ProcessInfoParameter[] para = getParameter();

        Menuto = para[0].getParameter().toString();
        Menuof = (( BigDecimal )para[1].getParameter()).intValue();
            
        log.info("A -> " + Menuto + " " + ", De -> " + Menuof);
        
	}
    /**
     * Descripcin de Mtodo: doIt
     * Exportamos el parametro al destino seleccionado:
     *
     *
     * @param 
     * @return String
     */
	protected String doIt() throws java.lang.Exception {
		
		//Main element
		Element Menu= new Element("Menu");
		
		//For each row:
		for (int i=0; i<MaxRow ; i++){
			Element fila = new Element("fila");
			
			//Aadimos los true/false por cada rol
			for(int j=0; j<Role.length; j++){
				//por cada fila aado la etiqueta Nombre Rol
				Element valorFila = new Element(getRoleId(j, Role));
				
				/* 
				 * Buscamos el tipo y lo establecemos como parametro
				 */
				
				if (P_getAction(j).equals("W")){
					for(int e=0; e<Window.length; e++){
						if (P_getWindow(j).equals(getId(e, Window)))
							valorFila.setText(getIsactive(e, Window));
					}	
				}
				else if (P_getAction(j).equals("F")){
					for(int e=0; e<Workflow.length; e++){
						if (P_getWorkflow(j).equals(getId(e, Workflow)))
							valorFila.setText(getIsactive(e, Workflow));
					}	
				}
				else if (P_getAction(j).equals("T")){
					for(int e=0; e<Task.length; e++){
						if (P_getTask(j).equals(getId(e, Task)))
							valorFila.setText(getIsactive(e, Task));
					}	
				}
				else if (P_getAction(j).equals("P") || P_getAction(j).equals("R")){
					for(int e=0; e<Process.length; e++){
						if (P_getProcess(j).equals(getId(e, Process)))
							valorFila.setText(getIsactive(e, Process));
					}	
				}
				else if (P_getAction(j).equals("X")){
					for(int e=0; e<Form.length; e++){
						if (P_getForm(j).equals(getId(e, Form)))
							valorFila.setText(getIsactive(e, Form));
					}	
				}
							
				fila.addContent(valorFila);
			}
			/* 
			 * Hemos aadido todas las clausulas true or false para todos los usuarios
			 * 
			 * Aadimos los Titulos por cada fila (un solo titulo con su profundidad)
			 */
			Element titulo=new Element("Titulo");
			
			fila.addContent(titulo);
			Menu.addContent(fila);
		}
		
		
		return "";
	}
    /**
     * Descripcin de Mtodo: MaxRows()
     * Establece el numero mximo de filas del menu
     *
     *
     * @param 
     * @return int
     */
	private int MaxRows(){
		//count rows
		int MaxRow=0;
		String sql="select count(*) from ad_treenodemm T, ad_menu_trl M where  T.node_id=M.ad_menu_id";
		PreparedStatement stmt = DB.prepareStatement( sql );
		
		try {
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next())
				MaxRow=rs.getInt(1);
			
				rs.close();
				return MaxRow;
			}
		catch( Exception e ){
			return 0;} 	
	}
    /**
     * Descripcin de Mtodo: Principal
     * Establece el Array principal
     * 
     * 0-ad_menu_id
     * 1-parent_id
     * 2-seqno
     * 3-name
     * 4-ad_window_id
     * 5-ad_workflow_id
     * 6-ad_task_id
     * 7-ad_process_id
     * 8-ad_form_id
     *
     * @param 
     * @return String[x][8]
     */	
	private String[][] Principal(){
		
		String [][] Resultado = new String[MaxRow][8];
		
		String sql="select M.ad_menu_id, T.parent_id, T.seqno, M.name, MP.ad_window_id, MP.ad_workflow_id, MP.ad_task_id, MP.ad_process_id, MP.ad_form_id, MP.action ";
		sql=sql + "from ad_treenodemm T, ad_menu_trl M, ad_menu MP where T.node_id=M.ad_menu_id and M.ad_menu_id=MP.ad_menu_id order by T.seqno";

		PreparedStatement stmt = DB.prepareStatement( sql );
		
		try {
			ResultSet rs = stmt.executeQuery();
			int i=0;
			while (rs.next()){
				Resultado[i][0]=rs.getString(1);
				Resultado[i][1]=rs.getString(2);
				Resultado[i][2]=rs.getString(3);
				Resultado[i][3]=rs.getString(4);
				Resultado[i][4]=rs.getString(5);
				Resultado[i][5]=rs.getString(6);
				Resultado[i][6]=rs.getString(7);
				Resultado[i][7]=rs.getString(8);
				Resultado[i][8]=rs.getString(9);
				Resultado[i][8]=rs.getString(10);
				i++;
			}
			rs.close();
			}
		catch( Exception e ){Resultado[0][0]="null";} 	
		
		return Resultado;
	}
    /**
     * Descripcin de los Mtodos: 
     * Devuelven el valor de columna en la fila x
     * 
     * 0-ad_menu_id
     * 1-parent_id
     * 2-seqno
     * 3-name
     * 4-ad_window_id
     * 5-ad_workflow_id
     * 6-ad_task_id
     * 7-ad_process_id
     * 8-ad_form_id
     * 9-Tipo de accion 
     *
     * @param 
     * @return String[x][8]
     */		
	private String P_getParent(int row){
		if (Principal[0][0].equals("null") || row>MaxRow)
			return "Error";
		return Principal[row][1];
	}
	private String P_getname(int row){
		if (Principal[0][0].equals("null") || row>MaxRow)
			return "Error";
		return Principal[row][3];
	}
	private String P_getWindow(int row){
		if (Principal[0][0].equals("null") || row>MaxRow)
			return "Error";
		return Principal[row][4];
	}
	private String P_getWorkflow(int row){
		if (Principal[0][0].equals("null") || row>MaxRow)
			return "Error";
		return Principal[row][5];
	}
	private String P_getTask(int row){
		if (Principal[0][0].equals("null") || row>MaxRow)
			return "Error";
		return Principal[row][6];
	}
	private String P_getProcess(int row){
		if (Principal[0][0].equals("null") || row>MaxRow)
			return "Error";
		return Principal[row][7];
	}
	private String P_getForm(int row){
		if (Principal[0][0].equals("null") || row>MaxRow)
			return "Error";
		return Principal[row][8];
	}
	private String P_getAction(int row){
		if (Principal[0][0].equals("null") || row>MaxRow)
			return "Error";
		return Principal[row][9];
	}
    /**
     * Descripcin de Mtodo: 
     * Establece el Array de Window, Workflow, Task, Process o Form
     * 
     * 0-ad_x_id
     * 1-isactive
     * 2-ad_role_id
     *
     * @param String "Window" or "Workflow" or "Task" or "Process" or "Form"
     * @return String[x][2]
     */	
	private String[][] Load(String type){
		
		String[][] Resultado=new String[0][0];
		String sql="";
		String sql1="";
		
		int row=0;
		
		if (type.equals("Window")){
			sql="select count(*) from ad_window_access";
			sql1="select ad_window_id, isactive, ad_role_id from ad_window_access";
		}else if (type.equals("Workflow")){
			sql="select count(*) from ad_workflow_access";	
			sql1="select ad_workflow_id, isactive, ad_role_id from ad_workflow_access";
		}else if (type.equals("Task")){
			sql="select count(*) from ad_task_access";	
			sql1="select ad_task_id, isactive, ad_role_id from ad_task_access";
		}else if (type.equals("Process")){
			sql="select count(*) from ad_process_access";
			sql1="select ad_process_id, isactive, ad_role_id from ad_process_access";
		}else if (type.equals("Form")){
			sql="select count(*) from ad_form_access";	
			sql1="select ad_form_id, isactive, ad_role_id from ad_form_access";
		}else if(type.equals("Role")){
			sql="select count from ad_role";
			sql1="select ad_role_id, isactive, name from ad_role";
		}
			
		
		//Defino el array
		PreparedStatement stmt = DB.prepareStatement( sql );
		
		try {
			ResultSet rs = stmt.executeQuery();
			if (rs.next()){
				row=rs.getInt(0);
			}
			Resultado= new String[row][2];
			rs.close();
		}
		catch( Exception e ){Resultado[0][0]="null";} 	
		//cargo el array

		stmt = DB.prepareStatement( sql1 );
		try {
			ResultSet rs = stmt.executeQuery();
			int i=0;
			while (rs.next()){
				Resultado[i][0]=rs.getString(1);
				Resultado[i][1]=rs.getString(2);
				Resultado[i][2]=rs.getString(3);
				i++;
			}
			rs.close();
		}
		catch( Exception e ){Resultado[0][0]="null";} 	
		return Resultado;
	}
	/**
     * Descripcin de los Mtodos:
     * Devuelven el valor de la columna en la fila x
     * Son metodos generales que se pueden invocar desde 
     * cualquiera de las variables:
     * 
     * Window, Workflow, Task, Process, Form
     * 
     * 0-ad_window_id
     * 1-isactive
     * 2-ad_role_id
     *
     * @param 
     * @return String[x][2]
     */	
	private String getId(int row, String[][] value){
		if (value[0][0].equals("null") || row>value.length)
			return "Error";
		return value[row][0];	
	}
	private String getIsactive(int row, String[][] value){
		if (value[0][0].equals("null") || row>value.length)
			return "Error";
		return value[row][1];	
	}
	private String getRoleId(int row, String[][] value){
		if (value[0][0].equals("null") || row>value.length)
			return "Error";
		return value[row][2];	
	}
    /**
     * Descripcin de Mtodo: 
     * Ordena en una Array las ID segun como queramos que se muestren
     * Segun el parent_id de menor a mayor, y despues de un parent id segun seqno
     *
     * @param void
     */	
	private String[] Order(){
		String[] Resultado=new String[0];
		int i=0;
		
		while (i<Principal.length){
			//si es menu principal
			if (P_getParent(i).equals("0")){
				
			}
			i++;
		}
		
		return Resultado;
	}
}


