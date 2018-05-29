
public class ClassPathAdaptorByDelay implements Comparable<ClassPathAdaptorByDelay>{
	public ClassPath path;
	
	public ClassPathAdaptorByDelay(ClassPath path) {
		// TODO Auto-generated constructor stub
		this.path = path;
	}

	@Override
	public int compareTo(ClassPathAdaptorByDelay path) {
		// TODO Auto-generated method stub
		return (int) (this.path.delay - path.path.delay);
	}
	
}
