package loguru.util;

public class MathUtil {
	public static int extreme(boolean min,int a, int b){
		if(min){
			return (a<b ? a : b);
		} else {
			return (b<a ? b : a);
		}
	}
}
