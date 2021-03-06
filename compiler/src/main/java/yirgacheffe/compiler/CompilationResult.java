package yirgacheffe.compiler;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.parallel.GeneratedClass;
import yirgacheffe.lang.Array;
import yirgacheffe.lang.Fold;

public class CompilationResult
{
	private String sourceFileName;

	private String classFileName;

	private byte[] bytecode;

	private Array<Error> errors;

	private Array<GeneratedClass> generatedClasses;

	public CompilationResult(
		String classFileName,
		byte[] bytecode,
		Array<GeneratedClass> generatedClasses)
	{
		this.classFileName = classFileName;
		this.bytecode = bytecode;
		this.generatedClasses = generatedClasses;
	}

	public CompilationResult(String sourceFileName, Array<Error> errors)
	{
		this.sourceFileName = sourceFileName;
		this.errors = errors;
	}

	public byte[] getBytecode()
	{
		return this.bytecode;
	}

	public Array<GeneratedClass> getGeneratedClasses()
	{
		return this.generatedClasses;
	}

	public boolean isSuccessful()
	{
		return this.errors == null;
	}

	public String getSourceFileName()
	{
		return this.sourceFileName;
	}

	public String getClassFileName()
	{
		return this.classFileName;
	}

	public String getErrors()
	{
		this.errors.sort();

		return new Fold<>(this.errors).with(new StringBuilder()).toString();
	}
}
