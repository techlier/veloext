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

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.util.introspection.Info;


/**
 * Helper methods for directive implementation.
 *
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 * @since 1.3
 */
public class DirectiveHelper {

    private final Directive directive_;
    private final RuntimeServices runtimeServices_;
    private final InternalContextAdapter context_;
    private final ASTDirective directiveNode_;

    /**
     * @param directive {@link Directive}
     * @param runtimeServices {@link RuntimeServices}
     * @param context {@link InternalContextAdapter}
     * @param directiveNode {@link Node}
     */
    public DirectiveHelper(final Directive directive,
                           final RuntimeServices runtimeServices,
                           final InternalContextAdapter context,
                           final Node directiveNode) {
        this.directive_ = directive;
        this.runtimeServices_ = runtimeServices;
        this.context_ = context;
        this.directiveNode_ = (ASTDirective)directiveNode;
    }


    public TemplateInitException newTemplateInitException(final Node node, String message) {
        error(message);
        return new TemplateInitException("#" + directive_.getName() + "() " + message,
                                         context_.getCurrentTemplateName(),
                                         node.getColumn(),
                                         node.getLine());
    }


    public void checkArgumentMustBeReference(final int index) {
        final Node childNode = directiveNode_.jjtGetChild(index);
        if (childNode.getType() != ParserTreeConstants.JJTREFERENCE) {
            throw newTemplateInitException(childNode, "argument #"+index+" must be a reference.");
        }
    }

    public void checkArgumentMustBeString(final int index) {
        final Node childNode = directiveNode_.jjtGetChild(index);
        if (childNode.getType() != ParserTreeConstants.JJTSTRINGLITERAL
                && childNode.getType() != ParserTreeConstants.JJTREFERENCE) {
            throw newTemplateInitException(childNode, "argument #"+index+" must be a literal or reference.");
        }
    }


    public Node getChildNode(final Node node, final int index) {
        if (node != null && node.jjtGetNumChildren() > index) {
            return node.jjtGetChild(index);
        }
        return null;
    }


    public String getReferenceName(final Node node) {
        if (node != null) {
            if (node instanceof ASTReference) {
                return ((ASTReference)node).getRootString();
            }
            else if (node.getType() == ParserTreeConstants.JJTREFERENCE) {
                return StringUtils.substringAfter(node.getFirstToken().image, "$");
            }
        }
        return null;
    }

    /**
     * @param index index of argument node.
     * @return The argument value.
     */
    public String getStringArgument(final int index) {
        return stringValue(getChildNode(directiveNode_, index));
    }

    public String stringValue(final Node node) {
        if (node == null ||
                (node.getType() != ParserTreeConstants.JJTSTRINGLITERAL
                    && node.getType() != ParserTreeConstants.JJTREFERENCE)) {
            error("argument must be a literal or reference");
        }
        else {
            final Object value = node.value(context_);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }


    public boolean getBooleanArgument(final int index) {
        return booleanValue(getChildNode(directiveNode_, index));
    }

    public boolean booleanValue(final Node node) {
        if (node != null) {
            final Object value = node.value(context_);
            if (value != null) {
                return value.equals(Boolean.TRUE) || Boolean.parseBoolean(value.toString());
            }
        }
        return false;
    }


    /**
     * Log an error message.
     * @param message error message
     * @return Always false.
     */
    public boolean error(final String message) {
        return error(message, null);
    }

    /**
     * Log an error message and accompanying Throwable.
     * @param message
     * @param cause
     * @return Always false.
     */
    public boolean error(final String message, final Throwable cause) {
        if (cause == null) {
            runtimeServices_.getLog().error(formatMessage(message));
        }
        else {
            runtimeServices_.getLog().error(formatMessage(message), cause);
        }
        return false;
    }

    public boolean warn(final String message) {
        runtimeServices_.getLog().warn(formatMessage(message));
        return false;
    }

    public void debug(final String message) {
        runtimeServices_.getLog().debug(formatMessage(message));
    }

    public String formatMessage(final String message) {
        return "#" + directive_.getName() + "() " + message + ", called at " + templateInfo();
    }

    public Info templateInfo() {
        return new Info(context_.getCurrentTemplateName(),
                        directiveNode_.getLine(), directiveNode_.getColumn());
    }

}
