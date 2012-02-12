/**
 * Directive extensions for Apache Velocity.
 * Copyright (c) 2010 Techlier Inc. All rights reserved.
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
package jp.techlier.extentions.velocity.directive;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.util.introspection.Info;


/**
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @version $Id: DirectiveUtils.java 9 2010-09-12 10:07:34Z kazuhide $
 */
public class DirectiveUtils {

    /**
     * Add all pluggable directives defined in this package.
     * @see Block
     * @see Apply
     * @see Import
     * @see Defvar
     * @see Defconst
     * @see Displace
     */
    public static void addUserDirectives() {
        Velocity.addProperty("userdirective", Defvar.class.getName());
        Velocity.addProperty("userdirective", Defconst.class.getName());
        Velocity.addProperty("userdirective", Unset.class.getName());
        Velocity.addProperty("userdirective", Import.class.getName());
        Velocity.addProperty("userdirective", Displace.class.getName());
        Velocity.addProperty("userdirective", Apply.class.getName());
        Velocity.addProperty("userdirective", Block.class.getName());
        Velocity.addProperty("userdirective", Prepend.class.getName());
        Velocity.addProperty("userdirective", Append.class.getName());
        Velocity.addProperty("userdirective", Nil.class.getName());
        Velocity.addProperty("userdirective", Nop.class.getName());
    }


    private final Directive directive_;
    private final RuntimeServices runtimeServices_;
    private final InternalContextAdapter context_;
    private final ASTDirective directiveNode_;
    private final Info info_;

    /**
     * @param directive {@link Directive}
     * @param runtimeServices {@link RuntimeServices}
     * @param context {@link InternalContextAdapter}
     * @param directiveNode {@link Node}
     */
    public DirectiveUtils(final Directive directive,
                          final RuntimeServices runtimeServices,
                          final InternalContextAdapter context,
                          final Node directiveNode) {
        this.directive_ = directive;
        this.runtimeServices_ = runtimeServices;
        this.context_ = context;
        this.directiveNode_ = (ASTDirective)directiveNode;
        this.info_ = new Info(context.getCurrentTemplateName(),
                              directiveNode.getLine(), directiveNode.getColumn());
    }

    /**
     * Log an error message.
     * @param message error message
     * @return Always false.
     */
    boolean error(final String message) {
        return error(message, null);
    }

    /**
     * Log an error message and accompanying Throwable.
     * @param message
     * @param cause
     * @return Always false.
     */
    boolean error(final String message, final Throwable cause) {
        if (cause == null) {
            runtimeServices_.getLog().error(outputMessage(message));
        }
        else {
            runtimeServices_.getLog().error(outputMessage(message), cause);
        }
        return false;
    }

    boolean warn(final String message) {
        runtimeServices_.getLog().warn(outputMessage(message));
        return false;
    }

    private String outputMessage(final String message) {
        return "#" + directive_.getName() + "() " + message + ": " + info_;
    }


    /**
     * @param index index of argument node.
     * @return The argument value.
     */
    String getStringArgument(final int index) {
        final Node arg = directiveNode_.jjtGetChild(index);
        if (arg == null) {
            error("null argument");
            return null;
        }

        else if (arg.getType() != ParserTreeConstants.JJTSTRINGLITERAL
                && arg.getType() != ParserTreeConstants.JJTREFERENCE) {
            error("first argument must be a token");
            return null;
        }

        final Object value = arg.value(context_);
        if (value == null) {
            error("null argument");
            return null;
        }
        return value.toString();
    }

}
