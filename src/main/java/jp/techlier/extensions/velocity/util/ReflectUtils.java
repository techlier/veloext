/*
 * Copyright (c) 2009-2010 Techlier Inc. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jp.techlier.extensions.velocity.util;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;


/**
 * Utilities for accessing to object by reflection.
 * This class was originally written as TestUtils.
 *
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 * @since 20090709
 */
public class ReflectUtils {

    /**
     * reflect処理によって、指定されたオブジェクトのフィールド値を取得する。
     * <p>
     * ReflectUtils.get(obj.getClass(), obj, name) に同じ。
     * </p>
     *
     * @see #get(Class,Object,String)
     * @param obj 取得対象となるオブジェクト
     * @param filedName フィールド名
     * @return フィールド値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     */
    public static Object get(final Object obj, final String filedName)
            throws ReflectUtilsException {
        return get(obj.getClass(), obj, filedName);
    }

    /**
     * reflect処理によって、指定されたクラスのスタティックフィールド値を取得する。
     * <p>
     * ReflectUtils.get(clazz, null, name) に同じ。
     * </p>
     *
     * @see #get(Class,Object,String)
     * @param clazz 取得対象となるクラス
     * @param name フィールド名
     * @return フィールド値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     */
    public static Object get(final Class<?> clazz, final String name)
            throws ReflectUtilsException {
        return get(clazz, null, name);
    }

    /**
     * reflect処理によって、指定されたフィールド値を取得する。
     * <p>
     * private修飾されたフィールドからも強制的に値の取得が可能。<br />
     * 指定されたクラスで宣言されたフィールドのみを取得対象とするため、
     * 親クラスで宣言されたフィールドの値を取得したい場合には、
     * <code>clazz</code>に当該クラスを指定すること。
     * </p>
     *
     * @see java.lang.Class#getDeclaredField
     * @see java.lang.reflect.Field#get
     * @param obj 取得対象となるオブジェクト
     * @param clazz 取得対象となるクラス
     * @param name フィールド名
     * @return フィールド値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     */
    public static Object get(final Class<?> clazz, final Object obj, final String name)
            throws ReflectUtilsException {
        final Field field;
        try {
            field = clazz.getDeclaredField(name);
        } catch (final NoSuchFieldException ex) {
            throw new ReflectUtilsException("No such field: "+clazz.getName()+"#"+name, ex);
        }
        return get(obj, field);
    }


    public static Object get(final Object obj, final Field field) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                final boolean previousAccessibility = field.isAccessible();
                try {
                    field.setAccessible(true);
                    return field.get(obj);
                } catch (final Exception ex) {
                    throw new ReflectUtilsException(field.toString(), ex);
                } finally {
                    field.setAccessible(previousAccessibility);
                }
            }});
    }


    /**
     * reflect処理によって、指定されたオブジェクトのフィールド値を変更する。
     * <p>
     * ReflectUtils.set(obj.getClass(), obj, name, value) に同じ。
     * </p>
     *
     * @see #set(Class,Object,String,Object)
     * @param obj 変更対象となるオブジェクト
     * @param name フィールド名
     * @param value フィールド値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     */
    public static void set(final Object obj, final String name, final Object value)
            throws ReflectUtilsException {
        set(obj.getClass(), obj, name, value);
    }

    /**
     * reflect処理によって、指定されたクラスのスタティックフィールド値を変更する。
     * <p>
     * ReflectUtils.set(clazz, null, name, value) に同じ。
     * </p>
     *
     * @see #set(Class,Object,String,Object)
     * @param clazz 変更対象となるクラス
     * @param name フィールド名
     * @param value フィールド値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     */
    public static void set(final Class<?> clazz, final String name, final Object value)
            throws ReflectUtilsException {
        set(clazz, null, name, value);
    }

    /**
     * reflect処理によって、指定されたフィールド値を変更する。
     * <p>
     * privateおよびfinal修飾されたフィールドも強制的に値の変更が可能だが、
     * コンパイル時に値が確定可能なfinalフィールド値への参照はリテラル値として扱われるため、
     * 変更後の値を正しく参照するためにはreflectを用いることが必要。<br />
     * static final修飾されたフィールドの変更は出来ない。<br />
     * 指定されたクラスで宣言されたフィールドのみを変更対象とするため、
     * 親クラスで宣言されたフィールドの値を変更したい場合には、
     * <code>clazz</code>に当該クラスを指定すること。
     * </p>
     *
     * @see java.lang.Class#getDeclaredField
     * @see java.lang.reflect.Field#set
     * @param clazz 変更対象となるクラス
     * @param obj 変更対象となるオブジェクト
     * @param name フィールド名
     * @param value フィールド値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     */
    public static void set(final Class<?> clazz, final Object obj, final String name,
                           final Object value)
            throws ReflectUtilsException {
        final Field field;
        try {
            field = clazz.getDeclaredField(name);
        } catch (final NoSuchFieldException ex) {
            throw new ReflectUtilsException("No such field: "+clazz.getName()+"."+name, ex);
        }
        set(obj, field, value);
    }


    public static void set(final Object obj, final Field field, final Object value) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                final boolean previousAccessibility = field.isAccessible();
                try {
                    field.setAccessible(true);
                    field.set(obj, value);
                    return null;
                } catch (final Exception ex) {
                    throw new ReflectUtilsException(field.toString(), ex);
                } finally {
                    field.setAccessible(previousAccessibility);
                }
            }});
    }


    /**
     * reflect処理によって、指定されたオブジェクトのメソッドを実行する。
     * <p>
     * invoke(obj.getClass(), obj, name, null, null) に同じ。
     * </p>
     *
     * @see #invoke(Class,String,Class[],Object[])
     * @param obj 実行対象となるオブジェクト
     * @param name メソッド名
     * @return メソッドの戻り値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     * @throws Exception メソッド実行中に発生した例外
     */
    public static Object invoke(final Object obj, final String name)
            throws ReflectUtilsException, Exception {
        return invoke(obj.getClass(), obj, name, null, (Object[])null);
    }

    /**
     * reflect処理によって、指定されたオブジェクトのメソッドを実行する。
     * <p>
     * invoke(obj.getClass(), obj, name, null, args) に同じ。
     * </p>
     *
     * @see #invoke(Class,String,Class[],Object[])
     * @param obj 実行対象となるオブジェクト
     * @param name メソッド名
     * @param args メソッドの引数リスト
     * @return メソッドの戻り値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     * @throws Exception メソッド実行中に発生した例外
     */
    public static Object invoke(final Object obj, final String name,
                                final Object... args)
            throws ReflectUtilsException, Exception {
        return invoke(obj.getClass(), obj, name, null, args);
    }

    /**
     * reflect処理によって、指定されたオブジェクトのメソッドを実行する。
     * <p>
     * invoke(obj.getClass(), obj, name, parameterTypes, args) に同じ。
     * </p>
     *
     * @see #invoke(Class,String,Class[],Object[])
     * @param obj 実行対象となるオブジェクト
     * @param name メソッド名
     * @param parameterTypes メソッド引数の型リスト
     * @param args メソッドの引数リスト
     * @return メソッドの戻り値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     * @throws Exception メソッド実行中に発生した例外
     */
    public static Object invoke(final Object obj, final String name,
                                final Class<?>[] parameterTypes, final Object... args)
            throws ReflectUtilsException, Exception {
        return invoke(obj.getClass(), obj, name, parameterTypes, args);
    }

    /**
     * reflect処理によって、指定されたクラスのスタティックメソッドを実行する。
     * <p>
     * invoke(clazz, null, name, null, null) に同じ。
     * </p>
     *
     * @see #invoke(Class,String,Class[],Object[])
     * @param clazz 実行対象となるクラス
     * @param name メソッド名
     * @return メソッドの戻り値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     * @throws Exception メソッド実行中に発生した例外
     */
    public static Object invoke(final Class<?> clazz, final String name)
            throws ReflectUtilsException, Exception {
        return invoke(clazz, null, name, null, (Object[])null);
    }

    /**
     * reflect処理によって、指定されたクラスのスタティックメソッドを実行する。
     * <p>
     * invoke(clazz, null, name, null, args) に同じ。
     * </p>
     *
     * @see #invoke(Class,String,Class[],Object[])
     * @param clazz 実行対象となるクラス
     * @param name メソッド名
     * @param args メソッドの引数リスト
     * @return メソッドの戻り値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     * @throws Exception メソッド実行中に発生した例外
     */
    public static Object invoke(final Class<?> clazz, final String name,
                                final Object... args)
            throws ReflectUtilsException, Exception {
        return invoke(clazz, null, name, null, args);
    }

    /**
     * reflect処理によって、指定されたクラスのスタティックメソッドを実行する。
     * <p>
     * invoke(clazz, null, name, parameterTypes, args) に同じ。
     * </p>
     *
     * @see #invoke(Class,String,Class[],Object[])
     * @param clazz 実行対象となるクラス
     * @param name メソッド名
     * @param parameterTypes メソッド引数の型リスト
     * @param args メソッドの引数リスト
     * @return メソッドの戻り値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     * @throws Exception メソッド実行中に発生した例外
     */
    public static Object invoke(final Class<?> clazz, final String name,
                                final Class<?>[] parameterTypes, final Object... args)
            throws ReflectUtilsException, Exception {
        return invoke(clazz, null, name, parameterTypes, args);
    }

    /**
     * reflect処理によって、指定されたオブジェクトのメソッドを実行する。
     * <p>
     * private修飾されたメソッドも強制的に実行可。<br />
     * 指定されたクラスで宣言されたメソッドのみを実行対象とするため、
     * 親クラスで宣言されたメソッドを実行したい場合には、
     * <code>clazz</code>に当該クラスを指定すること。
     * </p>
     *
     * @see java.lang.Class#getDeclaredMethod
     * @see java.lang.reflect.Method#invoke
     * @param clazz 実行対象となるクラス
     * @param obj 実行対象となるオブジェクト
     * @param name メソッド名
     * @param parameterTypes メソッド引数の型リスト
     * @param parameters メソッドの引数リスト
     * @return メソッドの戻り値
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     * @throws Exception メソッド実行中に発生した例外
     */
    public static Object invoke(final Class<?> clazz, final Object obj, final String name,
                                Class<?>[] parameterTypes, final Object... parameters)
            throws ReflectUtilsException, Exception {
        if (parameterTypes == null && parameters != null) {
            parameterTypes = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                parameterTypes[i] = parameters[i].getClass();
            }
        }

        final Method method;
        try {
            method = clazz.getDeclaredMethod(name, parameterTypes);
        } catch (final NoSuchMethodException ex) {
            throw new ReflectUtilsException("No such method: "+ex.getMessage(), ex);
        }
        return invoke(obj, method, parameters);
    }


    public static Object invoke(final Object obj, final Method method, final Object... parameters)
            throws ReflectUtilsException, Exception {
        try {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    final boolean previousAccessibility = method.isAccessible();
                    try {
                        method.setAccessible(true);
                        return method.invoke(obj, parameters);
                    } catch (final Exception ex) {
                        throw new ReflectUtilsException(method.toString(), ex);
                    } finally {
                        method.setAccessible(previousAccessibility);
                    }
                }});
        } catch (final ReflectUtilsException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof InvocationTargetException) {
                throwCause(cause.getCause());
            }
            throw ex;
        }
    }


    /**
     * reflect処理によって、指定されたクラスのインスタンスを生成する。
     * <p>
     * newInstance(clazz, null, null) に同じ。
     * </p>
     *
     * @see #newInstance(Class,Class[],Object[])
     * @param <T> 生成するクラス
     * @param clazz 生成するクラス
     * @return 生成されたインスタンス
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     * @throws Exception コンストラクタ実行中に発生した例外
     */
    public static<T> T newInstance(final Class<T> clazz)
            throws ReflectUtilsException, Exception {
        return newInstance(clazz, null, (Object[])null);
    }

    /**
     * reflect処理によって、指定されたクラスのインスタンスを生成する。
     * <p>
     * newInstance(clazz, null, args) に同じ。
     * </p>
     *
     * @see #newInstance(Class,Class[],Object[])
     * @param <T> 生成するクラス
     * @param clazz 生成するクラス
     * @param args コンストラクタの引数リスト
     * @return 生成されたインスタンス
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     * @throws Exception コンストラクタ実行中に発生した例外
     */
    public static<T> T newInstance(final Class<T> clazz,
                                   final Object... args)
            throws ReflectUtilsException, Exception {
        return newInstance(clazz, null, args);
    }

    /**
     * reflect処理によって、指定されたクラスのインスタンスを生成する。
     * <p>
     * private修飾されたコンストラクタも強制的に実行可。<br />
     * </p>
     *
     * @see java.lang.Class#getDeclaredConstructor
     * @see java.lang.reflect.Constructor#newInstance
     * @param <T> 生成するクラス
     * @param clazz 生成するクラス
     * @param parameterTypes コンストラクタ引数の型リスト
     * @param args コンストラクタの引数リスト
     * @return 生成されたインスタンス
     * @throws ReflectUtilsException reflect処理で発生した例外をcauseとして保持する
     * @throws Exception コンストラクタ実行中に発生した例外
     */
    public static<T> T newInstance(final Class<T> clazz,
                                   Class<?>[] parameterTypes,
                                   final Object... args)
            throws ReflectUtilsException, Exception {
        if (parameterTypes == null && args != null) {
            parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
        }

        final Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor(parameterTypes);
        } catch (final NoSuchMethodException ex) {
            throw new ReflectUtilsException(
                "No such constructor: "+ex.getMessage(), ex);
        }
        return newInstance(constructor, args);
    }


    public static<T> T newInstance(final Constructor<T> constructor,
                                   final Object... args)
            throws ReflectUtilsException, Exception {
        try {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {
                @Override
                public T run() {
                    final boolean previousAccessibility = constructor.isAccessible();
                    try {
                        constructor.setAccessible(true);
                        return constructor.newInstance(args);
                    } catch (final Exception ex) {
                        throw new ReflectUtilsException(constructor.toString(), ex);
                    } finally {
                        constructor.setAccessible(previousAccessibility);
                    }
                }});
        } catch (final ReflectUtilsException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof InvocationTargetException) {
                throwCause(cause.getCause());
            }
            throw ex;
        }
    }


    private static void throwCause(final Throwable cause) throws Exception {
        if (cause instanceof Exception) {
            throw (Exception)cause;
        }
        else if (cause instanceof Error) {
            throw (Error)cause;
        }
    }

}
