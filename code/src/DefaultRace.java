import cicontest.algorithm.abstracts.AbstractRace;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.Human;

public class DefaultRace extends AbstractRace {

	public int[] runRace(DefaultDriverGenome[] drivers, boolean withGUI){
		int size = Math.min(10, drivers.length);
		DefaultDriver[] driversList = new DefaultDriver[size];
		for(int i=0; i<size; i++){
			driversList[i] = new DefaultDriver();
			driversList[i].loadGenome(drivers[i]);
		}
		return runRace(driversList, withGUI, true);
	}

}