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
import java.util.Arrays;

import org.apache.velocity.Template;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.directive.InputBase;
import org.apache.velocity.runtime.parser.node.Node;



/**
 * Pluggable directive that handles the <code>#import()</code> statement.
 * <p>
 * 相対パスによるテンプレート指定が可能な、parseディレクティブの変形。
 * </p>
 *
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @version $Id: Import.java 9 2010-09-12 10:07:34Z kazuhide $
 */
public class Import extends InputBase {

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getName()
     */
    @Override
    public String getName() {
        return "import";
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
                          final Node node)
            throws IOException, ResourceNotFoundException,
                   ParseErrorException, MethodInvocationException {
        final DirectiveUtils util = new DirectiveUtils(this, rsvc, context, node);

        final int numChildren = node.jjtGetNumChildren();
        if (numChildren == 0 || numChildren > 2) {
            return util.error("invalid argument count");
        }
        if (context.getTemplateNameStack().length
                >= rsvc.getInt(RuntimeConstants.PARSE_DIRECTIVE_MAXDEPTH, 20)) {
            return util.error("max recursion depth reached: "
                              + Arrays.asList(context.getTemplateNameStack()));
        }

        final String templateName = getAbstructTemplateName(context, util.getStringArgument(0));
        try {
            final Template importingTemplate = rsvc.getTemplate(templateName, getInputEncoding(context));
            try {
                context.pushCurrentTemplateName(templateName);
                ((Node)importingTemplate.getData()).render(context, writer);
            } finally {
                context.popCurrentTemplateName();
            }
        } catch (Exception e) {
            return util.error("import error", e);
        }
        return true;
    }

    protected String getAbstructTemplateName(final InternalContextAdapter context,
                                             final String templateName) {
        if (templateName.startsWith("/")) {
            return templateName;
        }
        final String currentTemplateName = context.getCurrentTemplateName();
        final String currentTemplatePath = currentTemplateName.substring(0, currentTemplateName.lastIndexOf('/'));
        return currentTemplatePath + "/" + templateName;
    }

}
