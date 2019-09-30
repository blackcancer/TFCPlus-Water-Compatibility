package com.vidaj.tfcwatercompatibility.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.IClassTransformer;

public class FluidRegistryTransformer implements IClassTransformer{

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {

		if (name.equals("net.minecraftforge.fluids.FluidRegistry")) {
			ClassReader cr = new ClassReader(basicClass);
			ClassWriter cw = new ClassWriter(cr, 0);
			ClassVisitor ca = new FluidFixerClassVisitor(cw);
			cr.accept(ca, 0);
			
			return cw.toByteArray();
		}
		
		return basicClass;
	}
	
	class FluidFixerClassVisitor extends ClassVisitor {

		public FluidFixerClassVisitor(ClassVisitor cv) {
			super(ASM4, cv);
		}
		
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,String signature, String[] exceptions) {
			MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);
			if (name.equals("getFluid") && "(Ljava/lang/String;)Lnet/minecraftforge/fluids/Fluid;".equals(desc)) // generate
			{
				return new FluidRegistryTransformer.AddEventsMethodVisitor(ASM4, visitor);
			}
			
			return visitor;
		}
	}
	
	class AddEventsMethodVisitor extends MethodVisitor {
		public AddEventsMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

		@Override
		public void visitCode() {
		      this.mv.visitLdcInsn("water");
		      this.mv.visitVarInsn(25, 0);
		      this.mv.visitMethodInsn(182, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
		      Label l0 = new Label();
		      this.mv.visitJumpInsn(153, l0);
		      this.mv.visitFieldInsn(178, "com/dunk/tfc/api/TFCFluids", "FRESHWATER", "Lnet/minecraftforge/fluids/Fluid;");
		      this.mv.visitInsn(176);
		      this.mv.visitLabel(l0);
		      this.mv.visitFrame(3, 0, null, 0, null);
		      super.visitCode();
		}
	}

}
