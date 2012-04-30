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
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;


/**
 * Pluggable directive that handles the <code>#nop()</code> statement.
 *
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 * @since 1.2
 */
public class Nop extends Directive {

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getName()
     */
    @Override
    public String getName() {
        return "nop";
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
        return true;
    }

}
