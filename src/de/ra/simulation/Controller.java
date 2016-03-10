package de.ra.simulation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class Controller extends JPanel {

	private static final String PAUSE_TEXT = "Pause";

	private static final String CONTINUE_TEXT = "Weiter";

	private static final String START_TEXT = "Start";

	private static final String STARTED_TEXT = "Neustart";

	private static final double MIN_MASS = 100000;
	private static final double MAX_MASS = 10000000;

	private static final double MIN_DENSITY = 0.1;
	private static final double MAX_DENSITY = 15;

	private final Universe universe;

	public Controller(Universe universe) {
		this.universe = universe;
		initializeButtonControl(this);
	}

	/* The GUI for the control panel options will be set here */
	protected void initializeButtonControl(JPanel panel) {

		JButton button;
		JLabel label;
		JSeparator separator;

		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 5, 0);

		/*
		 * --- Simulation setup ---
		 */

		/* simulationtime */
		label = new JLabel("Simulationszeit [s]", (int) CENTER_ALIGNMENT);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(label, gbc);

		JSpinner spinnerTime = new JSpinner(new SpinnerNumberModel(universe.getSimulationTime(), 1, 600, 1));
		spinnerTime.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerTime.getValue();
				if (value instanceof Number)
					universe.setSimulationTime(((Number) value).longValue());
			}
		});
		gbc.gridy++;
		panel.add(spinnerTime, gbc);

		/* interval */
		label = new JLabel("Intervall [s]", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		panel.add(label, gbc);

		/*
		 * maxValue is always half as big as the simulationTime, so 10 is the
		 * default value
		 */
		JSpinner spinnerInterval = new JSpinner(
				new SpinnerNumberModel(universe.getInterval(), 1, universe.getSimulationTime() / 2, 1));
		spinnerInterval.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerInterval.getValue();
				if (value instanceof Number)
					universe.setInterval(((Number) value).intValue());
			}
		});
		gbc.gridy++;
		panel.add(spinnerInterval, gbc);

		/* particels per interval */
		label = new JLabel("Partikel / Intervall", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		panel.add(label, gbc);

		JSpinner spinnerParticles = new JSpinner(new SpinnerNumberModel(universe.getParticlesPerInterval(), 1, 5, 1));
		spinnerParticles.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Object value = spinnerParticles.getValue();
				if (value instanceof Number)
					universe.setParticlesPerInterval(((Number) value).intValue());
			}
		});
		gbc.gridy++;
		panel.add(spinnerParticles, gbc);

		/*
		 * --- Buttons ---
		 */

		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		/* startbutton */
		final JButton startButton = new JButton(START_TEXT);
		gbc.gridy++;
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (universe.isStartedFlag() == false) {
					universe.startSimulation();
					startButton.setText(universe.isStartedFlag() ? STARTED_TEXT : START_TEXT);
				} else {
					universe.stopSimulation();
					universe.startSimulation();
				}

			}
		});
		panel.add(startButton, gbc);

		/* pausebutton */
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

		/* resultbutton */
		button = new JButton("Auswertung");
		gbc.gridy++;
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				universe.stopSimulation();
			}
		});
		panel.add(button, gbc);

		/* deletebutton */
		button = new JButton("Löschen");
		gbc.gridy++;
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				universe.clearParticles();
			}
		});
		panel.add(button, gbc);

		/*
		 * --- Usercontrols for individually set particles ---
		 */

		/* masscontrol */
		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		label = new JLabel("Masse", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		panel.add(label, gbc);

		JSpinner spinnerMass = new JSpinner(
				new SpinnerNumberModel(universe.getParticleMass(), MIN_MASS, MAX_MASS, 100000));
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

		/* densitycontrol */
		label = new JLabel("Dichte", (int) CENTER_ALIGNMENT);
		gbc.gridy++;
		panel.add(label, gbc);

		JSpinner spinnerDensity = new JSpinner(
				new SpinnerNumberModel(universe.getParticleDensity(), MIN_DENSITY, MAX_DENSITY, 0.1));
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
		 * --- Counters to indicate time and particles ---
		 */

		separator = new JSeparator();
		gbc.gridy++;
		panel.add(separator, gbc);

		/* Label indicating the remaining simulation time */
		final JLabel coutdownLabel = new JLabel("00:00");
		universe.addObserver(new Observer() {

			String lastTime = "";

			public void update(Observable o, Object arg) {
				long remainingTime = universe.getRemainingTime();
				String timeString = "";
				long minutes = remainingTime / (60 * 1000);
				long seconds = remainingTime - (minutes * (60 * 1000));
				seconds /= 1000;
				timeString += (minutes < 10 ? ("0" + minutes) : (minutes + "")) + ":"
						+ (seconds < 10 ? ("0" + seconds) : (seconds + ""));
				if (!lastTime.equalsIgnoreCase(timeString)) {
					lastTime = timeString;
					coutdownLabel.setText(lastTime);
				}

				if (Universe.SIMULATION_FINISHED.equals(arg))
					showResults();
			}
		});
		coutdownLabel.setFont(getFont().deriveFont(14f));
		coutdownLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Restzeit"),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		coutdownLabel.setHorizontalTextPosition(JLabel.CENTER);
		coutdownLabel.setHorizontalAlignment(JLabel.CENTER);
		gbc.gridy++;
		panel.add(coutdownLabel, gbc);

		/*
		 * Label indicating the current amount of particles in the contentPane
		 */
		final JLabel particleCountLabel = new JLabel("0");
		universe.addObserver(new Observer() {

			String lastCount = "";

			@Override
			public void update(Observable o, Object arg) {
				int particleCount = universe.getParticleList().size();
				String countString = "";
				countString = particleCount + "";

				if (!lastCount.equalsIgnoreCase(countString)) {
					lastCount = countString;
					particleCountLabel.setText(lastCount);
				}
			}
		});
		particleCountLabel.setFont(getFont().deriveFont(14f));
		particleCountLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Partikel"),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		particleCountLabel.setHorizontalTextPosition(JLabel.CENTER);
		particleCountLabel.setHorizontalAlignment(JLabel.CENTER);
		gbc.gridy++;
		panel.add(particleCountLabel, gbc);
	}

	/*
	 * --- Resultdialog ---
	 */

	/* Method to create and show a dialog with data visualization */
	protected void showResults() {
		JDialog dialog = new ResultDialog(universe);
		dialog.setModal(true);
		dialog.setMinimumSize(new Dimension(800, 600));
		dialog.setLocationRelativeTo(SwingUtilities.getRootPane(this));
		dialog.setVisible(true);
	}
}
