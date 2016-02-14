package ra.de.GravSim;

import java.util.LinkedList;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class Universe{
	
	/** the list array which stores every particle in the universe */
	LinkedList particleList = new LinkedList();

	/** a defined particle*/
	Particle particle; 
	
	public void saveParticle (){
		for (int i = 0; i < particleList.size(); i++) {
			particle = new Particle();
			particleList.add(particle);
		}
	}
	
	// TODO: Math for Gravity and Distance
	
}
