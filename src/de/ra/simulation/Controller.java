package de.ra.simulation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

	private static final String PAUSE_TEXT = "Pause";

	private static final String CONTINUE_TEXT = "Weiter";

	private static final double MAX_MASS = 10000000;

	private static final double MAX_DENSITY = 15;

	public Controller(Universe universe) {
		initializeButtonControl(this, universe);
	}

	/* The GUI for the controll panel options will be set here */
	protected void initializeButtonControl(JPanel panel, Universe universe) {
		JButton button;
		JLabel label;
		JSeparator separator;

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
		panel.add(label, gbc);

		JSpinner spinnerMass = new JSpinner(new SpinnerNumberModel(universe.getParticleMass(), 1, MAX_MASS, 100));
		spinnerMass.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerMass.getValue();
				if (value instanceof Number)
					universe.setParticleMass(((Number) value).doubleValue());
			}
		});
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
		panel.add(label, gbc);

		JSpinner spinnerDensity = new JSpinner(
				new SpinnerNumberModel(universe.getParticleDensity(), 0.1, MAX_DENSITY, 0.1));
		spinnerDensity.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerDensity.getValue();
				if (value instanceof Number)
					universe.setParticleDensity(((Number) value).doubleValue());
			}
		});
		gbc.gridy++;
		panel.add(spinnerDensity, gbc);

		/*
		 * --- velocity ---
		 */
		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		JLabel velocityLabel = new JLabel("Zufallsgeschwindigkeit: ", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		panel.add(velocityLabel, gbc);

		/* JSpinner und label für untere Grenze */
		label = new JLabel("untere Grenze ", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		panel.add(label, gbc);

		JSpinner spinnerBottomVelocity = new JSpinner(new SpinnerNumberModel(
				universe.getParticleBottomVelocityMultiplier(), universe.getParticleBottomVelocityMultiplier(),
				universe.getParticleTopVelocityMultiplier(), 0.1));
		spinnerBottomVelocity.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerBottomVelocity.getValue();
				if (value instanceof Number)
					universe.setParticleDensity(((Number) value).doubleValue());
			}
		});
		gbc.gridy++;
		panel.add(spinnerBottomVelocity, gbc);

		/* JSpinner und label für obere Grenze */
		label = new JLabel("obere Grenze", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		panel.add(label, gbc);

		JSpinner spinnerTopVelocity = new JSpinner(new SpinnerNumberModel(universe.getParticleTopVelocityMultiplier(),
				universe.getParticleBottomVelocityMultiplier(), universe.getParticleTopVelocityMultiplier(), 0.1));
		spinnerTopVelocity.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerTopVelocity.getValue();
				if (value instanceof Number)
					universe.setParticleDensity(((Number) value).doubleValue());
			}
		});
		gbc.gridy++;
		panel.add(spinnerTopVelocity, gbc);

		final JButton velocityButton = new JButton("Berechnung");
		gbc.gridy++;
		gbc.anchor = GridBagConstraints.PAGE_END;
		velocityButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		panel.add(velocityButton, gbc);

		/*
		 * --- delete ---
		 */
		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		button = new JButton("löschen");
		gbc.gridy++;
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
		pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				universe.setPauseFlag(!universe.isPauseFlag());
				pauseButton.setText(universe.isPauseFlag() ? CONTINUE_TEXT : PAUSE_TEXT);
			}
		});
		panel.add(pauseButton, gbc);

		button = new JButton("Auswertung");
		gbc.gridy++;
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		panel.add(button, gbc);

		/*
		 * --- Testbuttons ---
		 */
		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		button = new JButton("Alpha = 90°");
		gbc.gridy++;
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
