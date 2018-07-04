package yirgacheffe.compiler.listener;

import yirgacheffe.compiler.error.Error;
import yirgacheffe.compiler.type.Classes;
import yirgacheffe.parser.YirgacheffeParser;

public class PackageListener extends YirgacheffeListener
{
	public PackageListener(String sourceFile, Classes classes)
	{
		super(sourceFile, classes);
	}

	@Override
	public void enterCompilationUnit(YirgacheffeParser.CompilationUnitContext context)
	{
		int classCount =
			context.interfaceDeclaration().size() + context.classDeclaration().size();

		if (classCount > 1)
		{
			String message = "File contains multiple class declarations.";

			this.errors.push(new Error(1, 0, message));
		}
	}

	@Override
	public void enterPackageDeclaration(
		YirgacheffeParser.PackageDeclarationContext context)
	{
		if (context.packageName() == null && this.directory.length() > 0)
		{
			String message =
				"Missing package declaration for file path " + this.directory + ".";

			this.errors.push(new Error(context, message));
		}
		else if (context.packageName() != null)
		{
			this.packageName = context.packageName().getText();
			String packageLocation =
				this.packageName.replace('.', '/') + "/";

			if (!packageLocation.equals(this.directory))
			{
				String message =
					"Package name " + this.packageName +
						" does not correspond to the file path " + this.directory + ".";

				this.errors.push(new Error(context.packageName(), message));
			}
		}
	}
}
