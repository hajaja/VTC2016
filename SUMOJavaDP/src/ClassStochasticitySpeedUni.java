import org.apache.commons.math3.distribution.UniformRealDistribution;


public class ClassStochasticitySpeedUni extends ClassStochasticitySpeed{
	public ClassStochasticitySpeedUni(double expectedSpeed, double edgeLength){
		UniformRealDistribution gammaDistribution = new UniformRealDistribution(expectedSpeed * (1 - PARAMS.range), expectedSpeed * (1 + PARAMS.range));
		double totalProbability = 0;
        for (int v = 0; v < PARAMS.speedMaximum; v++) {
        	pdfSpeed[v] = gammaDistribution.density(v);
        	cdfSpeed[v] = gammaDistribution.cumulativeProbability(v);
        	totalProbability += pdfSpeed[v];
        }

        // normalize pdf
        for (int v = 0; v < PARAMS.speedMaximum; v++) {
        	pdfSpeed[v] = pdfSpeed[v] / totalProbability;
        }
        
        // generate cdf from pdf;
        cdfSpeed[0] = pdfSpeed[0];
        for (int v = 1; v < PARAMS.speedMaximum; v++) {
        	cdfSpeed[v] = cdfSpeed[v-1] + pdfSpeed[v];
        }
        
        this.edgeLength = edgeLength;
        this.delayExpected = edgeLength / expectedSpeed * PARAMS.factorMicro;
        calculateTimeFromSpeed();
	}
}
