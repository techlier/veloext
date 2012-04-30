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

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.InputBase;
import org.apache.velocity.runtime.parser.node.Node;




/**
 * Pluggable directive that handles the <code>#import()</code> statement.
 * <p>
 * 相対パスによるテンプレート指定が可能な、parseディレクティブの変形。
 * </p>
 *
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @since 1.0
 */
public class Import extends InputBase {

    public static final int MAXDEPTH_DEFAULT = 20;


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


    protected DirectiveHelper helper_;

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#init(org.apache.velocity.runtime.RuntimeServices, org.apache.velocity.context.InternalContextAdapter, org.apache.velocity.runtime.parser.node.Node)
     */
    @Override
    public void init(final RuntimeServices rs, final InternalContextAdapter context, final Node node)
            throws TemplateInitException {
        super.init(rs, context, node);

        helper_ = new DirectiveHelper(this, rsvc, context, node);
        if (node.jjtGetNumChildren() != (getType() == LINE ? 1 : 2)) {
            throw helper_.newTemplateInitException(node, "requires exactly one argument.");
        }
        helper_.checkArgumentMustBeString(0);
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
        if (!context.getAllowRendering()) {
            return true;
        }

        if (context.getTemplateNameStack().length
                >= rsvc.getInt(RuntimeConstants.PARSE_DIRECTIVE_MAXDEPTH, MAXDEPTH_DEFAULT)) {
            return helper_.error("max recursion depth reached: "
                              + Arrays.asList(context.getTemplateNameStack()));
        }

        final String templateName = getAbstructTemplateName(context, helper_.getStringArgument(0));
        if (templateName == null) {
            return helper_.error("argument must not be null.");
        }
        helper_.debug(templateName);

        try {
            final Template importingTemplate = rsvc.getTemplate(templateName, getInputEncoding(context));
            try {
                context.pushCurrentTemplateName(templateName);
                ((Node)importingTemplate.getData()).render(context, writer);
            } finally {
                context.popCurrentTemplateName();
            }
        } catch (RuntimeException e) {
            helper_.error("rendering failure.", e);
            throw e;
        } catch (Exception e) {
            helper_.error("rendering failure.", e);
            throw new VelocityException(e);
        }
        return true;
    }

    protected String getAbstructTemplateName(final InternalContextAdapter context,
                                             final String templateName) {
        if (templateName == null || templateName.startsWith("/")) {
            return templateName;
        }
        final String currentTemplateName = context.getCurrentTemplateName();
        String currentTemplatePath = ".";
        if (currentTemplateName.indexOf('/') >= 0) {
            currentTemplatePath = StringUtils.substringBeforeLast(currentTemplateName, "/");
        }
        return currentTemplatePath + "/" + templateName;
    }

}
