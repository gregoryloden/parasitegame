import Gstuff.stuff;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Insets;
public class ParasitePerry extends JPanel implements MouseListener, KeyListener {
	public int width = 800;
	public int height = 600;
	public int pixelsize = 4;
	public static void main(String[] args) {
		JFrame window = new JFrame("Parasite Perry");
		ParasitePerry thepanel = new ParasitePerry();
		window.setContentPane(thepanel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		Insets insets = window.getInsets();
		window.setSize(thepanel.width + insets.left + insets.right, thepanel.height + insets.top + insets.bottom);
		window.setLocation((stuff.screenwidth() - thepanel.width) / 2 - insets.left, (stuff.screenheight() - thepanel.height) / 2 - insets.top);
		thepanel.setFocusable(true);
		thepanel.requestFocus();
		window.toFront();
	}
	public ParasitePerry() {
		addKeyListener(this);
		addMouseListener(this);
		setBackground(Color.BLACK);
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
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