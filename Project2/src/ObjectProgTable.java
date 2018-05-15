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
	 * object program�� header record�� �ۼ��Ѵ�.
	 * Control Section������ ���α׷��� ù �ּҴ� 0�̱� ������ "000000"���� �ʱ�ȭ�Ѵ�.
	 * @param program name : �ش� section�� program name
	 * @param program length : �ش� section�� program length
	 */ 
	public void putHeader(String progName, int progLength) {
		header += progName + "\t" + "000000" + String.format("%06X", progLength);
	}
	
	/**
	 * object program�� external define record�� �ۼ��Ѵ�.
	 * @param extdef : �ش� section�� external define
	 * @param address : external define�� �ּ�
	 */
	public void putExtdef(String extdef, int address) {
		this.extdef += extdef + String.format("%06X", address);
	}
	
	/**
	 * object program�� external reference record�� �ۼ��Ѵ�.
	 * @param extref : �ش� section�� external reference
	 */
	public void putExtref(String extref) {
		this.extref += extref;
	}
	
	/**
	 * text record �� ���� ���̸� �����Ѵ�.
	 * "T000000--"���� "--"�κ��� ���α׷� ���̷� �����Ѵ�.
	 * @param length : �ش� text record�� ����
	 */
	public void modifyLength(String length) {
		text = text.replace("--", length).toUpperCase();
	}
	
	/**
	 * object program�� modification record�� �ۼ��Ѵ�.
	 * @param startAddr : �����ؾ��� operand�� address
	 * @param length : �����ؾ��� operand�� length
	 * @param name : �����ؾ��� operand�� name
	 */
	public void putModification(int startAddr, String length, String name) {
		modification += String.format("%06X", startAddr) + length + "+" + name;
	}
	
	/**
	 * object program�� end record�� �ۼ��Ѵ�.
	 */
	public void putEnd() {
		end += "E";
	}
}