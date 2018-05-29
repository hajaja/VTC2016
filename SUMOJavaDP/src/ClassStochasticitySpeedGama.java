import org.apache.commons.math3.distribution.GammaDistribution;


public class ClassStochasticitySpeedGama extends ClassStochasticitySpeed{
	public ClassStochasticitySpeedGama(double shape, double expectedSpeed, double edgeLength){
		double scale = expectedSpeed / shape;
        GammaDistribution gammaDistribution = new GammaDistribution(shape, scale);
        for (int v = 0; v < PARAMS.speedMaximum; v++)
        {
        	pdfSpeed[v] = gammaDistribution.density(v);
        	cdfSpeed[v] = gammaDistribution.cumulativeProbability(v);
        }		
        this.edgeLength = edgeLength;
        this.delayExpected = edgeLength / expectedSpeed * PARAMS.factorMicro;
        calculateTimeFromSpeed();
        
	}
}
