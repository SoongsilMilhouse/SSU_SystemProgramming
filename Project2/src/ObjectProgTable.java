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
	 * object program의 header record를 작성한다.
	 * Control Section에서의 프로그램의 첫 주소는 0이기 때문에 "000000"으로 초기화한다.
	 * @param program name : 해당 section의 program name
	 * @param program length : 해당 section의 program length
	 */ 
	public void putHeader(String progName, int progLength) {
		header += progName + "\t" + "000000" + String.format("%06X", progLength);
	}
	
	/**
	 * object program의 external define record를 작성한다.
	 * @param extdef : 해당 section의 external define
	 * @param address : external define의 주소
	 */
	public void putExtdef(String extdef, int address) {
		this.extdef += extdef + String.format("%06X", address);
	}
	
	/**
	 * object program의 external reference record를 작성한다.
	 * @param extref : 해당 section의 external reference
	 */
	public void putExtref(String extref) {
		this.extref += extref;
	}
	
	/**
	 * text record 한 줄의 길이를 수정한다.
	 * "T000000--"에서 "--"부분을 프로그램 길이로 수정한다.
	 * @param length : 해당 text record의 길이
	 */
	public void modifyLength(String length) {
		text = text.replace("--", length).toUpperCase();
	}
	
	/**
	 * object program에 modification record를 작성한다.
	 * @param startAddr : 수정해야할 operand의 address
	 * @param length : 수정해야할 operand의 length
	 * @param name : 수정해야할 operand의 name
	 */
	public void putModification(int startAddr, String length, String name) {
		modification += String.format("%06X", startAddr) + length + "+" + name;
	}
	
	/**
	 * object program에 end record를 작성한다.
	 */
	public void putEnd() {
		end += "E";
	}
}