import java.util.concurrent.atomic.AtomicInteger;

/**
 * A counter-barrier using AtomicInteger
 * @author David
 *
 */
public class CounterBarrier {

	private int cap;
	private AtomicInteger counter;
	CounterBarrier(int cap)
	{
		this.cap = cap;
		counter = new AtomicInteger(0);
	}
	public void increment()
	{
		counter.incrementAndGet();
	}
	public boolean isDone()
	{
		if (counter.get() < cap)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}
