package e214.skeleton;

import javax.swing.JFrame;

public class MagneticGuidesFrame {

	public static void main(String[] args) {
		final MagneticGuides guides = new MagneticGuides("Magnetic guides", 600, 600);
		for (int i = 0; i < 20; ++i) {
			guides.populate();
		}
		guides.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
