import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Insets;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Toolkit;
public class ParasitePerry extends JPanel implements MouseListener, KeyListener {
	public static int screenwidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}
	public static int screenheight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}
	//images
	public static BufferedImage bed = null;
	public static BufferedImage background = null;
	public static BufferedImage personbed = null;
	public static BufferedImage parasite = null;
	//constants
	public static final int FPS = 30;
	public int width = 800;
	public int height = 600;
	//states
	public int pixelsize = 4;
	public int parasiteState = 0;
	public boolean painting = false;
	public static void main(String[] args) {
		try {
			background = ImageIO.read(new File("images/background.png"));
			bed = ImageIO.read(new File("images/bed.png"));
			personbed = ImageIO.read(new File("images/personbed.png"));
			parasite = ImageIO.read(new File("images/parasite.png"));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		JFrame window = new JFrame("Parasite Perry");
		ParasitePerry thepanel = new ParasitePerry();
		window.setContentPane(thepanel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		Insets insets = window.getInsets();
		window.setSize(thepanel.width + insets.left + insets.right, thepanel.height + insets.top + insets.bottom);
		window.setLocation((screenwidth() - thepanel.width) / 2 - insets.left, (screenheight() - thepanel.height) / 2 - insets.top);
		thepanel.setFocusable(true);
		thepanel.requestFocus();
		window.toFront();
		int next = 1;
		long now = System.currentTimeMillis();
		while (true) {
			while (System.currentTimeMillis() - now < next * 1000 / FPS) {}
			thepanel.update();
			//skip this frame if it's still painting
			if (!thepanel.painting) {
				thepanel.painting = true;
				thepanel.repaint();
			}
			next = next % FPS + 1;
			if (next == 1)
				now = System.currentTimeMillis();
		}
	}
	public ParasitePerry() {
		addKeyListener(this);
		addMouseListener(this);
		setBackground(Color.BLACK);
	}
	public void update() {
		parasiteState = (parasiteState + 1) % 4;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, 800, 600, null);
		g.drawImage(bed, 52, 48, 480, 480, null);
		g.drawImage(personbed.getSubimage(0, 0, 120, 120), 32, 144, 480, 480, null);
		g.drawImage(parasite.getSubimage(parasiteState * 5, 0, 5, 5), 200, 200, 20, 20, null);
		painting = false;
	}
	public void mousePressed(MouseEvent evt) {
		requestFocus();
	}
	public void mouseClicked(MouseEvent evt) {}
	public void mouseReleased(MouseEvent evt) {}
	public void mouseEntered(MouseEvent evt) {}
	public void mouseExited(MouseEvent evt) {}
	public void keyTyped(KeyEvent evt) {}
	public void keyPressed(KeyEvent evt) {}
	public void keyReleased(KeyEvent evt) {}
}