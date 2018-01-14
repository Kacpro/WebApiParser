package pac1;

/**
 * Klasa do przechowywania trzech alaementów niekoniecznie tego samego typu
 *
 * @param <T1>
 * @param <T2> 
 * @param <T3>
 */
public class Triple<T1, T2, T3>
{
	private T1 first;
	private T2 second;
	private T3 third;
	
	/**
	 * Konstruktor, s³u¿y do wprowadzenia elementów
	 * 
	 * @param first
	 * @param second
	 * @param third
	 */
	public Triple(T1 first, T2 second, T3 third)
	{
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	/**
	 * Funkcja zwraca pierwszy element
	 * 
	 * @return
	 */
	public T1 first()
	{
		return first;
	}
	
	/**
	 * Funkcja zwraca drugi element
	 * 
	 * @return
	 */
	public T2 second()
	{
		return second;
	}
	
	/**
	 * Funkcja zwraca trzeci element
	 * 
	 * @return
	 */
	public T3 third()
	{
		return third;
	}
}
