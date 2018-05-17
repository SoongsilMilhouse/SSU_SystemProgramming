import java.util.ArrayList;

/**
 * 모든 object code를 프로그램 별로 관리하는 테이블
 * section 마다 codeList가 하나씩 할당된다.
 */
public class CodeTable {
	ArrayList<String> codeList;
	
	public CodeTable() {
		codeList = new ArrayList<String>();
	}
	
	/**
	 * codeList에 해당 section의 object code를 저장한다.
	 * @param : object code
	 */
	public void putOpjectCode(String objectCode) {
		codeList.add(objectCode);
	}
	
	/**
	 * 해당 인덱스의 object code를 리턴한다.
	 * @param : index
	 * @return : object code
	 */
	public String getObjectCode(int index) {
		return codeList.get(index);
	}
}
