package ra.de.GravSim;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Controler extends JPanel {

	public Controler(Universe universe) {
		initializeButtonControl(this, universe);
	}

	protected void initializeMouseControl(JPanel panel, Universe universe) {
		panel.addMouseListener(new MouseAdapter() {

			/*
			 * the starting point of the mouse coursor, vector calculation
			 * starts here and a created particle will be shown at this position
			 */
			Point start;

			public void mousePressed(MouseEvent me) {
				start = me.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				super.mouseReleased(me);
				Point end = me.getPoint();
				Particle p = universe.createParticle(500000, (int) start.getX(), (int) start.getY());
				p.calculateVector(start, end);
			}
		});
	}

	/* controlpanel GUI options will be set here */
	protected void initializeButtonControl(JPanel panel, Universe universe) {
		JButton button;
		JLabel label;
		JTextField textField;
		JSeparator separator;
		JSpinner spinnerMass;
		JSpinner spinnerDensity;

		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		/*
		 * --- mass ---
		 */
		separator = new JSeparator();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(separator, gbc);

		label = new JLabel("Masse: ", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 0, 0);
		panel.add(label, gbc);

		spinnerMass = new JSpinner(new SpinnerNumberModel(10, 1, 1000000, 100));
		spinnerMass.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerMass.getValue();
				if (value instanceof Number)
					universe.setParticleMass(((Number) value).doubleValue());
			}
		});
		gbc.gridx = 0;
		gbc.gridy++;
		panel.add(spinnerMass, gbc);

		/*
		 * --- density ---
		 */
		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		label = new JLabel("Dichte: ", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 0, 0);
		panel.add(label, gbc);

		spinnerDensity = new JSpinner(new SpinnerNumberModel(10, 1, 1000000, 100));
		spinnerDensity.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerDensity.getValue();
				if (value instanceof Number)
					universe.setParticleMass(((Number) value).doubleValue());
			}
		});
		gbc.gridx = 0;
		gbc.gridy++;
		panel.add(spinnerDensity, gbc);

		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		/*
		 * --- Delete ---
		 */
		button = new JButton("löschen");
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.insets = new Insets(10, 0, 0, 0);
		panel.add(button, gbc);
	}
}
