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
	//constants
	public static final int FPS = 30;
	public int width = 800;
	public int height = 600;
	public static final double MAX_AIR = 900;
	//images
	public Sprite bed = null;
	public Sprite background = null;
	public Sprite personbed = null;
	public AnimatedSprite parasite = null;
	public Sprite breathing = null;
	//states
	public int pixelSize = 4;
	public int parasiteState = 0;
	public boolean painting = false;
	public int currentAir = (int)(MAX_AIR);
	public static void main(String[] args) {
		ParasitePerry thepanel = new ParasitePerry();
		JFrame window = new JFrame("Parasite Perry");
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
		try {
			background = new Sprite(ImageIO.read(new File("images/background.png")), 1, 1);
			bed = new Sprite(ImageIO.read(new File("images/bed.png")), 1, 1);
			personbed = new Sprite(ImageIO.read(new File("images/person_bed.png")), 1, 2);
			parasite = new AnimatedSprite(ImageIO.read(new File("images/parasite.png")), 4);
			breathing = new Sprite(ImageIO.read(new File("images/breathing.png")), 1, 2);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	public void update() {
		parasite.update();
		currentAir -= 1;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		background.draw(g, 0, 0);
		bed.draw(g, 52, 48);
		personbed.draw(g, 32, 144);
		parasite.draw(g, 200, 200);
		breathing.draw(g, 0, 1, 500, 100);
		breathing.drawBottom(g, 0, 0, 500, 100, currentAir / MAX_AIR);
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
	public class Sprite {
		private BufferedImage image;
		private int spritew;
		private int spriteh;
		public Sprite(BufferedImage i, int r, int c) {
			image = i;
			spriteh = i.getHeight() / r;
			spritew = i.getWidth() / c;
		}
		public void draw(Graphics g, int row, int col, int dx, int dy) {
			g.drawImage(
				image.getSubimage(col * spritew, row * spriteh, spritew, spriteh),
				dx, dy, spritew * pixelSize, spriteh * pixelSize, null);
		}
		public void draw(Graphics g, int dx, int dy) {
			draw(g, 0, 0, dx, dy);
		}
		public void drawBottom(Graphics g, int row, int col, int dx, int dy, double filled) {
			int sy = (int)((1 - filled) * spriteh);
			int sh = spriteh - sy;
			g.drawImage(
				image.getSubimage(col * spritew, row * spriteh + sy, spritew, sh),
				dx, dy + sy * pixelSize, spritew * pixelSize, sh * pixelSize, null);
		}
	}
	public class AnimatedSprite extends Sprite {
		private int frameState = 0;
		private int cols = 0;
		public AnimatedSprite(BufferedImage i, int c) {
			super(i, 1, c);
			cols = c;
		}
		public void update() {
			frameState = (frameState + 1) % cols;
		}
		public void draw(Graphics g, int dx, int dy) {
			draw(g, 0, frameState, dx, dy);
		}
	}
}