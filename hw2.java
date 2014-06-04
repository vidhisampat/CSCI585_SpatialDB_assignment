package hw2;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class hw2 extends JFrame {

	private static	ImageIcon imageicon;
	private static JLabel jLabel1;
	private BufferedImage img = null;
	static Connection conn;
	private static Graphics g ;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
				try {
					hw2 frame = new hw2();
									
					frame.setTitle("USC Map");
					frame.setVisible(true);					
					
					jLabel1.setAutoscrolls(true);
					jLabel1.setSize(820, 580);
					jLabel1.setIcon(imageicon);
		
					g = (Graphics) jLabel1.getGraphics();
					jLabel1.paint(g);
					
					display(args);
			
				} catch (Exception e) {
					e.printStackTrace();
				}
		
	}

	/**
	 * Create the frame.
	 */
	public hw2() {
		getContentPane().setSize(new Dimension(840, 600));
		populateDB p = new populateDB();	
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(840, 600, 840, 600);
		
		imageicon = new javax.swing.ImageIcon("map.jpg");
		//System.out.println(new java.io.File("map.jpg").exists());
		jLabel1 = new JLabel();
		getContentPane().add(jLabel1);
		
	try {
			connectDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void display(String[] args)
	{	
		//other_parameters
		int[] op1 = new int[4];	
		int op2 = 0;
		String op3="";
		op1[0]=0;
		op1[1]=0;
		op1[2]=820;
		op1[3]=580;
		
     		Statement stat;
			try {
				
				stat = conn.createStatement();
				String select = "SELECT f.X,f.Y from firehydrant fh,table(sdo_util.getvertices(fh.coords))f";								
				ResultSet rs;
				rs = stat.executeQuery(select);
				g.setColor(Color.GREEN);
	            
				while(rs.next())
				{		
						int x = rs.getInt("X");
			            int y = rs.getInt("Y");
				        g.fillRect(x, y, 5, 5);
				}
				
			String selectb = "SELECT bldg.vertices_no,b.X,b.Y from building bldg ,table(sdo_util.getvertices(bldg.coords))b";
			ResultSet rs1;
            rs1 = stat.executeQuery(selectb);
            while(rs1.next())
            {
            	int count = rs1.getInt("vertices_no");
                int[] x = new int[count];
                int[] y = new int[count];

                g.setColor(Color.YELLOW);
	            x[0]=rs1.getInt("X");
            	y[0]=rs1.getInt("Y");
            	//System.out.println(x[0]+" "+y[0]);
            	int i;     
	            for(i=1;i<count;i++)
	            {
	            	rs1.next();
	              	x[i]=rs1.getInt("X");
	            	y[i]=rs1.getInt("Y");
	            	//System.out.println(x[i]+" "+y[i]);
	            }
	            g.drawPolygon(x, y,count);
	            
            }
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
			if(args[0]!=null)
			{
				ResultSet res;
				g.setColor(Color.RED);
				
				Statement statq;
				statq = conn.createStatement();

				switch(args[0].toLowerCase())
				{			
				case "window": 
						System.out.println("Enters window");
						op1[0]=Integer.parseInt(args[2]);
						op1[1]=Integer.parseInt(args[3]);
						op1[2]=Integer.parseInt(args[4]);
						op1[3]=Integer.parseInt(args[5]);
						switch(args[1].toLowerCase())
						{
						case "firebuilding":
							String win1= "SELECT bldg.BLDG_name,b.x,b.y, bldg.vertices_no,bldg.bldg_id from building bldg, "
									+ "table(MDSYS.sdo_util.getvertices(bldg.coords))b," 
							+ " FIREBUILDING f  where bldg.bldg_name = f.f_name and"
							+ "	SDO_filter(bldg.coords,SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1), "
							+ "SDO_ORDINATE_ARRAY("+op1[0]+","+op1[1]+","+op1[2]+","+op1[3]+")),'mask=ANYINTERACT')='TRUE'"; 			
							
							System.out.println(win1);
				            res = statq.executeQuery(win1);
				            System.out.println("Ids of buildings on fire in the given window");
				            while(res.next())
				            {
				            	int count = res.getInt("vertices_no");
				                int[] x = new int[2*count];
				                int[] y = new int[2*count];
				                String id = res.getString("bldg_id");

					            System.out.println(id);
				                
				                x[0]=res.getInt("X");
				            	y[0]=res.getInt("Y");
				            	int i;     
					            for(i=1;i<count;i++)
					            {
					            	res.next();
					              	x[i]=res.getInt("X");
					            	y[i]=res.getInt("Y");
					            }
					            g.drawPolygon(x, y,count);
					            
				            }
							break;			
						case "firehydrant":
							try {
								String select1 = "SELECT f.X,f.Y, fh.fh_id from firehydrant fh,table(sdo_util.getvertices(fh.coords))f"+" "
												+"where "
												+ "SDO_filter(fh.coords,SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1), "
												+ "SDO_ORDINATE_ARRAY("+op1[0]+","+op1[1]+","+op1[2]+","+op1[3]+")),"
														+ "'mask=ANYINTERACT')='TRUE'"; 			
												

					            System.out.println(select1);

					            System.out.println("Ids of firehydrants in the window");
							    res = statq.executeQuery(select1);
					            while(res.next())
					            {
					            	int x = res.getInt("X");
					                int y = res.getInt("Y");

						            System.out.println(res.getString("fh_id"));
					                g.fillRect(x, y, 5, 5);
					            }} catch (SQLException e) {
					    			// TODO Auto-generated catch block
					    			e.printStackTrace();
					    		}
						break;
						case "building":
							System.out.println("Enters building");
							String win2= "SELECT bldg.BLDG_name,b.x,b.y, bldg.vertices_no,bldg.bldg_id from building bldg, "
									+ "table(MDSYS.sdo_util.getvertices(bldg.coords))b" 
							+ " where SDO_filter(bldg.coords,SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1), "
							+  "SDO_ORDINATE_ARRAY("+op1[0]+","+op1[1]+","+op1[2]+","+op1[3]+")),'mask=ANYINTERACT')='TRUE' and "
							+ "bldg.BLDG_name IN "
							+ "(SELECT bldg.BLDG_name from building bldg Minus Select f.f_name from firebuilding f)";
							
							System.out.println(win2);
				            res = statq.executeQuery(win2);

				            System.out.println("Ids of buildings not on fire in the given window");
				            while(res.next())
				            {
				            	int count = res.getInt("vertices_no");
				                int[] x = new int[count];
				                int[] y = new int[count];

				                x[0]=res.getInt("X");
				            	y[0]=res.getInt("Y");

					            System.out.println(res.getString("bldg_id"));
				            	int i;     
					            for(i=1;i<count;i++)
					            {
					            	res.next();
					              	x[i]=res.getInt("X");
					            	y[i]=res.getInt("Y");
					            }
					            g.drawPolygon(x, y,count);
					            
				            }
							break;
						}
						break;
///------------------------------------------------------------
				case "within":
					System.out.println("Enters within");
					op2 =Integer.parseInt( args[3]);
					switch(args[1].toLowerCase())
					{
					case "firebuilding":
						String temp,temp1;
						temp1= "Select b1.x,b1.y, bldg.vertices_no from building bldg, "
								+ "table(MDSYS.sdo_util.getvertices(bldg.coords))b1"
								+ " where bldg.bldg_name = '"+ args[2].toUpperCase()+"'"
										;
			            res = statq.executeQuery(temp1);
			            res.next();
			            int count = res.getInt("vertices_no");
			            int[] tx = new int[count];
		                int[] ty = new int[count];
		                temp =  "SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1), SDO_ORDINATE_ARRAY(";
		                
		                tx[0]=res.getInt("X");
			            ty[0]=res.getInt("Y");
			            temp = temp + tx[0]+","+ ty[0];
			                
				        for(int i=1;i<count;i++)
				        {
				           	res.next();
				          	tx[i]=res.getInt("X");
				           	ty[i]=res.getInt("Y");
				           	temp = temp + "," +tx[i]+","+ty[i];
				        }   
				        temp = temp +","+tx[0]+","+ty[0]+"))";
				        
						String win1= "SELECT bldg.BLDG_name,b.x,b.y, bldg.vertices_no,bldg.bldg_id from building bldg,"
								+ " table(MDSYS.sdo_util.getvertices(bldg.coords))b," 
						+ " FIREBUILDING f  where bldg.bldg_name = f.f_name and"
						+ " bldg.bldg_name<>'"+ args[2].toUpperCase()+"' and "
						+ " SDO_WITHIN_DISTANCE(bldg.coords, "+temp +",  'distance="+op2+"') = 'TRUE'";	
						
						System.out.println(win1);
			            res = statq.executeQuery(win1);
			            System.out.println("The ids of firebuildings at distance="+op2+"from "+args[2]);
		            	
			            while(res.next())
			            {
			            	int count1 = res.getInt("vertices_no");
			                int[] x = new int[2*count];
			                int[] y = new int[2*count];

			                x[0]=res.getInt("X");
			            	y[0]=res.getInt("Y");
			            	
			            	System.out.println(res.getString("bldg_id"));
			            	int i;     
				            for(i=1;i<count1;i++)
				            {
				            	res.next();
				              	x[i]=res.getInt("X");
				            	y[i]=res.getInt("Y");
				            }
				            g.drawPolygon(x, y,count1);
				            
			            }
						break;			
			case "firehydrant":
				String t,t1;
				t1= "Select b1.x,b1.y, bldg.vertices_no from building bldg, table(MDSYS.sdo_util.getvertices(bldg.coords))b1"
						+ " where bldg.bldg_name = '"+ args[2].toUpperCase()+"'";
				
	            res = statq.executeQuery(t1);
	            res.next();
	            int cnt1 = res.getInt("vertices_no");
	            int[] xt1 = new int[cnt1];
                int[] yt1 = new int[cnt1];
                t =  "SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1), SDO_ORDINATE_ARRAY(";
                
                xt1[0]=res.getInt("X");
	            yt1[0]=res.getInt("Y");
	            t = t + xt1[0]+","+ yt1[0];
	                
		        for(int i=1;i<cnt1;i++)
		        {
		           	res.next();
		          	xt1[i]=res.getInt("X");
		           	yt1[i]=res.getInt("Y");
		           	t = t + "," +xt1[i]+","+yt1[i];
		        }   
		        t = t +","+xt1[0]+","+yt1[0]+"))";
		      	
				String select1 = "SELECT f.X,f.Y, fh.fh_id from firehydrant fh,table(sdo_util.getvertices(fh.coords))f"+" where"
						+ " SDO_WITHIN_DISTANCE(fh.coords, "+t +",  'distance="+op2+"') = 'TRUE'";	
											
						    res = statq.executeQuery(select1);
						    System.out.println("Ids of firehydrants at distance="+op2+" from "+args[2]);
				            while(res.next())
				            {
				            	int x = res.getInt("X");
				                int y = res.getInt("Y");
				                
				                System.out.println(res.getString("fh_id"));
				                g.fillRect(x, y, 5, 5);
				            }
					break;
					case "building":
						String tmp="",tmp1;
						tmp1= "Select b1.x,b1.y, bldg.vertices_no from building bldg, "
								+ "table(MDSYS.sdo_util.getvertices(bldg.coords))b1"
								+ " where bldg.bldg_name = '"+ args[2].toUpperCase()+"'";
						
			            res = statq.executeQuery(tmp1);
			            res.next();
			            int count1 = res.getInt("vertices_no");
			            int[] tx1 = new int[count1];
		                int[] ty1 = new int[count1];
		                tmp =  "SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1), SDO_ORDINATE_ARRAY(";
		                
		                tx1[0]=res.getInt("X");
			            ty1[0]=res.getInt("Y");
			            tmp = tmp + tx1[0]+","+ ty1[0];
			                
				        for(int i=1;i<count1;i++)
				        {
				           	res.next();
				          	tx1[i]=res.getInt("X");
				           	ty1[i]=res.getInt("Y");
				           	tmp = tmp + "," +tx1[i]+","+ty1[i];
				        }   
				        tmp = tmp +","+tx1[0]+","+ty1[0]+"))";
				        
						String win= "SELECT bldg.BLDG_name,b.x,b.y, bldg.vertices_no, bldg.bldg_id from building bldg, "
								+ "table(MDSYS.sdo_util.getvertices(bldg.coords))b where" 
								+ " SDO_WITHIN_DISTANCE(bldg.coords, "+tmp +",  'distance="+op2+"') = 'TRUE'"
										+ " and bldg.bldg_name<>'"+args[2]+"'"
								+ " and bldg.bldg_name IN (Select bldg_name from building Minus Select f_name from firebuilding)"
							;	
						
						System.out.println(win);
			            res = statq.executeQuery(win);

					    System.out.println("Ids of buildings not on fire at distance="+op2+" from "+args[2]);
			           
			            while(res.next())
			            {

				            int count2 = res.getInt("vertices_no");
				            
			            	int[] x = new int[count2];
			                int[] y = new int[count2];
			                System.out.println(res.getString("bldg_id"));
			                x[0]=res.getInt("X");
			            	y[0]=res.getInt("Y");
			            	int i;     
				            for(i=1;i<count2;i++)
				            {
				            	res.next();
				              	x[i]=res.getInt("X");
				            	y[i]=res.getInt("Y");
				            }
				            g.drawPolygon(x, y,count2);
				            
			            }
			            break;
					}
					break;
///------------------------------------------------------------									
				case "nn":
					System.out.println("Enters nn");
					op3 = args[3];
					switch(args[1].toLowerCase())
					{
					case "firebuilding":
						String temp="",temp1;
						temp1= "Select b1.x,b1.y, bldg.vertices_no from building bldg, "
								+ "table(MDSYS.sdo_util.getvertices(bldg.coords))b1"
								+ " where bldg.bldg_id = '"+ args[2]+"'";
						//System.out.println(temp1);
			            res = statq.executeQuery(temp1);
			            
			            res.next();
			            int count = res.getInt("vertices_no");
			            int[] tx = new int[count];
		                int[] ty = new int[count];
		                temp =  "SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1), SDO_ORDINATE_ARRAY(";
		                
		                tx[0]=res.getInt("X");
			            ty[0]=res.getInt("Y");
			            temp = temp + tx[0]+","+ ty[0];
			                
				        for(int i=1;i<count;i++)
				        {
				           	res.next();
				          	tx[i]=res.getInt("X");
				           	ty[i]=res.getInt("Y");
				           	temp = temp + "," +tx[i]+","+ty[i];
				        }   
				        temp = temp +","+tx[0]+","+ty[0]+"))";
				        
				        //System.out.println(temp);

				        String win1= "SELECT bldg.BLDG_name,b.x,b.y, bldg.vertices_no, bldg.bldg_id from building bldg, "
				        		+ "table(MDSYS.sdo_util.getvertices(bldg.coords))b," 
								+ " FIREBUILDING f  where " 
				        		+" SDO_NN(bldg.coords, "+ temp+ ",  'sdo_num_res="+op3+"') = 'TRUE'"
				        		+ "and bldg.bldg_name = f.f_name and bldg.bldg_id<>'"+args[2]+"'";
								
						
						System.out.println(win1);
			            res = statq.executeQuery(win1);
			            System.out.println("ID’s of the "+ op3+" nearest firebuildings to "+args[2]);
			            while(res.next())
			            {
			            	int count1 = res.getInt("vertices_no");
			                int[] x = new int[count1];
			                int[] y = new int[count1];

			                x[0]=res.getInt("X");
			            	y[0]=res.getInt("Y");
			            	int i;     
				            for(i=1;i<count1;i++)
				            {
				            	res.next();
				              	x[i]=res.getInt("X");
				            	y[i]=res.getInt("Y");
				            }
				            g.drawPolygon(x, y,count1);
				            
			            }
						break;			
			case "firehydrant":
				String t,t1;
				t1= "Select b1.x,b1.y, bldg.vertices_no from building bldg, table(MDSYS.sdo_util.getvertices(bldg.coords))b1"
						+ " where bldg.bldg_id = '"+ args[2]+"'";
				
	            res = statq.executeQuery(t1);
	            res.next();
	            int cnt1 = res.getInt("vertices_no");
	            int[] xt1 = new int[cnt1];
                int[] yt1 = new int[cnt1];
                t =  "SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1), SDO_ORDINATE_ARRAY(";
                
                xt1[0]=res.getInt("X");
	            yt1[0]=res.getInt("Y");
	            t = t + xt1[0]+","+ yt1[0];
	                
		        for(int i=1;i<cnt1;i++)
		        {
		           	res.next();
		          	xt1[i]=res.getInt("X");
		           	yt1[i]=res.getInt("Y");
		           	t = t + "," +xt1[i]+","+yt1[i];
		        }   
		        t = t +","+xt1[0]+","+yt1[0]+"))";
		      	
				String select1 = "SELECT f.X,f.Y,fh.fh_id from firehydrant fh,table(sdo_util.getvertices(fh.coords))f"+" where"
						+ " SDO_NN(fh.coords, "+ t+ ",  'sdo_num_res="+op3+"') = 'TRUE'";	
				
							System.out.println(select1);				
						    res = statq.executeQuery(select1);
						    System.out.println("ID’s of the "+op3+" nearest firehydrants to "+args[2]);
						    
						    while(res.next())
				            {
				            	int x = res.getInt("X");
				                int y = res.getInt("Y");
				                System.out.println(res.getString("fh_id"));
				                g.fillRect(x, y, 5, 5);
				            }
					break;
					case "building":
						
						String tmp="",tmp1;
						tmp1= "Select b1.x,b1.y, bldg.vertices_no from building bldg, "
								+ "table(MDSYS.sdo_util.getvertices(bldg.coords))b1"
								+ " where bldg.bldg_id = '"+ args[2]+"'";
						
			            res = statq.executeQuery(tmp1);
			            res.next();
			            int count1 = res.getInt("vertices_no");
			            int[] tx1 = new int[count1];
		                int[] ty1 = new int[count1];
		                tmp =  "SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1), SDO_ORDINATE_ARRAY(";
		                
		                tx1[0]=res.getInt("X");
			            ty1[0]=res.getInt("Y");
			            tmp = tmp + tx1[0]+","+ ty1[0];
			                
				        for(int i=1;i<count1;i++)
				        {
				           	res.next();
				          	tx1[i]=res.getInt("X");
				           	ty1[i]=res.getInt("Y");
				           	tmp = tmp + "," +tx1[i]+","+ty1[i];
				        }   
				        tmp = tmp +","+tx1[0]+","+ty1[0]+"))";
				        
						String win= "SELECT bldg.BLDG_name,b.x,b.y, bldg.vertices_no, bldg.bldg_id from building bldg, "
								+ "table(MDSYS.sdo_util.getvertices(bldg.coords))b where" 
								+ " SDO_NN(bldg.coords, "+ tmp+ ",  'sdo_num_res="+(Integer.parseInt(args[3])+1)+"') = 'TRUE'"
								+ " and bldg.bldg_name IN (Select bldg_name from building Minus Select "
								+ "f_name from firebuilding) and bldg.bldg_id<>'"+args[2]+"'";	
						
						System.out.println(win);
			            res = statq.executeQuery(win);
			            System.out.println("ID’s of the "+op3+" nearest buildings to "+args[2]);
			            while(res.next())
			            {
				            int count2 = res.getInt("vertices_no");				            
			            	int[] x = new int[count2];
			                int[] y = new int[count2];
			                System.out.println(res.getString("bldg_id"));	
			                x[0]=res.getInt("X");
			            	y[0]=res.getInt("Y");
			            	int i;     
				            for(i=1;i<count2;i++)
				            {
				            	res.next();
				              	x[i]=res.getInt("X");
				            	y[i]=res.getInt("Y");
				            }
				            g.drawPolygon(x, y,count2);
				            
			            }
			            break;
					}

					
						break;
///--------------------------------------------------------------------------------						
				case "demo":
						demoqueries(args[1]);
						break;
				}
				
			}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
	}
	static void demoqueries(String no)
	{
		ResultSet res;
		g.setColor(Color.RED);
		Statement statq;
		try {
			statq = conn.createStatement();
		
		switch(no)
		{
		case "1":
			String q="select bldg.BLDG_name,b.x,b.y, bldg.vertices_no from building bldg, "
					+ "table(MDSYS.sdo_util.getvertices(bldg.coords))b"
			+" where bldg.bldg_name LIKE 'S%' and bldg.bldg_name IN "
			+ "(Select bldg_name from building Minus Select f_name from firebuilding)";
            res = statq.executeQuery(q);
			
            System.out.println(q);
            System.out.println("Names of buildings starting with S");
            while(res.next())
            {
	            int count = res.getInt("vertices_no");
	            
            	int[] x = new int[count];
                int[] y = new int[count];

                x[0]=res.getInt("X");
            	y[0]=res.getInt("Y");
            	
            	String name = res.getString("bldg_name");
            	System.out.println(name);
            	
            	int i;     
	            for(i=1;i<count;i++)
	            {
	            	res.next();
	              	x[i]=res.getInt("X");
	            	y[i]=res.getInt("Y");
	            }
	            g.drawPolygon(x, y,count);
	            
            }
			break;
		case "2":
			String t,t1, t2,q2;	
			
			q2 ="Select bldg.bldg_id, bldg.bldg_name from building bldg, firebuilding fb"
						+" where bldg.bldg_name = fb.f_name";
			res = statq.executeQuery(q2);

			while(res.next())
			{
				t2 = res.getString("bldg_id");
				System.out.print(t2+ ": ");
				String t3= res.getString("bldg_name");
				System.out.print(t3+" Five IDS: ");

				String select1 = "SELECT f.X,f.Y, fh.fh_id from firehydrant fh,table(sdo_util.getvertices(fh.coords))f"
					+", building b where"
					+ " SDO_NN(fh.coords,  b.coords,  'sdo_num_res=5') = 'TRUE' and b.bldg_id='"+t2.toLowerCase()+"'";	

				ResultSet res2;
				Statement stat2=conn.createStatement();
				res2 = stat2.executeQuery(select1);
			            while(res2.next())
			            {
			            	System.out.print(res2.getString("fh_id")+" 	");
			            	int x = res2.getInt("X");
			                int y = res2.getInt("Y");
			                g.fillRect(x, y, 5, 5);
			            }
			            System.out.println("");
				}
			break;
		case "3":
			int maxcnt=0;
			String maxid="";
			q = "SELECT F.FH_ID, count(B.bldg_id) FROM FIREHYDRANT F, BUILDING B WHERE SDO_WITHIN_DISTANCE( B.coords,F.coords,"
					+ " 'distance=120') = 'TRUE' group by FH_ID";
				ResultSet rs = statq.executeQuery(q);
				int c2=0;	
				while(rs.next())	
				{
			    String bid = rs.getString("FH_ID");
			    int cnt= rs.getInt("count(B.bldg_id)");
				if(cnt > maxcnt)
				{
					maxid = bid;
					maxcnt=cnt;
				}
				else if(cnt==maxcnt)
				{
					maxid = maxid +" "+bid;
				}
				else 	
				{continue;}
			    
			    }
				System.out.println(maxid);
				String[] id1 = maxid.split(" ");
				for(int i=0; i< id1.length; i++)
				{
					String select = "SELECT f.X,f.Y from firehydrant fh,table(sdo_util.getvertices(fh.coords))f"
							+ " where fh.fh_id='"+id1[i].toLowerCase()+"'";								
					ResultSet res1;
					Statement statx= conn.createStatement();
					res1 = statx.executeQuery(select);
					g.setColor(Color.RED);
			        
					while(res1.next())
					{		
							int x = res1.getInt("X");
				            int y = res1.getInt("Y");
					        g.fillRect(x, y, 5, 5);
					}
				}
			 
				rs.close();
	        break;
		case "4":
			q = "select fh_id, count(*) from (SELECT b.bldg_id ,fh.fh_id FROM FIREHYDRANT fh, building b  "
			 		+ "WHERE SDO_NN(fh.coords,b.coords,  'sdo_num_res=1') = 'TRUE' order by fh.fh_id) group BY fh_id "
			 		+ "order by count(*) desc ";
            res = statq.executeQuery(q);
            int i=1;
            System.out.println("No of Reverse Nearest Neighbour for top five firehydrants");
             while(res.next() && i<=5)
             {
                 String st = res.getString("fh_id");
                 int c = res.getInt("count(*)");
                 System.out.println(st + "     " + c  );
                 i++;
                 
             }
           res.close();    
            break;
		case "5":
            q="select min(t.x) as min_x, min(t.y) as min_y ,  max(t.x) as max_x, max(t.y) as max_y from "
            		+ "(SELECT SDO_GEOM.SDO_MBR(coords) as b_mbr FROM building where upper(bldg_name) like '%HE' ) g , "
            		+ "table(SDO_UTIL.GETVERTICES(g.b_mbr)) t";
            res = statq.executeQuery(q);
            while(res.next())
            {
                String x1 = res.getString("min_x");
                String y1 = res.getString("min_y");
                
                String x2 = res.getString("max_x");
                String y2 = res.getString("max_y");
                
                System.out.println("min("+x1+"," +y1 + ") max(" + x2 + ","+y2 +")" );
            }  
			break;
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	void connectDB() throws SQLException
	{
		DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
    //	System.out.print("registered.");
 		conn = DriverManager.getConnection( "jdbc:oracle:thin:system/oracle@localhost:1521/PDB1", "pmuser", "oracle");
    //	System.out.println("connected");
	}
	
	public class populateDB {

		public populateDB()
		{
			try 
			{
				connectDB();
				populate();
			} 
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		void connectDB() throws SQLException
		{
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
	    //	System.out.print("registered.");
	 		conn = DriverManager.getConnection( "jdbc:oracle:thin:system/oracle@localhost:1521/PDB1", "pmuser", "oracle");
	    //	System.out.println("connected");
		}
		
		public  void populate()
		{
			BufferedReader reader, reader1, reader2, reader3;
			 
			try {
				Statement stat = conn.createStatement();
				reader = new BufferedReader(new FileReader("building.xy"));
				String line = null;
				int i=0;		
				stat.executeUpdate("TRUNCATE TABLE building");
				while ((line = reader.readLine()) != null) {
					String tmp = "INSERT INTO BUILDING values('";
					String[] temp = line.split(", ");
					
					tmp = tmp + temp[0] +"','";
					tmp = tmp + temp[1] +"',";
					
					int count = Integer.parseInt(temp[2]);
	                tmp+=count+",SDO_Geometry (2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(";
	                for( i = 3; i < count*2; i++)
	                {	
	                	if(i==3)
	                		{tmp+=temp[i]+","+temp[i+1];
	                		i++;}
	                	else	
	                		{tmp+=","+temp[i]+","+temp[i+1];
	                		i++;}
	                }
	                tmp+=","+temp[count*2+1]+","+temp[count*2+2];
	                tmp+=")))";
	       //         System.out.println(tmp);
					stat.executeUpdate(tmp);	
				}
				
				
				
				
				
				Statement stat1 = conn.createStatement();
				stat1.executeUpdate("TRUNCATE TABLE firehydrant");
				reader1 = new BufferedReader(new FileReader("hydrant.xy"));
				String line1 = null;		
				while((line1=reader1.readLine()) != null)	//read each line
				{
					String tmp = "INSERT INTO FIREHYDRANT values('";
					String[] temp = line1.split(", ");
				
					tmp = tmp + temp[0] +"',SDO_Geometry (2001,null,sdo_point_type("+temp[1]+","+temp[2]+",null),null,null))";
					
					stat1.executeUpdate(tmp);
					//System.out.println(tmp);
					
				}			
				
				
				
				
				
				
				Statement stat2 = conn.createStatement();
				stat2.executeUpdate("TRUNCATE TABLE firebuilding");
				reader2 = new BufferedReader(new FileReader("firebuilding.txt"));
				String line2 = null;

				while((line2=reader2.readLine()) != null)
				{
					String tmp = "INSERT INTO firebuilding VALUES('"+line2.trim()+"')";
					stat2.executeUpdate(tmp);
				}
			}
			catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
}