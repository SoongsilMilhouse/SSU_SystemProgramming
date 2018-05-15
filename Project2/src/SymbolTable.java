import java.util.ArrayList;
import java.util.HashMap;


/**
 * symbol�� ���õ� �����Ϳ� ������ �����Ѵ�.
 * section ���� �ϳ��� �ν��Ͻ��� �Ҵ��Ѵ�.
 */
public class SymbolTable {
	/*ArrayList<String> symbolList;
	ArrayList<Integer> locationList;*/
	
	HashMap<String, Integer> symbolHashMap;
	ArrayList<String> externalDefine;
	ArrayList<String> externalReference;
	
	public SymbolTable() {
		symbolHashMap = new HashMap<String, Integer>();
		externalDefine = new ArrayList<String>();
		externalReference = new ArrayList<String>();
	}
	
	public void putExternalDefine(String extdef) {
		externalDefine.add(extdef);
	}
	
	public void putExternalReference(String extref) {
		externalReference.add(extref);
	}
	
	public int getExternalDefineAddress(String extdef) {
		int address = -1;
		address = search(extdef);
		return address;
	}
	
	public int getExternalReferenceAddress(String extref) {
		int address = -1;
		address = search(extref);
		return address;
	}
	
	/**
	 * ���ο� Symbol�� table�� �߰��Ѵ�.
	 * @param symbol : ���� �߰��Ǵ� symbol�� label
	 * @param location : �ش� symbol�� ������ �ּҰ�
	 * <br><br>
	 * ���� : ���� �ߺ��� symbol�� putSymbol�� ���ؼ� �Էµȴٸ� �̴� ���α׷� �ڵ忡 ������ ������ ��Ÿ����. 
	 * ��Ī�Ǵ� �ּҰ��� ������ modifySymbol()�� ���ؼ� �̷������ �Ѵ�.
	 */
	public void putSymbol(String symbol, int location) {
		
		if ( !symbol.isEmpty() && !symbolHashMap.containsKey(symbol) ) {
			symbolHashMap.put(symbol, location);
		
		}
	}
	
	/**
	 * ������ �����ϴ� symbol ���� ���ؼ� ����Ű�� �ּҰ��� �����Ѵ�.
	 * @param symbol : ������ ���ϴ� symbol�� label
	 * @param newLocation : ���� �ٲٰ��� �ϴ� �ּҰ�
	 */
	public void modifySymbol(String symbol, int newLocation) {
		
		if ( symbolHashMap.containsKey(symbol) ) {
			symbolHashMap.put(symbol, newLocation);
		}
	}
	
	/**
	 * ���ڷ� ���޵� symbol�� � �ּҸ� ��Ī�ϴ��� �˷��ش�. 
	 * @param symbol : �˻��� ���ϴ� symbol�� label
	 * @return symbol�� ������ �ִ� �ּҰ�. �ش� symbol�� ���� ��� -1 ����
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
