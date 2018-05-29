
public interface InterfaceStochasticity {
	public double[] pdf();
	public double[] cdf();
	
	public double delayExpected();
	public double edgeLength();
	
	public double[] pdfSpeed();
	public double[] cdfSpeed();
}
