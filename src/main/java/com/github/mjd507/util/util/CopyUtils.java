package com.github.mjd507.util.util;

import com.google.common.collect.Lists;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Created by mjd on 2018/8/20 15:17
 */
public class CopyUtils {

    private CopyUtils() {
    }

    public static <S, T> T copyObject(S s, Class<T> clazz) {
        try {
            if (s == null) {
                return null;
            }
            T t = clazz.newInstance();
            BeanUtils.copyProperties(s, t);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <S, T> List<T> copyList(List<S> s, Class<T> clazz) {
        List<T> tList = Lists.newArrayList();
        try {
            if (s == null) {
                return tList;
            }
            for (S e : s) {
                T t = copyObject(e, clazz);
                tList.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tList;
    }

    public static <S, T> T copyObjectIgnoreNull(S s, T t) {
        try {
            if (s == null) {
                return null;
            }
            NullValueBeanUtils.copyProperties(s, t);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class NullValueBeanUtils extends BeanUtils {

        public static void copyProperties(Object source, Object target) {
            copyProperties(source, target, null, new String[]{});
        }

        private static void copyProperties(Object source, Object target, @Nullable Class<?> editable,
                                           @Nullable String... ignoreProperties) throws BeansException {

            Assert.notNull(source, "Source must not be null");
            Assert.notNull(target, "Target must not be null");

            Class<?> actualEditable = target.getClass();
            if (editable != null) {
                if (!editable.isInstance(target)) {
                    throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                        "] not assignable to Editable class [" + editable.getName() + "]");
                }
                actualEditable = editable;
            }
            PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
            List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

            for (PropertyDescriptor targetPd : targetPds) {
                Method writeMethod = targetPd.getWriteMethod();
                if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                    PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                    if (sourcePd != null) {
                        Method readMethod = sourcePd.getReadMethod();
                        if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                            try {
                                if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                    readMethod.setAccessible(true);
                                }
                                Object value = readMethod.invoke(source);
                                if (value != null) {
                                    if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                        writeMethod.setAccessible(true);
                                    }
                                    writeMethod.invoke(target, value);
                                }
                            } catch (Throwable ex) {
                                throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                            }
                        }
                    }
                }
            }
        }

    }
}
