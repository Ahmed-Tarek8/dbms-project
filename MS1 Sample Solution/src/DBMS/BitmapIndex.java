package DBMS;

import java.util.TreeMap;
import java.io.Serializable;
public class BitmapIndex implements Serializable {
	TreeMap<String, String> a;
	public BitmapIndex(){
		a = new TreeMap<String, String>();
		}
}
