/*
 * Copyright (c) 2011 Techlier Inc. All rights reserved.
 */
package jp.techlier.extentions.velocity.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.parser.node.Node;

import jp.techlier.extentions.velocity.directive.Nil.NilWriter;


/**
 *
 *
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 * @since 2011/02/19
 * @version $Id$
 * <pre>
 * $Log$
 * </pre>
 */
public class Append extends Block {

    @Override
    public String getName() {
        return "append";
    }

    @Override
    public boolean render(final InternalContextAdapter context,
                          final Writer writer,
                          final Node node)
            throws IOException, ResourceNotFoundException,
                   ParseErrorException, MethodInvocationException {
        final List<Object> chainOfBlock = getBlockChain(context, node);
        if (chainOfBlock == null) return false;

        if (writer instanceof NilWriter) { // inside apply derective
            if (chainOfBlock.contains(BASEBLOCK_RENDERING_POSITION_MARKER)) {
                final Object blockObject = parseBlock(context, node);
                if (blockObject == null) return false;
                chainOfBlock.add(chainOfBlock.indexOf(BASEBLOCK_RENDERING_POSITION_MARKER) + 1,
                                 blockObject);
            }
            return true;
        }
        else {
            final DirectiveUtils util = new DirectiveUtils(this, rsvc, context, node);
            util.warn("found outside of #apply");
            return false;
        }
    }

}
