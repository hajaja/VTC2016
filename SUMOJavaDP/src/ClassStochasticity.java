
public class ClassStochasticity implements InterfaceStochasticity {
    protected double[] pdf;
    protected double[] cdf;
    protected double delayExpected;

    protected double[] pdfSpeed;
    protected double[] cdfSpeed;
    protected double edgeLength;
    
    public ClassStochasticity() {
    	pdf = new double[PARAMS.timeMaximum];
    	cdf = new double[PARAMS.timeMaximum];
    	pdfSpeed = new double[PARAMS.speedMaximum];
    	cdfSpeed = new double[PARAMS.speedMaximum];
    	delayExpected = 0;
    	edgeLength = 0;
    }
    
	
	public ClassStochasticity(InterfaceStochasticity stochasticity) {
		// TODO Auto-generated constructor stub
		pdf = new double[PARAMS.timeMaximum];
		cdf = new double[PARAMS.timeMaximum];
		for (int i = 0; i < stochasticity.pdf().length; i++) {
			pdf[i] = stochasticity.pdf()[i];
			cdf[i] = stochasticity.cdf()[i];
		}
    	pdfSpeed = new double[PARAMS.speedMaximum];
    	cdfSpeed = new double[PARAMS.speedMaximum];
		for (int i = 0; i < stochasticity.pdfSpeed().length; i++) {
			pdfSpeed[i] = stochasticity.pdfSpeed()[i];
			cdfSpeed[i] = stochasticity.cdfSpeed()[i];
		}
		delayExpected = stochasticity.delayExpected();
		edgeLength = stochasticity.edgeLength();
	}

	@Override
	public double[] pdf() {
		// TODO Auto-generated method stub
		return pdf;
	}

	@Override
	public double[] cdf() {
		// TODO Auto-generated method stub
		return cdf;
	}

	@Override
	public double delayExpected() {
		// TODO Auto-generated method stub
		return delayExpected;
	}
	
	
	public void calculateTimeFromSpeed() {
		for (int v = 0; v < PARAMS.speedMaximum; v++) {
			double time = edgeLength / (v + 0.01) * PARAMS.factorMicro;
			int t = (int) time; 
			t = Math.min(t, PARAMS.timeMaximum - 1);
			t = Math.max(t, 1);
			pdf[t] += pdfSpeed[v];
		}
		
		cdf[0] = pdf[0];
		for (int t = 1; t < PARAMS.timeMaximum; t++) {
			cdf[t] = pdf[t] + cdf[t - 1]; 
		}
	}
	
	public double[] cdfSpeed() {
		return cdfSpeed;
	}
	
	public double[] pdfSpeed() {
		return pdfSpeed;
	}
	
	public double edgeLength() {
		return edgeLength;
	}
}
