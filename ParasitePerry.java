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
	public static final double MAX_AIR = 600;
	//images
	public Sprite bed = null;
	public Sprite background = null;
	public Sprite person_bed = null;
	public AnimatedSprite parasite = null;
	public Sprite breathing = null;
	public Button breathe_button = null;
	//states
	public int pixelSize = 2;
	public int parasiteState = 0;
	public boolean painting = false;
	public int currentAir = (int)(MAX_AIR);
	public boolean showBreath = false;
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
			person_bed = new Sprite(ImageIO.read(new File("images/person_bed.png")), 1, 2);
			parasite = new AnimatedSprite(ImageIO.read(new File("images/parasite.png")), 4);
			breathing = new Sprite(ImageIO.read(new File("images/breathing.png")), 1, 2);
			breathe_button = new Button(ImageIO.read(new File("images/breathe_button.png")), 350, 400);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	public void update() {
		parasite.update();
		if (breathe_button.isPressed())
			currentAir = Math.min(currentAir + 10, (int)(MAX_AIR));
		else
			currentAir -= 1;
		if (!showBreath && currentAir <= (int)(MAX_AIR) / 2)
			showBreath = true;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		background.draw(g, 0, 0);
		bed.draw(g, 26, 24);
		person_bed.draw(g, 16, 72);
		parasite.draw(g, 100, 100);
		breathing.draw(g, 0, 1, 500, 100);
		breathing.drawLeft(g, 0, 0, 500, 100, currentAir / MAX_AIR);
		if (showBreath)
			breathe_button.draw(g);
		painting = false;
	}
	public void mousePressed(MouseEvent evt) {
		requestFocus();
		if (showBreath)
			breathe_button.press(evt.getX(), evt.getY());
	}
	public void mouseReleased(MouseEvent evt) {
		breathe_button.release();
	}
	public void mouseClicked(MouseEvent evt) {}
	public void mouseEntered(MouseEvent evt) {}
	public void mouseExited(MouseEvent evt) {}
	public void keyTyped(KeyEvent evt) {}
	public void keyPressed(KeyEvent evt) {}
	public void keyReleased(KeyEvent evt) {}
	public class Sprite {
		private BufferedImage image;
		protected int spritew;
		protected int spriteh;
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
		public void drawLeft(Graphics g, int row, int col, int dx, int dy, double filled) {
			int sw = (int)(spritew * filled);
			if (sw > 0)
				g.drawImage(
					image.getSubimage(col * spritew, row * spriteh, sw, spriteh),
					dx, dy, sw * pixelSize, spriteh * pixelSize, null);
		}
		// public void drawBottom(Graphics g, int row, int col, int dx, int dy, double filled) {
			// int sy = (int)((1 - filled) * spriteh);
			// int sh = spriteh - sy;
			// g.drawImage(
				// image.getSubimage(col * spritew, row * spriteh + sy, spritew, sh),
				// dx, dy + sy * pixelSize, spritew * pixelSize, sh * pixelSize, null);
		// }
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
	public class Button extends Sprite {
		private int x;
		private int y;
		private boolean pressed = false;
		public Button(BufferedImage i, int x0, int y0) {
			super(i, 1, 2);
			x = x0;
			y = y0;
		}
		public void press(int mousex, int mousey) {
			pressed = mousex >= x && mousex < x + spritew * pixelSize && mousey >= y && mousey < y + spriteh * pixelSize;
		}
		public void release() {
			pressed = false;
		}
		public void draw(Graphics g) {
			draw(g, 0, pressed ? 1 : 0, x, y);
		}
		public boolean isPressed() {return pressed;}
	}
}
