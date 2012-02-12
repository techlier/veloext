/**
 * Directive extensions for Apache Velocity.
 * Copyright (c) 2010,2011 Techlier Inc. All rights reserved.
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

import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.Node;


/**
 *
 *
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 * @since 2011/01/10
 * @version $Id$
 * <pre>
 * $Log$
 * </pre>
 */
public class Unset extends Directive {

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getName()
     */
    @Override
    public String getName() {
        return "unset";
    }

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getType()
     */
    @Override
    public int getType() {
        return LINE;
    }

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#render(org.apache.velocity.context.InternalContextAdapter, java.io.Writer, org.apache.velocity.runtime.parser.node.Node)
     */
    @Override
    public boolean render(final InternalContextAdapter context,
                          final Writer writer,
                          final Node node) {
        final DirectiveUtils util = new DirectiveUtils(this, rsvc, context, node);

        final int numChildren = node.jjtGetNumChildren();
        if (numChildren < 1 || numChildren > 2) {
            return util.error("invalid argument count");
        }

        final Node referenceNameNode = node.jjtGetChild(0);
        if (referenceNameNode.getType() != ParserTreeConstants.JJTREFERENCE) {
            return util.error("first argument must be a reference");
        }
        final String referenceName = ((ASTReference)referenceNameNode).getRootString();
        if (numChildren > 1) {
            Object value = node.jjtGetChild(1).value(context);
            if (value != null
                    && (value.equals(Boolean.TRUE) || Boolean.parseBoolean(value.toString()))) {
                deepRemove(context, referenceName);
                return true;
            }
        }
        context.remove(referenceName);
        return true;
    }

    private void deepRemove(InternalContextAdapter context, final String referenceName) {
        while (context != null) {
            context.remove(referenceName);
            context = context.getBaseContext();
        }
    }

}
