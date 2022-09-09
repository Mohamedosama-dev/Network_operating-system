package synchronization;

import java.awt.Font;
import java.util.Random;
import java.util.Scanner;


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class Network {
	
	private static JFrame frame;
	private static JPanel panel;
	private static JLabel dName;
	private static JLabel[] connections;
	
	

	public static void main(String[] args) throws InterruptedException {
		// N is the number of connections
		//TC number of devices
		int N=0, TC;
		// array of strings to save the devices name and type
		String tc_lines[];
		
		Scanner sc = new Scanner(System.in);
		
		
		
	
		
		
		
		
		
		while(true) {
			try {
				System.out.println("What is the number of WI-FI Connections?");
				N = sc.nextInt();
				
				break;
				
			} catch (Exception e) {
				sc.nextLine();
				continue;
			}
		}
		
		connections = new JLabel[N];
		//semaphore object to control the assigning devices to certain connections  
		final Router router = new Router(N);
		
		//The GUI for the connections behaviour
		// k is used to assign a flexible width with the makimum of 4 connections in a row
		int k=0;
		if(N>4) {k=4;}else{k=N;};
		
		
		// Jframe object to display the app and to add component to 
		frame = new JFrame("Synchronization");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(220*k, 150*((N/4)+1)+10);

		//Jpanel object added the labels of every connection on it to display
		panel = new JPanel();		
		frame.add(panel);
		frame.setAlwaysOnTop (true);
		
		// j is used to change the y-axis position of the Connection labels to organise the display 
		int j=0;
		// k is used to change the x-axis position of the Connection labels to organise the display
		k=0;
		for(int i=0;i<N;i++) {

			//assign the label with the name and type and status
			dName = new JLabel("<html><body>Connection:"+(i+1)+"<br> Occupied: none <br>type: unKnown<br>Status: empty</body></html>");
			
			//check how many connections in a row and adjust
			if(i%4==0 && i!=0) {j++;k=0;}

			//set the boundaries of the label
			dName.setBounds(k*200+30, j*150+10, 300, 100);
			k++;
			dName.setFont(new Font("serif", Font.PLAIN, 18));
			panel.add(dName);
			
			// add the connection label to the array of connections
			connections[i]=dName;
			
		}
		panel.setLayout(null);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
		//////////
		while(true) {
			try {
				System.out.println("What is the number of devices Clients want to connect?");
				TC = sc.nextInt();
				
				break;
				
			} catch (Exception e) {
				sc.nextLine();
				continue;
			}
		}
		
		sc.nextLine();
		tc_lines = new String[TC];
		
		String name, type;
		int space;
		//get the devices name and type from the user
		for (int i = 0; i < TC; i++) {
			
			name=sc.nextLine();
			space=name.indexOf(" ");
			if(name.length()>0 && space!=(-1) && space!=0) {
				tc_lines[i]=name;	
			}else {
				System.out.println("please enter the device name and type seperated by a space ex:'c1 mobile'");
			i--;}
			

		}
		
		// Create the threads of devices and start them and try to connect to the router
		for (String a : tc_lines) {
			
			type = a.substring(a.indexOf(" ")+1);
			name = a.substring(0, a.indexOf(" "));
			Device d= new Device(name, type);
			Thread t1 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						// call semaphore sleep function to occupy a connection with the router
						router.connect(d, connections);
						int i= router.find(d);
								
						 frame.repaint();
						 int j=new Random().nextInt(30);
						Thread.sleep(50*j);
						 connections[i].setText("<html><body>Connection:"+(i+1)+"<br> Occupied: "+d.getName()+"<br> type: "+d.getType()+"<br>Status: doing activities</body></html>");
						frame.repaint();
						d.job(d,router.getCon());
						//signal that the object finished work and release the connection
						router.disconnect(d,connections);
						
						frame.repaint();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
			
		
		// Start the connection
		t1.start();
			
		}
		
	
		
		
		

	}

}




class Router{
	//array of devices to add the connected devices in
	private Device[] con;
	private Semaphore sem;
	 Router(int cn){
		  
		  
		  con = new Device[cn];
		  sem = new Semaphore(cn);
		  for(int i=0;i<cn;i++) {
			  con[i]=null;
		  }
	 }
	 
	 public Device[] getCon() {
		return con;
	}
	 
	 public void connect(Device d,JLabel[] connections) throws InterruptedException {
		 sem.sleep(d);
		 int i;		
		 
		 
		 
		 for(i=0;i<con.length;i++) {
			 // search for a free connection to add the new device on
			  if(con[i]==null) {
				  con[i]=d;
				  
				  System.out.println("Connection "+(i+1)+": "+d.getName()+" Occupied");
				  break;
			  }
			 
		  }
		 //update the connection label in the GUI
		 connections[i].setText("<html><body>Connection:"+(i+1)+"<br> Occupied: "+d.getName()+"<br> type: "+d.getType()+"<br>Status: Logged in</body></html>");
		 
		 
		 	// return the index of the channel of the connection
			
			
			//
			
			
			
			
			
		
	 }
	 
	 public void disconnect(Device d,JLabel[] connections) throws InterruptedException {
		 
		 int i;
		 for(i=0;i<con.length;i++) {
			 //check the connection to be released and set that element in array to null
			 if(con[i]==d) {
				 con[i]=null;
				 System.out.println("Connection "+(i+1)+": "+d.getName()+" Logged out");
				 break;
			 }
		 }
		 connections[i].setText("<html><body>Connection:"+(i+1)+"<br> Occupied: none <br>type: none<br>Status: empty</body></html>");
		 sem.signal(d);
		 
		 
	 }
	 public int find(Device d) {
		 int i;
		 for(i=0;i<con.length;i++) {
			 if(con[i]==d) {break;}
		 }
		 
		 return i;
	 }
	 

}
class Semaphore{
	// size of the connections to be made
	private int size;
	// router object to add the connections to
	
	
	Semaphore(int s){
		size = s;
		

	
	}
	
	public int getSize() {
		return size;
	}

	
	// signal the release of a connection to notify the waiting threads of the empty place
	public void signal(Device d) throws InterruptedException {
		
		
		synchronized (this) {
				size++;
				
				
				
				
				notify();	
				
		}
	}
	
	// assign the device to the empty connection or put it to wait untill an empty connection exist
	public void sleep(Device d) throws InterruptedException {		
		synchronized (this) {
			
			
			if(! (size>0) ) {
				System.out.println(d.getName()+" ("+d.getType()+") "+"arrived and waiting");
				wait();
			}
			else {
				System.out.println(d.getName()+" ("+d.getType()+") "+"arrived");
			}
			
			// 
			
			size--;	
			
			
				
			}
		
		
		
		}
	}
	
	



class Device{
	
	private String name, type;
	private int time;
	
	Device(String name, String type){
		this.name = name;
		this.type = type;
		// set a random time from 1 to 10 as the activity time for the device
		time = new  Random().nextInt(10)+1;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getTime() {
		return time;
	}
	// the activity function for the device
	 public void job(Device d, Device[] con) {
		 int i=-1;
		 // get the index of the connection the device is on
		 for (int j = 0; j < con.length; j++) {
			if(con[j]==d) {
				
				i=j+1;
				
				break;
			}
		}
		 
		 System.out.println("Connection "+i+": "+d.getName()+" login");
		 System.out.println("Connection "+i+": "+d.getName()+" performs online activity");
		 
		 // for loop of the activity time of the device for each loop the thread sleep for 600ms
		 try {
			Thread.sleep(600*d.getTime());
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	 
}
	
	
}




