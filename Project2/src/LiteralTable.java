import java.util.ArrayList;
import java.util.Iterator;

public class LiteralTable {
	ArrayList<Literal> literalList;
	
	public LiteralTable() {
		literalList = new ArrayList<Literal>();
	}
	
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
	
	public Literal getLiteral(int index) {
		return literalList.get(index);
	}
	
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
	
	public void parsing(String line, int locctr, int section) {
		String[] array;
		String[] tmp;
		this.value = "";
		array = line.split("\t");
		
		this.name = array[2];
		tmp = array[2].split("'"); // tmp[1]�� "EOF" �������
		
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
	
	public void modifyAddress(int address) {
		this.address = address;
	}
	
	/**
	 * 10������ 16������ ��ȯ�ϰ� �� ���� �����Ѵ�.
	 * @param value : 16������ �ٲٰ��� �ϴ� 10����
	 * @return Integer.toHexString(value).toUpperCase() : 16������ ����� ��
	 */
	public static String decToHex(int value)
	 {
		return Integer.toHexString(value).toUpperCase();
	 }
	
}
