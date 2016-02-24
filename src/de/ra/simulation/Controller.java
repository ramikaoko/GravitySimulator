package de.ra.simulation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class Controller extends JPanel {

	private static String PAUSE_TEXT = "pausieren";

	private static String CONTINUE_TEXT = "fortsetzen";

	public Controller(Universe universe) {
		initializeButtonControl(this, universe);
	}

	/* The GUI for the controll panel options will be set here */
	protected void initializeButtonControl(JPanel panel, Universe universe) {
		JButton button;
		JLabel label;
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

		spinnerMass = new JSpinner(new SpinnerNumberModel(universe.getParticleMass(), 1, 10000000, 100));
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

		spinnerDensity = new JSpinner(new SpinnerNumberModel(universe.getParticleDensity(), 0.1, 15, 0.1));
		spinnerDensity.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerDensity.getValue();
				if (value instanceof Number)
					universe.setParticleDensity(((Number) value).doubleValue());
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
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				universe.clearParticles();

			}
		});
		panel.add(button, gbc);

		/*
		 * --- Pause ---
		 */
		final JButton pauseButton = new JButton(PAUSE_TEXT);
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.insets = new Insets(10, 0, 0, 0);
		pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				universe.setPauseFlag(!universe.isPauseFlag());
				pauseButton.setText(universe.isPauseFlag() ? CONTINUE_TEXT : PAUSE_TEXT);
			}
		});
		panel.add(pauseButton, gbc);

		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		button = new JButton("Auswertung");
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.insets = new Insets(10, 0, 0, 0);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		panel.add(button, gbc);

		/*
		 * --- Testbuttons ---
		 */

		button = new JButton("Alpha = 90°");
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.insets = new Insets(10, 0, 0, 0);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point startParticleOne = new Point(600, 500);
				Point startParticleTwo = new Point(200, 500);
				Point endParticleOne = new Point(200, 500);
				Point endParticleTwo = new Point(900, 500);

				Particle particleTestOne = universe.createParticle(startParticleOne.getX(), startParticleOne.getY());
				Particle particleTestTwo = universe.createParticle(startParticleTwo.getX(), startParticleTwo.getY());

				particleTestOne.calculateVector(startParticleOne, endParticleOne);
				particleTestTwo.calculateVector(startParticleTwo, endParticleTwo);
			}
		});
		panel.add(button, gbc);

		/* Alpha < 90° */
		button = new JButton("Alpha < 90°");
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.insets = new Insets(10, 0, 0, 0);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point startParticleOne = new Point(600, 500);
				Point startParticleTwo = new Point(200, 500);
				Point end = new Point(500, 800);

				Particle particleTestOne = universe.createParticle(startParticleOne.getX(), startParticleOne.getY());
				Particle particleTestTwo = universe.createParticle(startParticleTwo.getX(), startParticleTwo.getY());

				particleTestOne.calculateVector(startParticleOne, end);
				particleTestTwo.calculateVector(startParticleTwo, end);
			}
		});
		panel.add(button, gbc);

		/* Alpha > 90° */
		button = new JButton("Alpha > 90°");
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.insets = new Insets(10, 0, 0, 0);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point startParticleOne = new Point(600, 500);
				Point startParticleTwo = new Point(200, 500);
				Point end = new Point(500, 200);

				Particle particleTestOne = universe.createParticle(startParticleOne.getX(), startParticleOne.getY());
				particleTestOne.calculateVector(startParticleOne, end);
				Particle particleTestTwo = universe.createParticle(startParticleTwo.getX(), startParticleTwo.getY());
				particleTestTwo.calculateVector(startParticleTwo, end);

			}
		});
		panel.add(button, gbc);

	}
}
