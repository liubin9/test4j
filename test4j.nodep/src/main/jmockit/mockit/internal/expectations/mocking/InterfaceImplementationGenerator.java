/*
 * Copyright (c) 2006-2012 Rogério Liesenfeld
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations.mocking;

import java.lang.reflect.Type;
import java.util.*;

import static mockit.external.asm4.Opcodes.*;

import mockit.external.asm4.*;
import mockit.internal.*;
import mockit.internal.util.*;

final class InterfaceImplementationGenerator extends MockedTypeModifier
{
   private final List<String> implementedMethods;
   private final String implementationClassName;
   private String interfaceName;
   private String[] initialSuperInterfaces;

   InterfaceImplementationGenerator(ClassReader classReader, Type mockedType, String implementationClassName)
   {
      super(classReader, mockedType);
      implementedMethods = new ArrayList<String>();
      this.implementationClassName = implementationClassName.replace('.', '/');
      Class<?> mockedInterface = Utilities.getClassType(mockedType);
      implementationSignature =
         "Ljava/lang/Object;L" + mockedInterface.getName().replace('.', '/') + implementationSignature;
   }

   @Override
   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
   {
      interfaceName = name;
      initialSuperInterfaces = interfaces;

      super.visit(
         version, ACC_PUBLIC + ACC_FINAL, implementationClassName, implementationSignature,
         superName, new String[] {name});

      generateDefaultConstructor();
   }

   private void generateDefaultConstructor()
   {
      mw = super.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
      mw.visitVarInsn(ALOAD, 0);
      mw.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
      generateEmptyImplementation();
   }

   @Override
   public void visitInnerClass(String name, String outerName, String innerName, int access) {}

   @Override
   public void visitOuterClass(String owner, String name, String desc) {}

   @Override
   public void visitAttribute(Attribute attr) {}

   @Override
   public void visitSource(String file, String debug) {}

   @Override
   public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) { return null; }

   @Override
   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
   {
      if (name.charAt(0) != '<') { // ignores an eventual "<clinit>" class initialization "method"
         generateMethodImplementation(access, name, desc, signature, exceptions);
      }

      return null;
   }

   @SuppressWarnings("AssignmentToMethodParameter")
   private void generateMethodImplementation(
      int access, String name, String desc, String signature, String[] exceptions)
   {
      String methodNameAndDesc = name + desc;

      if (!implementedMethods.contains(methodNameAndDesc)) {
         if (signature != null) {
            signature = genericTypeMap.resolveReturnType(signature);
         }

         mw = super.visitMethod(ACC_PUBLIC, name, desc, signature, exceptions);
         generateDirectCallToHandler(interfaceName, access, name, desc, signature, exceptions, 0);
         generateReturnWithObjectAtTopOfTheStack(desc);
         mw.visitMaxs(1, 0);

         implementedMethods.add(methodNameAndDesc);
      }
   }

   @Override
   public void visitEnd()
   {
      for (String superInterface : initialSuperInterfaces) {
         new MethodGeneratorForImplementedSuperInterface(superInterface);
      }
   }

   private final class MethodGeneratorForImplementedSuperInterface extends ClassVisitor
   {
      String[] superInterfaces;

      MethodGeneratorForImplementedSuperInterface(String interfaceName)
      {
         ClassFile.visitClass(interfaceName, this);
      }

      @Override
      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
      {
         superInterfaces = interfaces;
      }

      @Override
      public FieldVisitor visitField(
         int access, String name, String desc, String signature, Object value) { return null; }

      @Override
      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
      {
         generateMethodImplementation(access, name, desc, signature, exceptions);
         return null;
      }

      @Override
      public void visitEnd()
      {
         for (String superInterface : superInterfaces) {
            new MethodGeneratorForImplementedSuperInterface(superInterface);
         }
      }
   }
}
