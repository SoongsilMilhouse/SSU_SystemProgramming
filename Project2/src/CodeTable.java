import java.util.ArrayList;

/**
 * ��� object code�� ���α׷� ���� �����ϴ� ���̺�
 * section ���� codeList�� �ϳ��� �Ҵ�ȴ�.
 */
public class CodeTable {
	ArrayList<String> codeList;
	
	public CodeTable() {
		codeList = new ArrayList<String>();
	}
	
	/**
	 * codeList�� �ش� section�� object code�� �����Ѵ�.
	 * @param : object code
	 */
	public void putOpjectCode(String objectCode) {
		codeList.add(objectCode);
	}
	
	/**
	 * �ش� �ε����� object code�� �����Ѵ�.
	 * @param : index
	 * @return : object code
	 */
	public String getObjectCode(int index) {
		return codeList.get(index);
	}
}
