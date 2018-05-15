/**
 * object program�� ���� Ŭ����
 * putObject �޼ҵ带 ����Ͽ� ���ڷ� �Ѿ�� codeList�� object code�� �޾Ƽ� 
 * �ش� section�� objectProgList�� object�� ����(header, text, end)�� ä���. 
 */
public class ObjectProgTable {
	String header;
	String extdef;
	String extref;
	String text;
	String modification;
	String end;
	
	public ObjectProgTable(int startAddr) {
		header = "H";
		extdef = "D";
		extref = "R";
		text = "T";
		text += String.format("%06X", startAddr) + "--";
		modification = "";
		end = "";
	}
	
	/** 
	 * @param program name, progLengthList's end address
	 *  
	 */ 
	public void putHeader(String progName, int progLength) {
		header += progName + "\t" + "000000" + String.format("%06X", progLength);
	}
	
	public void putExtdef(String extdef, int address) {
		this.extdef += extdef + String.format("%06X", address);
	}
	
	public void putExtref(String extref) {
		this.extref += extref;
	}
	
	public void putText(String objectCode) {
		text += objectCode;
	}
	
	public void modifyLength(String length) {
		text = text.replace("--", length).toUpperCase();
	}
	
	public void putModification(int startAddr, String length, String name) {
		modification += String.format("%06X", startAddr) + length + "+" + name;
	}
	
	public void putEnd() {
		end += "E";
	}
}