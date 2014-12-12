import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JFrame;
public class ParasitePerry extends JPanel implements MouseListener, KeyListener {
	public static int screenwidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}
	public static int screenheight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}
	//constants
	public static final int FPS = 30;
	public int width = 1200;
	public int height = 450;
	public static final int MAX_AIR = 600;
	//images
	public Sprite bed = null;
	public Sprite background = null;
	public Sprite person_bed = null;
	public AnimatedSprite parasite = null;
	public Sprite lungs = null;
	public Button breathe_button = null;
	public BufferedImage skilltree = null;
	public Sprite autobreathe = null;
	public BufferedImage skillbg = null;
	//states
	public int parasiteState = 0;
	public boolean painting = false;
	public int currentAir = MAX_AIR;
	public boolean showBreath = false;
	public int scene = 0;
	public int sceneFrame = 0;
	public boolean showText = false;
	public int exp = 0;
	public boolean autobreathing = false;
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
			double spritePixelSize = 3.0;
			background = new Sprite(ImageIO.read(new File("images/background.png")), 1, 1, spritePixelSize);
			bed = new Sprite(ImageIO.read(new File("images/bed.png")), 1, 1, spritePixelSize);
			person_bed = new Sprite(ImageIO.read(new File("images/person_bed.png")), 1, 2, spritePixelSize);
			parasite = new AnimatedSprite(ImageIO.read(new File("images/parasite.png")), 4, spritePixelSize);
			lungs = new Sprite(ImageIO.read(new File("images/lungs.png")), 1, 2, spritePixelSize);
			breathe_button = new Button(ImageIO.read(new File("images/skillbuttonbreathefull.png")), 697, 285, 0.5);
			skilltree = ImageIO.read(new File("images/skilltreel1.png"));
			autobreathe = new Sprite(ImageIO.read(new File("images/skillbuttonautobreathe00.png")), 1, 1, 0.5);
			skillbg = ImageIO.read(new File("images/skillbg.png"));
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	public void update() {
		if (scene <= 4 || scene == 6) {
			if (sceneFrame < 44)
				sceneFrame += 1;
			else {
				sceneFrame = 0;
				scene += 1;
			}
		}
		parasite.update();
		if (scene > 4) {
			boolean pressedButton = breathe_button.isPressed();
			if (pressedButton || autobreathing) {
				if (pressedButton && currentAir < MAX_AIR) {
					exp += 1;
					if (scene == 5 && exp >= 100)
						scene = 6;
				}
				currentAir += (autobreathing ? 6 : 10);
				if (currentAir >= MAX_AIR) {
					if (currentAir > MAX_AIR)
						currentAir = MAX_AIR;
					if (autobreathing)
						autobreathing = false;
					else
						breathe_button.release();
				}
			} else {
				currentAir -= 1;
				if (exp >= 100 && !autobreathing && currentAir <= MAX_AIR / 2)
					autobreathing = true;
			}
			if (!showBreath && currentAir <= MAX_AIR / 2) {
				showBreath = true;
				showText = true;
			}
		}
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)(g);
		background.draw(g, 0, 0);
		bed.draw(g, 39, 36);
		if (scene == 1) {
			Composite ac = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sceneFrame / 45.0f));
			parasite.draw(g, 150, 156);
			g2.setComposite(ac);
		} else if (scene == 2) {
			parasite.draw(g, 150 - sceneFrame, 156 + sceneFrame);
		}
		person_bed.draw(g, 0, (breathe_button.isPressed() || autobreathing) ? 1 : 0, 24, 108);
		if (scene >= 4) {
			Composite ac = g2.getComposite();
			if (scene == 4)
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sceneFrame / 45.0f));
			lungs.draw(g, 0, 1, 531, 30);
			lungs.drawBottom(g, 0, 0, 531, 30, (double)(currentAir) / MAX_AIR);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Dialog", Font.BOLD, 16));
			g.drawString(String.valueOf(exp), 531, 80);
			g.drawImage(skillbg.getSubimage(370, 1164, 1200, 900), 600, 0, 600, 450, null);
			g.drawImage(skilltree.getSubimage(370, 1164, 1200, 900), 600, 0, 600, 450, null);
			g2.setComposite(ac);
		}
		if (showBreath)
			breathe_button.draw(g);
		if (showText)
			g.drawString("Click the button to breathe!", 815, 400);
		if (scene >= 6) {
			autobreathe.draw(g, 824, 179);
			if (scene == 6)
				g.drawString("Auto breathing unlocked!", 845, 270);
		}
		painting = false;
	}
	public void mousePressed(MouseEvent evt) {
		requestFocus();
		if (showBreath) {
			breathe_button.press(evt.getX(), evt.getY());
			if (breathe_button.isPressed())
				showText = false;
		}
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
		protected double pixelSize;
		public Sprite(BufferedImage i, int r, int c, double p) {
			image = i;
			spriteh = i.getHeight() / r;
			spritew = i.getWidth() / c;
			pixelSize = p;
		}
		public void draw(Graphics g, int row, int col, int dx, int dy) {
			g.drawImage(image.getSubimage(col * spritew, row * spriteh, spritew, spriteh),
				dx, dy, (int)(spritew * pixelSize), (int)(spriteh * pixelSize), null);
		}
		public void draw(Graphics g, int dx, int dy) {
			draw(g, 0, 0, dx, dy);
		}
		// public void drawLeft(Graphics g, int row, int col, int dx, int dy, double filled) {
			// int sw = (int)(spritew * filled);
			// if (sw > 0)
				// g.drawImage(image.getSubimage(col * spritew, row * spriteh, sw, spriteh),
					// dx, dy, sw * pixelSize, spriteh * pixelSize, null);
		// }
		public void drawBottom(Graphics g, int row, int col, int dx, int dy, double filled) {
			int sy = (int)((1 - filled) * spriteh);
			int sh = spriteh - sy;
			if (sh > 0)
				g.drawImage(image.getSubimage(col * spritew, row * spriteh + sy, spritew, sh),
					dx, dy + (int)(sy * pixelSize), (int)(spritew * pixelSize), (int)(sh * pixelSize), null);
		}
	}
	public class AnimatedSprite extends Sprite {
		private int frameState = 0;
		private int cols = 0;
		public AnimatedSprite(BufferedImage i, int c, double p) {
			super(i, 1, c, p);
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
		public Button(BufferedImage i, int x0, int y0, double p) {
			super(i, 1, 2, p);
			x = x0;
			y = y0;
		}
		public void press(int mousex, int mousey) {
			pressed = mousex >= x && mousex < x + (int)(spritew * pixelSize) && mousey >= y && mousey < y + (int)(spriteh * pixelSize);
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
