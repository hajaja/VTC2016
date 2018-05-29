import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;

public class ClassStochasticityTimeGama extends ClassStochasticity{
	
	public ClassStochasticityTimeGama(double shape, double expectedTime){
		double scale = expectedTime / shape;
        GammaDistribution gammaDistribution = new GammaDistribution(shape, scale);
        for (int t = 0; t < PARAMS.timeMaximum; t++)
        {
        	pdf[t] = gammaDistribution.density(t);
        	cdf[t] = gammaDistribution.cumulativeProbability(t);
        }		
        this.delayExpected = expectedTime;
	}
}
