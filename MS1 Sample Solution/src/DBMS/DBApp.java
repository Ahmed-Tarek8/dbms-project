package DBMS;

import java.util.ArrayList;

public class DBApp
{
	public static int dataPageSize = 2;

	public static ArrayList<String[]> validateRecords(String tableName){
	    Table t = FileManager.loadTable(tableName);

	    ArrayList<String[]> alr = t.getRecords();
	    ArrayList<String[]> missing = new ArrayList<String[]>();

	    int i=0;
	    while(i<alr.size()){
	        int pageNum = i / dataPageSize;

	        Page pg = FileManager.loadTablePage(tableName, pageNum);
	        if(pg == null){
	            missing.add(alr.get(i));
	        }
	        i++;
	    }

	    t.addTrace("Validating records: " + missing.size() + " records missing.");
	    FileManager.storeTable(tableName, t);

	    return missing;
	}

	public static void recoverRecords(String tableName,ArrayList<String[]> missing){
		Table t = FileManager.loadTable(tableName);
		ArrayList<Integer> rp= new ArrayList<Integer>();
		ArrayList<String[]> alr=t.getRecords();
		
		int i=0;
		int p=0;
		while(i<missing.size()){
			String[] rrr=missing.get(i);
			int j=0;
			while(j<alr.size()){
				if(java.util.Arrays.equals(alr.get(j), missing.get(i))){
					p=j/ dataPageSize;
					break;
				}
				j++;
			}
			Page pg = FileManager.loadTablePage(tableName,p);
			if(pg==null){
				pg=new Page();
			}
			pg.insert(missing.get(i));
			FileManager.storeTablePage(tableName,p,pg);
			if(rp.contains(p)==false){
				rp.add(p);
			}
			i++;
		}
		t.addTrace("Recovering " + missing.size() + " records in pages: " + rp + ".");
		FileManager.storeTable(tableName,t);
	}

	public static void createBitMapIndex(String tableName, String colName){
		BitmapIndex b = new BitmapIndex();
		Table t = FileManager.loadTable(tableName);
		String[] columnnames = t.returncolumnsnames();
		ArrayList<String[]> records = t.getRecords();

		int colIndex = 0;
		while (colIndex < columnnames.length) {
			if (columnnames[colIndex].equals(colName)) {
				break;
			}
			colIndex++;
		}

		if (colIndex == columnnames.length) {
			return;
		}

		ArrayList<String> distinctValues = new ArrayList<String>();
		int i = 0;
		while (i < records.size()) {
			String value = records.get(i)[colIndex];
			if (distinctValues.contains(value) == false) {
				distinctValues.add(value);
			}
			i++;
		}

		int j = 0;
		while (j < distinctValues.size()) {
			String distinctValue = distinctValues.get(j);
			StringBuilder bits = new StringBuilder();

			int k = 0;
			while (k < records.size()) {
				String recordValue = records.get(k)[colIndex];
				if (distinctValue.equals(recordValue)) {
					bits.append('1');
				} else {
					bits.append('0');
				}
				k++;
			}

			b.a.put(distinctValue, bits.toString());
			j++;
		}

		FileManager.storeTableIndex(tableName, colName, b);
		t.addIndexedColumn(colName);
		FileManager.storeTable(tableName, t);
	}

	public static String getValueBits(String tableName, String colName, String value){ 
	    BitmapIndex a = FileManager.loadTableIndex(tableName, colName);
	    int size = FileManager.loadTable(tableName).getRecords().size();

	    if(a == null){
	    	String zeros = "";
	    	for(int i = 0; i < size; i++){
	    		zeros += "0";
	    	}
	    	return zeros;
	    }

	    String res = a.a.get(value);

	    if(res == null){
	        String zeros = "";
	        for(int i = 0; i < size; i++){
	            zeros += "0";
	        }
	        return zeros;
	    }

	    if(res.length() > size){
	    	res = res.substring(0, size);
	    } else if(res.length() < size){
	    	StringBuilder sb = new StringBuilder(res);
	    	while(sb.length() < size){
	    		sb.append('0');
	    	}
	    	res = sb.toString();
	    }

	    return res; 
	}

	/** Matches MS2 trace asserts that use {@code Arrays.sort} then {@code Arrays.toString}. */
	private static String traceSortedBracketList(ArrayList<String> cols) {
		String[] arr = cols.toArray(new String[cols.size()]);
		java.util.Arrays.sort(arr);
		return java.util.Arrays.toString(arr);
	}

	/** Array overload to mirror tests that sort the original condition columns array. */
	private static String traceSortedBracketList(String[] cols) {
		String[] arr = java.util.Arrays.copyOf(cols, cols.length);
		java.util.Arrays.sort(arr);
		return java.util.Arrays.toString(arr);
	}

	public static ArrayList<String[]> selectIndex(String tableName, String[] cols, String[] vals) {
		long start = System.currentTimeMillis();
	    Table t = FileManager.loadTable(tableName);
		ArrayList<String> ics = new ArrayList<String>();
		ArrayList<String> a = new ArrayList<String>();

	    int i = 0;
	    while (i < cols.length) {
	        BitmapIndex idx = FileManager.loadTableIndex(tableName, cols[i]);
	        if (idx != null) {
	            String b = getValueBits(tableName, cols[i], vals[i]);
	            a.add(b);
	            ics.add(cols[i]);
	        }
	        i++;
	    }

	    String r = "";
	    if (a.size() > 0) {
	        r = a.get(0);
	        int j = 1;
	        while (j < a.size()) {
	            int k = 0;
	            String c = "";
	            while (k < r.length()) {
	                if (r.charAt(k) == '1' && a.get(j).charAt(k) == '1') {
	                    c = c + "1";
                } else {
	                    c = c + "0";
	                }
	                k++;
	            }
	            r = c;
	            j++;
	        }
	    }

	    ArrayList<String[]> rr = new ArrayList<String[]>();
	    ArrayList<String[]> all = FileManager.loadTable(tableName).getRecords();

	    if (a.size() > 0) {
	        int x = 0;
	        while (x < r.length()) {
	            if (r.charAt(x) == '1') {
	                rr.add(all.get(x));
	            }
	            x++;
	        }
	    } else {
	        rr = all;
	    }

	    ArrayList<String> nic = new ArrayList<String>();
	    ArrayList<String> niv = new ArrayList<String>();
	    int ii = 0;
	    while (ii < cols.length) {
	        BitmapIndex ab = FileManager.loadTableIndex(tableName, cols[ii]);
	        if (ab == null) {
	            nic.add(cols[ii]);
	            niv.add(vals[ii]);
	        }
	        ii++;
	    }

	    if (nic.size() == 0) {
	    	long end = System.currentTimeMillis();
	    	long time = end - start;
	    	String trace = "Select index on " + tableName + " Conditions: " + java.util.Arrays.toString(cols) + " Indexed columns: " + traceSortedBracketList(cols) + " Final count: " + rr.size() + " in " + time + " ms";
	    	t.addTrace(trace);
	    	FileManager.storeTable(tableName, t);
	        return rr;
	    }

	    String[] columnnames = t.returncolumnsnames();

	    ArrayList<String[]> fr = new ArrayList<String[]>();
	    int iii = 0;
	    while (iii < rr.size()) {
	        boolean tf = true;
	        String[] row = rr.get(iii);

	        int jj = 0;
	        while (jj < nic.size()) {
	            String coln = nic.get(jj);
	            String valn = niv.get(jj);

	            int po = 0;
	            while (po < columnnames.length) {
	                if (columnnames[po].equals(coln)) {
	                    break;
	                }
	                po++;
	            }

	            if (!row[po].equals(valn)) {
	                tf = false;
	                break;
	            }

	            jj++;
	        }

	        if (tf==true) {
	            fr.add(row);
	        }

	        iii++;
	    }

	    long end = System.currentTimeMillis();
	    long time = end - start;
	    String trace;
	    if (ics.size() == 0) {
	    	trace = "Select index on " + tableName + " Conditions: " + java.util.Arrays.toString(cols) + " Non Indexed: " + traceSortedBracketList(nic) + " Final count: " + fr.size() + " in " + time + " ms";
	    } else {
	    	trace = "Select index on " + tableName + " Conditions: " + java.util.Arrays.toString(cols) + " Indexed columns: " + traceSortedBracketList(ics) + " Non Indexed: " + traceSortedBracketList(nic) + " Final count: " + fr.size() + " in " + time + " ms";
	    }
	    t.addTrace(trace);
	    FileManager.storeTable(tableName, t);
	    return fr;
	}

	public static void createTable(String tableName, String[] columnsNames)
	{
		Table t = new Table(tableName, columnsNames);
		FileManager.storeTable(tableName, t);
	}

	public static void insert(String tableName, String[] record)
	{
	    Table t = FileManager.loadTable(tableName);
	    t.insert(record);
	    // Persist table metadata before rebuilding indices so newly created pages are visible.
	    FileManager.storeTable(tableName, t);

	    ArrayList<String> indexedCols = t.getIndexedColumns();

	    int i = 0;
	    while(i < indexedCols.size()){
	        String col = indexedCols.get(i);

	        createBitMapIndex(tableName, col);

	        i++;
	    }

	    FileManager.storeTable(tableName, t);
	}

	public static ArrayList<String []> select(String tableName)
	{
		Table t = FileManager.loadTable(tableName);
		ArrayList<String []> res = t.select();
		FileManager.storeTable(tableName, t);
		return res;
	}

	public static ArrayList<String []> select(String tableName, int pageNumber, int recordNumber)
	{
		Table t = FileManager.loadTable(tableName);
		ArrayList<String []> res = t.select(pageNumber, recordNumber);
		FileManager.storeTable(tableName, t);
		return res;
	}

	public static ArrayList<String []> select(String tableName, String[] cols, String[] vals)
	{
		Table t = FileManager.loadTable(tableName);
		ArrayList<String []> res = t.select(cols, vals);
		FileManager.storeTable(tableName, t);
		return res;
	}

	public static String getFullTrace(String tableName)
	{
		Table t = FileManager.loadTable(tableName);
		String res = t.getFullTrace();
		return res;
	}

	public static String getLastTrace(String tableName)
	{
		Table t = FileManager.loadTable(tableName);
		String res = t.getLastTrace();
		return res;
	}
}
