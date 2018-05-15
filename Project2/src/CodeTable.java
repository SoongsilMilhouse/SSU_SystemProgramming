import java.util.ArrayList;

public class CodeTable {
	ArrayList<String> codeList;
	
	public CodeTable() {
		codeList = new ArrayList<String>();
	}
	
	public void putOpjectCode(String objectCode) {
		codeList.add(objectCode);
	}
	
	public String getObjectCode(int index) {
		return codeList.get(index);
	}
}
