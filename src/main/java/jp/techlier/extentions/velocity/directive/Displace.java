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

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.node.Node;


/**
 * Pluggable directive that handles the <code>#displace()</code> statement.
 * <p>
 * importディレクティブの変形。
 * 指定されたテンプレートが存在しない場合には#displace ～ #endで囲まれたブロックをパースする。
 * </p>
 * <p>
 * <b>記述例）</b>
 * <pre>
 * template...
 * #displace('import.vm')
 * import.vmが存在しない場合には、このブロックの内容が出力される。
 * import.vmが存在する場合には、import.vmの内容が出力され、
 * このブロックは無視される。
 * #end
 * </pre>
 *　</p>
 *
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @version $Id: Displace.java 9 2010-09-12 10:07:34Z kazuhide $
 */
public class Displace extends Import {

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getName()
     */
    @Override
    public String getName() {
        return "displace";
    }

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getType()
     */
    @Override
    public int getType() {
        return BLOCK;
    }

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#render(org.apache.velocity.context.InternalContextAdapter, java.io.Writer, org.apache.velocity.runtime.parser.node.Node)
     */
    @Override
    public boolean render(final InternalContextAdapter context,
                          final Writer writer,
                          final Node node)
            throws IOException, ResourceNotFoundException,
                   ParseErrorException, MethodInvocationException {
        final DirectiveUtils util = new DirectiveUtils(this, rsvc, context, node);

        if (node.jjtGetNumChildren() != 2) {
            return util.error("invalid argument count");
        }

        final Node arg = node.jjtGetChild(0);
        if (arg != null) {
            if (arg.getType() != ParserTreeConstants.JJTSTRINGLITERAL
                    && arg.getType() != ParserTreeConstants.JJTREFERENCE) {
                util.error("first argument must be a token");
                return false;
            }
            final Object value = arg.value(context);
            if (value != null) {
                final String templateName = getAbstructTemplateName(context, value.toString());
                if (rsvc.getLoaderNameForResource(templateName) != null) {
                    return super.render(context, writer, node);
                }
            }
        }
        return node.jjtGetChild(1).render(context, writer);
    }

}
