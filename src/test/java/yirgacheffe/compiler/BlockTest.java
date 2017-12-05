package yirgacheffe.compiler;

import org.junit.Test;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BlockTest
{
	@Test
	public void testClassWithIntegerField()
	{
		String source = "int myField;";

		Block block = new Block(source);
		ClassWriter writer = new ClassWriter(0);

		block.compile(writer);

		ClassReader reader = new ClassReader(writer.toByteArray());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<FieldNode> fields = classNode.fields;

		assertEquals(1, fields.size());

		FieldNode firstField = fields.get(0);

		assertEquals("myField", firstField.name);
		assertEquals(Opcodes.ACC_PRIVATE, firstField.access);
	}

	@Test
	public void testClassWithStringField()
	{
		String source = "String myStringField;";

		Block block = new Block(source);
		ClassWriter writer = new ClassWriter(0);

		block.compile(writer);

		ClassReader reader = new ClassReader(writer.toByteArray());
		ClassNode classNode = new ClassNode();

		reader.accept(classNode, 0);

		List<FieldNode> fields = classNode.fields;

		assertEquals(1, fields.size());

		FieldNode firstField = fields.get(0);

		assertEquals("myStringField", firstField.name);
		assertEquals(Opcodes.ACC_PRIVATE, firstField.access);
	}
}
