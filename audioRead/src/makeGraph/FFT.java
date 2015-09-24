package makeGraph;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FFT {
	 public static Double[] dft(Double v[]) {
		 Double r_data[] = null;
		 Double i_data[] = null;
		 
		 int N = v.length;
		 
		 Double twoPikOnN;
		 Double twoPijkOnN;
		 
		 N = (int)(Math.log(N) / Math.log(2));
		 
		 N = 1<<N;
		 Double twoPiOnN = 2 * Math.PI / N;

		 r_data = new Double [N];
		 i_data = new Double [N];
		 Double psd[] = new Double[N];
		 
		 for(int k=0; k<N; k++) {
			 twoPikOnN = twoPiOnN *k;
			 r_data[k] = 0.0;
			 i_data[k] = 0.0;
			 
			 for(int j = 0; j < N; j++) {
				 twoPijkOnN = twoPikOnN * j;
				 r_data[k] += v[j] * Math.cos( twoPijkOnN ); 
				 i_data[k] -= v[j] * Math.sin( twoPijkOnN );
			 }
			 r_data[k] /= N;
			 i_data[k] /= N;
			 psd[k] = r_data[k] * r_data[k] + i_data[k] * i_data[k];
		 }
		 return(psd);
	 }
	 
	 public static Double[] fft(Double v[]) {
		 double[] in = ArrayUtils.toPrimitive(v);
		 double[] tempConversion = new double[v.length];

		    FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		    try {           
		        Complex[] complx = transformer.transform(in, TransformType.FORWARD);

		        for (int i = 0; i < complx.length; i++) {               
		            double rr = (complx[i].getReal());
		            double ri = (complx[i].getImaginary());

		            tempConversion[i] = Math.sqrt((rr * rr) + (ri * ri));
		        }

		    } catch (IllegalArgumentException e) {
		        System.out.println(e);
		    }

		    return ArrayUtils.toObject(tempConversion);
	 }
}