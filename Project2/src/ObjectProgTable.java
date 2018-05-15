/**
 * object program을 위한 클래스
 * putObject 메소드를 사용하여 인자로 넘어온 codeList의 object code를 받아서 
 * 해당 section의 objectProgList에 object에 내용(header, text, end)을 채운다. 
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