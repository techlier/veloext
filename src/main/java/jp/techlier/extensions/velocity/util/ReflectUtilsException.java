/*
 * Copyright (c) 2009-2012 Techlier Inc. All rights reserved.
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


/**
 * ReflectUtils内で発生したExceptionをRuntimeExceptionとしてラッピングする。
 *
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @since 20090709
 */
@SuppressWarnings("serial")
public class ReflectUtilsException extends RuntimeException {

    ReflectUtilsException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
