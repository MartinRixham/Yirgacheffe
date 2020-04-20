package yirgacheffe.compiler.function;

import yirgacheffe.lang.Array;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.Set;

public interface Interface
{
	Set<Function> getConstructors();

	Set<Function> getPublicConstructors();

	Set<Function> getMethods();

	Set<Function> getPublicMethods();

	Set<Field> getFields();

	boolean hasField(String name);

	Field getField(String name);

	Set<java.lang.reflect.Type> getGenericInterfaces();

	Array<TypeVariable<?>> getTypeParameters();

	boolean isInterface();

	boolean hasMethod(String value);

	boolean doesImplement(Class<?> other);

	boolean isImplementedBy(Class<?> reflectionClass);

	boolean hasDefaultConstructor();

	String getSimpleName();
}
