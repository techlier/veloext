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

import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.Node;


/**
 * Pluggable directive that handles the <code>#defvar()</code> statement.
 * <p>
 * 指定された変数が未定義の場合にのみ、新しい変数を定義する。
 * </p>
 * <p>
 * <b>記述例）</b>
 * <pre>
 * template...
 * #defvar($var, 'value')
 * $var
 * #defvar($var, 'this statememnt is not effective')
 * $var
 * #set($var = 'value is changed')
 * $var
 *
 * output...
 * value
 * value
 * value is changed
 * </pre>
 * defvarディレクティブは、
 * #if(!$var)
 * #set($var = 'value')
 * #end
 * と同等の結果を生じる。
 *　</p>
 *
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @version $Id: Defvar.java 9 2010-09-12 10:07:34Z kazuhide $
 */
public class Defvar extends Directive {

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getName()
     */
    @Override
    public String getName() {
        return "defvar";
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

        if (node.jjtGetNumChildren() != 2) {
            return util.error("invalid argument count");
        }

        final Node referenceNameNode = node.jjtGetChild(0);
        if (referenceNameNode.getType() != ParserTreeConstants.JJTREFERENCE) {
            return util.error("first argument must be a reference");
        }
        final String referenceName = ((ASTReference)referenceNameNode).getRootString();
        if (context.get(referenceName) == null) {
            context.put(referenceName, node.jjtGetChild(1).value(context));
        }
        return true;
    }

}
