package loguru.api;

public class PassAllFilter extends Filter{

	@Override
	protected boolean passesFilter(String lineGroup) {
		return true;
	}

}
