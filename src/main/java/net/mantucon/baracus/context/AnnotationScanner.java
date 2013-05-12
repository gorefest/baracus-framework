package net.mantucon.baracus.context;

import android.content.Context;
import android.content.ContextWrapper;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import net.mantucon.baracus.util.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Annotation scanner
 *
 * Disfunctional on some android versions. do not use yet!
 *
 */

@Deprecated
public class AnnotationScanner {

    private AnnotationScanner() { }

    private static final Logger logger = new Logger(AnnotationScanner.class);

    public static <T extends Annotation> List<Class<?>> getClassesAnnotatedWith(Class<T> theAnnotation, Context context){

        // In theory, the class loader is not required to be a PathClassLoader
        Field field = null;
        List<Class<?>> candidates = new ArrayList<Class<?>>();
        String sourceDir = context.getApplicationInfo().sourceDir;
        try {
            DexFile dex = new DexFile(sourceDir);
            PathClassLoader classLoader2 = new PathClassLoader(sourceDir, Thread.currentThread().getContextClassLoader());
            DexClassLoader classLoader = new DexClassLoader(sourceDir, new ContextWrapper(context).getCacheDir().getAbsolutePath(), null, classLoader2);

            Enumeration<String> entries = dex.entries();
            while (entries.hasMoreElements()) {
                // Each entry is a class name, like "foo.bar.MyClass"
                String entry = entries.nextElement();
                if (!entry.contains("$")) {

                    try {
                        // Load the class
//                        Class<?> entryClass = dex.loadClass(entry, classLoader);
                        Class<?> entryClass = classLoader.loadClass(entry);//dexFile.loadClass(entry, classLoader);

                        logger.debug("FOUND CLASS $1", entry);
                        if (entryClass == null) {
                            logger.error("WARNING! Class of $1 is NULL!",entry);
                        }
                        if (entryClass != null && entryClass.getAnnotation(theAnnotation) != null) {
                            logger.error("ADDED CLASS " + entryClass.getName());
                            candidates.add(entryClass);
                        }
                    }catch (IllegalAccessError e) {
                        logger.error("Exception during scan on $1",entry);
                    }
                }
            }

        }catch (IOException e ) {
            throw new RuntimeException(e);
        }catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return candidates;
    }

    public static boolean carriesAnnotation(Class<?> clazz, Class<?> annotation) {
        for (Annotation a : clazz.getAnnotations()) {
            logger.error("$1 carries $2",clazz.getSimpleName(),a.getClass().getName());
            if (a.getClass().getName().equals(annotation.getName())) {
                return true;
            }
        }
        return false;
    }

}
