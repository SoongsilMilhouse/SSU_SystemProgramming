import java.util.ArrayList;
import java.util.Iterator;

/**
 * literal과 관련된 데이터와 연산을 소유한다.
 * pass1에서 '='을 만날 때 literal을 literalList에 추가한다.
 * LTORG를 만나면 이전에 저장했던 literal의 주소를 수정한다.
 * section 마다 인스턴스가 하나씩 할당된다.
 */
public class LiteralTable {
	ArrayList<Literal> literalList;
	
	public LiteralTable() {
		literalList = new ArrayList<Literal>();
	}
	
	/**
	 * literal을 해당 section의 literalList에 저장한다.
	 * @param line : 저장할 literal
	 * @param locctr : 저장할 location counter
	 * @param section : 해당 section
	 */
	public void putLiteral(String line, int locctr, int section) {
		boolean isDuplicated = false;
		
		Iterator<Literal> itr = literalList.iterator();
		while ( itr.hasNext() ) {
			String element = itr.next().name;
			String[] array = line.split("\t");
			
			if ( array[2].equals(element) ) {
				isDuplicated = true;
				break;
			}
		}
		//if there is no same name, add literal
		if ( !isDuplicated )
			literalList.add(new Literal(line, locctr, section));
	}
	
	/**
	 * 해당 index의 literal을 리턴한다.
	 * @param index
	 * @return : 해당 index의 literal
	 */
	public Literal getLiteral(int index) {
		return literalList.get(index);
	}
	
	/**
	 * 해당 index의 literal address를 리턴한다.
	 * @param index
	 * @return 해당 index의 literal address
	 */
	public int getLiteralAddress(int index) {
		return literalList.get(index).address;
	}
}

class Literal {
	String name;
	String value;
	int length;
	int address;
	int section;
	
	public Literal(String line, int locctr, int section) {
		parsing(line, locctr, section);
	}
	
	/**
	 * literal의 실질적인 분석을 수행하는 함수. Literal의 각 변수에 분석한 결과를 저장한다.
	 * @param line : 저장할 literal
	 * @param locctr : 저장할 location counter
	 * @param section : 해당 section
	 */
	public void parsing(String line, int locctr, int section) {
		String[] array;
		String[] tmp;
		this.value = "";
		array = line.split("\t");
		
		this.name = array[2];
		tmp = array[2].split("'"); 
		
		if ( tmp[0].charAt(1) == 'C' ) {
			for (int i = 0; i < tmp[1].length(); i++) {
				this.value += decToHex((int)(tmp[1].charAt(i)));
			}
		} else if ( tmp[0].charAt(1) == 'X' ) {
			this.value += tmp[1];
		}
				
		this.length = value.length();
		this.address = locctr;
		this.section = section;
	}
	
	/**
	 * LTORG를 만난 경우 literal의 address를 수정한다.
	 * @param address : 수정할 location counter
	 */
	public void modifyAddress(int address) {
		this.address = address;
	}
	
	/**
	 * 10진수를 16진수로 변환하고 그 값을 리턴한다.
	 * @param value : 16진수로 바꾸고자 하는 10진수
	 * @return Integer.toHexString(value).toUpperCase() : 16진수로 변경된 값
	 */
	public static String decToHex(int value)
	 {
		return Integer.toHexString(value).toUpperCase();
	 }
}
