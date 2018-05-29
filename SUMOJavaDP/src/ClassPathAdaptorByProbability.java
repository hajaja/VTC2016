
public class ClassPathAdaptorByProbability implements Comparable<ClassPathAdaptorByProbability>{
	public ClassPath path;
	
	public ClassPathAdaptorByProbability(ClassPath path) {
		// TODO Auto-generated constructor stub
		this.path = path;
	}

	@Override
	public int compareTo(ClassPathAdaptorByProbability path) {
		// TODO Auto-generated method stub
		return (int) (this.path.delay - path.path.delay);
	}
	
}
