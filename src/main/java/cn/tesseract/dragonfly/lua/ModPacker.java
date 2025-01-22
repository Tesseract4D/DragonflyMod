package cn.tesseract.dragonfly.lua;

import cn.tesseract.dragonfly.DragonflyCoreMod;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class ModPacker {
    public static void generateModFile(String packageName, String modid, File file) {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(new Attributes.Name("FMLCorePlugin"), packageName + ".CoreMod");
        manifest.getMainAttributes().put(new Attributes.Name("FMLCorePluginContainsFMLMod"), "true");

        String packagePath = packageName.replace('.', '/');
        String assetsPath = "assets/" + modid + "/";
        try (FileOutputStream fos = new FileOutputStream(file); JarOutputStream jos = new JarOutputStream(fos, manifest)) {
            jos.putNextEntry(new JarEntry(packagePath + "/Main.class"));
            jos.write(ModPacker.generateModClass(packagePath, modid));
            jos.closeEntry();

            jos.putNextEntry(new JarEntry(packagePath + "/CoreMod.class"));
            jos.write(ModPacker.generateCoreModClass(packagePath, modid));
            jos.closeEntry();

            File[] list = DragonflyCoreMod.scriptDir.listFiles();
            StringBuilder sb = new StringBuilder();
            for (File f : list) {
                String name = f.getName();
                if (f.isFile() && name.endsWith(".lua")) {
                    if (sb.length() != 0) sb.append('\n');
                    sb.append(name, 0, name.length() - 4);
                }
            }

            jos.putNextEntry(new JarEntry(assetsPath + "scriptList.txt"));
            jos.write(sb.toString().getBytes());
            jos.closeEntry();

            addFilesToJar(assetsPath + "scripts/", DragonflyCoreMod.scriptDir, jos);
            File resources = new File(Launch.minecraftHome, "resources/" + modid);
            if (resources.exists())
                addFilesToJar(assetsPath, resources, jos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addFilesToJar(String base, File dir, JarOutputStream jos) throws IOException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                addFilesToJar(base + file.getName() + "/", file, jos);
            } else {
                jos.putNextEntry(new JarEntry(base + file.getName()));
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        jos.write(buffer, 0, bytesRead);
                    }
                }
                jos.closeEntry();
            }
        }
    }

    private static byte[] generateModClass(String packageName, String modid) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, packageName + "/Main", null, "java/lang/Object", null);

        AnnotationVisitor av = cw.visitAnnotation("Lcpw/mods/fml/common/Mod;", true);
        av.visit("modid", modid);
        av.visitEnd();

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        cw.visitEnd();

        return cw.toByteArray();
    }

    private static byte[] generateCoreModClass(String packageName, String modid) {
        String superName = LuaCoreMod.class.getName().replace('.', '/');
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, packageName + "/CoreMod", null, superName, null);

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitLdcInsn(modid);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        cw.visitEnd();

        return cw.toByteArray();
    }
}
