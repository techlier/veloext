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

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.parser.node.Node;

import jp.techlier.extensions.velocity.directive.Nil.NilWriter;


/**
 * Pluggable directive that handles the <code>#apply()</code> statement.
 * <p>
 * importディレクティブの変形。
 * 指定されたテンプレート内で定義されたblockディレクティブを、#apply ~ #endで定義された
 * blockディレクティブの内容で置き換える。
 * </p>
 * <p>
 * <b>記述例）</b>
 * <pre>
 * template...
 * #apply('base.vm')
 * #block('block1')
 * base.vmで定義されたblock1が、ここで記述された内容に置き換えられる。
 * #end
 * #block('undefined')
 * base.vmで未定義のblockは出力されない。
 * #end
 * blockディレクティブ以外は出力されない。
 * #end
 *
 * base.vm...
 * 以下のブロックが置き換えられる。
 * #block('block1')
 * オリジナルのブロック1
 * #end
 * #block('block2')
 * オリジナルのブロック2
 * #end
 *
 * output...
 * 以下のブロックが置き換えられる。
 * base.vmで定義されたblock1が、ここで記述された内容に置き換えられる。
 * オリジナルのブロック2
 * </pre>
 *　</p>
 *
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @since 1.0
 */
public class Apply extends Import {

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getName()
     */
    @Override
    public String getName() {
        return "apply";
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
        if (!context.getAllowRendering()) {
            return true;
        }

        node.jjtGetChild(1).render(context, new ApplyWriter());
        return super.render(context, writer, node);
    }

    static class ApplyWriter extends NilWriter {
        // nothing
    }

}
