import javax.swing.*;

import model.ERModel;
import view.*;

import java.awt.*;

public class EREditor {

	public static void main(String[] args) {
		/** Model. */
		model.ERModel model = new ERModel();

		/** View/Controller. */
		FunctionBar functionBar = new FunctionBar(model);

		/** Layout. */
		JFrame frame = new JFrame("EREditor");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setContentPane(functionBar);

		frame.setSize(960, 1040);  // Half Screen for testing.
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);  // Start with full screen.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
