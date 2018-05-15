import java.util.ArrayList;
import java.util.HashMap;

/**
 * symbol과 관련된 데이터와 연산을 소유한다.
 * section 별로 하나씩 인스턴스를 할당한다.
 * 참고 : 기존의 선언된 ArrayList 대신 HashMap을 사용.
 * ArrayList<String> symbolList
 * ArrayList<Integer> locationList
 */
public class SymbolTable {
	HashMap<String, Integer> symbolHashMap;
	ArrayList<String> externalDefine;
	ArrayList<String> externalReference;
	
	public SymbolTable() {
		symbolHashMap = new HashMap<String, Integer>();
		externalDefine = new ArrayList<String>();
		externalReference = new ArrayList<String>();
	}
	
	/**
	 * externalDefine에 해당 section의 EXTDEF를 저장한다.
	 * @param extdef : 집어넣고자 하는 EXTDEF
	 */
	public void putExternalDefine(String extdef) {
		externalDefine.add(extdef);
	}
	
	/**
	 * externalReference에 해당 section의 EXTREF를 저장한다.
	 * @param extref : 집어넣고자 하는 EXTREF
	 */
	public void putExternalReference(String extref) {
		externalReference.add(extref);
	}
	
	/**
	 * 해당 EXTDEF의 주소 값을 리턴한다.
	 * @param extdef : 주소를 찾고자 하는 EXTDEF
	 * @return address : 해당 EXTDEF의 주소
	 */
	public int getExternalDefineAddress(String extdef) {
		int address = -1;
		address = search(extdef);
		return address;
	}
	
	/**
	 * 해당 EXTREF의 주소 값을 리턴한다.
	 * @param extref : 주소를 찾고자 하는 EXTREF
	 * @return address : 해당 EXTREF의 주소
	 */
	public int getExternalReferenceAddress(String extref) {
		int address = -1;
		address = search(extref);
		return address;
	}
	
	/**
	 * 새로운 Symbol을 table에 추가한다.
	 * @param symbol : 새로 추가되는 symbol의 label
	 * @param location : 해당 symbol이 가지는 주소값
	 * <br><br>
	 * 주의 : 만약 중복된 symbol이 putSymbol을 통해서 입력된다면 이는 프로그램 코드에 문제가 있음을 나타낸다. 
	 * 매칭되는 주소값의 변경은 modifySymbol()을 통해서 이루어져야 한다.
	 */
	public void putSymbol(String symbol, int location) {
		if ( !symbol.isEmpty() && !symbolHashMap.containsKey(symbol) ) 
			symbolHashMap.put(symbol, location);
	}
	
	/**
	 * 기존에 존재하는 symbol 값에 대해서 가리키는 주소값을 변경한다.
	 * @param symbol : 변경을 원하는 symbol의 label
	 * @param newLocation : 새로 바꾸고자 하는 주소값
	 */
	public void modifySymbol(String symbol, int newLocation) {
		if ( symbolHashMap.containsKey(symbol) ) 
			symbolHashMap.put(symbol, newLocation);
	}
	
	/**
	 * 인자로 전달된 symbol이 어떤 주소를 지칭하는지 알려준다. 
	 * @param symbol : 검색을 원하는 symbol의 label
	 * @return symbol이 가지고 있는 주소값. 해당 symbol이 없을 경우 -1 리턴
	 */
	public int search(String symbol) {
		
		int address = -1;
		
		try {
			if ( symbolHashMap.containsKey(symbol) ) {
				address = symbolHashMap.get(symbol);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		return address;
	}
}
