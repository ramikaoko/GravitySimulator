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

	private static final String PAUSE_TEXT = "Pause";

	private static final String CONTINUE_TEXT = "Weiter";

	private static final String START_TEXT = "Start";

	private static final String STARTED_TEXT = "läuft";

	private static final double MAX_MASS = 10000000;

	private static final double MAX_DENSITY = 15;

	public Controller(Universe universe) {
		initializeButtonControl(this, universe);
	}

	private static int simulationTime = 1;
	private static int interval = 1;
	private static int particlesPerInterval = 1;

	public static int getSimulationTime() {
		return simulationTime;
	}

	public static int getInterval() {
		return interval;
	}

	public static int getParticlesPerInterval() {
		return particlesPerInterval;
	}

	/* The GUI for the control panel options will be set here */
	protected void initializeButtonControl(JPanel panel, Universe universe) {

		JButton button;
		JLabel label;
		JSeparator separator;

		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 5, 0);

		/*
		 * --- Simulationtime ---
		 */
		separator = new JSeparator();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(separator, gbc);

		label = new JLabel("Zeit [s]: ", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		panel.add(label, gbc);

		JSpinner spinnerTime = new JSpinner(new SpinnerNumberModel(simulationTime, 1, 600, 1));
		spinnerTime.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerTime.getValue();
				if (value instanceof Number)
					simulationTime = ((Number) value).intValue();
			}
		});
		gbc.gridy++;
		panel.add(spinnerTime, gbc);

		/*
		 * --- Intervalspinner ---
		 */
		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		label = new JLabel("Intervall [s]: ", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		panel.add(label, gbc);

		JSpinner spinnerInterval = new JSpinner(new SpinnerNumberModel(interval, 1, 100, 1));
		spinnerInterval.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerInterval.getValue();
				if (value instanceof Number)
					interval = ((Number) value).intValue();
			}
		});
		gbc.gridy++;
		panel.add(spinnerInterval, gbc);

		/*
		 * --- Partikelspinner ---
		 */
		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		label = new JLabel("Partikelanzahl: ", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		panel.add(label, gbc);

		JSpinner spinnerParticles = new JSpinner(new SpinnerNumberModel(particlesPerInterval, 1, 10, 1));
		spinnerParticles.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerParticles.getValue();
				if (value instanceof Number)
					particlesPerInterval = ((Number) value).intValue();
			}
		});
		gbc.gridy++;
		panel.add(spinnerParticles, gbc);

		/*
		 * --- Startbutton ---
		 */

		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		final JButton startButton = new JButton(START_TEXT);
		gbc.gridy++;
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				universe.startSimulation();
				startButton.setText(universe.isStartedFlag() ? STARTED_TEXT : START_TEXT);
			}
		});
		panel.add(startButton, gbc);

		/*
		 * --- Mass ---
		 */
		separator = new JSeparator();
		gbc.gridy++;
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
		 * --- Density ---
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
		 * --- Pause ---
		 */
		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		final JButton pauseButton = new JButton(PAUSE_TEXT);
		gbc.gridy++;
		pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				universe.setPauseFlag(!universe.isPausedFlag());
				pauseButton.setText(universe.isPausedFlag() ? CONTINUE_TEXT : PAUSE_TEXT);
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
		 * --- delete ---
		 */

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
