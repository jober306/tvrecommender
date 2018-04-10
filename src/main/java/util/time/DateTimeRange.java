package util.time;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.stream.Stream;

public class DateTimeRange implements Iterator<LocalDateTime>, Iterable<LocalDateTime>{
	
	final LocalDateTime startTime;
	final LocalDateTime endTime;
	final Duration iterationDuration;
	final boolean periodIsNegative;
	
	LocalDateTime current;
	
	public DateTimeRange(LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.iterationDuration = duration;
		this.periodIsNegative = iterationDuration.isNegative();
		this.current = startTime.minus(iterationDuration);
	}
	
	public Stream<LocalDateTime> stream(){
		Duration totalDuration = Duration.between(startTime, endTime);
		if(iterationDuration.getSeconds() == 0){
			return Stream.generate(this::next);
		}
		long numberOfIterations = totalDuration.getSeconds() / iterationDuration.getSeconds();
		if(numberOfIterations < 0) {
			return Stream.empty();
		}else {
			return Stream.generate(this::next).limit(numberOfIterations + 1);
		}
	}

	@Override
	public Iterator<LocalDateTime> iterator() {
		Iterator<LocalDateTime> localDateTimeIterator = new Iterator<LocalDateTime>() {

			@Override
			public boolean hasNext() {
				if(periodIsNegative) {
					return !endTime.isAfter(current.plus(iterationDuration));
				}else {
					return !endTime.isBefore(current.plus(iterationDuration));
				}
			}

			@Override
			public LocalDateTime next() {
				current = current.plus(iterationDuration);
				return current;
			}
			
		};
		return localDateTimeIterator;
	}

	public boolean hasNext(LocalDateTime current) {
		if(periodIsNegative) {
			return !endTime.isAfter(current);
		}else {
			return !endTime.isBefore(current);
		}
	}
	
	@Override
	public boolean hasNext() {
		if(periodIsNegative) {
			return !endTime.isAfter(current.plus(iterationDuration));
		}else {
			return !endTime.isBefore(current.plus(iterationDuration));
		}
	}

	@Override
	public LocalDateTime next() {
		current = current.plus(iterationDuration);
		return current;
	}
}
