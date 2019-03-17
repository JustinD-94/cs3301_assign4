import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.nio.Buffer;
import javax.imageio.*;

// Main class
public class HoughTransform extends Frame implements ActionListener {
	BufferedImage input;
	int width, height, diagonal;
	ImageCanvas source, target;
	TextField texRad, texThres;
	// Constructor
	public HoughTransform(String name) {
		super("Hough Transform");
		// load image
		try {
			input = ImageIO.read(new File(name));
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		width = input.getWidth();
		height = input.getHeight();
		diagonal = (int)Math.sqrt(width * width + height * height);
		// prepare the panel for two images.
		Panel main = new Panel();
		source = new ImageCanvas(input);
		target = new ImageCanvas(input);
		main.setLayout(new GridLayout(1, 2, 10, 10));
		main.add(source);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Line Transform");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Radius:"));
		texRad = new TextField("10", 3);
		controls.add(texRad);
		button = new Button("Circle Transform");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Threshold:"));
		texThres = new TextField("25", 3);
		controls.add(texThres);
		button = new Button("Search");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(diagonal*2+100, Math.max(height,360)+100);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

    public static double convolution(int[][] pixelMatrix) {

        int gy = (pixelMatrix[0][0] * -1) + (pixelMatrix[0][1] * -2) + (pixelMatrix[0][2] * -1) + (pixelMatrix[2][0]) + (pixelMatrix[2][1] * 2) + (pixelMatrix[2][2] * 1);
        int gx = (pixelMatrix[0][0]) + (pixelMatrix[0][2] * -1) + (pixelMatrix[1][0] * 2) + (pixelMatrix[1][2] * -2) + (pixelMatrix[2][0]) + (pixelMatrix[2][2] * -1);
        return Math.sqrt(Math.pow(gy, 2) + Math.pow(gx, 2));

    }

    // display the spectrum of the transform.
    public void DisplayTransform(int wid, int hgt, int[][] g) {
        target.resetBuffer(wid, hgt);
        for (int y = 0, i = 0; y < hgt; y++)
            for (int x = 0; x < wid; x++, i++) {
                int value = g[y][x] > 255 ? 255 : g[y][x];
                target.image.setRGB(x, y, new Color(value, value, value).getRGB());
            }
        target.repaint();
    }


    public static void main(String[] args) {
        new HoughTransform(args.length == 1 ? args[0] : "rectangle.png");
    }

  
  private int compute_p(int x, int y, double cosValue, double sinValue, int theta) {
      int p = (int) ((x - width / 2) * cosValue + (y - height / 2) * sinValue);
      p += diagonal / 2;
      if (p < 0 || p >= diagonal) {
          return -1; // -1 stands for "false" here.
      }
      return p;
  }

	// Action listener
	public void actionPerformed(ActionEvent e) {
		// perform one of the Hough transforms if the button is clicked.
		if ( ((Button)e.getSource()).getLabel().equals("Line Transform") ) {
      int MAXIMUM_THETA = 360; // Length of whole circle.
      double MOVEMENT_THETA = Math.PI / MAXIMUM_THETA; // Value we move each time we generate lines.
      int[][] g = new int[MAXIMUM_THETA][diagonal];
      double[] cosList = new double[MAXIMUM_THETA];
      double[] sinList = new double[MAXIMUM_THETA];


      for(int i = 0; i < MAXIMUM_THETA; i++) {
        cosList[i] = Math.cos(i * MOVEMENT_THETA);
        sinList[i] = Math.sin(i * MOVEMENT_THETA);
      }
      
      for (int x = 1; x < width - 1; x++) {
          for (int y = 1; y < height - 1; y++) {
              if ((source.image.getRGB(x, y) & 0x000000ff) == 0) {
                  for (int theta = 0; theta < MAXIMUM_THETA; theta++) {
                    int p = compute_p(x, y, cosList[theta], sinList[theta], theta);
                    // -1 stands for "false" here.
                    if (p == -1) {
                        continue;
                    }
                    g[theta][p]++;
                  }
              }
          }
      }
			DisplayTransform(diagonal, 360, g);
		}
		else if ( ((Button)e.getSource()).getLabel().equals("Circle Transform") ) {
			int[][] g = new int[height][width];
			int radius = Integer.parseInt(texRad.getText());
			// insert your implementation for circle here.
			DisplayTransform(width, height, g);
		}
	}
}
