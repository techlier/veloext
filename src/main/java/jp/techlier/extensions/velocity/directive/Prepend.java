/*
 * Directive extensions for Apache Velocity.
 * Copyright (c) 2011 Techlier Inc. All rights reserved.
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
import java.util.List;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.parser.node.Node;

import jp.techlier.extensions.velocity.directive.Apply.ApplyWriter;


/**
 * Pluggable directive that handles the <code>#prepend()</code> statement.
 * <p>
 * </p>
 *
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 * @since 1.1
 */
public class Prepend extends Block {

    @Override
    public String getName() {
        return "prepend";
    }


    @Override
    public boolean render(final InternalContextAdapter context,
                          final Writer writer,
                          final Node node)
            throws IOException, ResourceNotFoundException,
                   ParseErrorException, MethodInvocationException {
        final List<Object> chainOfBlock = getBlockChain(context, node);
        if (chainOfBlock == null) return false;

        if (writer instanceof ApplyWriter) { // inside apply derective
            if (chainOfBlock.contains(BASEBLOCK_RENDERING_POSITION_MARKER)) {
                final Object blockObject = parseBlock(context, node.jjtGetChild(1));
                if (blockObject == null) return false;
                chainOfBlock.add(chainOfBlock.indexOf(BASEBLOCK_RENDERING_POSITION_MARKER),
                                 blockObject);
            }
            return true;
        }
        else {
            return helper_.warn("found outside of #apply");
        }
    }

}
