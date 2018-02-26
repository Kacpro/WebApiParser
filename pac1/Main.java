package pac1;

public class Main { 

	public static void main(String[] args)
	{
		GeneralAPI api = new NBPApi();
		api.parseAndInvoke(args, NBPApi.class);
	}
}

//A - currentGoldPrice
//B - currencyPrice <Kod> <Data>
//C - avgGoldPrice <Data> <Data>
//D - biggestAmplitude <Data>
//E - cheapestCurrency <Data>
//F - profitSort <Data> <Liczba>
//G - bestAndWorstDayToBuy <Kod>
//H - printGraph <Kod> <Data> <Data>

//test