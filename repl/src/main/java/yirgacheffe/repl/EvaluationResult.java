package yirgacheffe.repl;

public class EvaluationResult
{
	private String result;

	private boolean successful;

	public EvaluationResult(String result, boolean successful)
	{
		this.result = result;
		this.successful = successful;
	}

	public String getResult()
	{
		return this.result;
	}

	public boolean isSuccessful()
	{
		return this.successful;
	}
}
