/*
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
package jp.techlier.extensions.velocity.directive;

import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
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
 * @since 1.0
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


    protected DirectiveHelper helper_;

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#init(org.apache.velocity.runtime.RuntimeServices, org.apache.velocity.context.InternalContextAdapter, org.apache.velocity.runtime.parser.node.Node)
     */
    @Override
    public void init(final RuntimeServices rs, final InternalContextAdapter context, final Node node)
            throws TemplateInitException {
        super.init(rs, context, node);

        helper_ = new DirectiveHelper(this, rsvc, context, node);
        if (node.jjtGetNumChildren() != 2) {
            throw helper_.newTemplateInitException(node, "requires exactly two arguments.");
        }
        helper_.checkArgumentMustBeReference(0);
    }


    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#render(org.apache.velocity.context.InternalContextAdapter, java.io.Writer, org.apache.velocity.runtime.parser.node.Node)
     */
    @Override
    public boolean render(final InternalContextAdapter context,
                          final Writer writer,
                          final Node node) {
        final String referenceName = helper_.getReferenceName(node.jjtGetChild(0));
        if (context.get(referenceName) == null) {
            context.put(referenceName, node.jjtGetChild(1).value(context));
        }
        return true;
    }

}
