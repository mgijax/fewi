import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;


// This class just tests basic stuff
public class Main {

	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		HashMap<String, TestObject> map = new HashMap<String, TestObject>();
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("String 2");
		list.add("String 3");
		TestObject o = new TestObject();
		o.setList(list);
		map.put("String 1", o);

		list = new ArrayList<String>();
		list.add("String 4");
		list.add("String 5");
		o = new TestObject();
		o.setList(list);
		map.put("String 2", o);
		
		list = new ArrayList<String>();
		list.add("String 6");
		list.add("String 7");
		o = new TestObject();
		o.setList(list);
		map.put("String 3", o);
		
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			String json = mapper.writeValueAsString(map);
			System.out.println("JSON: " + json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public class TestObject {
		ArrayList<String> list = new ArrayList<String>();
		public ArrayList<String> getList() {
			return list;
		}
		public void setList(ArrayList<String> list) {
			this.list = list;
		}
	}

}
