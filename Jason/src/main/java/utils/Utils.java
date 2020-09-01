package utils;

/**
 * Classe utils.
 * @author ronan
 *
 */
public class Utils {
	
	public static int getRealArrayLength(Object[] array) {
		int cpt = 0;
		for(int i = 0; i < array.length; i++) {
			if(array[i] != null)
				cpt ++;
		}
		return cpt;
	}

}
