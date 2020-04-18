package yirgacheffe.compiler.function;

import java.util.Set;

public interface Interface
{
	Set<Function> getConstructors();

	Set<Function> getPublicConstructors();

	Set<Function> getMethods();

	Set<Function> getPublicMethods();
}
