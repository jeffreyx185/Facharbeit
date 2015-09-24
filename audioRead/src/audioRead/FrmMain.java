package audioRead;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.swing.*;

public class FrmMain extends JFrame {
	private static final long serialVersionUID = 1L;

	private final int sampleRate = 200000;
	private final int bufferSize = 8192;
	
	TargetDataLine input;
	AudioFormat currFormat = new AudioFormat(Encoding.PCM_SIGNED, sampleRate, 16, 1, 2, sampleRate, true);
	DataLine.Info info = new DataLine.Info(TargetDataLine.class, currFormat);
	
	FileOutputStream output;
	
	ArrayList<Integer> dataInt = new ArrayList<Integer>();
	
	ByteArrayOutputStream out  = new ByteArrayOutputStream();
	int numBytesRead;
	byte[] data;
	
	boolean aviable = false;
	boolean running = true;
	
	public FrmMain() throws LineUnavailableException{
		//DrawPane dp = new DrawPane(dataInt);
		//setContentPane(dp);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		setVisible(true);
		
		try {
			output = new FileOutputStream("C:/Users/Joscha/facharbeit/data.txt");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		this.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	            running = false;
	        }
	    });
		
   		if (AudioSystem.isLineSupported(info)) {
   		    input = (TargetDataLine) AudioSystem.getLine(info);
   		    input.open(currFormat);
   		    aviable = true;
   		    data = new byte[bufferSize];
   		    new ListenThread(input, data, dataInt).run();
   		    System.out.println("Starting Thread");
   		}	
	}
	
	class ListenThread extends Thread{
		TargetDataLine in;
		byte[] d;
		ArrayList<Integer> dti;
		//DrawPane dp;
		
		public ListenThread(TargetDataLine tda, byte[] d, ArrayList<Integer> dti){
			in = tda;
			this.d = d;
			this.dti = dti;
			//this.dp = dp;
		}
		
		public void run(){
			in.start();
			
			while (running){
				numBytesRead = in.read(d, 0, d.length);
				for (int i = 0; i <= d.length - 1; i++){
					try {
						output.write(d[i]);
					} catch (IOException e) {}
				}

				try {
					output.flush();
				} catch (IOException e) {}
			}
		}
	}
	
	class DrawPane extends JPanel{
		private static final long serialVersionUID = 1L;
		
		final double factor = 0.1;
		
		ArrayList<Integer> dti;
		
		public DrawPane(ArrayList<Integer> dti){
			this.dti = dti;
		}
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			
			for (int i = 0; i <= dti.size() - 1; i++){
				g.setColor(Color.BLACK);
				g.drawLine(i, (int)(this.getHeight() / 2), i, (int)(((dti.get(i) * factor) * -1) + this.getHeight() / 2));
			}
		}
	}
}