package makeGraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FrmMakeGraph extends JFrame {
	private static final long serialVersionUID = 1L;

	double zoom = 1;
	int pos = 0;
	Boolean drawRaw = true;
	
	ArrayList<Double> dftValues = new ArrayList<Double>();
	ArrayList<Double> rawValues = new ArrayList<Double>();
	
	DrawPane dp;
	
	double factor = 0.25;
	double factorRaw = 2;
	
	public FrmMakeGraph() throws IOException {
		/*BufferedReader br = new BufferedReader(new FileReader("C:/Users/Joscha/facharbeit/data.txt"));
	    String line = null;
	    String tmpLine = "";
	    
	    while ((line = br.readLine()) != null) {
	    	tmpLine += line;
	    	
	    	String numbers[] = tmpLine.split(",");
	    	numbers[numbers.length - 1] = null;
	    	Double[] tmp = new Double[4096];
	    	
	    	for (int i = 0; i < 4096; i++){
	    		tmp[i] = Double.parseDouble(numbers[i]);
	    	}
	    	
	    	dftValues.addAll(Arrays.asList(FFT.dft(tmp)));
	    	
	    }
	    
	    br.close();*/
		
		FileInputStream fis = new FileInputStream("C:/Facharbeit/data.txt");
		
		int bn, bo = 0;
		Boolean c = false;
		while ((bn = fis.read()) != -1){
			if(c){
				rawValues.add((double) (((byte) bo << 8) | (byte) bn));
				c = false;
			}else{
				c = true;
				bo = bn;
			}
		}
		
		fis.close();
		dftValues.addAll(Arrays.asList(FFT.fft(rawValues.toArray(new Double[rawValues.size()]))));
		
		dp = new DrawPane(dftValues, rawValues);
		setContentPane(dp);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		setVisible(true);
		dp.repaint();
		
		addKeyListener(new KeyListener());
		setFocusable(true);
	}
	
	class DrawPane extends JPanel{
		private static final long serialVersionUID = 1L;
		private final int freqs = 32;
		private final int zDist = 5;
		private final int barSize = 2;
		
		ArrayList<Double> dfts;
		ArrayList<Double> raws;

		
		public DrawPane(ArrayList<Double> data, ArrayList<Double> draws){
			dfts = data;
			raws = draws;
		}
		
		public void drawBar3D(int x, int y, int z, int height, Graphics g){
			int x2d = x+z;
			int y2d = y+z;

			if(height > 200){
				g.setColor(new Color(0, 0, 0, 1));
			}else{
				g.setColor(new Color(0, 0, 0, 10));
			}
			
			g.drawLine(x2d, this.getHeight() - y2d, x2d, this.getHeight() - y2d - (int)(height * factor));
		}
		
		public void drawBar3DEx(int x, int y, int z, int height, int width, Graphics g){
			int x2d = x+z;
			int y2d = y+z;
			
			x2d *= width;
			
			if(height > 200){
				g.setColor(new Color(0, 0, 0, 10));
			}else{
				g.setColor(new Color(0, 0, 0, 50));
			}
			
			for(int i = 0; i <= width; i++){
				g.drawLine(x2d + i, this.getHeight() - y2d, x2d + i, this.getHeight() - y2d - (int)(height * factor));
			}
		}
		
		public void connectDots(int index, Graphics g){
			int x1 = index - pos - 1;
			int y1 = (int)((this.getHeight() / 2) + (raws.get(index - 1) * factorRaw) * -1);
			int x2 = index - pos;
			int y2 = (int)(((raws.get(index) * factorRaw) * -1) + this.getHeight() / 2);
			
			g.drawLine(x1, y1, x2, y2);
		}
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			
			this.setBackground(Color.white);
			if(drawRaw){
				for (int i = pos + 1; i <= this.getWidth() + pos; i++){
					g.setColor(Color.black);
					
					//g.drawLine(i - pos, (int)(this.getHeight() / 2), i - pos, (int)(((raws.get(i) * factorRaw) * -1) + this.getHeight() / 2));
					//g.drawLine(i - pos - 1, (int)((this.getHeight() / 2) + (raws.get(i - 1) * factorRaw) * -1), i - pos, (int)(((raws.get(i) * factorRaw) * -1) + this.getHeight() / 2));
					connectDots(i, g);
				}
			g.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);				
			}else{
				for (int i = 0; i < dfts.size(); i += freqs){
					for (int n = 0; n < freqs; n++){
						//g.drawLine(n + (i/512 * 10), this.getHeight() - (i/512 * 10), n + (i/512 * 10), (int)((dfts.get(i + n) * factor * -1) + this.getHeight()) - (i/512 * 10));
						drawBar3DEx(i / freqs, 0, n * zDist, dfts.get(i + n).intValue(), barSize, g);
					}
				}	
			}
		}
	}
	
	class KeyListener extends KeyAdapter{
		
		@Override
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode() == 37 && pos > 9){pos = pos - 10;}
			if(e.getKeyCode() == 39 && pos < rawValues.size() - 9){pos = pos + 10;}
			if(e.getKeyCode() == 70){drawRaw = !drawRaw;}
			if(e.getKeyCode() == 38){
				if(drawRaw){
					factorRaw *= 2;
				}else{
					factor *= 2;
				}
			}
			if(e.getKeyCode() == 40){
				if(drawRaw){
					factorRaw /= 2;
				}else{
					factor /= 2;
				}
			}
			
			dp.repaint();
		}
	}
}




