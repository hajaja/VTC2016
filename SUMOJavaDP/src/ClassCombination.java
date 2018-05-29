import java.util.LinkedList;


public class ClassCombination {
	LinkedList<ClassPath> listPaths = new LinkedList<ClassPath>();
	
	public ClassCombination(ClassPath pathA, ClassPath pathB) {
		listPaths.add(pathA);
		listPaths.add(pathB);
	}
	
	public LinkedList<ClassPath> paths() {
		return listPaths;
	}

}
