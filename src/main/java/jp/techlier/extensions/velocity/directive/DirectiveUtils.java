/*
 * Directive extensions for Apache Velocity.
 * Copyright (c) 2010-2012 Techlier Inc. All rights reserved.
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
package jp.techlier.extensions.velocity.directive;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.directive.Directive;


/**
 * Utilities for directive implementations.
 * <p>
 * </p>
 *
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @since 1.0
 */
public class DirectiveUtils {

    private static final Class<Directive> DIRECTIVE_CLASSES[] = new Class[] {
        Defvar.class,
        Defconst.class,
        Unset.class,
        Import.class,
        Displace.class,
        Apply.class,
        Block.class,
        Prepend.class,
        Append.class,
        Nil.class,
        Nop.class,
    };


    /**
     * Add all pluggable directives defined in this package to default template engine.
     */
    public static void addUserDirectives() {
        for (final Class impl: DIRECTIVE_CLASSES) {
            Velocity.addProperty("userdirective", impl.getName());
        }
    }

    /**
     * Add all pluggable directives defined in this package.
     */
    public static void addUserDirectives(final VelocityEngine engine) {
        for (final Class impl: DIRECTIVE_CLASSES) {
            engine.addProperty("userdirective", impl.getName());
        }
    }

}
