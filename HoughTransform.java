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

	// Action listener
	public void actionPerformed(ActionEvent e) {
		// perform one of the Hough transforms if the button is clicked.
		if ( ((Button)e.getSource()).getLabel().equals("Line Transform") ) {
			int[][] g = new int[360][diagonal];
            int maxRadius = (int) Math.ceil(Math.hypot(width, height));
            BufferedImage outputImg = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);
            int[][] pixelMatrix = new int[3][3];
            try {
                for (int i = 1; i < input.getWidth() - 1; i++) {
                    for (int j = 1; j < input.getHeight() - 1; j++) {
                        pixelMatrix[0][0] = new Color(input.getRGB(i - 1, j - 1)).getRed();
                        pixelMatrix[0][1] = new Color(input.getRGB(i - 1, j)).getRed();
                        pixelMatrix[0][2] = new Color(input.getRGB(i - 1, j + 1)).getRed();
                        pixelMatrix[1][0] = new Color(input.getRGB(i, j - 1)).getRed();
                        pixelMatrix[1][2] = new Color(input.getRGB(i, j + 1)).getRed();
                        pixelMatrix[2][0] = new Color(input.getRGB(i + 1, j - 1)).getRed();
                        pixelMatrix[2][1] = new Color(input.getRGB(i + 1, j)).getRed();
                        pixelMatrix[2][2] = new Color(input.getRGB(i + 1, j + 1)).getRed();

                        int edge = (int) convolution(pixelMatrix);
                        outputImg.setRGB(i, j, (edge << 16 | edge << 8 | edge));
                    }
                }

                // New image with sobel filter applied for edge detection will be written to "test1" image file
                File outputfile = new File("test1.jpg");
                ImageIO.write(outputImg, "jpg", outputfile);
            } catch (IOException ex) {
                System.err.println("Image width:height=" + input.getWidth() + ":" + input.getHeight());
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
