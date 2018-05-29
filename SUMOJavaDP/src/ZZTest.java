import java.util.HashSet;
import java.util.Set;


public class ZZTest {
	public void replaceSharp() {
		String name = "123456#1";
		System.out.println(name);
		name = name.replace('#', 's');
		System.out.println(name);
	}
	
	public void setToString() {
		Set<String> set = new HashSet<String>();
		set.add("hello");
		set.add("everybody");
		System.out.print(set);
		System.out.print('.');

	}
	
	public static void main(String[] args) {
		ZZTest test = new ZZTest();
		test.replaceSharp();
		test.setToString();
	}
}
